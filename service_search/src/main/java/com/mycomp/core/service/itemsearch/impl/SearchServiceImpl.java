package com.mycomp.core.service.itemsearch.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.service.itemsearch.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    @Override
    public Map<String, Object> search(Map searchMap) {
        // 1. 高亮查询Item
        Map<String, Object> resultMap = queryWithHighLight(searchMap);

        // 2. 查询分类
        List<String> itemCatList = queryCategory(searchMap);
        resultMap.put("itemCatList", itemCatList);

        return resultMap;
    }

    /**
     * 高亮查询Item
     */
    private Map<String, Object> queryWithHighLight(Map searchMap) {
        /*
         * 1. 解析搜索条件
         */
        String keywords = String.valueOf(searchMap.get("keywords"));
        Integer page = Integer.parseInt(String.valueOf(searchMap.get("page")));
        Integer pageSize = Integer.parseInt(String.valueOf(searchMap.get("pageSize")));

        /*
         * 2. 设置查询
         */
        // 创建高亮查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        // 创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        // 设置哪个域需要高亮显示(这个要根据Solr服务器配置的"item_keywords"复制域进行设置)
        highlightOptions.addField("item_title");
        highlightOptions.addField("item_category");
        highlightOptions.addField("item_seller");
        highlightOptions.addField("item_brand");
        // 设置高亮前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        // 设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        // 将高亮选项加入到查询对象中
        query.setHighlightOptions(highlightOptions);
        // 创建查询条件对象("item_keywords"为Solr服务器配置的复制域)
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        // 设置分页
        if (page == null || page <= 0) {
            page = 1;
        }
        Integer start = (page - 1) * pageSize;
        query.setOffset(start);
        query.setRows(pageSize);

        /*
         * 3. 进行查询
         */
        // 定义结果集
        List<Item> itemListRes = new ArrayList<>();
        // 向Solr服务器发送请求进行查询
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        // 处理查询结果, 替换高亮字段
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        highlighted.forEach(itemHighlightEntry -> {
            Item item = itemHighlightEntry.getEntity();  // 原始数据(没有添加高亮的数据)
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0) {
                highlights.forEach(highlight -> {
                    String highLightContent = highlight.getSnipplets().get(0);
                    // 根据字段名称替换高亮内容(这个要根据上面设置的哪个域需要高亮显示进行判断)
                    switch (highlight.getField().getName()) {
                        case "item_title":
                            item.setTitle(highLightContent);
                            break;
                        case "item_category":
                            item.setCategory(highLightContent);
                            break;
                        case "item_seller":
                            item.setSeller(highLightContent);
                            break;
                        case "item_brand":
                            item.setBrand(highLightContent);
                            break;
                    }
                });
            }
            // 将替换后的数据装入结果集
            itemListRes.add(item);
        });

        /*
         * 4. 封装结果集并返回
         */
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("itemList", itemListRes);
        resultMap.put("total", items.getTotalElements());
        resultMap.put("totalPages", items.getTotalPages());
        return resultMap;
    }

    /**
     * 查询分类
     */
    private List<String> queryCategory(Map searchMap) {
        /*
         * 1. 解析搜索条件
         */
        String keywords = String.valueOf(searchMap.get("keywords"));

        /*
         * 2. 设置查询
         */
        // 创建查询对象
        SimpleQuery query = new SimpleQuery();
        // 创建查询条件对象("item_keywords"为Solr服务器配置的复制域)
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        // 创建分组选项对象(因为里面可能有重复的分类, 类似于MySql的group by操作)
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        // 将分组选项加入到查询对象中
        query.setGroupOptions(groupOptions);

        /*
         * 3. 进行查询
         */
        // 定义结果集
        List<String> itemCatListRes = new ArrayList<>();
        // 向Solr服务器发送请求进行查询
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        // 处理查询结果, 封装分组数据
        GroupResult<Item> groupResult = items.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        groupEntries.forEach(itemGroupEntry -> itemCatListRes.add(itemGroupEntry.getGroupValue()));

        /*
         * 4. 返回结果集
         */
        return itemCatListRes;
    }

}
