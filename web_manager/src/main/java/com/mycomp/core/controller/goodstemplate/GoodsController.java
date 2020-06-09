package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.cmsservice.CmsService;
import com.mycomp.core.service.exceptions.DeleteGoodsFromDBException;
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

    @Reference
    private CmsService cmsService;

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
            return new RestResult(true, "商品状态修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "商品状态修改失败...");
        }
    }

    @RequestMapping("/deleteGoodsFromDB")
    public RestResult deleteGoodsFromDB(Long[] targetIds) {
        try {
            goodsService.deleteGoodsFromDB(targetIds);
            return new RestResult(true, "商品删除成功！");
        } catch (DeleteGoodsFromDBException e) {
            return new RestResult(false, "选择的待删除商品中存在还未被商家删除的商品, 您不能删除...");
        } catch (Exception e) {
            return new RestResult(false, "商品删除失败...");
        }
    }

    /**
     * 测试生成静态页面的接口
     */
    @RequestMapping("/testPage")
    public Boolean testPage(Long goodsId) {
        try {
            cmsService.createStaticPage(goodsId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
