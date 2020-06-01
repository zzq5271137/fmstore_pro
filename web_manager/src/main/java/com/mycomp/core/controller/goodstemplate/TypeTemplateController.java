package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.pojo.template.TypeTemplate;
import com.mycomp.core.service.goodstemplate.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/template")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 分页查找TypeTemplate数据
     */
    @RequestMapping("/getTemplatePage")
    public PageResult<TypeTemplate> getTemplatePage(@RequestParam("page") Integer page,
                                                    @RequestParam("pageSize") Integer pageSize,
                                                    @RequestBody TypeTemplate templateSearch) {
        return typeTemplateService.getTemplatePage(page, pageSize, templateSearch);
    }

    /**
     * 添加TypeTemplate
     */
    @RequestMapping("/addTemplate")
    public RestResult addTemplate(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.addTemplate(typeTemplate);
            return new RestResult(true, "模板添加成功！");
        } catch (Exception e) {
            return new RestResult(false, "模板添加失败...");
        }
    }

    /**
     * 根据id查找TypeTemplate
     */
    @RequestMapping("/getTemplateById")
    public TypeTemplate getTemplateById(@RequestParam("id") Long id) {
        return typeTemplateService.getTemplateById(id);
    }

    /**
     * 更新TypeTemplate
     */
    @RequestMapping("/updateTemplate")
    public RestResult updateTemplate(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.updateTemplate(typeTemplate);
            return new RestResult(true, "模板修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "模板修改失败...");
        }
    }

    /**
     * 根据id删除TypeTemplate
     */
    @RequestMapping("/deleteTemplate")
    public RestResult deleteTemplate(@RequestParam("targetIds") Long[] targetIds) {
        try {
            typeTemplateService.deleteTemplate(targetIds);
            return new RestResult(true, "模板删除成功！");
        } catch (Exception e) {
            return new RestResult(false, "模板删除失败...");
        }
    }

}
