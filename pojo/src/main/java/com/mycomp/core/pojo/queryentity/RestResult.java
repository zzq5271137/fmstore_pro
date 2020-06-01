package com.mycomp.core.pojo.queryentity;

/*
 * 封装的操作执行是否成功的实体
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class RestResult implements Serializable {

    private boolean success;  // 操作是否成功
    private String message;  // 提示信息

}
