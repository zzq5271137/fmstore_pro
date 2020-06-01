package com.mycomp.core.dao.specification;

import com.mycomp.core.pojo.specification.SpecificationOption;
import com.mycomp.core.pojo.specification.SpecificationOptionQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpecificationOptionDao {

    SpecificationOption selectByPrimaryKey(Long id);

    List<SpecificationOption> selectByExample(SpecificationOptionQuery example);

    int insert(SpecificationOption record);

    int insertSelective(SpecificationOption record);

    int updateByPrimaryKey(SpecificationOption record);

    int updateByPrimaryKeySelective(SpecificationOption record);

    int updateByExample(@Param("record") SpecificationOption record, @Param("example") SpecificationOptionQuery example);

    int updateByExampleSelective(@Param("record") SpecificationOption record, @Param("example") SpecificationOptionQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(SpecificationOptionQuery example);

    int countByExample(SpecificationOptionQuery example);

}