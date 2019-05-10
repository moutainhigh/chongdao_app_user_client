package com.chongdao.client.controller.user;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.service.UserShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 用户分享
 * @Author onlineS
 * @Date 2019/5/10
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/share")
public class UserShareController {
    @Autowired
    private UserShareService userShareService;

    /**
     * 分享回调, app端分享是由前端调起分享api, 完成分享后调用此接口完成业务逻辑即可
     * @param userId
     * @param type
     * @return
     */
    @GetMapping("/userShareCallBack")
    public ResultResponse userShareCallBack(Integer userId, Integer type) {
        return userShareService.userShare(userId, type);
    }
}