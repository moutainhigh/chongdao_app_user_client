package com.chongdao.client.service.iml;

import com.chongdao.client.common.CommonRepository;
import com.chongdao.client.common.CouponConst;
import com.chongdao.client.common.CouponScopeCommon;
import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.Carts;
import com.chongdao.client.entitys.Good;
import com.chongdao.client.entitys.Shop;
import com.chongdao.client.entitys.coupon.CouponInfo;
import com.chongdao.client.entitys.coupon.CpnThresholdRule;
import com.chongdao.client.entitys.coupon.CpnUser;
import com.chongdao.client.enums.CouponStatusEnum;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.service.CouponService;
import com.chongdao.client.utils.DateTimeUtil;
import com.chongdao.client.vo.CpnInfoVo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.chongdao.client.common.CouponConst.*;

@Service
public class CouponServiceImpl extends CommonRepository implements CouponService {

    /**
     * 订单优惠券列表
     * @param shopId 商品id
     * @param type 优惠券类型(0：商品以及服务优惠券 1: 配送优惠券)
     * @param serviceType 服务类型 1.双程 2.单程 3.到店自取
     * @param categoryId 商品（服务）分类0,1
     * @param totalPrice 订单金额（商品或者服务折扣后的价格）
     * @param categoryId 检验商品类型（主要是针对服务和商品的优惠券使用场景）
     * @return
     */
    @Override
    public ResultResponse getCouponListByShopIdAndType(Integer userId, String shopId, String categoryId,
                                                       BigDecimal totalPrice, Integer type,
                                                       Integer serviceType) {
        //商品以及服务优惠券 类型为 1现金券（红包）3折扣券
        List<CouponInfo> couponInfoList = Lists.newArrayList();
        if (type.equals(CouponStatusEnum.COUPON_GOODS.getStatus())) {
            //查询优惠券列表(商品and服务) cpnScopeType: 1全场通用 3限商品 4限服务
            //cpnType:优惠券类型 1现金券 2满减券 3折扣券 4店铺满减
            List<CpnUser> cpnUserList = cpnUserRepository.findByShopIdAndUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(shopId, userId, 0, 0,Arrays.asList(1,2,3), Arrays.asList(1,3,4));
            cpnUserList.stream().forEach(cpnUser -> {
                //查询截止日期与当前日期差
                long result = this.computerTime(cpnUser.getValidityEndDate());
                if (result > 0 && cpnUser.getCount() != null && cpnUser.getCount() > 0 && cpnUser.getUserCpnState() != null &&  cpnUser.getUserCpnState() == 0) {
                    //逻辑处理
                    this.getCpnUser(cpnUser, totalPrice, categoryId);
                    CouponInfo couponInfo = this.assembelCpnInfoEnabled(cpnUser);
                    //设置优惠券限制范围名称
                    this.setCouponScope(couponInfo);
                    couponInfoList.add(couponInfo);
                }
            });
            ////////////////////////////////增加一个特殊的体检券的获取
            Shop shop = shopRepository.findById(Integer.valueOf(shopId)).orElse(null);
            if(shop != null && shop.getType() != null && shop.getType() == 2) {
                //医院类商家
                CpnUser tijianCoupon = getTijianCoupon(userId);
                if(tijianCoupon != null) {
                    long result = this.computerTime(tijianCoupon.getValidityEndDate());
                    if (result > 0 && tijianCoupon.getCount() != null && tijianCoupon.getCount() > 0 && tijianCoupon.getUserCpnState() != null &&  tijianCoupon.getUserCpnState() == 0) {
                        //逻辑处理
                        this.getCpnUser(tijianCoupon, totalPrice, categoryId);
                        List<Carts> cartList = cartRepository.findByUserIdAndShopId(userId, Integer.valueOf(shopId));
                        boolean hasTijian = isHasTijian(cartList);
                        if(hasTijian) {
                            tijianCoupon.setEnabled(1);
                        } else {
                            tijianCoupon.setEnabled(0);
                        }
                        CouponInfo couponInfo = this.assembelCpnInfoEnabled(tijianCoupon);
                        //设置优惠券限制范围名称
                        this.setCouponScope(couponInfo);
                        couponInfoList.add(couponInfo);
                    }
                }
            }
            return ResultResponse.createBySuccess(couponInfoList);
        }
        //配送优惠券(双程)
        if (serviceType == 1 && type.equals(CouponStatusEnum.COUPON_SERVICE_DELIVERY.getStatus())){
            //4限服务 6配送双程  8仅限服务（双程）10仅限商品（配送双程）
            //该查询查询所有，不包含限制店铺判断
            List<CpnUser> cpnUserList = cpnUserRepository.findByUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(userId, 0, 0, Arrays.asList(1,2,3),Arrays.asList(6, 8, 10));
            //限制店铺使用的查询结果
            List<CpnUser> cpnLimitUserList = cpnUserRepository.findByShopIdAndUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(shopId,userId, 0, 0, Arrays.asList(1,2,3),Arrays.asList(6, 8, 10));
            if (!CollectionUtils.isEmpty(cpnLimitUserList)){
                cpnUserList.addAll(cpnLimitUserList);
            }
            cpnUserList.stream().forEach(cpnUser -> {
                //查询截止日期与当前日期差
                long result = this.computerTime(cpnUser.getValidityEndDate());
                if (result > 0 && cpnUser.getCount() != null && cpnUser.getCount() > 0 && cpnUser.getUserCpnState() != null &&  cpnUser.getUserCpnState() == 0) {
                    //设置是否可用
                    this.setServiceCouponEnabled(cpnUser,shopId);
                    CouponInfo couponInfo = this.assembelCpnInfoEnabled(cpnUser);
                    //设置优惠券限制范围名称
                    this.setCouponScope(couponInfo);
                    couponInfoList.add(couponInfo);
                }
            });
            return ResultResponse.createBySuccess(couponInfoList);
        }else if (serviceType == 2 && type.equals(CouponStatusEnum.COUPON_SERVICE_DELIVERY.getStatus())){//单程
            //4限服务 5配送单程  7仅限服务（单程）9仅限商品（配送单程）
            List<CpnUser> cpnUserList =cpnUserRepository.findByUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(userId, 0, 0,Arrays.asList(1,2,3), Arrays.asList(5,7,9));
            //限制店铺使用的查询结果
            List<CpnUser> cpnLimitUserList = cpnUserRepository.findByShopIdAndUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(shopId,userId, 0, 0, Arrays.asList(1,2,3),Arrays.asList(5,7,9));
            if (!CollectionUtils.isEmpty(cpnLimitUserList)){
                cpnUserList.addAll(cpnLimitUserList);
            }
            cpnUserList.stream().forEach(cpnUser -> {
                //查询截止日期与当前日期差
                long result = this.computerTime(cpnUser.getValidityEndDate());
                if (result > 0 && cpnUser.getCount() != null && cpnUser.getCount() > 0 && cpnUser.getUserCpnState() != null &&  cpnUser.getUserCpnState() == 0) {
                    //设置是否可用
                    this.setServiceCouponEnabled(cpnUser,shopId);
                    CouponInfo couponInfo = this.assembelCpnInfoEnabled(cpnUser);
                    //设置优惠券限制范围名称
                    this.setCouponScope(couponInfo);
                    couponInfoList.add(couponInfo);
                }
            });
            return ResultResponse.createBySuccess(couponInfoList);
        }
        return ResultResponse.createBySuccess();
    }

