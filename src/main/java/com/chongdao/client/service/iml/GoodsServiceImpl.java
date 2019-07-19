package com.chongdao.client.service.iml;

import com.chongdao.client.common.CommonRepository;
import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.*;
import com.chongdao.client.entitys.coupon.CouponInfo;
import com.chongdao.client.entitys.coupon.CpnUser;
import com.chongdao.client.enums.GoodsStatusEnum;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.exception.PetException;
import com.chongdao.client.service.GoodsService;
import com.chongdao.client.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.chongdao.client.common.Const.OrderBy.*;
import static com.chongdao.client.service.iml.CouponServiceImpl.computerTime;

@Service
public class GoodsServiceImpl extends CommonRepository implements GoodsService {


    /**
     * 分页查询商品
     * @param keyword 搜索关键词
     * @param pageNum 页数
     * @param pageSize 每页数据数量
     * @param orderBy 排序方式(价格、销量、好评等)
     * @return
     */
    @Override
    public ResultResponse<PageInfo> getGoodsByKeyword(String keyword, int pageNum, int pageSize,Integer brandId,Integer goodsTypeId,Integer scopeId,Integer petCategoryId,String orderBy) {
        //搜索关键词不为空
        if (StringUtils.isNotBlank(keyword)){
            keyword =new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序规则
        if (StringUtils.isNotBlank(orderBy)){
            if (PRICE_ASC_DESC.contains(orderBy) || SALES_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                //价格排序、销量排序
                orderBy = orderByArray[0] + " " + orderByArray[1];
            }else if (ARRANGEMENT_KEY.contains(orderBy)){
                //综合排序
                orderBy = ARRANGEMENT_VALUE_GOODS;
            }
        }
        //查询所有上架商品(综合排序)
        List<Good> goodList = goodMapper.selectByName(StringUtils.isBlank(keyword) ? null: keyword,
                brandId,goodsTypeId,scopeId,petCategoryId,
                orderBy);
        PageInfo pageInfo = new PageInfo(goodList);
        pageInfo.setList(this.goodsListVOList(goodList));
        return ResultResponse.createBySuccess(pageInfo);
    }



    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    @Override
    public ResultResponse<GoodsDetailVo> getGoodsDetail(Integer goodsId, Integer userId) {
        if (goodsId == null){
            throw new PetException(ResultEnum.PARAM_ERROR);
        }
        //查询商品
        Good good = goodMapper.selectByPrimaryKey(goodsId);
        if (good == null){
            throw new PetException(ResultEnum.PARAM_ERROR);
        }
        //封装详情VO类
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsId(goodsId);
        //产品重量
        goodsDetailVo.setUnit(good.getUnit());
        //产品品牌
        Brand brand = brandRepository.findById(good.getBrandId()).get();
        goodsDetailVo.setBrandName(brand.getName());
        //适用范围
        ScopeApplication application = scopeApplicationRepository.findById(good.getScopeId()).get();
        goodsDetailVo.setScopeName(application.getName());
        //categoryId:2 即洗澡类 才会有服务内容
        if (good.getCategoryId() == 2) {
            goodsDetailVo.setServiceContent(bathingServiceRepository.findAll());
        }

        BeanUtils.copyProperties(good, goodsDetailVo);
        //计算打折后的价格。折扣必须大于0且不能为空
        if (good.getDiscount() > 0 && good.getDiscount() !=null){
            goodsDetailVo.setDiscountPrice(good.getPrice().multiply(new BigDecimal(good.getDiscount())));
        }
        //查询优惠券（属于该商品可以使用或者领取的）
        List<CouponInfo> couponInfoList = couponInfoRepository.findByShopIdInAndCpnState(good.getShopId(), 1);
        goodsDetailVo.setCouponInfoList(this.assembleCpn(couponInfoList,userId,good.getCategoryId(),good.getShopId()));
        //查询店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(good.getShopId());
        if (shop == null){
            throw new PetException(ResultEnum.PARAM_ERROR);
        }
        goodsDetailVo.setShopName(shop.getShopName());
        goodsDetailVo.setStartBusinessHours(shop.getStartBusinessHours());
        goodsDetailVo.setEndBusinessHours(shop.getEndBusinessHours());
        return ResultResponse.createBySuccess(ResultEnum.SUCCESS.getMessage(),goodsDetailVo);
    }




    /**
     * 封装分页查询商品功能
     * @param goodList
     * @return
     */
    private List<GoodsListVO> goodsListVOList(List<Good> goodList){
        List<GoodsListVO> goodsListVOList = Lists.newArrayList();
        goodList.forEach(good -> {
            GoodsListVO goodsListVO = new GoodsListVO();
            BeanUtils.copyProperties(good,goodsListVO);
            //折扣大于0时，才会显示折扣价
            if (good.getDiscount() > 0.0D && good.getDiscount() != null ){
                goodsListVO.setDiscountPrice(good.getPrice().multiply(new BigDecimal(good.getDiscount())));
            }
            //封装优惠券
            //根据店铺查询在架状态的优惠券
            goodsListVO.setCouponInfoList(this.assembleCouponVo(good.getShopId()));
            goodsListVOList.add(goodsListVO);
        });
        return goodsListVOList;
    }


    /**
     * 封装优惠券功能方便复用
     * type:0 代表店铺满减活动（不属于优惠券）;1代表优惠券
     * @return
     */

    public List<CouponInfo> assembleCouponVo(Integer shopId){
        //查询优惠券列表(商品and服务) cpnScopeType: 1全场通用 3限商品 4限服务
        //cpnType:优惠券类型 1现金券 2满减券 3折扣券 4店铺满减
        List<CouponInfo> couponInfoList = couponInfoRepository.findByShopIdInAndCpnState(shopId, 1);
        List<CouponInfo> couponInfos = Lists.newArrayList();
        couponInfoList.stream().forEach(couponInfo -> {
            //查询截止日期与当前日期差
            long result = computerTime(couponInfo.getValidityEndDate());
            if (result > 0) {
                //逻辑处理
                couponInfos.add(couponInfo);
            }
        });
        return couponInfos;
    }


    /**
     * 商品详情 封装当前商品可使用的优惠券
     * @param couponInfoList
     * @return
     */
    public List<CouponInfo> assembleCpn(List<CouponInfo> couponInfoList,Integer userId,Integer categoryId,Integer shopId){
        //查询优惠券列表(商品and服务) cpnScopeType: 1全场通用 3限商品 4限服务
        //cpnType:优惠券类型 1现金券 2满减券 3折扣券 4店铺满减
        List<CouponInfo> couponInfos = Lists.newArrayList();
        couponInfoList.stream().forEach(couponInfo -> {
            CpnUser cpnUser = null;
            if (userId != null) {
                cpnUser = cpnUserRepository.findByUserIdAndCpnIdAndShopId(userId, couponInfo.getId(), String.valueOf(shopId));
            }
            //查询截止日期与当前日期差
            long result = computerTime(couponInfo.getValidityEndDate());
            if (result > 0) {
                //逻辑处理
                //适用范围与当前商品的范围比较，店铺满减除外(未登录用户，需展示未领取的优惠券)
                if (couponInfo.getScopeType() == categoryId && userId == null && couponInfo.getCpnType() != 4){
                    couponInfos.add(couponInfo);
                }else if (couponInfo.getScopeType() == categoryId && userId != null && couponInfo.getCpnType() != 4){
                    //用户未领取该优惠券才会展示
                    if (cpnUser.getCpnId() != couponInfo.getId()){
                        couponInfos.add(couponInfo);
                    }
                }else{
                    couponInfos.add(couponInfo);
                }
            }
        });
        return couponInfos;
    }

    //-------------------------------------------------------------------商户端实现--------------------------------------------------------------------------//


    /**
     * 获取商品类别
     * @return
     */
    @Override
    public ResultResponse getGoodCategoryList(Integer shopId) {
        List<GoodsType> goodCategoryList = goodsTypeMapper.getGoodCategoryList(shopId);
        List<GoodsTypeVO> goodsTypeVOList = Lists.newArrayList();
        goodCategoryList.stream().forEach(goodsType -> {
            GoodsTypeVO goodsTypeVO = new GoodsTypeVO();
            goodsTypeVO.setGoodsTypeName(goodsType.getName());
            goodsTypeVO.setGoodsTypeId(goodsType.getId());
            goodsTypeVO.setCategoryId(goodsType.getCategoryId());
            goodsTypeVO.setSort(goodsType.getSort());
            goodsTypeVO.setStatus(goodsType.getStatus());
            goodsTypeVO.setModuleId(goodsType.getModuleId());
            goodsTypeVOList.add(goodsTypeVO);
        });
        return ResultResponse.createBySuccess(goodsTypeVOList);
    }


    /**
     * 获取商品列表
     * @param goodsTypeId
     * @param goodName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultResponse getGoodList(Integer shopId,Integer goodsTypeId, String goodName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Good> goodList = goodMapper.getGoodList(shopId,goodsTypeId, goodName);
        List<GoodsListVO> goodsListVOList = Lists.newArrayList();
        goodList.stream().forEach(good -> {
            GoodsListVO goodsListVO = new GoodsListVO();
            BeanUtils.copyProperties(good,goodsListVO);
            goodsListVOList.add(goodsListVO);
        });
        PageInfo pageResult = new PageInfo(goodList);
        pageResult.setList(goodsListVOList);
        return ResultResponse.createBySuccess(pageResult);
    }

    /**
     * 商品下架
     * @param goodId
     * @param status 1:上架,0下架，-1删除
     * @return
     */
    @Override
    public ResultResponse updateGoodsStatus(Integer goodId, Integer status) {
        if (goodId == null || status == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        goodMapper.updateGoodsStatus(goodId,status);
        return ResultResponse.createBySuccess();
    }


    /**
     * 商品打折
     * @param goodsTypeId
     * @param discount
     * @return
     */
    @Override
    public ResultResponse discountGood(Integer shopId,Integer goodsTypeId, Double discount) {
        if (goodsTypeId == null || discount <= 0 || discount > 9 || shopId == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        goodMapper.goodsDiscount(shopId,goodsTypeId,discount);
        return ResultResponse.createBySuccess();
    }


    /**
     * 获取商品分类
     * @param shopId
     * @return
     */
    @Override
    public ResultResponse categoryList(Integer shopId) {
        if (shopId == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        List<Category> categoryList = categoryRepository.findByStatus(1);
        return ResultResponse.createBySuccess(categoryList);
    }

    /**
     * 增加或编辑商品
     * @param shopId
     * @param goodsListVO
     * @return
     */
    @Override
    public ResultResponse saveOrEditGoods(Integer shopId, GoodsListVO goodsListVO) {
        if (shopId == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        //如果goodsId为null代表增加，否则为编辑
        Good good = new Good();
        BeanUtils.copyProperties(goodsListVO,good);
        if (goodsListVO.getId() == null){
            int result = goodMapper.insert(good);
            if (result == 0){
                return ResultResponse.createByErrorCodeMessage(GoodsStatusEnum.SAVE_GOODS_ERROR.getStatus(),
                        GoodsStatusEnum.SAVE_GOODS_ERROR.getMessage());
            }
            return ResultResponse.createBySuccess();
        }
        //编辑
        int result = goodMapper.insertSelective(good);
        if (result == 0){
            return ResultResponse.createByErrorCodeMessage(GoodsStatusEnum.SAVE_GOODS_ERROR.getStatus(),
                    GoodsStatusEnum.SAVE_GOODS_ERROR.getMessage());
        }
        return ResultResponse.createBySuccess();
    }

    @Override
    public ResultResponse saveGood(Good good) {
        if(good.getId() == null) {
            good.setCreateTime(new Date());
        }
        return ResultResponse.createBySuccess(ResultEnum.SUCCESS.getMessage(), goodsRepository.saveAndFlush(good));
    }

    /**
     * 根据商品id查询
     * @param shopId
     * @param goodsId
     * @return
     */
    @Override
    public ResultResponse selectGoodsById(Integer shopId, Integer goodsId) {
        if (shopId == null || goodsId == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        Good good = goodMapper.selectByPrimaryKey(goodsId);
        GoodsListVO goodsListVO = new GoodsListVO();
        BeanUtils.copyProperties(good,goodsListVO);
        PetCategory petCategory = petCategoryRepository.findById(good.getCategoryId()).get();
        goodsListVO.setPetCategoryName(petCategory.getName());
        ScopeApplication application = applicationRepository.findById(good.getScopeId()).get();
        goodsListVO.setScopeName(application.getName());
        Brand brand = brandRepository.findById(good.getBrandId()).get();
        goodsListVO.setBrandName(brand.getName());
        //如果无洗澡服务内容则展示所有
        if (StringUtils.isBlank(good.getBathingServiceId())){
            goodsListVO.setBathingServiceList(bathingServiceRepository.findAll());
        }else{
            goodsListVO.setBathingServiceList(bathingServiceRepository.findByIdIn(good.getBathingServiceId()));
        }
        return ResultResponse.createBySuccess(goodsListVO);
    }


    /**
     * 提高系数
     * @param goodsTypeId
     * @return
     */
    @Override
    public ResultResponse improveRatio(Double ratio,Integer goodsTypeId,Integer shopId) {
        if (goodsTypeId == null || shopId == null || ratio == null || ratio <= 0d || ratio>= 10d){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        //全部
        if (goodsTypeId == 0){
            Integer integer = goodsRepository.updateRatioAndShopId(ratio, shopId);
            if (integer == 0){
                return ResultResponse.createByErrorMessage("提高系数失败");
            }
            return ResultResponse.createBySuccess();
        }
        Integer integer = goodsRepository.updateRatioAndGoodsTypeIdAndShopId(ratio, goodsTypeId, shopId);
        if (integer == 0){
            return ResultResponse.createByErrorMessage("提高系数失败");
        }
        return ResultResponse.createBySuccess();
    }

    /**
     * 一键恢复
     * @param shopId
     * @return
     */
    @Override
    public ResultResponse recoverAll(Integer shopId) {
        if (shopId == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        Integer integer = goodsRepository.updateRatio(shopId);
        if (integer == 0){
            return ResultResponse.createByErrorMessage("一键恢复失败");
        }
        return ResultResponse.createBySuccess();
    }

    /**
     * 启用/禁用/删除 商品类别
     * @param goodTypeId
     * @param status
     * @return
     */
    @Override
    public ResultResponse updateGoodTypeStatus(Integer goodTypeId, Integer status) {
        if (goodTypeId == null || status == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        goodsTypeMapper.updateGoodTypeStatus(goodTypeId,status);
        return ResultResponse.createBySuccess();
    }

    /**
     * 获取所有品牌
     * @return
     */
    @Override
    public ResultResponse<List<Brand>> getBrandList(Integer goodsTypeId) {
//        List<Brand> brandList = brandRepository.findAll();
        List<Brand> brandList = brandRepository.findByGoodsTypeId(goodsTypeId).orElse(null);
        return ResultResponse.createBySuccess(brandList);
    }

    /**
     * 获取宠物试用期以及使用范围分类
     * @param petCategoryId
     * @return
     */
    @Override
    public ResultResponse<List<PetCategoryAndScopeVO>> getPetCategory(Integer goodsTypeId,Integer petCategoryId) {
        List<PetCategory> categoryList = petCategoryRepository.findByGoodsTypeId(goodsTypeId);
        List<PetCategoryAndScopeVO> petCategoryAndScopeVOList = Lists.newArrayList();
        categoryList.forEach(e->{
            //填充宠物分类
            PetCategoryAndScopeVO petCategoryAndScopeVO = new PetCategoryAndScopeVO();
            petCategoryAndScopeVO.setId(e.getId());
            petCategoryAndScopeVO.setPetCategoryName(e.getName());
            if (petCategoryId != null){
                //填充适应期分类
                List<ScopeApplication> scopeApplicationList = applicationRepository.findByPetCategoryId(petCategoryId);
                for (ScopeApplication scopeApplication : scopeApplicationList) {
                    if (scopeApplication.getPetCategoryId() == e.getId()) {
                        petCategoryAndScopeVO.setScopeId(scopeApplication.getId());
                        petCategoryAndScopeVO.setScopeName(scopeApplication.getName());
                    }
                }
            }
            petCategoryAndScopeVOList.add(petCategoryAndScopeVO);
        });
        return ResultResponse.createBySuccess(petCategoryAndScopeVOList);
    }

    /**
     * 获取洗澡服务内容
     * @return
     */
    @Override
    public ResultResponse getBathingService() {
        List<BathingService> bathingServiceList = bathingServiceRepository.findAll();
        return ResultResponse.createBySuccess(bathingServiceList);
    }

    /**
     * 获取适用期和适用类型
     * @param goodsTypeId
     * @param brandId
     * @return
     */
    @Override
    public ResultResponse getScopeType(Integer goodsTypeId, Integer brandId) {
        if (goodsTypeId == null && brandId == null){
            return ResultResponse.createBySuccess();
        }

        List<ScopeApplication> scopeApplicationList = null;
        List<PetCategory> petCategoryList = null;
        //狗粮
        if (goodsTypeId == 1) {
            //获取狗粮适用类型
            petCategoryList = petCategoryRepository.findByGoodsTypeId(2);
            //获取狗粮适用期
            scopeApplicationList = scopeApplicationRepository.findAll();
//            scopeApplicationList = scopeApplicationRepository.findByBrandIdAndType(brandId,2);
        }
        //猫粮
        else if (goodsTypeId == 2) {
            //猫粮没有适用范围
            //获取猫粮适用期
            scopeApplicationList = scopeApplicationRepository.findByBrandIdAndType(brandId,1);
        }
        ScopeVO scopeVO = new ScopeVO();
        scopeVO.setPetCategoryList(petCategoryList);
        scopeVO.setScopeApplicationList(scopeApplicationList);
        return ResultResponse.createBySuccess(scopeVO);
    }
}
