package com.myapp.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.article.ArticleIndexConstant;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.UserSearchDto;
import com.myapp.model.search.vo.SearchArticleVo;
import com.myapp.model.user.pojo.ApUser;
import com.myapp.search.service.ApArticleSearchService;
import com.myapp.search.service.ApUserSearchService;
import com.myapp.util.thread.ApThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ApArticleSearchServiceImpl implements ApArticleSearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * Elasticsearch app 文章搜索
     * @param dto 查询条件
     * @return 响应
     */
    @Override
    public ResponseResult search(UserSearchDto dto) {
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 异步调用ApUserSearchService-save，保存用户搜索记录
        // 在搜索页滚屏查询，不需要保存
        if(dto.getFromIndex()==0){
            apUserSearchService.save(dto.getSearchWords(), user.getId());
        }

        List<SearchArticleVo> articleVoList;
        try {
            SearchRequest searchRequest = buildSearchRequest(dto);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            articleVoList = parseSearchResponse(searchResponse);
        } catch (IOException e) {
            log.error("es search error UserSearchDto={}", dto);
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(articleVoList);
    }

    /**
     * 处理搜索结果
     * @param searchResponse 搜索响应
     */
    private static List<SearchArticleVo> parseSearchResponse(SearchResponse searchResponse) {
        List<SearchArticleVo> articleVoList = new ArrayList<>();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        Arrays.stream(searchHits).forEach(hit->{
            String source = hit.getSourceAsString();
            SearchArticleVo articleVo = JSON.parseObject(source, SearchArticleVo.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!=null && highlightFields.size()>0){
                Text[] fragments = highlightFields.get(ArticleIndexConstant.FIELD_TITLE).getFragments();
                String title = StringUtils.collectionToCommaDelimitedString(Arrays.asList(fragments));
                // articleVo.setTitle(title);
                articleVo.setH_title(title);
            }else {
                articleVo.setH_title(articleVo.getTitle());
            }


            articleVoList.add(articleVo);
        });
        return articleVoList;
    }


    /**
     * 创建查询请求
     * @param dto 查询条件
     * @return SearchRequest
     */
    private static SearchRequest buildSearchRequest(UserSearchDto dto) {
        SearchRequest searchRequest = new SearchRequest(ArticleIndexConstant.INDEX);
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // 关键字
        String searchWords = dto.getSearchWords();
        if (StringUtils.hasLength(searchWords)){
            boolQuery.must(new QueryStringQueryBuilder(searchWords)
                    .field(ArticleIndexConstant.FIELD_TITLE)
                    .field(ArticleIndexConstant.FIELD_CONTENT));
        }
        // 时间
        Date minBehotTime = dto.getMinBehotTime();
        if (minBehotTime!=null){
            boolQuery.filter(new RangeQueryBuilder(ArticleIndexConstant.FIELD_PUBLISH_TIME)
                    .lt(minBehotTime));
        }
        // 分页
        searchRequest.source().from(dto.getFromIndex());
        searchRequest.source().size(dto.getPageSize());
        // 高亮
        searchRequest.source().highlighter(new HighlightBuilder()
                .field(ArticleIndexConstant.FIELD_TITLE)
                .preTags("<font style='color: red; font-size: inherit;'>")
                .postTags("</font>"));
        searchRequest.source().query(boolQuery);
        return searchRequest;
    }
}
