package com.mycomp.core.dao.template;

import com.mycomp.core.pojo.template.TypeTemplate;
import com.mycomp.core.pojo.template.TypeTemplateQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TypeTemplateDao {

    TypeTemplate selectByPrimaryKey(Long id);

    List<TypeTemplate> selectByExample(TypeTemplateQuery example);

    int insert(TypeTemplate record);

    int insertSelective(TypeTemplate record);

    int updateByPrimaryKey(TypeTemplate record);

    int updateByPrimaryKeySelective(TypeTemplate record);

    int updateByExample(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByExampleSelective(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(TypeTemplateQuery example);

    int countByExample(TypeTemplateQuery example);

}