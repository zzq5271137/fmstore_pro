package com.mycomp.core.controller.goodsmanage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.template.TypeTemplate;
import com.mycomp.core.service.goodstemplate.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/template")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/getTemplateById")
    public TypeTemplate getTemplateById(@RequestParam("id") Long id) {
        return typeTemplateService.getTemplateById(id);
    }

    @RequestMapping("/getSpecByTemplateId")
    public List<Map> getSpecByTemplateId(@RequestParam("id") Long id) {
        return typeTemplateService.getSpecByTemplateId(id);
    }

}
