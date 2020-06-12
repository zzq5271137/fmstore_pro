package com.mycomp.core.service.userlogin;

/*
 * 自定义认证类:
 * 在之前这里负责用户名密码的校验工作, 并给当前用户赋予对应的访问权限;
 * 现在CAS和SpringSecurity集成, 集成后, 用户名密码的校验工作交给CAS完成;
 * 进入到这里类的方法中的都是已经成功认证的用户, 所以这里只需要给登录过的用户赋予对应的访问权限就可以;
 */

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*
         * 定义权限的集合;
         * 这里是直接写死的, 默认每一个User用户都有这个权限(即spring-security.xml中配置的那个权限名称);
         * 实际开发中, 应该是在数据库中维护每个用户的权限, 然后在这里读出来, 直接去做认证;
         * 参考PermissionPro项目中的Shiro自定义Realm;
         */
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, "", authorityList);
    }

}
