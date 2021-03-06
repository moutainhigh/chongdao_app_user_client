package com.chongdao.client.service.iml;

import com.chongdao.client.entitys.User;
import com.chongdao.client.entitys.UserXcx;
import com.chongdao.client.repository.UserRepository;
import com.chongdao.client.repository.UserXcxRepository;
import com.chongdao.client.service.CouponService;
import com.chongdao.client.service.UserXcxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/12/17
 * @Version 1.0
 **/
@Service
public class UserXcxServiceImpl implements UserXcxService {
    @Autowired
    private UserXcxRepository userXcxRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponService couponService;

    @Override
    public boolean checkIsXcxOldUser(String phone) {
        List<UserXcx> list = userXcxRepository.findByPhone(phone);
        if(list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void addServiceCpnToXcxUser(String phone) {
        User user = userRepository.findByPhone(phone);
        System.out.println("user:>>>>>>>" + user.getId());
        if(user != null) {
            couponService.presentService30Card(user.getId());
        }
    }
}
