package com.chongdao.client.repository;

import com.chongdao.client.entitys.ShopSignInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopSignInfoRepository extends JpaRepository<ShopSignInfo, Integer> {
    List<ShopSignInfo> findByUserId(Integer userId);

    Page<ShopSignInfo> findByNameLike(String shopName, Pageable pageable);
}
