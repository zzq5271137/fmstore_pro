package com.mycomp.core.service.goodstemplate.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.specification.SpecificationOptionDao;
import com.mycomp.core.dao.template.TypeTemplateDao;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.specification.SpecificationOption;
import com.mycomp.core.pojo.specification.SpecificationOptionQuery;
import com.mycomp.core.pojo.template.TypeTemplate;
import com.mycomp.core.pojo.template.TypeTemplateQuery;
import com.mycomp.core.service.goodstemplate.TypeTemplateService;
import com.mycomp.utils.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specOptDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查找TypeTemplate数据
     *
     * @param page           当前页
     * @param pageSize       每页的大小
     * @param templateSearch 搜索的条件
     * @return 封装的查询结果
     */
    @Override
    public PageResult<TypeTemplate> getTemplatePage(Integer page, Integer pageSize, TypeTemplate templateSearch) {
        PageHelper.startPage(page, pageSize);
        TypeTemplateQuery templateQuery = new TypeTemplateQuery();
        templateQuery.setOrderByClause("id desc");
        if (templateSearch != null) {
            TypeTemplateQuery.Criteria criteria = templateQuery.createCriteria();
            if (templateSearch.getName() != null && !templateSearch.getName().equals("")) {
                criteria.andNameLike("%" + templateSearch.getName() + "%");
            }
        }
        Page<TypeTemplate> pageRes = (Page<TypeTemplate>) typeTemplateDao.selectByExample(templateQuery);
        return new PageResult<TypeTemplate>(pageRes.getTotal(), pageRes.getResult());
    }

    /**
     * 添加TypeTemplate
     *
     * @param typeTemplate 要添加的TypeTemplate数据
     */
    @Override
    public void addTemplate(TypeTemplate typeTemplate) {
        // 清除Redis中的相关数据
        redisTemplate.delete(RedisKeys.BRAND_LIST_REDIS);
        redisTemplate.delete(RedisKeys.SPEC_LIST_REDIS);

        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 根据id查找TypeTemplate
     *
     * @param id 待查找的TypeTemplate的id
     * @return 查找结果
     */
    @Override
    public TypeTemplate getTemplateById(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 更新TypeTemplate
     *
     * @param typeTemplate 待更新的内容
     */
    @Override
    public void updateTemplate(TypeTemplate typeTemplate) {
        // 清除Redis中的相关数据
        redisTemplate.delete(RedisKeys.BRAND_LIST_REDIS);
        redisTemplate.delete(RedisKeys.SPEC_LIST_REDIS);

        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 根据id删除TypeTemplate
     *
     * @param targetIds 待删除的所有TypeTemplate的id
     */
    @Override
    public void deleteTemplate(Long[] targetIds) {
        // 清除Redis中的相关数据
        redisTemplate.delete(RedisKeys.BRAND_LIST_REDIS);
        redisTemplate.delete(RedisKeys.SPEC_LIST_REDIS);

        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(targetIds));
        typeTemplateDao.deleteByExample(typeTemplateQuery);
    }

    /**
     * 根据模板id查询出模板, 再进一步查询出规格和规格选项
     *
     * @param id 模板的id
     * @return 查询结果
     */
    @Override
    public List<Map> getSpecByTemplateId(Long id) {
        // 1. 根据id查询出模板
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        // 2. 根据模板中的数据查询出规格
        if (typeTemplate != null) {
            String specs = typeTemplate.getSpecIds();
            List<Map> specList = JSON.parseArray(specs, Map.class);
            specList = specList.stream().map(spec -> {
                // 3. 根据规格的id查询出相应的规格选项
                Object specId = spec.get("id");
                Long specIdLong = Long.parseLong(String.valueOf(specId));
                SpecificationOptionQuery specOptQuery = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = specOptQuery.createCriteria();
                criteria.andSpecIdEqualTo(specIdLong);
                List<SpecificationOption> specOptList = specOptDao.selectByExample(specOptQuery);
                // 4. 将查询出的规格选项添加进Map(一个Map就是一个规格)
                spec.put("options", specOptList);
                return spec;
            }).collect(Collectors.toList());
            return specList;
        }
        return null;
    }

}
