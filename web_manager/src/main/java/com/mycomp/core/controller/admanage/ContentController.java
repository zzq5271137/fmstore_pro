package com.mycomp.core.controller.admanage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.ad.Content;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.admanage.ContentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/getContentPage")
    public PageResult<Content> getContentPage(@RequestParam("page") Integer page,
                                              @RequestParam("pageSize") Integer pageSize,
                                              @RequestBody Content contentSearch) {
        return contentService.getContentPage(page, pageSize, contentSearch);
    }

    @RequestMapping("/addContent")
    public RestResult addContent(@RequestBody Content content) {
        try {
            contentService.addContent(content);
            return new RestResult(true, "广告添加成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告添加失败...");
        }
    }

    @RequestMapping("/getContentById")
    public Content getContentById(@RequestParam("id") Long id) {
        return contentService.getContentById(id);
    }

    @RequestMapping("/updateContent")
    public RestResult updateContent(@RequestBody Content content) {
        try {
            contentService.updateContent(content);
            return new RestResult(true, "广告修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告修改失败...");
        }
    }

    @RequestMapping("/updateContentStatus")
    public RestResult updateContentStatus(@RequestParam("targetIds") Long[] targetIds,
                                          @RequestParam("status") String status) {
        try {
            contentService.updateContentStatus(targetIds, status);
            return new RestResult(true, "广告状态更新成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告状态更新失败...");
        }
    }

    @RequestMapping("/deleteContent")
    public RestResult deleteContent(@RequestParam("targetIds") Long[] targetIds) {
        try {
            contentService.deleteContent(targetIds);
            return new RestResult(true, "广告删除成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告删除失败...");
        }
    }

}
