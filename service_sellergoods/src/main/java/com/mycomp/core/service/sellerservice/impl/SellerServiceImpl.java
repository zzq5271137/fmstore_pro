package com.mycomp.core.service.sellerservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.seller.SellerDao;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.seller.Seller;
import com.mycomp.core.pojo.seller.SellerQuery;
import com.mycomp.core.service.sellerservice.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    /**
     * 分页查找Seller数据
     *
     * @param page         当前页
     * @param pageSize     每页的大小
     * @param sellerSearch 搜索的条件
     * @return 封装的查询结果
     */
    @Override
    public PageResult<Seller> getSellerPage(Integer page, Integer pageSize, Seller sellerSearch) {
        PageHelper.startPage(page, pageSize);
        SellerQuery sellerQuery = null;
        if (sellerSearch != null) {
            sellerQuery = new SellerQuery();
            SellerQuery.Criteria criteria = sellerQuery.createCriteria();
            if (sellerSearch.getName() != null && !sellerSearch.getName().equals("")) {
                criteria.andNameLike("%" + sellerSearch.getName() + "%");
            }
            if (sellerSearch.getNickName() != null && !sellerSearch.getNickName().equals("")) {
                criteria.andNickNameLike("%" + sellerSearch.getNickName() + "%");
            }
            if (sellerSearch.getStatus() != null && !sellerSearch.getStatus().equals("-1")) {
                criteria.andStatusEqualTo(sellerSearch.getStatus());
            }
        }
        Page<Seller> pageRes = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult<Seller>(pageRes.getTotal(), pageRes.getResult());
    }

    /**
     * 添加Seller(商家注册账号)
     *
     * @param seller 要添加(注册)的Seller数据
     */
    @Override
    public void addSeller(Seller seller) {
        seller.setCreateTime(new Date());
        seller.setStatus("0");  // 还未经过运营商审核, 商家新注册的账号的状态默认为"0"
        sellerDao.insertSelective(seller);
    }

    /**
     * 根据id查找Seller
     *
     * @param sellerId 待查找的Seller的sellerId
     * @return 查找结果
     */
    @Override
    public Seller getSellerById(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    /**
     * 更新Seller
     *
     * @param seller 待更新的内容
     */
    @Override
    public void updateSeller(Seller seller) {
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    /**
     * 查询Seller密码
     *
     * @param sellerId 待查找的Seller的sellerId
     * @return 查询结果
     */
    @Override
    public String getSellerPwd(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId).getPassword();
    }

}
