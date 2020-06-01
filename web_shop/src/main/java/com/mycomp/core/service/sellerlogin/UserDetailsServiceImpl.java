package com.mycomp.core.service.sellerlogin;

import com.mycomp.core.pojo.seller.Seller;
import com.mycomp.core.service.sellerservice.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    /*
     * 通过bean的形式注入远程服务(不使用@Reference注解注入远程服务);
     * 详见spring-security.xml
     */
    private SellerService sellerService;

    /*
     * 通过配置bean标签的property进行属性注入需要提供setter方法
     */
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*
         * 定义权限的集合;
         * 这里是直接写死的, 默认每一个Seller用户都有这个权限(即spring-security.xml中配置的那个权限名称);
         * 实际开发中, 应该是在数据库中维护每个用户的权限, 然后在这里读出来, 直接去做认证;
         * 参考PermissionPro项目中的Shiro自定义Realm;
         */
        List<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        if (username == null || username.equals("")) {
            return null;
        }

        /*
         * 到数据库中查询相关用户
         */
        Seller seller = sellerService.getSellerById(username);
        if (seller != null) {
            // 只有审核通过的商家才能登陆(status为"1")
            if (seller.getStatus().equals("1")) {
                // 密码前拼接"{noop}"表示不加密
                // return new User(username, "{noop}" + seller.getPassword(), authList);
                return new User(username, seller.getPassword(), authList);
            }
        }

        return null;
    }

}
