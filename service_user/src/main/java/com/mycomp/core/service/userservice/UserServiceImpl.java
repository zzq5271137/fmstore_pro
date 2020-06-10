package com.mycomp.core.service.userservice;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mycomp.core.dao.user.UserDao;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    // 点对点模式, 自定义Queue, 用于存放SMS(Short Message Service, 短信息服务)的消息
    @Resource(name = "smsDestination")
    private ActiveMQQueue smsDestination;

    @Override
    public void sendCode(String phone) {
        // 1. 生成一个随机6位数字作为验证码
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            code.append(new Random().nextInt(10));
        }

        // 2. 手机号作为key, 验证码作为value, 存到Redis中, 生存时间为10分钟
        final String smsCode = code.toString();
        redisTemplate.boundValueOps(phone).set(smsCode, 60 * 10, TimeUnit.SECONDS);

        // 3. 封装数据, 发送给消息服务器
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setString("phoneNumbers", phone);
                message.setString("templateCode", "SMS_192715386");
                message.setString("signName", "小商城");
                /*
                 * 因为阿里云API发送短信的接口中, "TemplateParam"参数要求传一个JSON字符串,
                 * 所以要在这里需要对短信验证码做一下封装, 创建一个Map, 插入数据, 然后再将Map转成JSON字符串;
                 * 详见SmsUtil.java
                 */
                Map<String, Object> templateParam = new HashMap<>();
                /*
                 * 这里的参数名"code", 需要与阿里云中你申请的短信模板中的变量名一致
                 */
                templateParam.put("code", smsCode);
                message.setString("templateParam", JSON.toJSONString(templateParam));
                return message;
            }
        });
    }

}
