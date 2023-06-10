package com.myapp.es;

import com.alibaba.fastjson.JSON;
import com.myapp.es.mapper.ApArticleMapper;
import com.myapp.es.vo.SearchArticleVo;
import com.myapp.model.search.dto.UserSearchDto;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@SpringBootTest
public class ElasticsearchApplicationTest {

    @Autowired
    private ApArticleMapper mapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String PUBLISH_TIME = "publishTime";
    private static String index = "app_info_article";


    @Test
    public void init(){
        List<SearchArticleVo> articleVoList = mapper.loadArticleList();

        BulkRequest bulkRequest =  new BulkRequest();
        articleVoList.forEach(item->{
            String jsonString = JSON.toJSONString(item);
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index("app_info_article")
                    .id(item.getId().toString())
                    .source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        });

        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void query(){

        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setSearchWords("java");
        userSearchDto.setPageNum(0);
        userSearchDto.setPageSize(20);
        userSearchDto.setMinBehotTime(new Date(20000000000000L));
        System.out.println("userSearchDto = " + userSearchDto);
        try {
            SearchRequest searchRequest = buildSearchRequest(userSearchDto);
            // System.out.println(searchRequest);
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // System.out.println(response);
            List<com.myapp.model.search.vo.SearchArticleVo> list = parseSearchResponse(response);
            // System.out.println(list);
            System.out.println("list.get(0).getH_title() = " + list.get(0).getH_title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<com.myapp.model.search.vo.SearchArticleVo> parseSearchResponse(SearchResponse searchResponse) {
        List<com.myapp.model.search.vo.SearchArticleVo> articleVoList = new ArrayList<>();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        Arrays.stream(searchHits).forEach(hit->{
            String source = hit.getSourceAsString();
            com.myapp.model.search.vo.SearchArticleVo articleVo = JSON.parseObject(source, com.myapp.model.search.vo.SearchArticleVo.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!=null && highlightFields.size()>0){
                Text[] fragments = highlightFields.get(TITLE).getFragments();
                String title = StringUtils.collectionToCommaDelimitedString(Arrays.asList(fragments));
                // articleVo.setTitle(title);
                articleVo.setH_title(title);
            }

            articleVoList.add(articleVo);
        });
        return articleVoList;
    }

    private static SearchRequest buildSearchRequest(UserSearchDto dto) {
        SearchRequest searchRequest = new SearchRequest(index);
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // 关键字
        String searchWords = dto.getSearchWords();
        if (StringUtils.hasLength(searchWords)){
            boolQuery.must(new QueryStringQueryBuilder(searchWords)
                    .field(TITLE)
                    .field(CONTENT));
        }
        // 时间
        Date minBehotTime = dto.getMinBehotTime();
        if (minBehotTime!=null){
            boolQuery.filter(new RangeQueryBuilder(PUBLISH_TIME)
                    .lte(minBehotTime));
        }
        // 分页
        searchRequest.source().from(dto.getFromIndex());
        searchRequest.source().size(dto.getPageSize());
        // 高亮
        searchRequest.source().highlighter(new HighlightBuilder()
                .field(TITLE)
                .preTags("<font style='color: red; font-size: inherit;'>")
                .postTags("</font>"));
        searchRequest.source().query(boolQuery);
        return searchRequest;
    }
}