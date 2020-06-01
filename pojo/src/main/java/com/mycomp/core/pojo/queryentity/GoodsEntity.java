package com.mycomp.core.pojo.queryentity;

import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.good.GoodsDesc;
import com.mycomp.core.pojo.item.Item;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GoodsEntity implements Serializable {

    private Goods goods;
    private GoodsDesc goodsDesc;
    private List<Item> itemList;

}
