package com.mycomp.core.service.goodstemplate.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.specification.SpecificationDao;
import com.mycomp.core.dao.specification.SpecificationOptionDao;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.SpecEntity;
import com.mycomp.core.pojo.specification.Specification;
import com.mycomp.core.pojo.specification.SpecificationOption;
import com.mycomp.core.pojo.specification.SpecificationOptionQuery;
import com.mycomp.core.pojo.specification.SpecificationQuery;
import com.mycomp.core.service.goodstemplate.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 分页查找Specification数据
     *
     * @param page       当前页
     * @param pageSize   每页的大小
     * @param searchSpec 搜索的条件
     * @return 封装的查询结果
     */
    @Override
    public PageResult<Specification> getSpecPage(Integer page, Integer pageSize, Specification searchSpec) {
        PageHelper.startPage(page, pageSize);
        SpecificationQuery specQuery = new SpecificationQuery();
        specQuery.setOrderByClause("id desc");
        if (searchSpec != null) {
            SpecificationQuery.Criteria criteria = specQuery.createCriteria();
            if (searchSpec.getSpecName() != null && !searchSpec.getSpecName().equals("")) {
                criteria.andSpecNameLike("%" + searchSpec.getSpecName() + "%");
            }
        }
        Page<Specification> pageRes = (Page<Specification>) specificationDao.selectByExample(specQuery);
        return new PageResult<Specification>(pageRes.getTotal(), pageRes.getResult());
    }

    /**
     * 添加Specification及其相关的SpecificationOption
     *
     * @param specEntity 要添加的Specification数据及其相关的SpecificationOption数据
     */
    @Override
    public void addSpecification(SpecEntity specEntity) {
        // 1. 先保存Specification(拿到id)
        specificationDao.insertSelective(specEntity.getSpec());

        specEntity.getOptions().forEach((o) -> {
            // 2. 设置每个SpecificationOption的specId
            o.setSpecId(specEntity.getSpec().getId());

            // 3. 再保存SpecificationOption
            specificationOptionDao.insertSelective(o);
        });
    }

    /**
     * 根据id查找Specification
     *
     * @param id 待查找的Specification的id
     * @return 查找结果
     */
    @Override
    public SpecEntity getSpecById(Long id) {
        // 1. 先根据Specification的id查询出Specification
        Specification spec = specificationDao.selectByPrimaryKey(id);

        // 2. 再根据Specification的id查询出SpecificationOption
        SpecificationOptionQuery specOptQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria specOptQueryCriteria = specOptQuery.createCriteria();
        specOptQueryCriteria.andSpecIdEqualTo(id);
        List<SpecificationOption> options = specificationOptionDao.selectByExample(specOptQuery);

        // 3. 封装成实体并返回
        return new SpecEntity(spec, options);
    }

    /**
     * 更新Specification
     *
     * @param specEntity 待更新的内容
     */
    @Override
    public void updateSpec(SpecEntity specEntity) {
        // 1. 更新Specification
        specificationDao.updateByPrimaryKeySelective(specEntity.getSpec());

        // 2. 先打破原来的Specification与SpecificationOption的关系
        SpecificationOptionQuery specOptQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria specOptQueryCriteria = specOptQuery.createCriteria();
        specOptQueryCriteria.andSpecIdEqualTo(specEntity.getSpec().getId());
        specificationOptionDao.deleteByExample(specOptQuery);

        // 3. 再重新建立新的关系
        specEntity.getOptions().forEach((o) -> {
            // a). 设置每个SpecificationOption的specId
            o.setSpecId(specEntity.getSpec().getId());

            // b). 保存SpecificationOption
            specificationOptionDao.insertSelective(o);
        });
    }

    /**
     * 根据id删除Specification
     *
     * @param targetIds 待删除的所有Specification的id
     */
    @Override
    public void deleteSpec(Long[] targetIds) {
        // 1. 先根据Specification的id删除SpecificationOption
        SpecificationOptionQuery specOptQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria specOptQueryCriteria = specOptQuery.createCriteria();
        specOptQueryCriteria.andSpecIdIn(Arrays.asList(targetIds));
        specificationOptionDao.deleteByExample(specOptQuery);

        // 2. 再删除Specification
        SpecificationQuery specQuery = new SpecificationQuery();
        SpecificationQuery.Criteria specQueryCriteria = specQuery.createCriteria();
        specQueryCriteria.andIdIn(Arrays.asList(targetIds));
        specificationDao.deleteByExample(specQuery);
    }

    /**
     * 获取Specification下拉列表数据
     *
     * @return 获取的Specification下拉列表数据
     */
    @Override
    public List<Map<String, Object>> getSelectionListData() {
        return specificationDao.getSelectionListData();
    }

}
