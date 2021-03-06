package com.chongdao.client.service;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.Shop;
import com.chongdao.client.vo.ShopVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;

public interface ShopService {
    /**
     * 首页商铺展示
     *
     * @param categoryId
     * @param proActivities
     * @param orderBy
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultResponse<PageInfo> list(Integer userId, String categoryId, String proActivities, String orderBy, Double lng, Double lat, String areaCode, int pageNum, int pageSize);

    /**
     * 获取商店列表(管理员)
     *
     * @param managementId
     * @param shopName
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultResponse<PageInfo> getShopDataList(Integer managementId, String shopName, Integer pageNum, Integer pageSize);

    /**
     * 获取店铺
     *
     * @param shopId
     * @return
     */
    ResultResponse getShopById(Integer shopId, Double lat, Double lng, Integer userId);

    ResultResponse addShop(Shop shop);

    /**
     * 获取店铺商品
     * @param shopId
     * @param goodsTypeId
     * @return
     */
    ResultResponse getShopService(Integer shopId, Integer goodsTypeId, Integer userId);

    /**
     * 获取店铺所有订单评价以及店铺总评价
     *
     * @param shopId
     * @return
     */
    ResultResponse getShopEvalAll(Integer shopId);

    /**
     * 更新商店余额
     *
     * @param shopId
     * @param money
     * @return
     */
    ResultResponse updateShopMoney(Integer shopId, BigDecimal money);

    /**
     * 搜索店铺
     * @param keyword
     * @return
     */
    ResultResponse<List<ShopVO>> pageQuery(String keyword, String areaCode,String categoryId,Integer userId,
                                           String  proActivities,String orderBy,Double lng,Double lat);

    /**
     * 关注店铺
     *
     * @param userId
     * @param shopId
     * @return
     */
    ResultResponse concernShop(Integer userId, Integer shopId);

    /**
     * 查看关注店铺列表
     *
     * @param userId
     * @return
     */
    ResultResponse queryConcernShopList(Integer userId, Double lng, Double lat);

    /**
     * 地图商家数据
     *
     * @param lng
     * @param lat
     * @return
     */
    ResultResponse listGeo(Double lng, Double lat, String areaCode);

    /**
     * 获取店铺信息(PC端)
     *
     * @param token
     * @return
     */
    ResultResponse getShopInfo(String token);

    /**
     * 获取3公里内的医院类店铺
     * @param lng
     * @param lat
     * @param areaCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultResponse getInsranceShopLimit3KM(Double lng, Double lat, String areaCode, Integer pageNum, Integer pageSize);
}
