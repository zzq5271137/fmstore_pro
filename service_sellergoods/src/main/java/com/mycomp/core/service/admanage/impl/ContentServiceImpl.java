package com.mycomp.core.service.admanage.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.ad.ContentDao;
import com.mycomp.core.pojo.ad.Content;
import com.mycomp.core.pojo.ad.ContentQuery;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.admanage.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Override
    public PageResult<Content> getContentPage(Integer page, Integer pageSize, Content contentSearch) {
        PageHelper.startPage(page, pageSize);
        ContentQuery contentQuery = new ContentQuery();
        contentQuery.setOrderByClause("id desc");
        if (contentSearch != null) {
            ContentQuery.Criteria criteria = contentQuery.createCriteria();
            if (contentSearch.getTitle() != null && !contentSearch.getTitle().equals("")) {
                criteria.andTitleLike("%" + contentSearch.getTitle() + "%");
            }
            if (contentSearch.getUrl() != null && !contentSearch.getUrl().equals("")) {
                criteria.andUrlLike("%" + contentSearch.getUrl() + "%");
            }
        }
        Page<Content> pageRes = (Page<Content>) contentDao.selectByExample(contentQuery);
        return new PageResult<Content>(pageRes.getTotal(), pageRes.getResult());
    }

    @Override
    public void addContent(Content content) {
        contentDao.insertSelective(content);
    }

    @Override
    public Content getContentById(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateContent(Content content) {
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void updateContentStatus(Long[] targetIds, String status) {
        Arrays.asList(targetIds).forEach(id -> {
            Content content = new Content();
            content.setId(id);
            content.setStatus(status);
            contentDao.updateByPrimaryKeySelective(content);
        });
    }

    @Override
    public void deleteContent(Long[] targetIds) {
        ContentQuery contentQuery = new ContentQuery();
        ContentQuery.Criteria criteria = contentQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(targetIds));
        contentDao.deleteByExample(contentQuery);
    }

    @Override
    public List<Content> getContentByCategoryId(Long categoryId) {
        ContentQuery contentQuery = new ContentQuery();
        ContentQuery.Criteria contentQueryCriteria = contentQuery.createCriteria();
        contentQueryCriteria.andCategoryIdEqualTo(categoryId);
        return contentDao.selectByExample(contentQuery);
    }

}
