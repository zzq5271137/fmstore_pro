package com.mycomp.core.service.buyerservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mycomp.core.dao.log.PayLogDao;
import com.mycomp.core.dao.order.OrderDao;
import com.mycomp.core.dao.order.OrderItemDao;
import com.mycomp.core.pojo.log.PayLog;
import com.mycomp.core.pojo.order.Order;
import com.mycomp.core.pojo.order.OrderItem;
import com.mycomp.core.pojo.queryentity.BuyerCart;
import com.mycomp.core.service.buyerservice.OrderService;
import com.mycomp.utils.IdWorker;
import com.mycomp.utils.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "idWorker")
    private IdWorker idWorker;

    @Override
    public void add(Order order) {
        // 1. 从订单对象中获取当前登录用户用户名
        String userId = order.getUserId();
        // 2. 根据用户名获取购物车集合
        List<BuyerCart> cartList = (List<BuyerCart>) redisTemplate.boundHashOps(RedisKeys.CART_LIST_REDIS).get(userId);
        List<String> orderIdList = new ArrayList<>();  // 订单ID列表
        double total_money = 0;  // 总金额(元)
        // 3. 遍历购物车集合
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                // 4. 根据购物车对象保存订单数据
                long orderId = idWorker.nextId();
                System.out.println("sellerId:" + cart.getSellerId());
                Order tborder = new Order();  // 新创建订单对象
                tborder.setOrderId(orderId);  // 订单ID
                tborder.setUserId(order.getUserId());  // 用户名
                tborder.setPaymentType(order.getPaymentType());  // 支付类型
                tborder.setStatus("1");  //状态: 未付款
                tborder.setCreateTime(new Date());  // 订单创建日期
                tborder.setUpdateTime(new Date());  // 订单更新日期
                tborder.setReceiverAreaName(order.getReceiverAreaName());  // 地址
                tborder.setReceiverMobile(order.getReceiverMobile());  // 手机号
                tborder.setReceiver(order.getReceiver());  // 收货人
                tborder.setSourceType(order.getSourceType());  // 订单来源
                tborder.setSellerId(cart.getSellerId());  // 商家ID
                // 循环购物车明细
                double money = 0;
                // 5. 从购物车中获取购物项集合
                List<OrderItem> orderItemList = cart.getOrderItemList();
                // 6. 遍历购物项集合
                if (orderItemList != null) {
                    for (OrderItem orderItem : orderItemList) {
                        // 7. 根据购物项对象保存订单详情数据
                        orderItem.setId(idWorker.nextId());
                        orderItem.setOrderId(orderId);//订单ID
                        orderItem.setSellerId(cart.getSellerId());
                        money += orderItem.getTotalFee().doubleValue();  // 金额累加
                        orderItemDao.insertSelective(orderItem);
                    }
                }
                tborder.setPayment(new BigDecimal(money));
                orderDao.insertSelective(tborder);
                orderIdList.add(orderId + "");  // 添加到订单列表
                total_money += money;  // 累加到总金额
            }
        }
        // 8. 计算总价钱保存支付日志数据
        if ("1".equals(order.getPaymentType())) {  // 如果是微信支付
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(idWorker.nextId() + "");  // 支付订单号
            payLog.setCreateTime(new Date());  // 创建时间
            // 订单号列表, 逗号分隔 aa,bb
            String ids = orderIdList.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", "");
            payLog.setOrderList(ids);  // 订单号列表, 逗号分隔
            payLog.setPayType("1");  // 支付类型  100
            payLog.setTotalFee((long) (total_money * 100));  // 总金额(分*100 转成元)
            payLog.setTradeState("0");  // 支付状态
            payLog.setUserId(order.getUserId());  // 用户ID
            payLogDao.insertSelective(payLog);  // 插入到支付日志表
            // 9. 使用当前登录用户的用户名作为key, 支付日志对象作为value存入redis中供支付使用
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);  // 放入缓存
        }
        // 10. 根据当前登录用户的用户名删除购物车
        redisTemplate.boundHashOps(RedisKeys.CART_LIST_REDIS).delete(order.getUserId());
    }

}
