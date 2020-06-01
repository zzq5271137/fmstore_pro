package com.mycomp.core.dao.good;

import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.good.GoodsQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDao {

    Goods selectByPrimaryKey(Long id);

    List<Goods> selectByExample(GoodsQuery example);

    int insert(Goods record);

    int insertSelective(Goods record);

    int updateByPrimaryKey(Goods record);

    int updateByPrimaryKeySelective(Goods record);

    int updateByExample(@Param("record") Goods record, @Param("example") GoodsQuery example);

    int updateByExampleSelective(@Param("record") Goods record, @Param("example") GoodsQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(GoodsQuery example);

    int countByExample(GoodsQuery example);

}