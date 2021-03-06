package com.chongdao.client.service;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.Banner;

import java.util.List;

/**
 * @Description 轮播图
 * @Author onlineS
 * @Date 2019/4/23
 * @Version 1.0
 **/
public interface BannerService {
    ResultResponse<List<Banner>> getBannerByAreaCode(String areaCode);

    ResultResponse getBannerList(String token, Integer status);

    ResultResponse saveBanner(Banner banner);

    ResultResponse deleteBanner(Integer id);
}
