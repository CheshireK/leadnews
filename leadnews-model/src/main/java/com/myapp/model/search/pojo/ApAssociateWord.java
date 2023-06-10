package com.myapp.model.search.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联想词表
 * </p>
 *
 * @author luosa
 */
@Data
@Document("ap_associate_word")
public class ApAssociateWord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 联想词
     */
    private String associateWords;

    /**
     * 创建时间
     */
    private Date createdTime;

}