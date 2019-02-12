package com.fred.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author liuxiaokun
 * @version 0.0.1
 * @since 2019/2/11
 */
@Document(indexName = "accounts", type = "person")
@Data
public class Account {

    @Id
    private String id;

    @Field
    private String user;

    @Field
    private String title;

    @Field
    private String desc;

}
