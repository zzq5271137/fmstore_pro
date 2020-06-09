package com.mycomp.core.listener;

import com.mycomp.core.service.itemsearch.SolrManagementService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Arrays;

public class DeleteSolrListener implements MessageListener {

    @Autowired
    private SolrManagementService solrManagementService;

    @Override
    public void onMessage(Message message) {
        ActiveMQObjectMessage aom = (ActiveMQObjectMessage) message;
        try {
            Long[] targetIds = (Long[]) aom.getObject();
            solrManagementService.deleteItemsFromSolr(Arrays.asList(targetIds));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
