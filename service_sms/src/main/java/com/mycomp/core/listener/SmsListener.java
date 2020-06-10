package com.mycomp.core.listener;

import com.mycomp.utils.SmsUtil;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class SmsListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        MapMessage map = (MapMessage) message;
        try {
            SmsUtil.sendSms(map.getString("phoneNumbers"),
                    map.getString("templateCode"),
                    map.getString("signName"),
                    map.getString("templateParam"));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
