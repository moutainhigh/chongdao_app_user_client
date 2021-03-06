package com.chongdao.client.service.iml;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.*;
import com.chongdao.client.enums.DeductPercentEnum;
import com.chongdao.client.enums.RecommendTypeEnum;
import com.chongdao.client.repository.*;
import com.chongdao.client.service.CashAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/10/17
 * @Version 1.0
 **/
@Service
public class CashAccountServiceImpl implements CashAccountService {
    @Autowired
    private ManagementRepository managementRepository;
    @Autowired
    private SuperAdminBillRepository superAdminBillRepository;
    @Autowired
    private AreaBillRepository areaBillRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ShopBillRepository shopBillRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTransRepository userTransRepository;
    @Autowired
    private OrderTranRepository orderTranRepository;
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private OrderInfoReRepository orderInfoReRepository;

    @Override
    @Transactional
    public ResultResponse customOrderPayedCashIn(OrderInfo orderInfo) {
        BigDecimal servicePrice = conversionNullBigDecimal(orderInfo.getServicePrice());
        BigDecimal goodsPrice = conversionNullBigDecimal(orderInfo.getGoodsPrice());
        BigDecimal insurancePrice = conversionNullBigDecimal(orderInfo.getInsurancePrice());
        BigDecimal total = servicePrice.add(goodsPrice).add(insurancePrice);
        //商家的钱+系统的钱 入地区账户
        String areaCode = orderInfo.getAreaCode();
        Management areaAdmin = getAreaAdmin(areaCode);
        managementMoneyDeal(areaAdmin, total);
        //生成流水记录
        generateAreaBill(orderInfo.getId(), orderInfo.getShopId(), areaCode, total, "订单消费", 1);
        //商家的钱+系统的钱 入超级账户
        Management superAdmin = getSuperAdmin();
        managementMoneyDeal(superAdmin, total);
        //生成流水记录
        generateSuperAdminBill(orderInfo.getId(), orderInfo.getShopId(), orderInfo.getAreaCode(), total, "订单消费", 1);
        //生成订单资金交易记录
        generateOrderTrans(orderInfo.getId(), orderInfo.getPayment(), orderInfo.getOrderNo(), "订单消费");
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse customOrderCashIn(OrderInfo orderInfo) {
        BigDecimal servicePrice = conversionNullBigDecimal(orderInfo.getServicePrice());
        BigDecimal goodsPrice = conversionNullBigDecimal(orderInfo.getGoodsPrice());
        BigDecimal insurancePrice = conversionNullBigDecimal(orderInfo.getInsurancePrice());
        BigDecimal total = servicePrice.add(goodsPrice).add(insurancePrice);
        Integer shopId = orderInfo.getShopId();
        Shop shop = shopRepository.findById(shopId).orElse(null);
        //商家的钱入商家, 并生成流水记录
        BigDecimal toShopRealMoney = getDeductedPrice(goodsPrice, DeductPercentEnum.CUSTOM_ORDER_DEDUCT.getCode());//扣除手续费
        shopMoneyDeal(shop, toShopRealMoney);
        generateShopBill(orderInfo.getId(), shopId, toShopRealMoney, "订单消费", 1);
        //商家的钱+系统的钱 入地区账户
        String areaCode = orderInfo.getAreaCode();
        Management areaAdmin = getAreaAdmin(areaCode);
        managementMoneyDeal(areaAdmin, total);
        //生成流水记录
        generateAreaBill(orderInfo.getId(), orderInfo.getShopId(), areaCode, total, "订单消费", 1);
        //商家的钱+系统的钱 入超级账户
        Management superAdmin = getSuperAdmin();
        managementMoneyDeal(superAdmin, total);
        //生成流水记录
        generateSuperAdminBill(orderInfo.getId(), orderInfo.getShopId(), orderInfo.getAreaCode(), total, "订单消费", 1);
        //生成订单资金交易记录
        generateOrderTrans(orderInfo.getId(), orderInfo.getPayment(), orderInfo.getOrderNo(), "订单消费");
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse customReOrderCashIn(OrderInfoRe orderInfoRe) {
        BigDecimal payment = orderInfoRe.getPayment();
        Integer shopId = orderInfoRe.getShopId();
        Shop shop = shopRepository.findById(shopId).orElse(null);
        BigDecimal toShopRealMoney = getDeductedPrice(payment, DeductPercentEnum.CUSTOM_ORDER_DEDUCT.getCode());
        String orderNo = orderInfoRe.getOrderNo();
        OrderInfo orderInfo = orderInfoRepository.findByOrderNo(orderNo);
        //商家的钱入商家, 并生成流水记录
        shopMoneyDeal(shop, toShopRealMoney);
        generateShopBill(orderInfo.getId(), shopId, toShopRealMoney, "追加订单", 5);
        //商家的钱+系统的钱 入地区账户
        String areaCode = orderInfo.getAreaCode();
        Management areaAdmin = getAreaAdmin(areaCode);
        managementMoneyDeal(areaAdmin, payment);
        //生成流水记录
        generateAreaBill(orderInfo.getId(), orderInfo.getShopId(), areaCode, payment, "追加订单", 5);
        //商家的钱+系统的钱 入超级账户
        Management superAdmin = getSuperAdmin();
        managementMoneyDeal(superAdmin, payment);
        //生成流水记录
        generateSuperAdminBill(orderInfo.getId(), orderInfo.getShopId(), orderInfo.getAreaCode(), payment, "追加订单", 5);
        //生成订单资金交易记录
        generateOrderTrans(orderInfo.getId(), orderInfo.getPayment(), orderInfo.getOrderNo(), "追加订单");
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse insuranceFeeCashIn(InsuranceFeeRecord insuranceFeeRecord) {
        System.out.println("进入资金流转方法");
        BigDecimal money = insuranceFeeRecord.getMoney();
        BigDecimal toShopMoney = money;
        Integer type = insuranceFeeRecord.getType();//是否保险类
        Integer shopId = insuranceFeeRecord.getShopId();
        Shop shop = shopRepository.findById(shopId).orElse(null);
        //钱入商家(医院), 并生成流水记录
        if (type == 1) {
            //保险类
            //存入时扣除费用
            toShopMoney = getDeductedPrice(money, DeductPercentEnum.INSURANCE_FEE_DEDUCT.getCode());
        }
        shopMoneyDeal(shop, toShopMoney);
        generateShopBill(insuranceFeeRecord.getId(), shopId, money, "医疗费用订单", 4);
        //钱入地区账户, 并生成流水记录
        generateAreaBill(insuranceFeeRecord.getId(), shopId, shop.getAreaCode(), money, "医疗费用订单", 4);
        Management areaAdmin = getAreaAdmin(shop.getAreaCode());
        managementMoneyDeal(areaAdmin, money);
        //钱入超级账户, 并生成流水记录
        generateSuperAdminBill(insuranceFeeRecord.getId(), shopId, shop.getAreaCode(), money, "医疗费用订单", 4);
        Management superAdmin = getSuperAdmin();
        managementMoneyDeal(superAdmin, money);
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse couponCashIn(Coupon coupon) {
        return null;
    }

    @Override
    @Transactional
    public ResultResponse customOrderCashRefund(OrderInfo orderInfo) {
        String comment = "订单退款";
        Integer id = orderInfo.getId();
        //当产生退款时, 根据各账户的流水入账记录进行资金扣除, 如果没有入账记录, 那么无需扣除
        //商家账户资金扣除, 并生成流水记录
        List<ShopBill> sbList = shopBillRepository.findByOrderIdAndTypeOrTypeAndPriceGreaterThan(id, 1, 5, new BigDecimal(0));//查询出入账记录(正常订单和追加订单)
        if (sbList.size() > 0) {
            for(ShopBill inShopBill : sbList) {
                Integer shopId = inShopBill.getShopId();
                Shop shop = shopRepository.findById(shopId).orElse(null);
                BigDecimal outMoney = inShopBill.getPrice().multiply(new BigDecimal(-1));
                shopMoneyDeal(shop, outMoney);
                if(inShopBill.getType() == 5) {
                    comment = "订单退款(追加订单部分)";
                }
                generateShopBill(id, shopId, outMoney, comment, 2);
            }
        }
        //从地区账户资金扣除, 并生成流水记录
        List<AreaBill> abList = areaBillRepository.findByOrderIdAndTypeOrTypeAndPriceGreaterThan(id, 1, 5, new BigDecimal(0));//查询出入账记录(正常订单和追加订单)
        if (abList.size() > 0) {
            for(AreaBill inAreaBill : abList) {
                BigDecimal outMoney = inAreaBill.getPrice().multiply(new BigDecimal(-1));
                String areaCode = inAreaBill.getAreaCode();
                Management areaAdmin = getAreaAdmin(areaCode);
                managementMoneyDeal(areaAdmin, outMoney);
                if(areaAdmin.getType() == 5) {
                    comment = "订单退款(追加订单部分)";
                }
                generateAreaBill(id, inAreaBill.getShopId(), areaCode, outMoney, comment, 2);
            }
        }
        //从超级账户资金扣除, 并生成流水记录
        List<SuperAdminBill> sabList = superAdminBillRepository.findByOrderIdAndTypeOrTypeAndPriceGreaterThan(id, 1, 5, new BigDecimal(0));//查询出入账记录(正常订单和追加订单)
        if (sabList.size() > 0) {
            for(SuperAdminBill inSuperAdminBill : sabList) {
                BigDecimal outMoney = inSuperAdminBill.getPrice().multiply(new BigDecimal(-1));
                Management superAdmin = getSuperAdmin();
                managementMoneyDeal(superAdmin, outMoney);
                if(inSuperAdminBill.getType() == 5) {
                    comment = "订单退款(追加订单部分)";
                }
                generateSuperAdminBill(id, inSuperAdminBill.getShopId(), inSuperAdminBill.getAreaCode(), outMoney, comment, 2);
            }
        }
        ////生成订单资金交易记录
        generateOrderTrans(orderInfo.getId(), orderInfo.getPayment(), orderInfo.getOrderNo(), comment);
        OrderInfoRe oiRe = orderInfoReRepository.findByOrderNo(orderInfo.getOrderNo());
        if(oiRe != null) {
            //如果该订单有追加订单一并退款
            generateOrderTrans(orderInfo.getId(), oiRe.getPayment(), orderInfo.getOrderNo(), "订单退款(追加订单部分)");
        }
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse insuranceFeeCashRefund(InsuranceFeeRecord insuranceFeeRecord) {
        return null;
    }

    @Override
    @Transactional
    public ResultResponse recommendCashIn(RecommendRecord recommendRecord) {
        Integer userId = recommendRecord.getUserId();
        BigDecimal rewardMoney = recommendRecord.getRewardMoney();
        User user = userRepository.findById(userId).orElse(null);
        userMoneyDeal(user, rewardMoney);
        Integer consumeType = recommendRecord.getConsumeType();
        if (consumeType == 1) {
            //医疗险
            generateUserTrans(null, userId, RecommendTypeEnum.MEDICAL_INSURANCE.getName(), rewardMoney, RecommendTypeEnum.MEDICAL_INSURANCE.getCode() + 3);
        } else if (consumeType == 2) {
            //家责险
            generateUserTrans(null, userId, RecommendTypeEnum.FAMILY_INSURANCE.getName(), rewardMoney, RecommendTypeEnum.FAMILY_INSURANCE.getCode() + 3);
        } else if (consumeType == 3) {
            //新用户首单
            generateUserTrans(null, userId, RecommendTypeEnum.ORDER.getName(), rewardMoney, RecommendTypeEnum.ORDER.getCode() + 3);
        }
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse userWithdrawal(UserWithdrawal userWithdrawal, Boolean isFail) {
        // 申请提现时就扣除资金, 如果审核失败再将资金退回
        BigDecimal money = conversionNullBigDecimal(userWithdrawal.getMoney());
        Integer userId = userWithdrawal.getUserId();
        BigDecimal realMoney = conversionNullBigDecimal(userWithdrawal.getRealMoney());
        User user = userRepository.findById(userId).orElse(null);
        if(!isFail) {
            //如果是提现, 金额设为负数
            //如果是提现审核失败, 再将金额退还给用户账户
            money = money.multiply(new BigDecimal(-1));
            realMoney = realMoney.multiply(new BigDecimal(-1));
            userMoneyDeal(user, money);
            generateUserTrans(null, userId, "用户提现", realMoney, 7);
        } else {
            userMoneyDeal(user, money);
            generateUserTrans(null, userId, "用户提现失败退还", realMoney.multiply(new BigDecimal(-1)), 7);
        }
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse shopWithdrawal(ShopApply shopApply, Boolean isFail) {
        // 申请提现时就扣除资金, 如果审核失败再将资金退回
        BigDecimal applyMoney = conversionNullBigDecimal(shopApply.getApplyMoney());
        BigDecimal realMoney = conversionNullBigDecimal(shopApply.getRealMoney());
        Integer shopId = shopApply.getShopId();
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if(!isFail) {
            //如果是提现, 金额设为负数
            //如果是提现审核失败, 再将金额退还给商家账户
            applyMoney = applyMoney.multiply(new BigDecimal(-1));
            realMoney = realMoney.multiply(new BigDecimal(-1));
            shopMoneyDeal(shop, applyMoney);
            generateShopBill(shopApply.getId(), shopId, applyMoney, "店铺提现", 3);
        } else {
            shopMoneyDeal(shop, applyMoney);
            generateShopBill(shopApply.getId(), shopId, applyMoney.multiply(new BigDecimal(-1)), "店铺提现失败退还", 3);
        }
        return ResultResponse.createBySuccess();
    }

    @Override
    @Transactional
    public ResultResponse areaAdminWithdrawal(AreaWithdrawalApply areaWithdrawalApply, Boolean isFail) {
        // 申请提现时就扣除资金, 如果审核失败再将资金退回
        BigDecimal applyMoney = conversionNullBigDecimal(areaWithdrawalApply.getApplyMoney());
        BigDecimal realMoney = conversionNullBigDecimal(areaWithdrawalApply.getRealMoney());
        Integer managementId = areaWithdrawalApply.getManagementId();
        Management management = managementRepository.findById(managementId).orElse(null);
        if(!isFail) {
            //如果是提现, 金额设为负数
            //如果是提现审核失败, 再将金额退还给地区账户
            applyMoney = applyMoney.multiply(new BigDecimal(-1));
            realMoney = realMoney.multiply(new BigDecimal(-1));
            managementMoneyDeal(management, applyMoney);
            generateAreaBill(null, null, management.getAreaCode(), applyMoney, "地区账户提现", 3);
        } else {
            managementMoneyDeal(management, applyMoney);
            generateAreaBill(null, null, management.getAreaCode(), applyMoney.multiply(new BigDecimal(-1)), "地区账户提现退还", 3);
        }
        return ResultResponse.createBySuccess();
    }

    /**
     * 生成订单资金交易记录
     * @param orderId
     * @param comment
     */
    private void generateOrderTrans(Integer orderId, BigDecimal payment, String orderNo, String comment) {
        OrderTran ot = new OrderTran();
        ot.setOrderId(orderId);
        ot.setComment(comment + payment + "," + "订单号:" + orderNo);
        ot.setCreateTime(new Date());
        orderTranRepository.saveAndFlush(ot);
    }

    /**
     * 获取扣费过的资金
     *
     * @param old
     * @param deductPercent
     * @return
     */
    private BigDecimal getDeductedPrice(BigDecimal old, Integer deductPercent) {
        String deductedPercent = String.valueOf((double)(100 - deductPercent) / (double) 100);
        System.out.println("入账money>>>>>>>>>>" + old.multiply(new BigDecimal(deductedPercent)));
        return old.multiply(new BigDecimal(deductedPercent)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 用户资金出入账
     *
     * @param u
     * @param money
     */
    private void userMoneyDeal(User u, BigDecimal money) {
        BigDecimal oldMoney = conversionNullBigDecimal(u.getMoney());
        u.setMoney(oldMoney.add(money));
        userRepository.save(u);
    }

    /**
     * 商家资金出入账
     */
    private void shopMoneyDeal(Shop s, BigDecimal money) {
        System.out.println("添加资金, money: " + money);
        BigDecimal oldMoney = conversionNullBigDecimal(s.getMoney());
        s.setMoney(oldMoney.add(money));
        shopRepository.save(s);
    }

    /**
     * 区域账户资金出入账/超级管理员资金出入账
     */
    private void managementMoneyDeal(Management m, BigDecimal money) {
        BigDecimal oldMoney = conversionNullBigDecimal(m.getMoney());
        m.setMoney(oldMoney.add(money));
        managementRepository.save(m);
    }

    /**
     * 生成用户资金流水日志
     *
     * @param orderId
     * @param userId
     * @param comment
     * @param money
     * @param type
     */
    private void generateUserTrans(Integer orderId, Integer userId, String comment, BigDecimal money, Integer type) {
        UserTrans ut = new UserTrans();
        ut.setOrderId(orderId);
        ut.setUserId(userId);
        ut.setComment(comment);
        ut.setMoney(money);
        ut.setType(type);
        userTransRepository.save(ut);
    }

    /**
     * 生成商家流水日志
     *
     * @param orderId
     * @param shopId
     * @param price
     * @param note
     * @param type
     */
    private void generateShopBill(Integer orderId, Integer shopId, BigDecimal price, String note, Integer type) {
        ShopBill sb = new ShopBill();
        sb.setOrderId(orderId);
        sb.setShopId(shopId);
        sb.setPrice(price);
        sb.setNote(note);
        sb.setType(type);
        sb.setCreateTime(new Date());
        shopBillRepository.save(sb);
    }

    /**
     * 生成区域账户流水日志
     *
     * @param orderId
     * @param shopId
     * @param areaCode
     * @param price
     * @param note
     * @param type
     */
    private void generateAreaBill(Integer orderId, Integer shopId, String areaCode, BigDecimal price, String note, Integer type) {
        //生成流水记录
        AreaBill ab = new AreaBill();
        ab.setOrderId(orderId);
        ab.setShopId(shopId);
        ab.setNote(note);
        ab.setType(type);
        ab.setPrice(price);
        ab.setCreateTime(new Date());
        ab.setAreaCode(areaCode);
        ab.setCreateTime(new Date());
        areaBillRepository.save(ab);
    }

    /**
     * 生成超级管理账户流水日志
     *
     * @param orderId
     * @param shopId
     * @param areaCode
     * @param price
     * @param note
     * @param type
     */
    private void generateSuperAdminBill(Integer orderId, Integer shopId, String areaCode, BigDecimal price, String note, Integer type) {
        //生成流水记录
        SuperAdminBill sab = new SuperAdminBill();
        sab.setOrderId(orderId);
        sab.setShopId(shopId);
        sab.setNote(note);
        sab.setType(type);
        sab.setPrice(price);
        sab.setAreaCode(areaCode);
        sab.setCreateTime(new Date());
        superAdminBillRepository.save(sab);
    }

    private Management getAreaAdmin(String areaCode) {
        List<Management> list = managementRepository.findByAreaCodeAndStatus(areaCode, 1);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private Management getSuperAdmin() {
        List<Management> list = managementRepository.findByLevel(99);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private BigDecimal conversionNullBigDecimal(BigDecimal d) {
        if (d == null) {
            return new BigDecimal(0);
        } else {
            return d;
        }
    }
}
