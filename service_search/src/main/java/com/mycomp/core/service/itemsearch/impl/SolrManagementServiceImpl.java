package com.mycomp.core.service.itemsearch.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.service.itemsearch.SolrManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SolrManagementServiceImpl implements SolrManagementService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void saveItemsToSolr(List<Item> items) {
        if (items != null) {
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteItemsFromSolr(List<Long> goodsIds) {
        if (goodsIds != null) {
            SimpleQuery query = new SimpleQuery();
            Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
            query.addCriteria(criteria);
            solrTemplate.delete(query);
            solrTemplate.commit();
        }
    }

}
