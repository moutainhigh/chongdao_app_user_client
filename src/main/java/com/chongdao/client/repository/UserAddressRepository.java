package com.chongdao.client.repository;

import com.chongdao.client.entitys.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Description 用户收货地址JPA
 * @Author onlineS
 * @Date 2019/4/28
 * @Version 1.0
 **/
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
    Optional<List<UserAddress>> findByUserId(Integer userId);
}