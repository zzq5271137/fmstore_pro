package com.mycomp.utils;

/*
 * Redis的常量工具类, 在这里统一管理Redis的key, 因为Redis的一级key是非常宝贵的
 */

public interface RedisKeys {

    // 广告列表
    String CONTENT_LIST_REDIS = "contentList";

    // 分类列表
    String CATEGORY_LIST_REDIS = "categoryList";

    // 品牌列表
    String BRAND_LIST_REDIS = "brandList";

    // 规格列表
    String SPEC_LIST_REDIS = "specList";

}
