package com.mycomp.core.controller.goodsmanage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.item.ItemCat;
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

    @RequestMapping("/getItemCatListByParentId")
    public List<ItemCat> getItemCatListByParentId(@RequestParam("parentId") Long parentId) {
        return itemCatService.getItemCatListByParentId(parentId);
    }

    @RequestMapping("/getItemCatById")
    public ItemCat getItemCatById(@RequestParam("id") Long id) {
        return itemCatService.getItemCatById(id);
    }

    @RequestMapping("/getAllItemCat")
    public List<ItemCat> getAllItemCat() {
        return itemCatService.getAllItemCat();
    }

}
