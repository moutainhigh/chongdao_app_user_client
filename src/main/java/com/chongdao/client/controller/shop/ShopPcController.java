package com.chongdao.client.controller.shop;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.GoodsType;
import com.chongdao.client.entitys.InsuranceFeeRecord;
import com.chongdao.client.entitys.InsuranceShopChip;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.service.*;
import com.chongdao.client.service.insurance.InsuranceService;
import com.chongdao.client.utils.LoginUserUtil;
import com.chongdao.client.vo.AddShopChipVO;
import com.chongdao.client.vo.ResultTokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @Description 商家PC端
 * @Author onlineS
 * @Date 2019/6/10
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/shop_pc/")
public class ShopPcController {
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private GoodsTypeService goodsTypeService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private ShopBillService shopBillService;
    @Autowired
    private ShopApplyService shopApplyService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private InsuranceFeeRecordService insuranceFeeRecordService;
    @Autowired
    private ShopChipService shopChipService;
    @Autowired
    private GoodTasteService goodTasteService;
    @Autowired
    private UserService userService;
    @Autowired
    private InsuranceService insuranceService;

    @GetMapping("getMyDetailInfo")
    public ResultResponse getMyDetailInfo(String token) {
        return shopService.getShopInfo(token);
    }

    @GetMapping("getModuleData")
    public ResultResponse getModuleData(String token) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        String role = tokenVo.getRole();
        if (role != null) {
            return moduleService.getModuleData();
        } else {
            return ResultResponse.createByErrorCodeMessage(ResultEnum.ERROR.getStatus(), ResultEnum.ERROR.getMessage());
        }
    }

    @GetMapping("getCategoryData")
    public ResultResponse getCategoryData(String token) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        String role = tokenVo.getRole();
        if (role != null && role.equals("SHOP_PC")) {
            return categoryService.getCategoryData();
        } else {
            return ResultResponse.createByErrorCodeMessage(ResultEnum.ERROR.getStatus(), ResultEnum.ERROR.getMessage());
        }
    }

    @RequestMapping(value = "addGoodsType", method = RequestMethod.POST)
    @ResponseBody
    public ResultResponse addGoodsType(GoodsType goodsType) {
        return goodsTypeService.addGoodsType(goodsType);
//        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
//        String role = tokenVo.getRole();
//        if(role != null && role.equals("SHOP_PC")) {
//            return goodsTypeService.addGoodsType(goodsType);
//        } else {
//            return ResultResponse.createByErrorCodeMessage(ResultEnum.ERROR.getStatus(), ResultEnum.ERROR.getMessage());
//        }
    }

    /**
     * 获取规格单位
     *
     * @param moduleId
     * @param categoryId
     * @return
     */
    @GetMapping("getUnitList")
    public ResultResponse getUnitList(Integer moduleId, Integer categoryId) {
        return unitService.getUnitList(moduleId, categoryId);
    }

    @GetMapping("getGoodTasteList")
    public ResultResponse getGoodTasteList() {
        return goodTasteService.getGoodTasteList();
    }

    /**
     * getGoodCategoryList
     *
     * @param moduleId
     * @param categoryId
     * @return
     */
    @GetMapping("getSelectGoodsTypeSpecialConfig")
    public ResultResponse getSelectGoodsTypeSpecialConfig(Integer moduleId, Integer categoryId, Integer goodsTypeId) {
        return goodsTypeService.getSelectGoodsTypeSpecialConfig(moduleId, categoryId, goodsTypeId);
    }

    /**
     * 获取商店的流水记录
     *
     * @param token
     * @param startDate
     * @param endDate
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("getShopBillListData")
    public ResultResponse getShopBillListData(String token, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return shopBillService.getShopBillByShopId(tokenVo.getUserId(), startDate, endDate, pageNum, pageSize);
    }

    /**
     * 获取商店提现记录
     *
     * @param token
     * @param startDate
     * @param endDate
     * @param pageSize
     * @param pageNum
     * @return
     */
    @PostMapping("getShopApplyListData")
    public ResultResponse getShopApplyListData(String token, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        ResultTokenVo tokenVo = LoginUserUtil.resultTokenVo(token);
        return shopApplyService.getShopApplyList(tokenVo.getUserId(), null, null, null, startDate, endDate, pageNum, pageSize);
    }

    /**
     * 获取使用过优惠券的订单
     *
     * @param token
     * @param orderNo
     * @param username
     * @param phone
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("getConcessionalOrderListShop")
    public ResultResponse getConcessionalOrderListShop(String token, String orderNo, String username, String phone, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        return orderService.getConcessionalOrderList(token, null, orderNo, username, phone, startDate, endDate, pageNum, pageSize);
    }

    /**
     * 获取店铺各类型订单消费明细
     *
     * @param token
     * @param type(1:配送商品订单,2:配送服务订单,3:到店订单)
     * @param userName
     * @param orderNo
     * @param phone
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("getMoneyOrderList")
    public ResultResponse getMoneyOrderList(String token, Integer type, String userName, String orderNo, String phone, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        return null;
    }

    /**
     * 获取店铺医疗费用消费明细
     *
     * @param token
     * @param userName
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("getInsuranceFeeRecordData")
    public ResultResponse getInsuranceFeeRecordData(String token, String userName, String shopName, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        return insuranceFeeRecordService.getInsuranceFeeRecordData(token, userName, shopName, status, startDate, endDate, pageNum, pageSize);
    }

    /**
     * 根据手机号获取用户
     * @param phone
     * @return
     */
    @PostMapping("getUserByPhone")
    public ResultResponse getUserByPhone(String phone) {
        return userService.getUserByPhone(phone);
    }

    /**
     * 根据用户ID获取该用户已经付款的保单
     * @param userId
     * @return
     */
    @PostMapping("getEffectedInsuranceOrderByUserId")
    public ResultResponse getEffectedInsuranceOrderByUserId(Integer userId) {
        return insuranceService.getEffectedInsuranceOrderByUserId(userId);
    }

    /**
     * 添加保险医疗费用记录
     * @return
     */
    @PostMapping("addInsuranceFeeRecord")
    public ResultResponse addInsuranceFeeRecord(@RequestBody InsuranceFeeRecord insuranceFeeRecord) {
        return insuranceFeeRecordService.addInsuranceFeeRecord(insuranceFeeRecord);
    }

    /**
     * 获取店铺推广奖励明细
     *
     * @param token
     * @param userName
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("getMoneyRecommendList")
    public ResultResponse getMoneyRecommendList(String token, String userName, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        return null;
    }

    /**
     * 下载模版
     * @param request
     * @param response
     */
    @GetMapping("downloadTemplate")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        shopChipService.downloadTemplate(request, response);
    }

    /**
     * 导入宠物芯片数据
     * @param request
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("importShopChipData")
    public ResultResponse importShopChipData(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        return shopChipService.importShopChipData(request.getHeader("token"), file);
    }

    /**
     * 添加/编辑宠物芯片
     * @param insuranceShopChip
     * @return
     */
    @PostMapping("addShopChip")
    public ResultResponse addShopChip(@RequestBody InsuranceShopChip insuranceShopChip) {
        return shopChipService.addShopChip(insuranceShopChip);
    }

    /**
     * 批量添加宠物芯片
     * @param addShopChipVO
     * @return
     */
    @PostMapping("batchAddShopChop")
    public ResultResponse batchAddShopChop(@RequestBody AddShopChipVO addShopChipVO) {
        return shopChipService.batchAddShopChip(addShopChipVO.getCores(), addShopChipVO.getShopId());
    }

    /**
     * 删除芯片
     * @param insuranceShopChipId
     * @return
     */
    @PostMapping("removeShopChip")
    public ResultResponse removeShopChip(Integer insuranceShopChipId) {
        return shopChipService.removeShopChop(insuranceShopChipId);
    }

    /**
     * 获取宠物芯片数据列表
     * @return
     */
    @PostMapping("getShopChipData")
    public ResultResponse getShopChipData(String token, String core, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        return shopChipService.getShopChipData(token, core, status, startDate, endDate, pageNum, pageSize);
    }

    /**
     * 商家发起宠物核销
     * @return
     */
    @PostMapping("startChipVerify")
    public ResultResponse startChipVerify(Integer shopChipId) {
        return shopChipService.startShopChipVerify(shopChipId);
    }

    @PostMapping("getShopBillList")
    public ResultResponse getShopBillList(Integer shopId, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        return shopBillService.getShopBillByShopId(shopId, startDate, endDate, pageNum, pageSize);
    }
}