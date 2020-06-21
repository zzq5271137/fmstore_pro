package com.mycomp.core.controller.portal;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.order.Order;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.buyerservice.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add")
    public RestResult add(@RequestBody Order order) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userName);
            orderService.add(order);
            return new RestResult(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResult(false, "保存失败!");
        }
    }

}
