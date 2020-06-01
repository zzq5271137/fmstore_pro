package com.mycomp.core.dao.seller;

import com.mycomp.core.pojo.seller.Seller;
import com.mycomp.core.pojo.seller.SellerQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SellerDao {

    Seller selectByPrimaryKey(String sellerId);

    List<Seller> selectByExample(SellerQuery example);

    int insert(Seller record);

    int insertSelective(Seller record);

    int updateByPrimaryKey(Seller record);

    int updateByPrimaryKeySelective(Seller record);

    int updateByExample(@Param("record") Seller record, @Param("example") SellerQuery example);

    int updateByExampleSelective(@Param("record") Seller record, @Param("example") SellerQuery example);

    int deleteByPrimaryKey(String sellerId);

    int deleteByExample(SellerQuery example);

    int countByExample(SellerQuery example);

}