package com.mycomp.core.service.admanage;

import com.mycomp.core.pojo.ad.Content;
import com.mycomp.core.pojo.queryentity.PageResult;

import java.util.List;

public interface ContentService {

    PageResult<Content> getContentPage(Integer page, Integer pageSize, Content contentSearch);

    void addContent(Content content);

    Content getContentById(Long id);

    void updateContent(Content content);

    void updateContentStatus(Long[] targetIds, String status);

    void deleteContent(Long[] targetIds);

    List<Content> getContentByCategoryId(Long categoryId);

    /*
     * 获取数据的时候先从Redis中获取, 如果获取到数据则直接返回, 就不用访问数据库了;
     * 如果获取不到数据, 可以从数据库中查询, 查询到后放入Redis中一份, 下回就可以直接从Redis中查询到;
     * 这样大大降低了数据库的高并发访问压力;
     */
    List<Content> getContentByCategoryIdFromRedis(Long categoryId);

}
