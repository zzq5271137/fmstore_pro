package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Brand;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.goodstemplate.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 分页查找Brand数据
     */
    @RequestMapping("/getBrandPage")
    public PageResult<Brand> getBrandPage(@RequestParam("page") Integer page,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestBody Brand brandSearch) {
        return brandService.getBrandPage(page, pageSize, brandSearch);
    }

    /**
     * 添加Brand
     */
    @RequestMapping("/addBrand")
    public RestResult addBrand(@RequestBody Brand brand) {
        try {
            brandService.addBrand(brand);
            return new RestResult(true, "品牌添加成功！");
        } catch (Exception e) {
            return new RestResult(false, "品牌添加失败...");
        }
    }

    /**
     * 根据id查找Brand
     */
    @RequestMapping("/getBrandById")
    public Brand getBrandById(@RequestParam("id") Long id) {
        return brandService.getBrandById(id);
    }

    /**
     * 更新Brand
     */
    @RequestMapping("/updateBrand")
    public RestResult updateBrand(@RequestBody Brand brand) {
        try {
            brandService.updateBrand(brand);
            return new RestResult(true, "品牌修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "品牌修改失败...");
        }
    }

    /**
     * 根据id删除Brand
     */
    @RequestMapping("/deleteBrand")
    public RestResult deleteBrand(@RequestParam("targetIds") Long[] targetIds) {
        try {
            brandService.deleteBrand(targetIds);
            return new RestResult(true, "品牌删除成功！");
        } catch (Exception e) {
            return new RestResult(false, "品牌删除失败...");
        }
    }

    /**
     * 获取Brand下拉列表数据
     */
    @RequestMapping("/getSelectionListData")
    public List<Map<String, Object>> getSelectionListData() {
        return brandService.getSelectionListData();
    }

}
