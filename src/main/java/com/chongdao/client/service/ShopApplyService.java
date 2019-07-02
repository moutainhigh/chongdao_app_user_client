package com.chongdao.client.service;

import com.chongdao.client.common.ResultResponse;

import java.math.BigDecimal;
import java.util.Date;

public interface ShopApplyService {
    ResultResponse addShopApplyRecord(Integer shopId, BigDecimal applyMoney, String applyNote);

    ResultResponse acceptShopApplyRecord(Integer shopApplyId, BigDecimal realMoney, String checkNote);

    ResultResponse refuseShopApplyRecord(Integer shopApplyId, String checkNote);

    ResultResponse getShopApplyList(Integer shopId, String shopName, Date startDate, Date endDate, Integer pageNum, Integer pageSize);
}
