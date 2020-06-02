package com.mycomp.core.service.admanage.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.ad.ContentDao;
import com.mycomp.core.pojo.ad.Content;
import com.mycomp.core.pojo.ad.ContentQuery;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.admanage.ContentService;
import com.mycomp.utils.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

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
        // 删除Redis缓存中的相关内容(使得下一次读取时会先从数据库中查询, 再存入Redis, 保持Redis中的数据为最新)
        redisTemplate.boundHashOps(RedisKeys.CONTENT_LIST_REDIS).delete(content.getCategoryId());

        contentDao.insertSelective(content);
    }

    @Override
    public Content getContentById(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateContent(Content content) {
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());

        // 删除Redis缓存中的相关内容(使得下一次读取时会先从数据库中查询, 再存入Redis, 保持Redis中的数据为最新)
        redisTemplate.boundHashOps(RedisKeys.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        if (content.getCategoryId() != null && !content.getCategoryId().equals(oldContent.getCategoryId())) {
            redisTemplate.boundHashOps(RedisKeys.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        }

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
        Arrays.asList(targetIds).forEach(id -> {
            Content content = contentDao.selectByPrimaryKey(id);

            // 删除Redis缓存中的相关内容(使得下一次读取时会先从数据库中查询, 再存入Redis, 保持Redis中的数据为最新)
            redisTemplate.boundHashOps(RedisKeys.CONTENT_LIST_REDIS).delete(content.getCategoryId());

            contentDao.deleteByPrimaryKey(id);
        });
    }

    @Override
    public List<Content> getContentByCategoryId(Long categoryId) {
        ContentQuery contentQuery = new ContentQuery();
        ContentQuery.Criteria contentQueryCriteria = contentQuery.createCriteria();
        contentQueryCriteria.andCategoryIdEqualTo(categoryId);
        return contentDao.selectByExample(contentQuery);
    }

    /*
     * 获取数据的时候先从Redis中获取, 如果获取到数据则直接返回, 就不用访问数据库了;
     * 如果获取不到数据, 可以从数据库中查询, 查询到后放入Redis中一份, 下回就可以直接从Redis中查询到;
     * 这样大大降低了数据库的高并发访问压力;
     */
    @Override
    public List<Content> getContentByCategoryIdFromRedis(Long categoryId) {
        // 1. 先从Redis中查询
        List<Content> res = (List<Content>) redisTemplate.boundHashOps(RedisKeys.CONTENT_LIST_REDIS).get(categoryId);
        if (res == null) {
            // 2. 如果Redis中没有相关数据, 则从数据库中查询, 再放入一份到Redis中
            res = getContentByCategoryId(categoryId);
            redisTemplate.boundHashOps(RedisKeys.CONTENT_LIST_REDIS).put(categoryId, res);
        }
        return res;
    }

}
