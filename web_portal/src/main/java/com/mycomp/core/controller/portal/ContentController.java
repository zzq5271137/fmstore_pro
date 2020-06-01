package com.mycomp.core.controller.portal;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.ad.Content;
import com.mycomp.core.service.admanage.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/getContentByCategoryId")
    public List<Content> getContentByCategoryId(@RequestParam("categoryId") Long categoryId) {
        return contentService.getContentByCategoryId(categoryId);
    }

}
