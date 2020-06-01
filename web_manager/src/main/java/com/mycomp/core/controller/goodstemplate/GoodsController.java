package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.goodstemplate.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/getGoodsPage")
    public PageResult<Goods> getGoodsPage(@RequestParam("page") Integer page,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestBody Goods goodsSearch) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsSearch.setSellerId(sellerId);
        return goodsService.getGoodsPage(page, pageSize, goodsSearch);
    }

    @RequestMapping("/updateStatus")
    public RestResult updateStatus(Long[] targetIds, String status) {
        try {
            goodsService.updateStatus(targetIds, status);
            return new RestResult(true, "状态修改成功！");
        } catch (Exception e) {
            return new RestResult(true, "状态修改成功！");
        }
    }

}
