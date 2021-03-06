package com.chongdao.client.service;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.InsuranceShopChip;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public interface ShopChipService {

    /**
     * 导入宠物芯片数据(通过excel)
     * @param token
     * @param file
     * @return
     * @throws IOException
     */
    ResultResponse importShopChipData(String token, MultipartFile file) throws IOException;

    /**
     * 获取宠物芯片数据
     * @return
     */
    ResultResponse getShopChipData(String token, String core, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize);

    /**
     * 获取指定医院的可用芯片数据
     * @param shopId
     * @param core
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultResponse getShopChipAppointShop(Integer shopId, String core, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 新增/编辑
     * @param insuranceShopChip
     * @return
     */
    ResultResponse addShopChip(InsuranceShopChip insuranceShopChip);

    ResultResponse batchAddShopChip(String[] cores, Integer shopId);

    /**
     * 删除
     * @param insuranceShopChipId
     * @return
     */
    ResultResponse removeShopChop(Integer insuranceShopChipId);

    /**
     * 发起芯片核销
     * @param insuranceShopChipId
     * @return
     */
    ResultResponse startShopChipVerify(Integer insuranceShopChipId);

    /**
     * 确认芯片核销
     * @param insuranceShopChipId
     * @return
     */
    ResultResponse confirmShopChipVerify(Integer insuranceShopChipId);

    /**
     * 下载模版
     * @param request
     * @param response
     */
    void downloadTemplate(HttpServletRequest request, HttpServletResponse response);
}
