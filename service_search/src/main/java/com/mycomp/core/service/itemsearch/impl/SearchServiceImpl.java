package com.mycomp.core.service.itemsearch.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mycomp.core.dao.item.ItemCatDao;
import com.mycomp.core.dao.specification.SpecificationOptionDao;
import com.mycomp.core.dao.template.TypeTemplateDao;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemCat;
import com.mycomp.core.pojo.specification.SpecificationOption;
import com.mycomp.core.pojo.specification.SpecificationOptionQuery;
import com.mycomp.core.pojo.template.TypeTemplate;
import com.mycomp.core.service.itemsearch.SearchService;
import com.mycomp.utils.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specOptDao;

    @Override
    public Map<String, Object> search(Map searchMap) {
        /*
         * 0. 首次访问时, 将相应的数据存入Redis中, 以便之后的访问
         */
        if (!redisTemplate.hasKey(RedisKeys.CATEGORY_LIST_REDIS)) {
            // 缓存分类信息到Redis中, 以分类名称为key, 以typeId为value
            loadCategoryDataToRedis();
        }
        if (!redisTemplate.hasKey(RedisKeys.BRAND_LIST_REDIS)) {
            // 缓存品牌信息到Redis中, 以typeId为key, 以品牌数据为value
            loadBrandDataToRedis();
        }
        if (!redisTemplate.hasKey(RedisKeys.SPEC_LIST_REDIS)) {
            // 缓存规格信息到Redis中, 以typeId为key, 以规格数据为value
            loadSpecDataToRedis();
        }

        /*
         * 1. 高亮查询Item
         */
        Map<String, Object> resultMap = queryWithHighLight(searchMap);

        /*
         * 2. 查询分类
         */
        List<String> itemCatList = queryCategory(searchMap);
        resultMap.put("itemCatList", itemCatList);

        /*
         * 3. 根据分类名称查询相应的品牌和规格
         */
        Map<String, Object> brandAndSpecMap;
        String categoryName = String.valueOf(searchMap.get("category"));
        if (categoryName != null && !categoryName.equals("")) {
            // 情况一. searchMap查询条件中有分类的条件, 直接使用分类的条件
            brandAndSpecMap = quertBrandAndSpecWithCategoryName(categoryName);
        } else {
            // 情况二. searchMap查询条件中没有分类的条件, 从上面查出的所有分类中取出第一个进行查询
            assert itemCatList.size() > 0;
            brandAndSpecMap = quertBrandAndSpecWithCategoryName(itemCatList.get(0));
        }
        assert brandAndSpecMap != null;
        resultMap.putAll(brandAndSpecMap);

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

    /**
     * 根据分类名称查询相应的品牌和规格(从Redis中查询)
     */
    private Map<String, Object> quertBrandAndSpecWithCategoryName(String categoryName) {
        // 1. 根据分类名称, 取得typeId
        Long typeId = (Long) redisTemplate.boundHashOps(RedisKeys.CATEGORY_LIST_REDIS).get(categoryName);

        if (typeId != null) {
            // 2. 根据typeId, 取得品牌数据
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps(RedisKeys.BRAND_LIST_REDIS).get(typeId);

            // 3. 根据typeId, 取得规格数据
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps(RedisKeys.SPEC_LIST_REDIS).get(typeId);

            // 4. 封装结果并返回
            Map<String, Object> res = new HashMap<>();
            res.put("brandList", brandList);
            res.put("specList", specList);
            return res;
        }
        return null;
    }

    /**
     * 缓存分类信息到Redis中, 以分类名称为key, 以typeId为value
     */
    private void loadCategoryDataToRedis() {
        List<ItemCat> allItemCats = itemCatDao.selectByExample(null);
        allItemCats.forEach(itemCat ->
                redisTemplate.boundHashOps(RedisKeys.CATEGORY_LIST_REDIS).put(itemCat.getName(), itemCat.getTypeId()));
    }

    /**
     * 缓存品牌信息到Redis中, 以typeId为key, 以品牌数据为value
     */
    private void loadBrandDataToRedis() {
        List<TypeTemplate> allTypeTemplates = typeTemplateDao.selectByExample(null);
        allTypeTemplates.forEach(typeTemplate -> {
            String brandsStr = typeTemplate.getBrandIds();
            List<Map> brandsList = JSON.parseArray(brandsStr, Map.class);
            redisTemplate.boundHashOps(RedisKeys.BRAND_LIST_REDIS).put(typeTemplate.getId(), brandsList);
        });
    }

    /**
     * 缓存规格信息到Redis中, 以typeId为key, 以规格数据为value
     */
    private void loadSpecDataToRedis() {
        List<TypeTemplate> allTypeTemplates = typeTemplateDao.selectByExample(null);
        allTypeTemplates.forEach(typeTemplate -> {
            List<Map> specsList = getSpecByTemplateId(typeTemplate.getId());
            redisTemplate.boundHashOps(RedisKeys.SPEC_LIST_REDIS).put(typeTemplate.getId(), specsList);
        });
    }

    /**
     * 根据模板id查询出模板, 再进一步查询出规格和规格选项
     */
    private List<Map> getSpecByTemplateId(Long id) {
        // 1. 根据id查询出模板
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        // 2. 根据模板中的数据查询出规格
        if (typeTemplate != null) {
            String specs = typeTemplate.getSpecIds();
            List<Map> specList = JSON.parseArray(specs, Map.class);
            specList = specList.stream().map(spec -> {
                // 3. 根据规格的id查询出相应的规格选项
                Object specId = spec.get("id");
                Long specIdLong = Long.parseLong(String.valueOf(specId));
                SpecificationOptionQuery specOptQuery = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = specOptQuery.createCriteria();
                criteria.andSpecIdEqualTo(specIdLong);
                List<SpecificationOption> specOptList = specOptDao.selectByExample(specOptQuery);
                // 4. 将查询出的规格选项添加进Map(一个Map就是一个规格)
                spec.put("options", specOptList);
                return spec;
            }).collect(Collectors.toList());
            return specList;
        }
        return null;
    }

}
