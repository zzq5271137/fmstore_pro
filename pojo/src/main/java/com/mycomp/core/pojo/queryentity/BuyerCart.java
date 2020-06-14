package com.mycomp.core.pojo.queryentity;

import com.mycomp.core.pojo.order.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BuyerCart implements Serializable {

    private String sellerId;
    private String sellerName;
    private List<OrderItem> orderItemList;

}
