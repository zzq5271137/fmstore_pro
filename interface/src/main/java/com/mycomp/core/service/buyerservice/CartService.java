package com.mycomp.core.service.buyerservice;

import com.mycomp.core.pojo.queryentity.BuyerCart;

import java.util.List;

public interface CartService {

    List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num);

    void setCartListToRedis(String username, List<BuyerCart> cartList);

    List<BuyerCart> getCartListFromRedis(String username);

    List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);

}
