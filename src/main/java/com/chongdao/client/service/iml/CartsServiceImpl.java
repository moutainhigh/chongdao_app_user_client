package com.chongdao.client.service.iml;

import com.chongdao.client.common.Const;
import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.Carts;
import com.chongdao.client.entitys.Good;
import com.chongdao.client.entitys.OrderDetail;
import com.chongdao.client.entitys.OrderInfo;
import com.chongdao.client.enums.GoodsStatusEnum;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.mapper.CartsMapper;
import com.chongdao.client.mapper.GoodMapper;
import com.chongdao.client.repository.CartRepository;
import com.chongdao.client.repository.GoodsRepository;
import com.chongdao.client.repository.OrderDetailRepository;
import com.chongdao.client.repository.OrderInfoRepository;
import com.chongdao.client.service.CartsService;
import com.chongdao.client.utils.BigDecimalUtil;
import com.chongdao.client.vo.CartGoodsVo;
import com.chongdao.client.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class CartsServiceImpl implements CartsService {

    @Autowired
    private CartsMapper cartsMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderInfoRepository orderInfoRepository;





    /**
     * 加入购物车,同时校验用户是否登录
     * @param userId
     * @param count
     * @param goodsId
     * @return
     */
    @Transactional
    @Override
    public ResultResponse<CartVo> add(Integer userId, Integer count, Integer goodsId,Integer shopId, Integer petId,Integer petCount) {
        if (count == null || goodsId == null || shopId == null){
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),ResultEnum.PARAM_ERROR.getMessage());
        }
        //查询当前用户的购物车是否存在该商品
        Carts cart = cartsMapper.selectCartByUserIdAndGoodsId(userId,goodsId,shopId,petId);
        if (cart == null){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Carts cartItem = new Carts();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setGoodsId(goodsId);
            cartItem.setUserId(userId);
            cartItem.setCreateTime(new Date());
            cartItem.setUpdateTime(new Date());
            cartItem.setShopId(shopId);
            if (petId != null && petCount != null) {
                cartItem.setPetCount(petCount);
                cartItem.setPetId(petId);
            }
            cartsMapper.insert(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            if (petId != null && petCount != null) {
                petCount =  cart.getPetCount() + petCount;
                cart.setPetCount(petCount);
                cart.setPetId(petId);
            }
            cart.setUpdateTime(new Date());
            cartsMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId,shopId);
    }

    /**
     * 再来一单（添加购物车）
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ResultResponse anotherAdd(Integer userId, String orderNo,Integer shopId) {
        //再来一单为优先，清空之前选中的商品
        cartRepository.deleteByUserIdAndShopId(userId, shopId);
        List<OrderDetail> orderDetailList = orderDetailRepository.findByUserIdAndOrderNo(userId, orderNo);
        OrderInfo orderInfo = orderInfoRepository.findByOrderNo(orderNo);
        for (OrderDetail orderDetail : orderDetailList) {
            //排除已下架或已删除的商品
            Good good = goodsRepository.findByIdAndStatus(orderDetail.getGoodId(), (byte) 1);
            if (good == null){
                return ResultResponse.createByErrorCodeMessage(GoodsStatusEnum.GOODS_NOT_EXIST.getStatus(),GoodsStatusEnum.GOODS_NOT_EXIST.getMessage());
            }
            Carts cartItem = new Carts();
            cartItem.setQuantity(orderDetail.getCount());
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setGoodsId(orderDetail.getGoodId());
            cartItem.setUserId(userId);
            cartItem.setCreateTime(new Date());
            cartItem.setUpdateTime(new Date());
            cartItem.setShopId(shopId);
            if (StringUtils.isNotBlank(orderInfo.getPetId())){
                List<String> petIds = Splitter.on(",").trimResults().splitToList(orderInfo.getPetId());
                petIds.stream().forEach(petId -> {
                    cartItem.setPetId(Integer.valueOf(petId));
                    cartItem.setPetCount(1);
                });
            }
            int result = cartsMapper.insert(cartItem);
            if (result > 0){
                return list(userId,shopId);
            }
        }
        return ResultResponse.createByErrorCodeMessage(GoodsStatusEnum.GOODS_NOT_EXIST.getStatus(),GoodsStatusEnum.GOODS_NOT_EXIST.getMessage());
    }

    /**
     * 查询购物车
     * @param userId
     * @return
     */
    @Override
    public ResultResponse<CartVo> list(Integer userId,Integer shopId) {
        CartVo cartVo = this.getCartVoLimit(userId,shopId);
        return ResultResponse.createBySuccess(cartVo);
    }

    /**
     * 删除购物车
     * @param userId
     * @param goodsIds
     * @return
     */
    @Transactional
    @Override
    public ResultResponse<CartVo> deleteGoods(Integer userId, Integer goodsIds,Integer shopId,Integer petId) {
        if (userId == null || goodsIds == null || shopId == null) {
            return ResultResponse.createByErrorCodeMessage(400, "goodsIds或shopId不能为空");
        }
        Carts carts = cartsMapper.selectCartByUserIdAndGoodsId(userId, Integer.valueOf(goodsIds), shopId,petId);
        if (carts == null) {
            return ResultResponse.createByErrorMessage("无法删除，该服务不存在");
        }
        if (carts.getQuantity() > 1) {
            //更新数量
            cartsMapper.updateCartByUserIdAndGoodsId(userId,goodsIds,shopId,petId);
        }else {
            //删除
            cartsMapper.deleteByUserIdAndProductIds(userId, shopId, goodsIds,petId);
        }
        return this.list(userId,shopId);
    }

    /**
     * 清空购物车
     * @param userId
     * @param shopId
     * @return
     */
    @Override
    public ResultResponse<CartVo> clear(Integer userId, Integer shopId) {
        if (userId == null || shopId == null) {
            return ResultResponse.createByErrorCodeMessage(400, "shopId不能为空");
        }
        cartsMapper.clearCart(userId,shopId);
        return this.list(userId,shopId);
    }


    /**
     * 根据userId封装当前购物车信息
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId,Integer shopId){
        CartVo cartVo = new CartVo();
        //查询当前用户的购物车
        List<Carts> cartList = cartsMapper.selectCartByUserId(userId,shopId);
        List<CartGoodsVo> cartGoodsVoList = Lists.newArrayList();
        //购物车总价
        BigDecimal cartTotalPrice = new BigDecimal(BigInteger.ZERO);
        //购物车总数量
        Integer cartsGoodsCount = 0;
        if (!CollectionUtils.isEmpty(cartList)){
            for (Carts cart : cartList) {
                CartGoodsVo cartGoodsVo = new CartGoodsVo();
                //将cart值拷贝到cartGoodsVo
                BeanUtils.copyProperties(cart, cartGoodsVo);
                //查询商品
                Good good = goodMapper.selectByPrimaryKey(cart.getGoodsId());
                if (good != null){
                    cartGoodsVo.setGoodsName(good.getName());
                    cartGoodsVo.setGoodsPrice(good.getPrice());
                    cartGoodsVo.setGoodsStatus(Integer.valueOf(good.getStatus()));
                    cartGoodsVo.setCategoryId(good.getCategoryId());
                    cartGoodsVo.setPetCount(cart.getPetCount());
                    cartGoodsVo.setPetId(cart.getPetId());
                    //用户购买的商品数量
                    cartGoodsVo.setQuantity(cart.getQuantity());
                    cartGoodsVo.setGoodsChecked(cart.getChecked());
                    //计算总价
                    cartGoodsVo.setGoodsTotalPrice(BigDecimalUtil.mul(good.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
                    //折扣设置为原价（前端要求）
                    cartGoodsVo.setDiscountPrice(good.getPrice());
                    //折扣价
                    if (good.getDiscount() != null && good.getDiscount() > 0.0d) {
                        cartGoodsVo.setDiscountPrice(good.getPrice().multiply(BigDecimal.valueOf(good.getDiscount()/10)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        cartGoodsVo.setDiscount(good.getDiscount());
                    }
                    cartGoodsVo.setReDiscount(good.getReDiscount());
                }
                //如果用户选中商品，则价格相加
                if (cart.getChecked() == Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartGoodsVo.getGoodsTotalPrice().doubleValue());
                }
                cartGoodsVoList.add(cartGoodsVo);
                cartsGoodsCount = cart.getQuantity() + cartsGoodsCount;
            }

        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartGoodsVoList(cartGoodsVoList);
        cartVo.setCount(cartsGoodsCount);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        return cartVo;
    }

    /**
     * 获取所有选中状态的商品
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if (userId == null){
            return false;
        }
        return cartsMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
