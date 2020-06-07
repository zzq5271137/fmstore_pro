package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.goodstemplate.GoodsService;
import com.mycomp.core.service.itemsearch.SolrManagementService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private SolrManagementService solrManagementService;

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
            }
            return new RestResult(true, "状态修改成功！");
        } catch (Exception e) {
            return new RestResult(true, "状态修改成功！");
        }
    }

}
