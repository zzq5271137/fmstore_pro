package com.mycomp.core.service.goodstemplate;

import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    /**
     * 分页查找TypeTemplate数据
     *
     * @param page           当前页
     * @param pageSize       每页的大小
     * @param templateSearch 搜索的条件
     * @return 封装的查询结果
     */
    PageResult<TypeTemplate> getTemplatePage(Integer page, Integer pageSize, TypeTemplate templateSearch);

    /**
     * 添加TypeTemplate
     *
     * @param typeTemplate 要添加的TypeTemplate数据
     */
    void addTemplate(TypeTemplate typeTemplate);

    /**
     * 根据id查找TypeTemplate
     *
     * @param id 待查找的TypeTemplate的id
     * @return 查找结果
     */
    TypeTemplate getTemplateById(Long id);

    /**
     * 更新TypeTemplate
     *
     * @param typeTemplate 待更新的内容
     */
    void updateTemplate(TypeTemplate typeTemplate);

    /**
     * 根据id删除TypeTemplate
     *
     * @param targetIds 待删除的所有TypeTemplate的id
     */
    void deleteTemplate(Long[] targetIds);

    /**
     * 根据模板id查询出模板, 再进一步查询出规格和规格选项
     *
     * @param id 模板的id
     * @return 查询结果
     */
    List<Map> getSpecByTemplateId(Long id);

}
