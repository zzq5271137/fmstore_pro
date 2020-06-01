package com.mycomp.core.service.admanage.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.ad.ContentCategoryDao;
import com.mycomp.core.pojo.ad.ContentCategory;
import com.mycomp.core.pojo.ad.ContentCategoryQuery;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.admanage.ContentCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ContentCatServiceImpl implements ContentCatService {

    @Autowired
    private ContentCategoryDao contentCatDao;

    @Override
    public PageResult<ContentCategory> getContentCatPage(Integer page, Integer pageSize,
                                                         ContentCategory contentCatSearch) {
        PageHelper.startPage(page, pageSize);
        ContentCategoryQuery contentCatQuery = new ContentCategoryQuery();
        contentCatQuery.setOrderByClause("id desc");
        if (contentCatSearch != null) {
            ContentCategoryQuery.Criteria criteria = contentCatQuery.createCriteria();
            if (contentCatSearch.getName() != null && !contentCatSearch.getName().equals("")) {
                criteria.andNameLike("%" + contentCatSearch.getName() + "%");
            }
        }
        Page<ContentCategory> pageRes = (Page<ContentCategory>) contentCatDao.selectByExample(contentCatQuery);
        return new PageResult<ContentCategory>(pageRes.getTotal(), pageRes.getResult());
    }

    @Override
    public void addContentCat(ContentCategory contentCat) {
        contentCatDao.insertSelective(contentCat);
    }

    @Override
    public ContentCategory getContentCatById(Long id) {
        return contentCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateContentCat(ContentCategory contentCat) {
        contentCatDao.updateByPrimaryKeySelective(contentCat);
    }

    @Override
    public void deleteContentCat(Long[] targetIds) {
        ContentCategoryQuery contentCatQuery = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = contentCatQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(targetIds));
        contentCatDao.deleteByExample(contentCatQuery);
    }

    @Override
    public List<ContentCategory> getAllContentCat() {
        return contentCatDao.selectByExample(null);
    }

}
