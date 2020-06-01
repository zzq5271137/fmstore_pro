package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.pojo.queryentity.SpecEntity;
import com.mycomp.core.pojo.specification.Specification;
import com.mycomp.core.service.goodstemplate.SpecService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Reference
    private SpecService specService;

    /**
     * 分页查找Specification数据
     */
    @RequestMapping("/getSpecPage")
    public PageResult<Specification> getSpecPage(@RequestParam("page") Integer page,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestBody Specification specSearch) {
        return specService.getSpecPage(page, pageSize, specSearch);
    }

    /**
     * 添加Specification
     */
    @RequestMapping("/addSpecification")
    public RestResult addSpecification(@RequestBody SpecEntity specEntity) {
        try {
            specService.addSpecification(specEntity);
            return new RestResult(true, "规格添加成功！");
        } catch (Exception e) {
            return new RestResult(false, "规格添加失败...");
        }
    }

    /**
     * 根据id查找Specification
     */
    @RequestMapping("/getSpecById")
    public SpecEntity getSpecById(@RequestParam("id") Long id) {
        return specService.getSpecById(id);
    }

    /**
     * 更新Specification
     */
    @RequestMapping("/updateSpec")
    public RestResult updateSpec(@RequestBody SpecEntity specEntity) {
        try {
            specService.updateSpec(specEntity);
            return new RestResult(true, "规格修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "规格修改失败...");
        }
    }

    /**
     * 根据id删除Specification
     */
    @RequestMapping("/deleteSpec")
    public RestResult deleteSpec(@RequestParam("targetIds") Long[] targetIds) {
        try {
            specService.deleteSpec(targetIds);
            return new RestResult(true, "规格删除成功！");
        } catch (Exception e) {
            return new RestResult(false, "规格删除失败...");
        }
    }

    /**
     * 获取Specification下拉列表数据
     */
    @RequestMapping("/getSelectionListData")
    public List<Map<String, Object>> getSelectionListData() {
        return specService.getSelectionListData();
    }

}
