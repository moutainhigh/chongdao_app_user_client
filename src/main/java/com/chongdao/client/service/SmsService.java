package com.chongdao.client.service;

import com.chongdao.client.common.ResultResponse;

import java.util.List;

/**
 * 短信通知服务(标准模版:msg+phone+shopName/null+orderNo, 非标准模版请单独写方法)
 */
public interface SmsService {

    /**
     * 发送验证码到指定手机 并 缓存验证码 10分钟 及 请求间隔时间1分钟
     * @param telephone
     * @return
     */
    ResultResponse<String> sendSms(String telephone);

    /**
     * 获取缓存中的验证码
     * @param telephone
     * @return
     */
    String getSmsCode(String telephone);

    /**
     * 移除指定手机号的验证码缓存
     */
    void remove(String telephone);

    /**
     * 通用短信通知(单个人)
     *
     * @param msg
     * @param shopName
     * @param orderNo
     * @param phone
     */
    void customOrderMsgSenderSimple(String msg, String shopName, String orderNo, String phone);

    /**
     * 通用短信通知(单个人, 只有订单号)
     * @param msg
     * @param orderNo
     * @param phone
     */
    void customOrderMsgSenderSimpleNoShopName(String msg, String orderNo, String phone);

    /**
     * 通用短信通知(批量)
     *
     * @param msg
     * @param shopName
     * @param orderNo
     * @param phoneList
     */
    void customOrderMsgSenderPatch(String msg, String shopName, String orderNo, List<String> phoneList);

    /**
     * 通用短信通知(批量, 只有订单号)
     * @param msg
     * @param orderNo
     * @param phoneList
     */
    void customOrderMsgSenderPatchNoShopName(String msg, String orderNo, List<String> phoneList);

    /**
     * 获取订单所在地区的配送员号码列表
     * @param orderId
     * @return
     */
    List<String> getExpressPhoneListByOrderId(Integer orderId);

    /**
     * 获取负责订单的配送员号码
     * @param orderId
     * @return
     */
    String getExpressPhoneByOrderId(Integer orderId);

    /**
     * 获取订单所在地区的管理员号码列表
     * @param orderId
     * @return
     */
    List<String> getAdminPhoneListByOrderId(Integer orderId);

    /**
     * 获取商家联系号码
     * @param orderId
     * @return
     */
    String getShopPhoneByOrderId(Integer orderId);

    /**
     * 获取订单联系人号码列表
     * @param orderId
     * @return
     */
    List<String> getUserPhoneListByOrderId(Integer orderId);
}
