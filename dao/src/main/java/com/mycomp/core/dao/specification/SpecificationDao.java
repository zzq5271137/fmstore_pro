package com.mycomp.core.dao.specification;

import com.mycomp.core.pojo.specification.Specification;
import com.mycomp.core.pojo.specification.SpecificationQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SpecificationDao {

    Specification selectByPrimaryKey(Long id);

    List<Specification> selectByExample(SpecificationQuery example);

    int insert(Specification record);

    int insertSelective(Specification record);

    int updateByPrimaryKey(Specification record);

    int updateByPrimaryKeySelective(Specification record);

    int updateByExample(@Param("record") Specification record, @Param("example") SpecificationQuery example);

    int updateByExampleSelective(@Param("record") Specification record, @Param("example") SpecificationQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(SpecificationQuery example);

    int countByExample(SpecificationQuery example);

    List<Map<String, Object>> getSelectionListData();
}