package com.mycomp.core.controller.login;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getLoginName")
    public Map<String, Object> getLoginName() {
        /*
         * SecurityContextHolder.getContext()类似于Shrio中的SecurityUtils.getSubject(),
         * 都是去获取当前登录的用户, 通过当前用户(SecurityContext对象)就可以获得各种信息,
         * 比如认证信息和授权信息;
         */
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String username = securityContext.getAuthentication().getName();
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", username);
        return map;
    }

}
