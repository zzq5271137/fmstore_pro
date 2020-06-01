package com.mycomp.core.service.admanage;

import com.mycomp.core.pojo.ad.ContentCategory;
import com.mycomp.core.pojo.queryentity.PageResult;

import java.util.List;

public interface ContentCatService {

    PageResult<ContentCategory> getContentCatPage(Integer page, Integer pageSize, ContentCategory contentCatSearch);

    void addContentCat(ContentCategory contentCat);

    ContentCategory getContentCatById(Long id);

    void updateContentCat(ContentCategory contentCat);

    void deleteContentCat(Long[] targetIds);

    List<ContentCategory> getAllContentCat();

}
