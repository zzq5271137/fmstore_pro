package com.mycomp.core.service.buyerservice;

import com.mycomp.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    List<Address> getAddressListByLoginUser(String username);

}
