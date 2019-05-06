package com.chongdao.client.vo;

import lombok.Data;


@Data
public class OrderCommonVO {

    //0.商品 1.服务
    private Integer isService;

    // 1.单程,2.双程
    private Integer serviceType;

    private Integer cardId;

    private Integer couponId;

    //1.支付宝 2.微信
    private Integer payType;

    //接地址
    private Integer receiveAddressId;

    //送地址
    private Integer deliverAddressId;

    //单程 服务类型:1.家到宠物店，2.宠物店到家
    private Integer singleServiceType;

}