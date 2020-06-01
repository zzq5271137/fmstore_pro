package com.mycomp.core.service.goodstemplate.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.good.BrandDao;
import com.mycomp.core.pojo.good.Brand;
import com.mycomp.core.pojo.good.BrandQuery;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.goodstemplate.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    /**
     * 分页查找Brand数据
     *
     * @param page        当前页
     * @param pageSize    每页的大小
     * @param brandSearch 搜索的条件
     * @return 封装的查询结果
     */
    @Override
    public PageResult<Brand> getBrandPage(Integer page, Integer pageSize, Brand brandSearch) {
        // 分页进行查询
        PageHelper.startPage(page, pageSize);  // PageHelper插件通过拦截的方式修改sql语句进行分页查询(通过limit)
        // 添加降序排列的查询子句
        BrandQuery brandQuery = new BrandQuery();
        brandQuery.setOrderByClause("id desc");
        // 添加搜索条件
        if (brandSearch != null) {
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            if (brandSearch.getName() != null && !brandSearch.getName().equals("")) {
                criteria.andNameLike("%" + brandSearch.getName() + "%");
            }
            if (brandSearch.getFirstChar() != null && !brandSearch.getFirstChar().equals("")) {
                criteria.andFirstCharLike("%" + brandSearch.getFirstChar() + "%");
            }
        }
        Page<Brand> pageRes = (Page<Brand>) brandDao.selectByExample(brandQuery);
        return new PageResult<Brand>(pageRes.getTotal(), pageRes.getResult());
    }

    /**
     * 添加Brand
     *
     * @param brand 要添加的Brand数据
     */
    @Override
    public void addBrand(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 根据id查找Brand
     *
     * @param id 待查找的Brand的id
     * @return 查找结果
     */
    @Override
    public Brand getBrandById(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 更新Brand
     *
     * @param brand 待更新的内容
     */
    @Override
    public void updateBrand(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    /**
     * 根据id删除Brand
     *
     * @param targetIds 待删除的所有Brand的id
     */
    @Override
    public void deleteBrand(Long[] targetIds) {
        // 遍历数组, 通过主键一个一个地删除(多条sql)
        // Arrays.asList(targetIds).forEach((id) -> brandDao.deleteByPrimaryKey(id));

        // 也可以设置条件, 一次性删除(一条sql)
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(targetIds));
        brandDao.deleteByExample(brandQuery);
    }

    /**
     * 获取Brand下拉列表数据
     *
     * @return 获取的Brand下拉列表数据
     */
    @Override
    public List<Map<String, Object>> getSelectionListData() {
        return brandDao.getSelectionListData();
    }

}
