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

}
