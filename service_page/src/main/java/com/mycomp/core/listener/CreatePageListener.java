package com.mycomp.core.listener;

import com.mycomp.core.service.cmsservice.CmsService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Arrays;

public class CreatePageListener implements MessageListener {

    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQObjectMessage aom = (ActiveMQObjectMessage) message;
        try {
            Long[] targetIds = (Long[]) aom.getObject();
            Arrays.asList(targetIds).forEach(id -> {
                try {
                    cmsService.createStaticPage(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
