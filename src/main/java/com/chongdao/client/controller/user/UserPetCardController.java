package com.chongdao.client.controller.user;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.PetCard;
import com.chongdao.client.service.PetCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/5/6
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/petCard")
public class UserPetCardController {
    @Autowired
    private PetCardService petCardService;

    /**
     *  获取用户的宠物卡片列表
     * @param userId
     * @param status
     * @return
     */
    @GetMapping("/getPetCardList")
    public ResultResponse<List<PetCard>> getPetCardList(Integer userId, Integer status) {
        return petCardService.getPetCardByUserIdAndStatus(userId, status);
    }

    /**
     * 获取宠物卡片详细信息
     * @param cardId
     * @return
     */
    @GetMapping("/getPetCardInfo")
    public ResultResponse<PetCard> getPetCardInfo(Integer cardId) {
        return petCardService.getPetCardById(cardId);
    }

    /**
     * 保存宠物卡片信息
     * @param petCard
     * @return
     */
    @GetMapping("/savePetCard")
    public ResultResponse savePetCard(PetCard petCard) {
        return petCardService.savePetCard(petCard);
    }
}
