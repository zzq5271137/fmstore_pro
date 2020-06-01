package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.item.ItemCat;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.goodstemplate.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/getItemCatPageByParentId")
    public PageResult<ItemCat> getItemCatPageByParentId(@RequestParam("page") Integer page,
                                                        @RequestParam("pageSize") Integer pageSize,
                                                        @RequestParam("parentId") Long parentId) {
        return itemCatService.getItemCatPageByParentId(page, pageSize, parentId);
    }

    @RequestMapping("/getAllItemCat")
    public List<ItemCat> getAllItemCat() {
        return itemCatService.getAllItemCat();
    }

}
