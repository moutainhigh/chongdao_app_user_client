package com.chongdao.client.controller.user;


import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.Package;
import com.chongdao.client.service.PackageService;
import com.chongdao.client.service.UserCardService;
import com.chongdao.client.service.UserService;
import com.chongdao.client.utils.LoginUserUtil;
import com.chongdao.client.vo.CardUserVo;
import com.chongdao.client.vo.ResultTokenVo;
import com.chongdao.client.vo.UserLoginVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private PackageService packageService;

    /**
     * 用户登录接口
     * @return
     */
    @PostMapping("login")
    public ResultResponse<UserLoginVO> login(@RequestParam("phone") String phone,@RequestParam("code") String code){
        return userService.login(phone, code);
    }

    /**
     * 用户通过推广链接完成注册
     * @return
     */
    @PostMapping("recommendSign")
    public ResultResponse recommendSign(String phone, Integer type, String recommendId) {
        return null;
    }

    /**
     * 获取用户优惠券
     * @param token
     * @param type 1:优惠券;2:配送券
     * @return
     */
    @GetMapping("getUserCard")
    public ResultResponse<PageInfo<CardUserVo>> getUserCard(String token, Integer type, Integer pageNum, Integer pageSize) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return userCardService.getUserCard(tokenVo.getUserId(), type, pageNum, pageSize);
    }

    /**
     * 获取礼包列表
     * @return
     */
    @GetMapping("getPackage")
    public ResultResponse<List<Package>> getPackage() {
        return packageService.getPackageList();
    }
}
