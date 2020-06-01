package com.mycomp.core.service.goodstemplate;

import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.SpecEntity;
import com.mycomp.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecService {

    /**
     * 分页查找Specification数据
     *
     * @param page       当前页
     * @param pageSize   每页的大小
     * @param searchSpec 搜索的条件
     * @return 封装的查询结果
     */
    PageResult<Specification> getSpecPage(Integer page, Integer pageSize, Specification searchSpec);

    /**
     * 添加Specification及其相关的SpecificationOption
     *
     * @param specEntity 要添加的Specification数据及其相关的SpecificationOption数据
     */
    void addSpecification(SpecEntity specEntity);

    /**
     * 根据id查找Specification
     *
     * @param id 待查找的Specification的id
     * @return 查找结果
     */
    SpecEntity getSpecById(Long id);

    /**
     * 更新Specification
     *
     * @param specEntity 待更新的内容
     */
    void updateSpec(SpecEntity specEntity);

    /**
     * 根据id删除Specification
     *
     * @param targetIds 待删除的所有Specification的id
     */
    void deleteSpec(Long[] targetIds);

    /**
     * 获取Specification下拉列表数据
     *
     * @return 获取的Specification下拉列表数据
     */
    List<Map<String, Object>> getSelectionListData();
}
