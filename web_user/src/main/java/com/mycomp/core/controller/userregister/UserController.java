package com.mycomp.core.controller.userregister;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.userservice.UserService;
import com.mycomp.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendCode")
    public RestResult sendCode(@RequestParam("phone") String phone) {
        try {
            if (phone == null || phone.equals("")) {
                return new RestResult(false, "手机号不能为空...");
            }
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                return new RestResult(false, "手机号格式不正确...");
            }
            userService.sendCode(phone);
            return new RestResult(true, "验证码短信已发送，请注意查收！");
        } catch (Exception e) {
            return new RestResult(false, "验证码短信发送失败...");
        }
    }

//    @RequestMapping("/addUser")
//    public RestResult addUser(@RequestBody User user) {
//        try {
//            // 注册时, 对密码进行加密, 数据库中存储的是加密后的密码
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//            sellerService.addSeller(seller);
//            return new RestResult(true, "注册成功！");
//        } catch (Exception e) {
//            return new RestResult(true, "注册失败...");
//        }
//    }

}
