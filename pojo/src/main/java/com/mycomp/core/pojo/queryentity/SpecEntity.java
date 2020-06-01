package com.mycomp.core.pojo.queryentity;

/*
 * 封装的Specification的请求实体
 */

import com.mycomp.core.pojo.specification.Specification;
import com.mycomp.core.pojo.specification.SpecificationOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecEntity implements Serializable {

    private Specification spec;  // Specification信息
    private List<SpecificationOption> options;  // 相应的SpecificationOption信息(多个)

}
