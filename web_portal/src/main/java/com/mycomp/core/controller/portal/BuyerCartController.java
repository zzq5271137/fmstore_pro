package com.mycomp.core.controller.portal;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mycomp.core.pojo.queryentity.BuyerCart;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.buyerservice.CartService;
import com.mycomp.utils.CookieKeys;
import com.mycomp.utils.CookieUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class BuyerCartController {

    @Reference
    private CartService cartService;

    /*
     * @CrossOrigin注解相当于设置了响应头信息, 是w3c支持的一种跨域解决方案,
     * origins属性设置的是允许访问本站资源的其他域的地址列表, 在这里就是静态页面所在服务器地址,
     * 也即是service_page项目地址(或者可以设置为"*", 表示允许所有人访问本站资源),
     * 会在响应头中设置"Access-Control-Allow-Origin: 你设置的地址"和"Access-Control-Allow-Credentials: true",
     * 表示该返回的资源数据不会被浏览器拦截;
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8086", allowCredentials = "true")
    public RestResult addGoodsToCartList(@RequestParam("itemId") Long itemId,
                                         @RequestParam("num") Integer num,
                                         HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // 2. 获取购物车列表
            List<BuyerCart> cartList = getCartList(request, response);
            // 3. 将当前商品加入到购物车列表
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            // 4. 判断当前用户是否登录, 未登录时的默认用户名为"anonymousUser"
            if ("anonymousUser".equals(username)) {
                // 4a. 如果未登录, 则将购物车列表存入cookie中
                CookieUtil.setCookie(request, response, CookieKeys.CART_LIST_COOKIE, JSON.toJSONString(cartList),
                        60 * 60 * 24 * 30, "utf-8");
            } else {
                // 4b. 如果已登录, 则将购物车列表存入Redis中
                cartService.setCartListToRedis(username, cartList);
            }
            return new RestResult(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResult(false, "添加失败!");
        }
    }

    @RequestMapping("/getCartList")
    public List<BuyerCart> getCartList(HttpServletRequest request, HttpServletResponse response) {
        // 1. 获取当前登录用户名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. 从Cookie中获取购物车列表json格式字符串
        String cookieCartListStr = CookieUtil.getCookieValue(request, CookieKeys.CART_LIST_COOKIE, "utf-8");
        // 3. 如果购物车列表json串为空则返回"[]"
        if (cookieCartListStr == null || "null".equals(cookieCartListStr) || "".equals(cookieCartListStr)) {
            cookieCartListStr = "[]";
        }
        // 4. 将购物车列表json转换为对象
        List<BuyerCart> cookieCartList = JSON.parseArray(cookieCartListStr, BuyerCart.class);
        // 5. 判断用户是否登录, 未登录时的默认用户名为"anonymousUser"
        if ("anonymousUser".equals(userName)) {
            // 5a. 未登录, 返回Cookie中的购物车列表对象
            return cookieCartList;
        } else {
            // 5b. 已登录, 从Redis中获取购物车列表对象
            List<BuyerCart> redisCartList = cartService.getCartListFromRedis(userName);
            // 判断Cookie中是否存在购物车列表
            if (cookieCartList.size() > 0) {
                // 如果Cookie中存在购物车列表, 则将其和Redis中的购物车列表合并成一个对象
                redisCartList = cartService.mergeCookieCartListToRedisCartList(cookieCartList, redisCartList);
                // 删除cookie中购物车列表
                CookieUtil.deleteCookie(request, response, CookieKeys.CART_LIST_COOKIE);
                // 将合并后的购物车列表存入Redis中
                cartService.setCartListToRedis(userName, redisCartList);
            }
            // 返回购物车列表对象
            return redisCartList;
        }
    }

}
