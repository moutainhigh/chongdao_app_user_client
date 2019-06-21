package com.chongdao.client.repository;

import com.chongdao.client.entitys.GoodsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsTypeRepository extends JpaRepository<GoodsType,Integer> {
    List<GoodsType> findAllByCategoryIdAndStatus(Integer categoryId, Integer status);
}
