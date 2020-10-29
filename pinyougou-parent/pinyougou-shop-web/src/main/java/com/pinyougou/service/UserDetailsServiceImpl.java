package com.pinyougou.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>pinyougou</h3>
 * <p>security验证类</p>
 *
 * @author : 沈疴
 * @date : 2020-10-09 13:48
 **/
public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了userDetailsServiceImpl");

        /*创建角色列表*/
        List<GrantedAuthority> grantAuth = new ArrayList<>();
        /*向角色列表中添加角色'ROLE_SELLER'*/
        grantAuth.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        /*获取商家对象*/
        TbSeller seller = sellerService.findOne(username);
        if (seller!=null){
            if (seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),grantAuth);
            }else {
                return null;
            }
        }else {
            return  null;
        }
    }
}
