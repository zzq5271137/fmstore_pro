package com.mycomp.core.service.goodstemplate;

import com.mycomp.core.pojo.good.Brand;
import com.mycomp.core.pojo.queryentity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 分页查找Brand数据
     *
     * @param page        当前页
     * @param pageSize    每页的大小
     * @param brandSearch 搜索的条件
     * @return 封装的查询结果
     */
    PageResult<Brand> getBrandPage(Integer page, Integer pageSize, Brand brandSearch);

    /**
     * 添加Brand
     *
     * @param brand 要添加的Brand数据
     */
    void addBrand(Brand brand);

    /**
     * 根据id查找Brand
     *
     * @param id 待查找的Brand的id
     * @return 查找结果
     */
    Brand getBrandById(Long id);

    /**
     * 更新Brand
     *
     * @param brand 待更新的内容
     */
    void updateBrand(Brand brand);

    /**
     * 根据id删除Brand
     *
     * @param targetIds 待删除的所有Brand的id
     */
    void deleteBrand(Long[] targetIds);

    /**
     * 获取Brand下拉列表数据
     *
     * @return 获取的Brand下拉列表数据
     */
    List<Map<String, Object>> getSelectionListData();
}
