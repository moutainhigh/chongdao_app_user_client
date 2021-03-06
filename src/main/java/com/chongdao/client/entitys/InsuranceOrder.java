package com.chongdao.client.entitys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 医疗险
 * @Author onlineS
 * @Date 2019/5/28
 * @Version 1.0
 **/
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String insuranceOrderNo;

    //投保人信息
    private Integer userId;//用户id
    private String name;
    private String cardType;//01:身份证;02:户口本;03:护照;04:军人证件;05:驾驶执照;06:返乡证;07:港澳身份证;08:工号;09:赴台通行证;10:港澳通行证;15:士兵证;16:外国人永久通行证;25:港澳居民来往内地通行证;26:台湾居民来往内地通行证;31:组织机构代码;37:统一社会信用代码;99:其他;
    private String cardNo;
    private String phone;
    private String email;
    private String address;
    private Integer isSendMsg;//是否发送短信给投保人, 默认为1

    private String idCardFrontPhoto;//投保人身份证正面照片
    private String idCardReversePhoto;//投保人身份证反面照片
    private String bankCardPhoto;//银行卡照片
    private String bankCardNo;//银行卡号

    //被保人信息
    private Integer acceptSeqNo;//被保人序列号, 默认为1
    private String acceptName;
    private String acceptCardType;//01:身份证;02:户口本;03:护照;04:军人证件;05:驾驶执照;06:返乡证;07:港澳身份证;08:工号;09:赴台通行证;10:港澳通行证;15:士兵证;16:外国人永久通行证;25:港澳居民来往内地通行证;26:台湾居民来往内地通行证;31:组织机构代码;37:统一社会信用代码;99:其他;
    private String acceptCardNo;
    private String acceptPhone;
    private String acceptAddress;
    private String acceptMail;

    //受益人
    private Integer beneficiary;//默认为0即本人, 否则为其他即9

    private String rationType;//方案代码-用户所选保险及方案
    private Integer insuranceType;//保险类型, 1:宠物医疗, 2:家庭责任, 3:运输险

    //宠物属性(宠物即被保人)
    private Integer petCardId;//宠物卡片id
    private String petName;//宠物姓名
    private Integer petBreedId;//宠物品种ID
    private String petBreedName;//宠物品种名称
    private BigDecimal petAge;//宠物年龄
    private String petPhoto;//宠物图片->用于审核
    private String petPhotoFlank;//侧面宠物照片
    private String petPhotoFront;//正面面宠物照片
    private String petPhotoReverse;//反面宠物照片
    private Integer medicalInsuranceShopChipId;//选择的宠物芯片的id
    private String shopChipCode;//宠物芯片代码

    private String orderNo;//运输险, 订单id

    //订单相关属性
    private Integer status;//订单状态(-1:删除;0:保存;1:已投保待支付;2:已经支付保单生成(进入30天等待期);3:保障期(过完30天等待期);4:理赔中;5:保障期结束;)
    private Date createTime;//数据创建时间
    private Date applyTime;//保险订单下单时间(付款时间/投保日期)
    private Date auditTime;//审核完成时间(平台投保审核完成日期)
    private Date signBillTime;//签单日期(即与保险公司交互后生成保险单号的时间)

    private Date insuranceEffectTime;//保单起保日期(生效日期)(默认为当前时间往后推24小时的第一个零点, 用户可自主选择30天内的任何一个零点)
    private Date insuranceFailureTime;//保单终保日期(失效截止日期)

    //保单相关属性
    private BigDecimal sumAmount;//保额
    private BigDecimal sumPremium;//保费

    private String payUrl;//支付链接
    private String proposalNo;//投保预下单单号(只有见费出单的才会有, 如ZFO, I9Q)
    private String policyNo;//电子保单号
    private String policyDownloadUrl;//电子保单下载地址
    private String policyImage;//电子保单图片
    private String policyCdxxDownloadUrl;//电子单证下载地址(见费出单时和电子保单下载地址一致, 非见费出单时由我们系统生成)
    private String policyCdxxImage;//电子单证图片(见费出单时和电子保单下载地址一致, 非见费出单时由我们系统生成)
    private String invoiceDownloadUrl;//电子发票下载地址
    private String invoiceImage;//电子发票直接访问地址

    //分销
    private String recommendCode;//推广码

    private Integer isPresent;//是否赠送, -1:否; 1:是;

    @Transient
    private String shopName;
}
