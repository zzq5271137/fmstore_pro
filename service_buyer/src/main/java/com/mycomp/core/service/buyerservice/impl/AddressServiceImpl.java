package com.mycomp.core.service.buyerservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mycomp.core.dao.address.AddressDao;
import com.mycomp.core.pojo.address.Address;
import com.mycomp.core.pojo.address.AddressQuery;
import com.mycomp.core.service.buyerservice.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> getAddressListByLoginUser(String username) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(username);
        return addressDao.selectByExample(query);
    }

}
