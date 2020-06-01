package com.mycomp.core.service.goodstemplate;

import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.queryentity.GoodsEntity;
import com.mycomp.core.pojo.queryentity.PageResult;

public interface GoodsService {

    void addGoods(GoodsEntity goodsEntity);

    PageResult<Goods> getGoodsPage(Integer page, Integer pageSize, Goods goodsSearch);

    GoodsEntity getGoodsEntity(Long id);

    void updateGoods(GoodsEntity goodsEntity);

    void deleteGoods(Long[] targetIds);

    void updateStatus(Long[] targetIds, String status);

}
