package com.chongdao.client.service.iml;

import com.chongdao.client.common.GuavaCache;
import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.exception.PetException;
import com.chongdao.client.service.SmsService;
import com.chongdao.client.utils.sms.SMSUtil;
import com.chongdao.client.utils.sms.SmsSender253;
import com.chongdao.client.utils.sms.SmsVariableRequest;
import com.chongdao.client.utils.sms.SmsVariableResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SMSUtil smsUtil;

    private static final Random random = new Random();
    private static final String[] NUMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final static String SMS_CODE_CONTENT_PREFIX = "SMS::CODE::CONTENT";

    @Autowired
    private Gson gson;

    /**
     * 发送验证码到指定手机 并 缓存验证码 10分钟 及 请求间隔时间1分钟
     * @param telephone
     * @return
     */
    @Override
    public ResultResponse<String> sendSms(String telephone) {
        String gapKey = "SMS::CODE::INTERVAL::" + telephone;
 /*       String result = GuavaCache.getKey(gapKey);
        if (result != null) {
            return  ResultResponse.createByErrorMessage( "请求次数太频繁");
        }*/
        //生成验证码
        String code = generateRandomSmsCode();
        String params = telephone+","+code;

        String report= "true";

        SmsVariableRequest smsVariableRequest=new SmsVariableRequest(this.smsUtil.getAccount(), this.smsUtil.getPassword(),
                this.smsUtil.getSmsIdentifyCode(), params, report);

        String requestJson = gson.toJson(smsVariableRequest);

        String response = SmsSender253.sendSmsByPost(this.smsUtil.getUrl(), requestJson);

        SmsVariableResponse resp = gson.fromJson(response, SmsVariableResponse.class);
        boolean success = false;
        //请求成功
        if ("0".equals(resp.getCode())){
            success= true;
        }
        if (success) {
            //将验证码key存入guava缓存中
            GuavaCache.setKey(gapKey, code);
            GuavaCache.setKey(SMS_CODE_CONTENT_PREFIX + telephone, code);
            return ResultResponse.createBySuccess(code);
        } else {
            throw new PetException(ResultEnum.ERROR);
        }
    }

    /**
     * 获取缓存中的验证码
     * @param telephone
     * @return
     */
    @Override
    public String getSmsCode(String telephone) {
        return GuavaCache.getKey(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    /**
     * 移除指定手机号的验证码缓存
     */
    @Override
    public void remove(String telephone) {

    }

    /**
     * 6位验证码生成器
     * @return
     */
    private static String generateRandomSmsCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            sb.append(NUMS[index]);
        }
        return sb.toString();
    }


    ////////////////////////////订单流程短信服务///////////////////////////////////////////////

    /**
     * 接单-配送员短信通知
     * @param orderNo
     * @param phoneList
     * @return
     */
    @Override
    public void acceptOrderMsgExpressSender(String orderNo, String shopName, List<String> phoneList) {
        String params = assemblePhoneList(orderNo, shopName, phoneList);
        String report= "true";
        customMsgSender(this.smsUtil.getExpressNewOrder(), params, report);
    }

    /**
     * 接单-店铺短信通知
     * @param orderNo
     * @param shopName
     * @param telephone
     * @return
     */
    @Override
    public void acceptOrderMsgShopSender(String orderNo, String shopName, String telephone) {
        String params = telephone+","+ orderNo + "," + shopName;
        String report= "true";
        customMsgSender(this.smsUtil.getShopAcceptRefund(), params, report);
    }

    /**
     * 商家同意退款-管理员短信通知
     * @param orderNo
     * @param shopName
     * @param phoneList
     */
    @Override
    public void refundOrderMsgAdminSender(String orderNo, String shopName, List<String> phoneList) {
        String params = assemblePhoneList(orderNo, shopName, phoneList);
        String report= "true";
        customMsgSender(this.smsUtil.getShopAgreeRefundOrder(), params, report);
    }

    /**
     * 拒单-用户短信通知
     * @param orderNo
     * @param shopName
     * @param telephone
     */
    @Override
    public void refuseOrderMsgUserSender(String orderNo, String shopName, String telephone) {
        String params = telephone+","+ shopName + "," + orderNo;
        String report= "true";
        customMsgSender(this.smsUtil.getShopRefuseOrder(), params, report);
    }

    private String assemblePhoneList(String orderNo, String shopName, List<String> phoneList) {
        String params = "";
        for (int i = 0; i < phoneList.size(); i ++) {
            params = params + phoneList.get(i) + "," + shopName +","+ orderNo;
            if (i < phoneList.size() - 1) {
                params = params + ";";
            }
        }
        return params;
    }

    private void customMsgSender(String msg, String params, String report) {
        SmsVariableRequest smsVariableRequest=new SmsVariableRequest(this.smsUtil.getAccount(), this.smsUtil.getPassword(),
                msg, params, report);
        String requestJson = gson.toJson(smsVariableRequest);
        String response = SmsSender253.sendSmsByPost(this.smsUtil.getUrl(), requestJson);
        gson.fromJson(response, SmsVariableResponse.class);
    }
}
