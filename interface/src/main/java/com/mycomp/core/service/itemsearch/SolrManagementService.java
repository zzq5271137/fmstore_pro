package com.mycomp.core.service.itemsearch;

import com.mycomp.core.pojo.item.Item;

import java.util.List;

public interface SolrManagementService {

    void saveItemsToSolr(List<Item> items);

    void deleteItemsFromSolr(List<Long> goodsIds);

}
