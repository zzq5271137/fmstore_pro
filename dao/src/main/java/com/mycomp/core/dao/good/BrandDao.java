package com.mycomp.core.dao.good;

import com.mycomp.core.pojo.good.Brand;
import com.mycomp.core.pojo.good.BrandQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BrandDao {

    Brand selectByPrimaryKey(Long id);

    List<Brand> selectByExample(BrandQuery example);

    int insert(Brand record);

    int insertSelective(Brand record);

    int updateByPrimaryKey(Brand record);

    int updateByPrimaryKeySelective(Brand record);

    int updateByExample(@Param("record") Brand record, @Param("example") BrandQuery example);

    int updateByExampleSelective(@Param("record") Brand record, @Param("example") BrandQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(BrandQuery example);

    int countByExample(BrandQuery example);

    List<Map<String, Object>> getSelectionListData();

}