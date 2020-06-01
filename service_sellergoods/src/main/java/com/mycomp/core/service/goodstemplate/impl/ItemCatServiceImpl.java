package com.mycomp.core.service.goodstemplate.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.item.ItemCatDao;
import com.mycomp.core.pojo.item.ItemCat;
import com.mycomp.core.pojo.item.ItemCatQuery;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.goodstemplate.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Override
    public PageResult<ItemCat> getItemCatPageByParentId(Integer page, Integer pageSize, Long parentId) {
        PageHelper.startPage(page, pageSize);
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        Page<ItemCat> pageRes = (Page<ItemCat>) itemCatDao.selectByExample(itemCatQuery);
        return new PageResult<ItemCat>(pageRes.getTotal(), pageRes.getResult());
    }

    @Override
    public List<ItemCat> getItemCatListByParentId(Long parentId) {
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    @Override
    public ItemCat getItemCatById(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> getAllItemCat() {
        return itemCatDao.selectByExample(null);
    }

}
