package com.mycomp.core.controller.selleraudit;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.pojo.seller.Seller;
import com.mycomp.core.service.sellerservice.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 分页查找Seller数据
     */
    @RequestMapping("/getSellerPage")
    public PageResult<Seller> getSellerPage(@RequestParam("page") Integer page,
                                            @RequestParam("pageSize") Integer pageSize,
                                            @RequestBody Seller sellerSearch) {
        return sellerService.getSellerPage(page, pageSize, sellerSearch);
    }

    /**
     * 根据id查找Seller
     */
    @RequestMapping("/getSellerById")
    public Seller getSellerById(@RequestParam("sellerId") String sellerId) {
        return sellerService.getSellerById(sellerId);
    }

    /**
     * 更新Seller
     */
    @RequestMapping("/updateSeller")
    public RestResult updateSeller(@RequestBody Seller seller) {
        try {
            sellerService.updateSeller(seller);
            return new RestResult(true, "商铺修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "商铺修改失败...");
        }
    }

}
