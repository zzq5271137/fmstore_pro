package com.mycomp.core.dao.order;

import com.mycomp.core.pojo.order.OrderItem;
import com.mycomp.core.pojo.order.OrderItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemDao {

    OrderItem selectByPrimaryKey(Long id);

    List<OrderItem> selectByExample(OrderItemQuery example);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByExample(@Param("record") OrderItem record, @Param("example") OrderItemQuery example);

    int updateByExampleSelective(@Param("record") OrderItem record, @Param("example") OrderItemQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(OrderItemQuery example);

    int countByExample(OrderItemQuery example);

}