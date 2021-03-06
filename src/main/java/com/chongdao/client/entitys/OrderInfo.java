package com.chongdao.client.entitys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderNo;

    private Integer userId;

    private Integer shopId;

    private BigDecimal goodsPrice;

    private BigDecimal originGoodsPrice;

    private BigDecimal servicePrice;

    private BigDecimal originServicePrice;

    private BigDecimal totalDiscount  = BigDecimal.ZERO;

    private BigDecimal payment;

    private Integer follow;

    private Integer serviceType;//1:双程;2:单程;3:到店;

    private Integer cardId;


    private String remark;

    private Date createTime;

    private Date updateTime;

    private Date deliverTime;

    private Date receiveTime;

    private Date paymentTime;

    private Integer paymentType;

    private Date shopReceiveTime;

    private Date shopFinishTime;

    private Integer expressId;

    private Date expressReceiveTime;

    private Date expressFinishTime;

    private Integer orderStatus;

    private Integer receiveAddressId;

    private Integer deliverAddressId;

    private BigDecimal reward;

    private Integer isService;

    private Integer couponId;

    private String areaCode;


    //单程服务类型:1.家到宠物店，2.宠物店到家
    private Integer singleServiceType;

    //0 不拼单 1 拼单
    private Integer enabledSpell;

    private String petId;

    private Integer petCount;

    private BigDecimal insurancePrice;


    public OrderInfo(Integer id, Integer serviceType, String orderNo, Integer userId, Integer shopId, Integer orderStatus, BigDecimal goodsPrice,BigDecimal originGoodsPrice,
                     BigDecimal servicePrice, BigDecimal originServicePrice,BigDecimal totalDiscount, Integer isService, BigDecimal payment, Integer follow, Integer cardId, String remark, String areaCode,
                     Date deliverTime, Date paymentTime, Integer paymentType, Date shopReceiveTime, Date shopFinishTime, Integer expressId, Date expressReceiveTime, Date expressFinishTime,
                     BigDecimal reward, Integer receiveAddressId, Integer deliverAddressId, Date receiveTime, Integer couponId, Date createTime, Integer singleServiceType,
                     Date updateTime, Integer enabledSpell, String petId, Integer petCount,BigDecimal insurancePrice) {
        this.id = id;
        this.serviceType = serviceType;
        this.orderNo = orderNo;
        this.userId = userId;
        this.shopId = shopId;
        this.orderStatus = orderStatus;
        this.goodsPrice = goodsPrice;
        this.originGoodsPrice = originGoodsPrice;
        this.servicePrice = servicePrice;
        this.originServicePrice = originServicePrice;
        this.totalDiscount = totalDiscount;
        this.isService = isService;
        this.payment = payment;
        this.follow = follow;
        this.cardId = cardId;
        this.remark = remark;
        this.deliverTime = deliverTime;
        this.paymentTime = paymentTime;
        this.paymentType = paymentType;
        this.shopReceiveTime = shopReceiveTime;
        this.shopFinishTime = shopFinishTime;
        this.expressId = expressId;
        this.expressReceiveTime = expressReceiveTime;
        this.expressFinishTime = expressFinishTime;
        this.reward = reward;
        this.receiveAddressId = receiveAddressId;
        this.deliverAddressId = deliverAddressId;
        this.receiveTime = receiveTime;
        this.couponId = couponId;
        this.createTime = createTime;
        this.singleServiceType = singleServiceType;
        this.updateTime = updateTime;
        this.areaCode = areaCode;
        this.enabledSpell = enabledSpell;
        this.insurancePrice = insurancePrice;
        this.petId = petId;
        this.petCount = petCount;
    }


}