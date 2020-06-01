package com.mycomp.core.dao.item;

import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDao {

    Item selectByPrimaryKey(Long id);

    List<Item> selectByExample(ItemQuery example);

    int insert(Item record);

    int insertSelective(Item record);

    int updateByPrimaryKey(Item record);

    int updateByPrimaryKeySelective(Item record);

    int updateByExample(@Param("record") Item record, @Param("example") ItemQuery example);

    int updateByExampleSelective(@Param("record") Item record, @Param("example") ItemQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(ItemQuery example);

    int countByExample(ItemQuery example);

}