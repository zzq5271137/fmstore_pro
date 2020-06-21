package com.mycomp.core.controller.portal;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.address.Address;
import com.mycomp.core.service.buyerservice.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping("/getAddressListByLoginUser")
    public List<Address> getAddressListByLoginUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.getAddressListByLoginUser(userName);
    }

}
