package com.mycomp.core.service.cmsservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mycomp.core.dao.good.GoodsDao;
import com.mycomp.core.dao.good.GoodsDescDao;
import com.mycomp.core.dao.item.ItemCatDao;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.good.GoodsDesc;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemQuery;
import com.mycomp.core.service.cmsservice.CmsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    private ServletContext servletContext;

    /**
     * ServletContextAware接口用于在非控制器(Controller)的类中获取当前项目的路径
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void createStaticPage(Long goodsId) throws Exception {
        /*
         * 1. 获取商品相关数据, 准备模板数据
         */
        Map<String, Object> templateData = getTemplateData(goodsId);

        /*
         * 2. 通过FreeMarker生成html文件
         */
        // 通过注入的FreeMarkerConfigurer对象获取Configuration对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        // 加载一个模板, 创建一个模板对象
        Template template = configuration.getTemplate("item.ftl");
        // 创建输出流, 指定生成的文件名
        String outputPath = servletContext.getRealPath(goodsId + ".html");
        Writer writer = new OutputStreamWriter(new FileOutputStream(new File(outputPath)), StandardCharsets.UTF_8);
        // 调用模板对象的process()方法输出静态页面
        template.process(templateData, writer);
        // 关闭流
        writer.close();
    }

    /**
     * 获取商品相关数据, 准备模板数据
     */
    private Map<String, Object> getTemplateData(Long goodsId) {
        Map<String, Object> templateData = new HashMap<>();
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
        itemQueryCriteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        if (goods != null) {
            templateData.put("itemCat1", itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
            templateData.put("itemCat2", itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
            templateData.put("itemCat3", itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        }
        templateData.put("goods", goods);
        templateData.put("goodsDesc", goodsDesc);
        templateData.put("itemList", itemList);
        return templateData;
    }

}
