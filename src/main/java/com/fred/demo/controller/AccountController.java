package com.fred.demo.controller;

import com.fred.demo.dao.AccountDao;
import com.fred.demo.entity.Account;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author liuxiaokun
 * @version 0.0.1
 * @since 2019/2/11
 */
@RestController
@RequestMapping("/es")
public class AccountController {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private AccountDao accountDao;

    @GetMapping("/add/{id}")
    public String add(@PathVariable("id") String id) {

        Account account = new Account();
        account.setId(id);
        account.setUser("Y.S.K");
        account.setTitle("CTO");
        account.setDesc("首席计数管");
        accountDao.save(account);

        System.err.println("add a obj");

        return "success";
    }

    @GetMapping("/delete")
    public String delete() {
        Account account = new Account();
        account.setId("1");
        accountDao.delete(account);

        return "success";
    }

    /*局部更新*/
    @GetMapping("/update")
    public String update() {

        Account account = accountDao.findById("1").orElseThrow(() -> new RuntimeException("不合法的参数"));
        account.setUser("Y.S.K 局部更新");
        accountDao.save(account);

        System.err.println("update a obj");

        return "success";
    }

    @GetMapping("/query/{id}")
    public Account query(@PathVariable("id") String id) {

        Optional<Account> optional = accountDao.findById(id);

        return optional.orElse(null);
    }

    /**
     * 全文检索
     *
     * @param q 检索内容
     * @return 检索结果
     */
    @GetMapping("/select/{q}")
    public List<Account> testSearch(@PathVariable String q) {
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(q);
        Iterable<Account> searchResult = accountDao.search(builder);
        Iterator<Account> iterator = searchResult.iterator();
        List<Account> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * 3、查   +++：分页、分数、分域（结果一个也不少）
     * @param page page
     * @param size size
     * @param q q
     * @return
     */
    /*@GetMapping("/{page}/{size}/{q}")
    public List<Account> searchCity(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String q) {

        // 分页参数
        Pageable pageable = PageRequest.of(page, size);

        // 分数，并自动按分排序
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(1)
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q)),
                        ScoreFunctionBuilders.weightFactorFunction(1000)) // 权重：name 1000分
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("message", q)),
                        ScoreFunctionBuilders.weightFactorFunction(100)); // 权重：message 100分

        // 分数、分页
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        Page<Account> searchPageResults = accountDao.search(searchQuery);
        return searchPageResults.getContent();
    }*/

    /**
     * 查询所有
     *
     * @throws Exception
     */
    @GetMapping("/all")
    public List<Map<String, Object>> searchAll() throws Exception {
        //这一步是最关键的
        Client client = elasticsearchTemplate.getClient();
        // @Document(indexName = "product", type = "book")
        SearchRequestBuilder srb = client.prepareSearch("accounts").setTypes("person");
        // 查询所有
        SearchResponse sr = srb.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
        SearchHits hits = sr.getHits();
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSourceAsMap();
            list.add(source);
            System.out.println(hit.getSourceAsString());
        }
        return list;
    }
}