package com.mycomp.core.controller.userregister;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.exceptions.WrongSmscodeException;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.pojo.user.User;
import com.mycomp.core.service.userservice.UserService;
import com.mycomp.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("/addUser")
    public RestResult addUser(@RequestParam("smscode") String smscode,
                              @RequestBody User user) {
        try {
            userService.addUser(smscode, user);
            return new RestResult(true, "注册成功！");
        } catch (WrongSmscodeException e) {
            return new RestResult(false, "短信验证码输入有误...");
        } catch (Exception e) {
            return new RestResult(false, "注册失败...");
        }
    }

}
