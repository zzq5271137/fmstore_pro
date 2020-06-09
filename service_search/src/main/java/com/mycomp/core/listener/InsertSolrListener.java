package com.mycomp.core.listener;

import com.alibaba.fastjson.JSON;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemQuery;
import com.mycomp.core.service.itemsearch.SolrManagementService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InsertSolrListener implements MessageListener {

    @Autowired
    private SolrManagementService solrManagementService;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void onMessage(Message message) {
        ActiveMQObjectMessage aom = (ActiveMQObjectMessage) message;
        try {
            Long[] targetIds = (Long[]) aom.getObject();
            List<Item> items = getItemsByGoodsIds(targetIds);
            if (items != null) {
                items.forEach(item -> {
                    String specJsonStr = item.getSpec();
                    Map specMap = JSON.parseObject(specJsonStr, Map.class);
                    item.setSpecMap(specMap);
                });
                solrManagementService.saveItemsToSolr(items);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private List<Item> getItemsByGoodsIds(Long[] goodsIds) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        return itemDao.selectByExample(query);
    }

}
