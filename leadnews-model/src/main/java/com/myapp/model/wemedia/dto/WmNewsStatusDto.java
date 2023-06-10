package com.myapp.model.wemedia.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WmNewsStatusDto implements Serializable {
    /**
     * 自媒体文章id
     */
    private Integer id;
    /**
     * 上下架：上架 1，下架 0
     */
    private Short enable;

}
