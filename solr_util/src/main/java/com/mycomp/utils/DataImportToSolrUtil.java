package com.mycomp.utils;

import com.alibaba.fastjson.JSON;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImportToSolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    public void importItemDataToSolr() {
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andStatusEqualTo("2");  // 2: 审核通过的商品
        List<Item> items = itemDao.selectByExample(itemQuery);
        if (items != null) {
            items.forEach(item -> {
                String specJsonStr = item.getSpec();
                Map specMap = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(specMap);
            });
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }

    public static void main(String[] args) {
        ApplicationContext contexnt = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolrUtil bean = (DataImportToSolrUtil) contexnt.getBean("dataImportToSolrUtil");
        bean.importItemDataToSolr();
    }

}
