package com.chongdao.client.controller.user;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.service.UserRegInfoService;
import com.chongdao.client.service.UserService;
import com.chongdao.client.utils.LoginUserUtil;
import com.chongdao.client.vo.ResultTokenVo;
import com.chongdao.client.vo.UserSettingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 我的设置
 * @Author onlineS
 * @Date 2019/5/6
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/setting/")
public class UserSettingController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRegInfoService userRegInfoService;

    /**
     * 获取用户设置信息
     * @param token
     * @return
     */
    @GetMapping("getUserSettingInfo")
    public ResultResponse<UserSettingVO> getUserSettingInfo(String token) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return userService.getUserSettingInfo(tokenVo.getUserId());
    }

    /**
     *  保存用户设置信息
     * @param uso
     * @return
     */
    @PostMapping("saveUserSetting")
    public ResultResponse saveUserSetting(@RequestBody UserSettingVO uso){
        return userService.saveUserSetting(uso);
    }

    /**
     * 保存/更新用户设备标识
     * @param userId
     * @param regId
     * @param alias
     * @param userAccount
     * @param type 1:安卓, 2:IOS
     * @return
     */
    @PostMapping("saveUserRegInfo")
    public ResultResponse saveUserRegInfo(Integer userId, String regId, String alias, String userAccount, Integer type) {
        return userRegInfoService.addUserRegInfo(userId, regId, alias, userAccount, type);
    };
}
