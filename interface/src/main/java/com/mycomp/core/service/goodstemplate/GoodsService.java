package com.mycomp.core.service.goodstemplate;

import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.queryentity.GoodsEntity;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.exceptions.DeleteGoodsFromDBException;

import java.util.List;

public interface GoodsService {

    void addGoods(GoodsEntity goodsEntity);

    PageResult<Goods> getGoodsPage(Integer page, Integer pageSize, Goods goodsSearch);

    GoodsEntity getGoodsEntity(Long id);

    void updateGoods(GoodsEntity goodsEntity);

    void deleteGoods(Long[] targetIds);

    void updateStatus(Long[] targetIds, String status);

    List<Item> getItemsByGoodsIds(Long[] goodsIds, String status);

    void deleteGoodsFromDB(Long[] targetIds) throws DeleteGoodsFromDBException;

}
