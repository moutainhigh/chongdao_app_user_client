package com.chongdao.client.controller.manage.shop;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.Shop;
import com.chongdao.client.service.ShopManageService;
import com.chongdao.client.utils.LoginUserUtil;
import com.chongdao.client.vo.ResultTokenVo;
import com.chongdao.client.vo.ShopManageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 商家端主页
 * @Author onlineS
 * @Date 2019/5/13
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/shop_manage/")
@CrossOrigin
public class ShopManageController {
    @Autowired
    private ShopManageService shopManageService;

    /**
     * 商家注册
     * @return
     */
    @GetMapping("shopSign")
    public ResultResponse shopSign() {
        return null;
    }

    /**
     * 商家登录
     * @return
     */
    @GetMapping("shopLogin")
    public ResultResponse shopLogin(String name, String password) {
        return shopManageService.shopLogin(name, password);
    }

    /**
     * 商家登出
     * @param token
     * @return
     */
    @GetMapping("shopLogout")
    public ResultResponse shopLogout(String token) {
        // token缓存在前端  登出建议前端主动销毁token, 页面直接跳转到登录页
//        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
//        //TODO 销毁token
        return null;
    }

    /**
     * 获取店铺基本信息
     * @param token
     * @return
     */
    @GetMapping("getShopInfo")
    public ResultResponse<ShopManageVO> getShopInfo(String token) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return shopManageService.getShopInfo(tokenVo.getUserId());
    }

    /**
     * 保存店铺信息
     * @param s
     * @return
     */
    @PostMapping("saveShopInfo")
    public ResultResponse saveShopInfo(@RequestBody Shop s) {
        return shopManageService.saveShopInfo(s);
    }

    /**
     * 获取店铺销售统计
     * @param token
     * @return
     */
    @GetMapping("getShopOrderStatistics")
    public ResultResponse getShopOrderStatistics(String token) {
        return null;
    }

    @PostMapping("saveShopConfig")
    public ResultResponse saveShopConfig(Integer shopId, Byte isAutoAccept, Integer isInService, String phone) {
        return shopManageService.saveShopConfig(shopId, isAutoAccept, isInService, phone);
    }

    @PostMapping("downloadQrCodeImg")
    public ResponseStatus downloadQrCodeImg(Integer shopId) {
        return null;
    }

    /**
     * 更改商家目标状态
     * @param shopId
     * @param targetStatus
     * @return
     */
    @PostMapping("changeShopStopStatus")
    public ResultResponse changeShopStopStatus(Integer shopId, Integer targetStatus) {
        return shopManageService.changeShopStopStatus(shopId, targetStatus);
    }
}
