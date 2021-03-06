package com.chongdao.client.controller.order;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.OrderInfo;
import com.chongdao.client.entitys.Shop;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.repository.OrderInfoRepository;
import com.chongdao.client.repository.ShopRepository;
import com.chongdao.client.service.OrderService;
import com.chongdao.client.service.ServiceRuleService;
import com.chongdao.client.service.SmsService;
import com.chongdao.client.service.insurance.InsuranceService;
import com.chongdao.client.utils.LoginUserUtil;
import com.chongdao.client.vo.OrderCommonVO;
import com.chongdao.client.vo.OrderEvalVO;
import com.chongdao.client.vo.OrderVo;
import com.chongdao.client.vo.ResultTokenVo;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/order/")
@Slf4j
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ServiceRuleService serviceRuleService;

    @Autowired
    private InsuranceService insuranceService;



    /**
     * 预下单/提交订单
     *  orderType 1代表预下单 2代表下单 3 拼单
     *  serviceType 服务类型 1.双程 2.单程 3.到店自取
     * @return
     */
    @PostMapping("preOrCreateOrder")
    public ResultResponse<OrderVo> preOrCreateOrder(String token, OrderCommonVO orderCommonVO){
        //校验用户是否登录
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return orderService.preOrCreateOrder(tokenVo.getUserId(),orderCommonVO);
    }

    /**
     * 追加订单
     * @param
     * @param token
     * @param shopId
     * @param orderType (4)
     * @return
     */
    @PostMapping("reAddOrder")
    public ResultResponse<OrderVo> reAddOrder(@RequestParam String token, @RequestParam String orderNo,
                                              @RequestParam Integer shopId, @RequestParam Integer orderType,
                                              @RequestParam BigDecimal totalPrice){
        //校验用户是否登录
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return orderService.reAddOrder(tokenVo.getUserId(),orderNo,shopId,orderType,totalPrice);
    }


    /**
     * 再来一单
     * @param orderNo
     * @param token
     * @return
     */
    @GetMapping("anotherOrder/{shopId}")
    public ResultResponse anotherOrder(@PathVariable Integer shopId,String orderNo,String token){
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return orderService.anotherOrder(tokenVo.getUserId(),orderNo,shopId);
    }


    /**
     * 根据type获取相应的订单
     * @param type 1:已支付未接单,2:服务中,3.已完成
     * @return
     */
    @GetMapping("orderTypeList")
    public ResultResponse<PageInfo> getOrderTypeList(@RequestParam("type") String type,
                                                     @RequestParam("token") String token,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return orderService.getOrderTypeList(tokenVo.getUserId(), type,pageNum,pageSize);
    }


    /**
     * 订单详情
     * @param token
     * @param orderNo
     * @return
     */
    @GetMapping("orderDetail")
    public ResultResponse orderDetail(@RequestParam("token") String token,
                                      @RequestParam("orderNo") String orderNo){

        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return orderService.orderDetail(tokenVo.getUserId(), orderNo);
    }

    /**
     * 评价晒单 初始化
     * @param orderNo
     * @param token
     * @return
     */
    @GetMapping("initOrderEval")
    public ResultResponse initOrderEval(String orderNo,String token){
        LoginUserUtil.resultTokenVo(token);
        return orderService.initOrderEval(orderNo);
    }

    /**
     * 订单评价
     * @param orderEvalVO
     * @return
     */
    @PostMapping("orderEval")
    public ResultResponse evalOrder(@Valid OrderEvalVO orderEvalVO,
                                    BindingResult bindingResult){
        LoginUserUtil.resultTokenVo(orderEvalVO.getToken());
        if (bindingResult.hasErrors()){
            log.error("【订单评价】参数不正确，orderEvalVO={}:",orderEvalVO);
            return ResultResponse.createByErrorCodeMessage(ResultEnum.PARAM_ERROR.getStatus(),bindingResult.getFieldError().getDefaultMessage());
        }
        return orderService.orderEval(orderEvalVO);
    }

    /**
     * 用户申请退款
     * @param orderNo
     * @param token
     * @return
     */
    @PostMapping("refund")
    public ResultResponse refund(String orderNo,String token){
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return orderService.refund(tokenVo.getUserId(), orderNo);
    }


    /**
     * 催单
     * @param orderNo
     * @param token
     * @return
     */
    @GetMapping("reminder")
    public ResultResponse reminder(String orderNo,String token){
        LoginUserUtil.resultTokenVo(token);
        OrderInfo orderInfo = orderInfoRepository.findByOrderNo(orderNo);
        if (orderInfo != null) {
            Shop shop = shopRepository.findById(orderInfo.getId()).orElse(null);
            if (shop != null){
                smsService.sendUserReminder(orderNo,shop.getPhone());
            }
        }
        return ResultResponse.createBySuccess();
    }

    /**
     * 获取配送规则页面所需详细参数
     * @param originServicePrice - 订单总配送价格(未减免)
     * @param serviceType - 订单服务类型(单程/双程/到店)
     * @param isService - 订单类型(服务/商品)
     * @param areaCode - 订单所需区域码
     * @param serviceDistance - 订单配送距离
     * @return
     */
    @PostMapping("getServiceRuleInfo")
    public ResultResponse getServiceRuleInfo(BigDecimal originServicePrice, Integer serviceType, Integer isService, String areaCode, BigDecimal serviceDistance) {
        return serviceRuleService.getServiceRuleInfo(originServicePrice, serviceType, isService, areaCode, serviceDistance);
    }

    /**
     * 获取订单是否购买了运输险
     * @param orderNo
     * @return
     */
    @PostMapping("getMyOrderIsBuyInsurance")
    public ResultResponse getMyOrderIsBuyInsurance(String orderNo) {
        return insuranceService.getMyOrderIsBuyInsurance(orderNo);
    }

    /**
     * 获取我的订单运输险列表
     * @return
     */
    @PostMapping("getMyPickupInsuranceOrderList")
    public ResultResponse getMyPickupInsuranceOrderList(String orderNo) {
        return insuranceService.getMyPickupInsuranceOrderList(orderNo);
    }
}
