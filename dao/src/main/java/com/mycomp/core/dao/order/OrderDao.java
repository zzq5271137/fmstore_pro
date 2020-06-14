package com.mycomp.core.dao.order;

import com.mycomp.core.pojo.order.Order;
import com.mycomp.core.pojo.order.OrderQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDao {

    Order selectByPrimaryKey(Long orderId);

    List<Order> selectByExample(OrderQuery example);

    int insert(Order record);

    int insertSelective(Order record);

    int updateByPrimaryKey(Order record);

    int updateByPrimaryKeySelective(Order record);

    int updateByExample(@Param("record") Order record, @Param("example") OrderQuery example);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderQuery example);

    int deleteByPrimaryKey(Long orderId);

    int deleteByExample(OrderQuery example);

    int countByExample(OrderQuery example);

}