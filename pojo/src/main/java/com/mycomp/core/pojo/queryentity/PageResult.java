package com.mycomp.core.pojo.queryentity;

/*
 * 封装的分页查询的查询结果的实体
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageResult<E> implements Serializable {

    private Long total;  // 总记录数
    private List<E> rows;  // 当前页的数据

}
