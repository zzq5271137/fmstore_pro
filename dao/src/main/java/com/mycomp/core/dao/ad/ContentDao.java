package com.mycomp.core.dao.ad;

import com.mycomp.core.pojo.ad.Content;
import com.mycomp.core.pojo.ad.ContentQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentDao {

    Content selectByPrimaryKey(Long id);

    List<Content> selectByExample(ContentQuery example);

    int insert(Content record);

    int insertSelective(Content record);

    int updateByPrimaryKey(Content record);

    int updateByPrimaryKeySelective(Content record);

    int updateByExample(@Param("record") Content record, @Param("example") ContentQuery example);

    int updateByExampleSelective(@Param("record") Content record, @Param("example") ContentQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(ContentQuery example);

    int countByExample(ContentQuery example);

}