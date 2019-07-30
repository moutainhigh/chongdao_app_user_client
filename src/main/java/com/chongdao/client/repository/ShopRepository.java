package com.chongdao.client.repository;

import com.chongdao.client.entitys.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Description 商铺JPA
 * @Author onlineS
 * @Date 2019/5/13
 * @Version 1.0
 **/
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    Optional<Shop> findByAccountName(String name);

    Optional<List<Shop>> findByAccountNameAndPassword(String accountName, String password);

    Page<Shop> findAllByStatusNot(Integer status,Pageable pageable);

    Page<Shop> findByShopNameLikeAndStatusNot(String shopName,Integer status,Pageable pageable);

    Optional<List<Shop>> findAllById(List<Integer> shopIds);
}
