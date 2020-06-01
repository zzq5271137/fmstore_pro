package com.mycomp.core.dao.item;

import com.mycomp.core.pojo.item.ItemCat;
import com.mycomp.core.pojo.item.ItemCatQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemCatDao {

    ItemCat selectByPrimaryKey(Long id);

    List<ItemCat> selectByExample(ItemCatQuery example);

    int insert(ItemCat record);

    int insertSelective(ItemCat record);

    int updateByPrimaryKey(ItemCat record);

    int updateByPrimaryKeySelective(ItemCat record);

    int updateByExample(@Param("record") ItemCat record, @Param("example") ItemCatQuery example);

    int updateByExampleSelective(@Param("record") ItemCat record, @Param("example") ItemCatQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(ItemCatQuery example);

    int countByExample(ItemCatQuery example);

}