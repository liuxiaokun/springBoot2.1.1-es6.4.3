package com.fred.demo.dao;

import com.fred.demo.entity.Account;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @author liuxiaokun
 * @version 0.0.1
 * @since 2019/2/11
 */
@Component
public interface AccountDao extends ElasticsearchRepository<Account,String> {
}
