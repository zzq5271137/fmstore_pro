package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.cmsservice.CmsService;
import com.mycomp.core.service.exceptions.DeleteGoodsFromDBException;
import com.mycomp.core.service.goodstemplate.GoodsService;
import com.mycomp.core.service.itemsearch.SolrManagementService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private SolrManagementService solrManagementService;

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
            if ("2".equals(status)) {  // 2: 审核通过
                // 审核通过的商品, 需要保存到Solr搜索服务器中
                List<Item> items = goodsService.getItemsByGoodsIds(targetIds, "2");
                solrManagementService.saveItemsToSolr(items);

                // 审核通过的商品, 需要生成相应的静态页面
                Arrays.asList(targetIds).forEach(id -> {
                    try {
                        cmsService.createStaticPage(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            return new RestResult(true, "商品状态修改成功！");
        } catch (Exception e) {
            goodsService.updateStatus(targetIds, "0");
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
