package com.chongdao.client.entitys;

import com.chongdao.client.enums.ResultEnum;
import lombok.Getter;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 用户名 */
    private String name;

    /** 密码 */
    private String password;

    /** 手机号码 */
    private String phone;

    /** 头像 */
    private String icon;

    /** 用户标识（微信） */
    private String openId;

    /** 用户余额（充值） */
    private BigDecimal money = BigDecimal.ZERO;

    /** 集分 */
    private Integer points = 0;

    private Integer status; //待明确

    /** 用户类型（1:app用户,2:微信用户） */
    private Integer type = ResultEnum.USER_TYPE_APP.getCode();

    /** 用户创建时间 */
    private Date createTime = new Date();

    /** 更新时间 */
    private Date updateTime = new Date();

    /** 最后一次登录时间（token鉴别有效期使用） */
    private Date lastLoginTime;


}
