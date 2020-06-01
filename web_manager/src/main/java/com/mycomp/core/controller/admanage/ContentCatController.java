package com.mycomp.core.controller.admanage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.ad.ContentCategory;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.admanage.ContentCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCat")
public class ContentCatController {

    @Reference
    private ContentCatService contentCatService;

    @RequestMapping("/getContentCatPage")
    public PageResult<ContentCategory> getContentCatPage(@RequestParam("page") Integer page,
                                                         @RequestParam("pageSize") Integer pageSize,
                                                         @RequestBody ContentCategory contentCatSearch) {
        return contentCatService.getContentCatPage(page, pageSize, contentCatSearch);
    }

    @RequestMapping("/addContentCat")
    public RestResult addContentCat(@RequestBody ContentCategory ContentCat) {
        try {
            contentCatService.addContentCat(ContentCat);
            return new RestResult(true, "广告分类添加成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告分类添加失败...");
        }
    }

    @RequestMapping("/getContentCatById")
    public ContentCategory getContentCatById(@RequestParam("id") Long id) {
        return contentCatService.getContentCatById(id);
    }

    @RequestMapping("/updateContentCat")
    public RestResult updateContentCat(@RequestBody ContentCategory ContentCat) {
        try {
            contentCatService.updateContentCat(ContentCat);
            return new RestResult(true, "广告分类修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告分类修改失败...");
        }
    }

    @RequestMapping("/deleteContentCat")
    public RestResult deleteContentCat(@RequestParam("targetIds") Long[] targetIds) {
        try {
            contentCatService.deleteContentCat(targetIds);
            return new RestResult(true, "广告分类删除成功！");
        } catch (Exception e) {
            return new RestResult(false, "广告分类删除失败...");
        }
    }

    @RequestMapping("/getAllContentCat")
    public List<ContentCategory> getAllContentCat() {
        return contentCatService.getAllContentCat();
    }

}
