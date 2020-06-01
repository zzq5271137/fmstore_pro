package com.mycomp.core.dao.ad;

import com.mycomp.core.pojo.ad.ContentCategory;
import com.mycomp.core.pojo.ad.ContentCategoryQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentCategoryDao {

    ContentCategory selectByPrimaryKey(Long id);

    List<ContentCategory> selectByExample(ContentCategoryQuery example);

    int insert(ContentCategory record);

    int insertSelective(ContentCategory record);

    int updateByPrimaryKey(ContentCategory record);

    int updateByPrimaryKeySelective(ContentCategory record);

    int updateByExample(@Param("record") ContentCategory record, @Param("example") ContentCategoryQuery example);

    int updateByExampleSelective(@Param("record") ContentCategory record, @Param("example") ContentCategoryQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(ContentCategoryQuery example);

    int countByExample(ContentCategoryQuery example);

}