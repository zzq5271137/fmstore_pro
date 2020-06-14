package com.mycomp.core.dao.log;

import com.mycomp.core.pojo.log.PayLog;
import com.mycomp.core.pojo.log.PayLogQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayLogDao {

    PayLog selectByPrimaryKey(String outTradeNo);

    List<PayLog> selectByExample(PayLogQuery example);

    int insert(PayLog record);

    int insertSelective(PayLog record);

    int updateByPrimaryKey(PayLog record);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogQuery example);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogQuery example);

    int deleteByPrimaryKey(String outTradeNo);

    int deleteByExample(PayLogQuery example);

    int countByExample(PayLogQuery example);

}