package com.mycomp.core.controller.goodsmanage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.queryentity.GoodsEntity;
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

    @RequestMapping("/addGoods")
    public RestResult addGoods(@RequestBody GoodsEntity goodsEntity) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(sellerId);
            goodsService.addGoods(goodsEntity);
            return new RestResult(true, "商品添加成功！");
        } catch (Exception e) {
            return new RestResult(false, "商品添加失败...");
        }
    }

    @RequestMapping("/getGoodsPage")
    public PageResult<Goods> getGoodsPage(@RequestParam("page") Integer page,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestBody Goods goodsSearch) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsSearch.setSellerId(sellerId);
        return goodsService.getGoodsPage(page, pageSize, goodsSearch);
    }

    @RequestMapping("/getGoodsEntity")
    public GoodsEntity getGoodsEntity(@RequestParam("id") Long id) {
        return goodsService.getGoodsEntity(id);
    }

    @RequestMapping("/updateGoods")
    public RestResult updateGoods(@RequestBody GoodsEntity goodsEntity) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!goodsEntity.getGoods().getSellerId().equals(sellerId)) {
                return new RestResult(false, "没有权限更新该商品, 商品更新失败...");
            }
            goodsService.updateGoods(goodsEntity);
            return new RestResult(true, "商品更新成功！");
        } catch (Exception e) {
            return new RestResult(false, "商品更新失败...");
        }
    }

    @RequestMapping("/deleteGoods")
    public RestResult deleteGoods(@RequestParam("targetIds") Long[] targetIds) {
        try {
            goodsService.deleteGoods(targetIds);
            return new RestResult(true, "商品删除成功！");
        } catch (Exception e) {
            return new RestResult(false, "商品删除失败...");
        }
    }

}
