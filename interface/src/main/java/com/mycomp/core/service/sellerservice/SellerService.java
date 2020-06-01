package com.mycomp.core.service.sellerservice;

import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.seller.Seller;

public interface SellerService {

    /**
     * 分页查找Seller数据
     *
     * @param page         当前页
     * @param pageSize     每页的大小
     * @param sellerSearch 搜索的条件
     * @return 封装的查询结果
     */
    PageResult<Seller> getSellerPage(Integer page, Integer pageSize, Seller sellerSearch);

    /**
     * 添加Seller(商家注册账号)
     *
     * @param seller 要添加(注册)的Seller数据
     */
    void addSeller(Seller seller);

    /**
     * 根据id查找Seller
     *
     * @param sellerId 待查找的Seller的sellerId
     * @return 查找结果
     */
    Seller getSellerById(String sellerId);

    /**
     * 更新Seller
     *
     * @param seller 待更新的内容
     */
    void updateSeller(Seller seller);

    /**
     * 查询Seller密码
     *
     * @param sellerId 待查找的Seller的sellerId
     * @return 查询结果
     */
    String getSellerPwd(String sellerId);

}
