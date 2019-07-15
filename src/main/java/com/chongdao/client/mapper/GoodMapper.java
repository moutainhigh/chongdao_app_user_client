package com.chongdao.client.mapper;

import com.chongdao.client.entitys.Good;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface GoodMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Good record);

    int insertSelective(Good record);

    Good selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Good record);

    int updateByPrimaryKey(Good record);


    /**
     * 查询所有上架状态的商品
     * @return
     */
    List<Good> selectList();

    List<Good> selectByName(@Param("goodsName") String goodsName, @Param("brandId")Integer brandId,@Param("goodsTypeId")Integer goodsTypeId,@Param("scopeId")Integer scopeId,@Param("petCategoryId")Integer petCategoryId,@Param("orderBy") String orderBy);

    List<Good> getFavouriteGoodList(@Param("userId") Integer userId);

    List<Good> selectListByShopId(@Param("shopId")Integer shopId);

    List<Good> selectByShopIdAndCategoryId(@Param("shopId") Integer shopId, @Param("categoryId")Integer categoryId);



    //----------------------------------商户端------------------------------------------//
    List<Good> getGoodList(@Param("shopId")Integer shopId, @Param("goodsTypeId") Integer goodsTypeId, @Param("goodName") String goodName);


    void updateGoodsStatus(@Param("goodId") Integer goodId, @Param("status") Integer status);

    void goodsDiscount(@Param("shopId")Integer shopId, @Param("goodsTypeId")Integer goodsTypeId, @Param("discount")Double discount);
}