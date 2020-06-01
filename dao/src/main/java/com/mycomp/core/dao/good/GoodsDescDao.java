package com.mycomp.core.dao.good;

import com.mycomp.core.pojo.good.GoodsDesc;
import com.mycomp.core.pojo.good.GoodsDescQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDescDao {

    GoodsDesc selectByPrimaryKey(Long goodsId);

    List<GoodsDesc> selectByExample(GoodsDescQuery example);

    int insert(GoodsDesc record);

    int insertSelective(GoodsDesc record);

    int updateByPrimaryKey(GoodsDesc record);

    int updateByPrimaryKeySelective(GoodsDesc record);

    int updateByExample(@Param("record") GoodsDesc record, @Param("example") GoodsDescQuery example);

    int updateByExampleSelective(@Param("record") GoodsDesc record, @Param("example") GoodsDescQuery example);

    int deleteByPrimaryKey(Long goodsId);

    int deleteByExample(GoodsDescQuery example);

    int countByExample(GoodsDescQuery example);

}