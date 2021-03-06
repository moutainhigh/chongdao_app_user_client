package com.chongdao.client.enums;

import lombok.Getter;

@Getter
public enum CouponStatusEnum {

    UP_COUPON(0,"上架满减优惠券"),
    //逻辑删除
    DELETE_COUPON(-1, "删除满减优惠券"),
    DOWN_COUPON(1, "下架满减优惠券"),


    COUPON_FULL_AC(0,"店铺满减"),
    COUPON_TICKET(2,"优惠券"),

    RECEIVED_COUPON_CARD(5001,"您已领取过该优惠券，不能再次领取"),

    COUPON_GOODS(0,"商品以及服务（洗澡美容等）优惠券"),
    COUPON_SERVICE_DELIVERY(1,"配送优惠券"),


    COUPON_PUBLISHED(1,"已发布"),

    COUPON_NOT_EXIST(5002,"优惠券不存在");







    ;

    /** 错误码 */
    private Integer status;

    /** 错误信息 */
    private String message;

    CouponStatusEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

}
