package com.mycomp.core.controller.sellerregister;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.pojo.seller.Seller;
import com.mycomp.core.service.sellerservice.SellerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    // 注入加密工具类(在spring-security.xml中配置的)
    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /*
     * 添加Seller(商家注册账号)
     */
    @RequestMapping("/addSeller")
    public RestResult addSeller(@RequestBody Seller seller) {
        try {
            // 注册时, 对密码进行加密, 数据库中存储的是加密后的密码
            seller.setPassword(passwordEncoder.encode(seller.getPassword()));

            sellerService.addSeller(seller);
            return new RestResult(true, "注册成功, 等待运行商审核");
        } catch (Exception e) {
            return new RestResult(false, "注册失败...");
        }
    }

    /*
     * 获取当前登录的Seller信息
     */
    @RequestMapping("/getSeller")
    public Seller getSeller() {
        return sellerService.getSellerById(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /*
     * 更新Seller信息
     */
    @RequestMapping("/updateSeller")
    public RestResult updateSeller(@RequestBody Seller seller) {
        try {
            sellerService.updateSeller(seller);
            return new RestResult(true, "商家信息更新成功！");
        } catch (Exception e) {
            return new RestResult(false, "商家信息更新失败...");
        }
    }

    /*
     * 检查密码是否正确
     */
    @RequestMapping("/checkPwd")
    public RestResult checkPwd(@RequestParam("password") String password) {
        String sellerPwd = sellerService.getSellerPwd(SecurityContextHolder.getContext().getAuthentication().getName());
        if (passwordEncoder.matches(password, sellerPwd)) {
            return new RestResult(true, "密码正确！");
        } else {
            return new RestResult(false, "密码错误...");
        }
    }

    /*
     * 修改密码
     */
    @RequestMapping("/changePwd")
    public RestResult changePwd(@RequestParam("password") String password) {
        try {
            Seller seller = new Seller();
            seller.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
            seller.setPassword(passwordEncoder.encode(password));
            sellerService.updateSeller(seller);
            return new RestResult(true, "密码修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "密码修改失败...");
        }
    }

}
