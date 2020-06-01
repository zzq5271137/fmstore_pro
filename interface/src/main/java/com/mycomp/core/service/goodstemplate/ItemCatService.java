package com.mycomp.core.service.goodstemplate;

import com.mycomp.core.pojo.item.ItemCat;
import com.mycomp.core.pojo.queryentity.PageResult;

import java.util.List;

public interface ItemCatService {

    PageResult<ItemCat> getItemCatPageByParentId(Integer page, Integer pageSize, Long parentId);

    List<ItemCat> getItemCatListByParentId(Long parentId);

    ItemCat getItemCatById(Long id);

    List<ItemCat> getAllItemCat();

}