    /**
     * 领取优惠券
     * @param userId
     * @param couponInfo
     * @return
     */
    @Override
    @Transactional
    public ResultResponse receiveCoupon(Integer userId, CouponInfo couponInfo) {
        if (userId == null || couponInfo.getShopId() == null || couponInfo.getId() == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),
                    ResultEnum.PARAM_ERROR.getMessage());
        }
        //判断该优惠券是否被领取过
        CpnUser cpnUser = cpnUserRepository.findByUserIdAndCpnIdAndShopId(userId, couponInfo.getId(), String.valueOf(couponInfo.getShopId()));
        if (cpnUser != null){
            return ResultResponse.createByErrorCodeMessage(CouponStatusEnum.RECEIVED_COUPON_CARD.getStatus(),
                    CouponStatusEnum.RECEIVED_COUPON_CARD.getMessage());
        }
        //领取数量+1
        couponInfoRepository.updateReceiveCountByCouponId(couponInfo.getId(),String.valueOf(couponInfo.getShopId()));
        //插入用户优惠券表记录
        couponInfo = couponInfoRepository.findById(couponInfo.getId()).orElse(null);
        if (couponInfo == null) {
            return ResultResponse.createByErrorCodeMessage(CouponStatusEnum.COUPON_NOT_EXIST.getStatus(),
                    CouponStatusEnum.COUPON_NOT_EXIST.getMessage());
        }
        cpnUser = new CpnUser();
        cpnUser.setCount(1);
        cpnUser.setCpnBatchId(couponInfo.getBatchId());
        cpnUser.setCpnCode(couponInfo.getCpnCode());
        cpnUser.setCpnId(couponInfo.getId());
        cpnUser.setCpnScopeType(couponInfo.getScopeType());
        cpnUser.setCpnType(couponInfo.getCpnType());
        cpnUser.setCpnValue(couponInfo.getCpnValue());
        cpnUser.setGainDate(new Date());
        cpnUser.setGainDesc(couponInfo.getCpnDesc());
        cpnUser.setRuleType(couponInfo.getRuleType());

        //默认未删除
        cpnUser.setIsDelete(0);
        //默认未使用
        cpnUser.setUserCpnState(0);
        cpnUser.setUseDesc("");
        cpnUser.setValidityStartDate(couponInfo.getValidityStartDate());
        cpnUser.setValidityEndDate(couponInfo.getValidityEndDate());
        cpnUser.setCreateDate(new Date());
        cpnUser.setUpdateDate(new Date());
        cpnUser.setUserId(userId);
        if(couponInfo.getShopId() != null) {
            cpnUser.setShopId(String.valueOf(couponInfo.getShopId()));
        }
        cpnUserRepository.save(cpnUser);
        //代表已领取
        cpnUser.setReceive(1);
        List<CpnUser> cpnUsers = Lists.newArrayList();
        cpnUsers.add(cpnUser);
        return ResultResponse.createBySuccess(cpnUsers);
    }

    /**
     * 计算优惠券有效期
     * @param date
     * @return
     */
    public static long computerTime(Date date){
        long result = DateTimeUtil.costTime(DateTimeUtil.dateToStr(date),
                DateTimeUtil.dateToStr(new Date()));
        return result;
    }

    /**
     * 获取优惠券数量(商品/服务)
     * @param userId
     * @return
     */
    @Override
    public int countByUserIdAndIsDeleteAndAndCpnType(List<Carts> cartList, Integer userId, Integer shopId, List<Integer> categoryIds, BigDecimal totalPrice) {
        Integer count = 0;
        //查询优惠券列表(商品and服务) cpnScopeType: 1全场通用 3限商品 4限服务
        //cpnType:优惠券类型 1现金券 2满减券 3折扣券 4店铺满减
        List<CpnUser> cpnUserList = cpnUserRepository.findByShopIdAndUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(String.valueOf(shopId), userId, 0, 0, Arrays.asList(1,2,3), Arrays.asList(1,3,4));
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        //医院类商家, 额外查询体检券
        if(shop != null && shop.getType() != null && shop.getType() == 2 && isHasTijian(cartList)) {
            List<CpnUser> tijianList = cpnUserRepository.findByCpnIdAndUserIdAndUserCpnStateAndIsDeleteAndCpnTypeInAndCpnScopeTypeIn(999, userId, 0, 0, Arrays.asList(1, 2, 3), Arrays.asList(1, 3, 4));
            cpnUserList.addAll(tijianList);
        }
        for (CpnUser cpnUser : cpnUserList) {
            //查询截止日期与当前日期差
            long result = this.computerTime(cpnUser.getValidityEndDate());
            //二次校验，过滤过期的优惠券
            if (result > 0 && cpnUser.getCount() != null && cpnUser.getCount() > 0 && cpnUser.getUserCpnState() != null &&  cpnUser.getUserCpnState() == 0){
                count = count + this.getCpnUserCount(cpnUser, totalPrice, categoryIds);
            }
        };
        //查询是否参加公益
        if (shop.getIsJoinCommonWeal() == 1) {
            int result = cpnUserRepository.countByUserIdAndIsDeleteAndCpnTypeAndCountGreaterThanAndUserCpnState(userId, 0, CouponConst.COMMON,0,0);
            count = count + result;
        }
        return count;
    }

    /**
     * 判断购物车中是否含有基础体检套餐
     * @param cartList
     * @return
     */
    private boolean isHasTijian(List<Carts> cartList) {
        boolean flag = false;
        for(Carts cart : cartList) {
            Integer goodsId = cart.getGoodsId();
            if(goodsId != null) {
                Good good = goodsRepository.findById(goodsId).orElse(null);
                if(good != null) {
                    String name = good.getName();
                    if(name.equals("基础体检套餐")) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    private CpnUser getTijianCoupon(Integer userId) {
        //体检券id写死为6
        List<CpnUser> list = cpnUserRepository.getCanUserTijianCpn(userId, 999);
        if(list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 卡包（优惠券）
     * @param userId
     * @return
     */
    @Override
    public ResultResponse couponList(Integer userId) {
        List<CpnUser> cpnUserList = cpnUserRepository.findByUserIdAndUserCpnStateAndIsDelete(userId, 0, 0);
        List<CpnInfoVo> cpnInfoVos = Lists.newArrayList();
        List<Integer> cpnIds = Lists.newArrayList();
        cpnUserList.stream().forEach(e ->{
            //二次校验，过滤过期的优惠券
            //查询截止日期与当前日期差
            long result = this.computerTime(e.getValidityEndDate());
            if (result > 0 && e.getCount() != null && e.getCount() > 0 && e.getUserCpnState() != null &&  e.getUserCpnState() == 0){
                cpnIds.add(e.getCpnId());
            }
        });
        List<CouponInfo> couponInfoList = couponInfoRepository.findAllByIdIn(cpnIds);
        if (!CollectionUtils.isEmpty(couponInfoList)) {
            couponInfoList.stream().forEach(couponInfo -> {
                StringBuilder sb = new StringBuilder();
                CpnInfoVo cpnInfoVo = new CpnInfoVo();
                cpnInfoVo.setCpnId(couponInfo.getId());
                cpnInfoVo.setCpnName(couponInfo.getCpnName());
                cpnInfoVo.setCpnType(couponInfo.getCpnType());
                cpnInfoVo.setCpnValue(couponInfo.getCpnValue());
                cpnInfoVo.setValidityStartDate(DateTimeUtil.dateToStr2(couponInfo.getValidityStartDate()));
                cpnInfoVo.setValidityEndDate(DateTimeUtil.dateToStr2(couponInfo.getValidityEndDate()));
                List<Shop> shopList = shopMapper.selectByShopIds(StringUtils.isBlank(couponInfo.getScopeShopId())? null:couponInfo.getScopeShopId());
                if (!CollectionUtils.isEmpty(shopList)) {
                    cpnInfoVo.setScopeTypeShopId(couponInfo.getScopeShopId());
                    for (Shop shop : shopList) {
                        sb.append(shop.getShopName() + ",");
                    }
                    //拼接适用范围名称 一般指适用店铺
                    cpnInfoVo.setScopeName(sb.substring(0, sb.length() - 1));
                }
                if (couponInfo.getShopId() != null) {
                    cpnInfoVo.setShopId(couponInfo.getShopId());
                    cpnInfoVo.setShopName(shopRepository.findById(couponInfo.getShopId()).orElse(null).getShopName());
                }
                cpnInfoVo.setScopeType(couponInfo.getScopeType());
                cpnInfoVos.add(cpnInfoVo);
            });

        }
        return ResultResponse.createBySuccess(cpnInfoVos);
    }

    @Override
    public ResultResponse presentMedicalCard(Integer userId) {
        Integer cpnId = 999;
        CouponInfo couponInfo = couponInfoRepository.findById(cpnId).orElse(null);
        if(couponInfo != null) {
            receiveCoupon(userId, couponInfo);
            return ResultResponse.createBySuccess();
        }
        return ResultResponse.createByError();
    }

    @Override
    public ResultResponse presentService30Card(Integer userId) {
        Integer cpnId = 8;
        CouponInfo couponInfo = couponInfoRepository.findById(cpnId).orElse(null);
        if(couponInfo != null) {
            receiveCoupon(userId, couponInfo);
            return ResultResponse.createBySuccess();
        }
        return ResultResponse.createByError();
    }


    /**
     * 获取配送优惠券
     * @param userId
     * @param serviceType
     * @return
     */
    public int getExpressCouponCount(Integer userId,Integer serviceType){
        if (serviceType == 1){ //双程
            int count = cpnUserRepository.countByUserIdAndIsDeleteAndCpnScopeTypeInAndCountGreaterThanAndUserCpnState(userId, 0, Arrays.asList(6, 8, 10),0,0);
            return count;
        }else{
            int count = cpnUserRepository.countByUserIdAndIsDeleteAndCpnScopeTypeInAndCountGreaterThanAndUserCpnState(userId, 0, Arrays.asList(5, 7, 9),0,0);
            return count;
        }
    }
    /**
     * 封装优惠券逻辑代码（商品和服务）
     * @param cpnUser
     * @return
     */
    private CpnUser getCpnUser(CpnUser cpnUser, BigDecimal totalPrice, String categoryId){
        //获取使用范围
        Integer cpnScopeType = cpnUser.getCpnScopeType();
        //获取门槛规则，当前需要满足的条件
        CpnThresholdRule cpnRule = thresholdRuleRepository.findByCpnId(cpnUser.getCpnId());
        if (cpnScopeType == EXPERT && cpnUser.getRuleType() == NO_THRESHOLD){//全场通用（无门槛）
            cpnUser.setEnabled(1);
        }else if (cpnScopeType == EXPERT && cpnUser.getRuleType() == THRESHOLD){//全场通用（有门槛）
            //满减判断
            this.compare(cpnUser,totalPrice,cpnRule.getMinPrice());
        }else if (cpnScopeType == LIMITED_GOODS && categoryId.contains("3")){//限制商品 categoryId包含3即可
            //无门槛
            if (cpnUser.getRuleType() == NO_THRESHOLD){
                cpnUser.setEnabled(1);
            }else {//有门槛
                //满减判断
                this.compare(cpnUser, totalPrice, cpnRule.getMinPrice());
            }
        }else if (cpnScopeType == LIMITED_SERVICE && !categoryId.contains("3")){//限制服务 categoryId不包含3即可
            //无门槛
            if (cpnUser.getRuleType() == NO_THRESHOLD){
                cpnUser.setEnabled(1);
            }else {//有门槛
                //满减判断
                if (cpnRule != null) {
                    this.compare(cpnUser, totalPrice, cpnRule.getMinPrice());
                }
            }
        }
        return cpnUser;
    }

    /**
     * 满减判断
     * @param cpnUser
     * @param totalPrice
     * @param conditionPrice
     * @return
     */
    private CpnUser compare(CpnUser cpnUser,BigDecimal totalPrice,BigDecimal conditionPrice){
        if (conditionPrice != null) {
            if (totalPrice.compareTo(conditionPrice) == 0 || totalPrice.compareTo(conditionPrice) == 1) {
                cpnUser.setEnabled(1);
            }
        }
        return cpnUser;
    }


    /**
     * 计算满足条件的优惠券个数
     * @param cpnUser
     * @param totalPrice
     * @param categoryId
     * @return
     */
    protected Integer getCpnUserCount(CpnUser cpnUser, BigDecimal totalPrice, List<Integer> categoryId){
        //获取使用范围
        Integer cpnScopeType = cpnUser.getCpnScopeType();
        //获取门槛规则，当前需要满足的条件
        CpnThresholdRule cpnRule = thresholdRuleRepository.findByCpnId(cpnUser.getCpnId());
        List<CpnUser> count = Lists.newArrayList();
        if (cpnScopeType == EXPERT && cpnUser.getRuleType() == NO_THRESHOLD){//全场通用（无门槛）
            cpnUser.setEnabled(1);
            count.add(cpnUser);
        }else if (cpnScopeType == EXPERT && cpnUser.getRuleType() == THRESHOLD){//全场通用（有门槛）
            //满减判断
            CpnUser compare = this.compare(cpnUser, totalPrice, cpnRule.getMinPrice());
            if (compare.getEnabled() == 1){
                count.add(compare);
            }
        }else if (cpnScopeType == LIMITED_GOODS && categoryId.contains(3)){//限制商品 categoryId包含3即可
            //无门槛
            if (cpnUser.getRuleType() == NO_THRESHOLD){
                cpnUser.setEnabled(1);
                count.add(cpnUser);
            }else {//有门槛
                //满减判断
                CpnUser compare = this.compare(cpnUser, totalPrice, cpnRule.getMinPrice());
                if (compare.getEnabled() == 1){
                    count.add(compare);
                }
            }
        }else if (cpnScopeType == LIMITED_SERVICE && !categoryId.contains(3)){//限制服务 categoryId不包含3即可
            //无门槛
            if (cpnUser.getRuleType() == NO_THRESHOLD){
                cpnUser.setEnabled(1);
                count.add(cpnUser);
            }else {//有门槛
                //满减判断
                if (cpnRule != null) {
                    CpnUser compare = this.compare(cpnUser, totalPrice, cpnRule.getMinPrice());
                    if (compare.getEnabled() == 1) {
                        count.add(compare);
                    }
                }
            }
        }
        return count.size();
    }


    /**
     * 封装优惠券是否可用
     * @param cpnUser
     * @return
     */
    private CouponInfo assembelCpnInfoEnabled(CpnUser cpnUser){
        CouponInfo couponInfo = couponInfoRepository.findById(cpnUser.getCpnId()).orElse(null);
        //设置门槛金额（仅针对满减）
        CpnThresholdRule cpnThresholdRule = cpnThresholdRuleRepository.findByCpnId(couponInfo.getId());
        if (cpnThresholdRule != null && cpnThresholdRule.getMinPrice() != null) {
            couponInfo.setMinPrice(cpnThresholdRule.getMinPrice());
        }
        if (couponInfo != null && cpnUser.getEnabled() == 1) {//优惠券可用
            couponInfo.setEnabled(1);
        }
        if (couponInfo.getShopId() != null) {
            Shop shop = shopRepository.findById(couponInfo.getShopId()).orElse(null);
            if (shop != null) {
                couponInfo.setShopName(shop.getShopName());
            }
        }
        //判断是否有特殊优惠券（仅限几家店铺使用的这种）
        if (StringUtils.isNotBlank(couponInfo.getScopeShopId())) {
            List<Shop> shopList = shopMapper.selectByShopIds(StringUtils.isBlank(couponInfo.getScopeShopId()) ? null: couponInfo.getScopeShopId());
            if (!CollectionUtils.isEmpty(shopList)) {
                String shopName = "";
                for (Shop shop : shopList) {
                    shopName = Joiner.on(",").skipNulls().join(shop.getShopName(),shopName);
                }
                if (StringUtils.isNotBlank(shopName)) {
                    couponInfo.setShopName(shopName.substring(0, shopName.length() - 1));
                }
            }
        }
        return couponInfo;
    }


    public static CouponInfo setCouponScope(CouponInfo couponInfo) {
        //设置限制范围名称
        if (couponInfo.getScopeType() != null) {
            couponInfo.setScopeName(CouponScopeCommon.cpnScope(couponInfo.getScopeType(),couponInfo).getScopeName());
        }
        return couponInfo;
    }


    /**
     * 设置配送券可用范围
     * @param cpnUser
     * @param shopId
     * @return
     */
    private CpnUser setServiceCouponEnabled(CpnUser cpnUser,String shopId){
        if(StringUtils.isNotBlank(cpnUser.getShopId()) && cpnUser.getShopId().equals("1")) {
            //官方店铺发送的优惠券全场都可用
            cpnUser.setEnabled(1);
            return cpnUser;
        }

        if (StringUtils.isNotBlank(cpnUser.getShopId()) && StringUtils.contains(cpnUser.getShopId(),shopId)){
            cpnUser.setEnabled(1);
        }else if (StringUtils.isBlank(cpnUser.getShopId())){
            cpnUser.setEnabled(1);
        }else{
            cpnUser.setEnabled(0);
        }
        return cpnUser;
    }
}
