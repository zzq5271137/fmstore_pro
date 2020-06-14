package com.mycomp.core.dao.address;

import com.mycomp.core.pojo.address.Address;
import com.mycomp.core.pojo.address.AddressQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressDao {

    Address selectByPrimaryKey(Long id);

    List<Address> selectByExample(AddressQuery example);

    int insert(Address record);

    int insertSelective(Address record);

    int updateByPrimaryKey(Address record);

    int updateByPrimaryKeySelective(Address record);

    int updateByExample(@Param("record") Address record, @Param("example") AddressQuery example);

    int updateByExampleSelective(@Param("record") Address record, @Param("example") AddressQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(AddressQuery example);

    int countByExample(AddressQuery example);

}
