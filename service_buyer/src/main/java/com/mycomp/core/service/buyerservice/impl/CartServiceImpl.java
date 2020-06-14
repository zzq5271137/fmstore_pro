package com.mycomp.core.service.buyerservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.order.OrderItem;
import com.mycomp.core.pojo.queryentity.BuyerCart;
import com.mycomp.core.service.buyerservice.CartService;
import com.mycomp.utils.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ItemDao itemDao;

    @Override
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        // 1. 根据itemId查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        // 2. 判断商品是否存在
        if (item == null) {
            throw new RuntimeException("此商品不存在!");
        }
        // 3. 判断商品状态是否为"2"(已审核)
        if (!"2".equals(item.getStatus())) {
            throw new RuntimeException("此商品审核未通过, 不允许购买!");
        }
        // 4. 获取商家ID
        String sellerId = item.getSellerId();
        // 5. 根据商家ID查询购物车列表中是否存在该商家的购物车
        BuyerCart buyerCart = findBuyerCartBySellerId(cartList, sellerId);
        // 6. 判断该购物车是否存在
        if (buyerCart == null) {  // 不存在
            // 新建购物车对象
            buyerCart = new BuyerCart();
            // 设置新创建的购物车对象的卖家id
            buyerCart.setSellerId(sellerId);
            //设置新创建的购物车对象的卖家名称
            buyerCart.setSellerName(item.getSeller());
            // 创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<>();
            // 创建购物项
            OrderItem orderItem = createOrderItem(item, num);
            // 将购物项加入到购物项集合中
            orderItemList.add(orderItem);
            // 将购物项集合加入到购物车中
            buyerCart.setOrderItemList(orderItemList);
            // 将新建的购物车对象添加到购物车列表
            cartList.add(buyerCart);
        } else {  // 存在
            // 查询购物车明细列表中是否存在该商品
            List<OrderItem> orderItemList = buyerCart.getOrderItemList();
            OrderItem orderItem = findOrderItemByItemId(orderItemList, itemId);
            // 判断购物车明细是否为空
            if (orderItem == null) {
                // 为空, 新增购物车明细
                orderItem = createOrderItem(item, num);
                // 将新增的购物项加入到购物项集合中
                orderItemList.add(orderItem);
            } else {
                // 不为空, 在原购物车明细上添加数量, 更改金额
                orderItem.setNum(orderItem.getNum() + num);  // 设置购买数量 = 原有购买数量 + 现在购买数量
                // 设置总价格
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                // 如果购物车明细中数量操作后小于等于0, 则移除
                if (orderItem.getNum() <= 0) {
                    orderItemList.remove(orderItem);
                }
                // 如果购物车中购物车明细列表为空, 则移除
                if (orderItemList.size() <= 0) {
                    cartList.remove(buyerCart);
                }
            }
        }
        // 7. 返回购物车列表对象
        return cartList;
    }

    private BuyerCart findBuyerCartBySellerId(List<BuyerCart> cartList, String sellerId) {
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    private OrderItem findOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("购买数量非法!");
        }
        OrderItem orderItem = new OrderItem();
        // 购买数量
        orderItem.setNum(num);
        // 商品id
        orderItem.setGoodsId(item.getGoodsId());
        // 库存id
        orderItem.setItemId(item.getId());
        // 示例图片
        orderItem.setPicPath(item.getImage());
        // 单价
        orderItem.setPrice(item.getPrice());
        // 卖家id
        orderItem.setSellerId(item.getSellerId());
        // 商品库存标题
        orderItem.setTitle(item.getTitle());
        // 总价 = 单价 * 购买数量
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }

    @Override
    public void setCartListToRedis(String username, List<BuyerCart> cartList) {
        redisTemplate.boundHashOps(RedisKeys.CART_LIST_REDIS).put(username, cartList);
    }

    @Override
    public List<BuyerCart> getCartListFromRedis(String username) {
        List<BuyerCart> cartList = (List<BuyerCart>) redisTemplate.boundHashOps(RedisKeys.CART_LIST_REDIS).get(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList,
                                                              List<BuyerCart> redisCartList) {
        if (cookieCartList != null) {
            // 遍历Cookie购物车集合
            for (BuyerCart cookieCart : cookieCartList) {
                // 遍历Cookie购物车中的购物项集合, 把每个商品取出来, 加入到Redis购物车当中
                for (OrderItem cookieOrderItem : cookieCart.getOrderItemList()) {
                    // 将Cookie中的购物项, 加入到Redis的购物车集合中
                    redisCartList = addItemToCartList(redisCartList, cookieOrderItem.getItemId(),
                            cookieOrderItem.getNum());
                }
            }
        }
        return redisCartList;
    }

}
