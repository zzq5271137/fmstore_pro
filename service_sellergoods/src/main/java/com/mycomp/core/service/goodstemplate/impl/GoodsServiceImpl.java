package com.mycomp.core.service.goodstemplate.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.good.BrandDao;
import com.mycomp.core.dao.good.GoodsDao;
import com.mycomp.core.dao.good.GoodsDescDao;
import com.mycomp.core.dao.item.ItemCatDao;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.dao.seller.SellerDao;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.good.GoodsDesc;
import com.mycomp.core.pojo.good.GoodsQuery;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemQuery;
import com.mycomp.core.pojo.queryentity.GoodsEntity;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.service.goodstemplate.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void addGoods(GoodsEntity goodsEntity) {
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());
        insertItem(goodsEntity);
    }

    private void insertItem(GoodsEntity goodsEntity) {
        if ("1".equals(goodsEntity.getGoods().getIsEnableSpec())) {
            if (goodsEntity.getItemList() != null) {
                goodsEntity.getItemList().forEach(item -> {
                    StringBuilder title = new StringBuilder(goodsEntity.getGoods().getGoodsName());
                    String specStr = item.getSpec();
                    Map specMap = JSON.parseObject(specStr);
                    for (Object value : specMap.values()) {
                        title.append(" ").append(value);
                    }
                    item.setTitle(title.toString());
                    setItemValues(item, goodsEntity);
                    itemDao.insertSelective(item);
                });
            }
        } else {
            Item item = new Item();
            item.setPrice(new BigDecimal("99999999999"));
            item.setNum(0);
            item.setSpec("{}");
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            setItemValues(item, goodsEntity);
            itemDao.insertSelective(item);
        }
    }

    private void setItemValues(Item item, GoodsEntity goodsEntity) {
        item.setSellerId(goodsEntity.getGoods().getSellerId());
        item.setGoodsId(goodsEntity.getGoods().getId());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setStatus("0");
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id()).getName());
        item.setBrand(brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId()).getName());
        item.setSeller(sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId()).getName());
        List<Map> itemImages = JSON.parseArray(goodsEntity.getGoodsDesc().getItemImages(), Map.class);
        if (itemImages != null && itemImages.size() > 0) {
            String url = String.valueOf(itemImages.get(0).get("url"));
            item.setImage(url);
        }
    }

    @Override
    public PageResult<Goods> getGoodsPage(Integer page, Integer pageSize, Goods goodsSearch) {
        PageHelper.startPage(page, pageSize);
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.setOrderByClause("id desc");
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goodsSearch != null) {
            if (goodsSearch.getGoodsName() != null && !goodsSearch.getGoodsName().equals("")) {
                criteria.andGoodsNameLike("%" + goodsSearch.getGoodsName() + "%");
            }
            if (goodsSearch.getAuditStatus() != null && !goodsSearch.getAuditStatus().equals("")) {
                criteria.andAuditStatusEqualTo(goodsSearch.getAuditStatus());
            }
            if (goodsSearch.getSellerId() != null && !goodsSearch.getSellerId().equals("")
                    && !goodsSearch.getSellerId().equals("admin")) {
                criteria.andSellerIdEqualTo(goodsSearch.getSellerId());
            }
        }
        criteria.andIsDeleteIsNull();
        Page<Goods> pageRes = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult<Goods>(pageRes.getTotal(), pageRes.getResult());
    }

    @Override
    public GoodsEntity getGoodsEntity(Long id) {
        // 查询goods
        Goods goods = goodsDao.selectByPrimaryKey(id);
        // 查询goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        // 查询itemList
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
        itemQueryCriteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        // 封装成GoodsEntity并返回
        return new GoodsEntity(goods, goodsDesc, itemList);
    }

    @Override
    public void updateGoods(GoodsEntity goodsEntity) {
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
        itemQueryCriteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        insertItem(goodsEntity);
    }

    @Override
    public void deleteGoods(Long[] targetIds) {
        if (targetIds != null && targetIds.length > 0) {
            Arrays.asList(targetIds).forEach(id -> {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsDelete("1");  // 并不是真正的从数据库中删掉数据
                goodsDao.updateByPrimaryKeySelective(goods);
            });
        }
    }

    @Override
    public void updateStatus(Long[] targetIds, String status) {
        if (targetIds != null && targetIds.length > 0) {
            Arrays.asList(targetIds).forEach(targetId -> {
                Goods goods = new Goods();
                goods.setId(targetId);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);
                Item item = new Item();
                item.setStatus(status);
                ItemQuery itemQuery = new ItemQuery();
                ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
                itemQueryCriteria.andGoodsIdEqualTo(targetId);
                itemDao.updateByExampleSelective(item, itemQuery);
            });
        }
    }

    @Override
    public List<Item> getItemsByGoodsIds(Long[] goodsIds, String status) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return itemDao.selectByExample(query);
    }

}
