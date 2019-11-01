package com.chongdao.client.service;

import com.chongdao.client.common.ResultResponse;

public interface InsuranceTeamService {
    /**
     * 发起组队
     * @param builderId
     */
    ResultResponse buildInsuranceTeam(Integer builderId) throws Exception;

    /**
     * 访问组队分享页面
     * @param builderId
     */
    ResultResponse visitRecommendUrl(Integer builderId);

    /**
     * 注册并参加组队
     * @param phone
     * @param code
     * @param teamId
     * @return
     */
    ResultResponse signAndAttendInsuranceTeam(String phone, String code, Integer teamId);

    /**
     * 参加组队
     * @param teamId
     * @param attenderId
     */
    ResultResponse attendInsuranceTeam(Integer teamId, Integer attenderId);

    /**
     * 获取我待确认的组队
     * @param userId
     * @return
     */
    ResultResponse getMyTodoAttend(Integer userId);

    /**
     * 确认参加活动
     * @param attenderId
     */
    ResultResponse confirmAttend(Integer attenderId);

    /**
     * 获取我的组队详情
     * @param builderId
     * @return
     */
    ResultResponse getAttendDetail(Integer builderId);

    /**
     * 系统自动填补组队人员(除开每第100/200/300...整数位队员所在队伍外, 均需要添加2个机器人, 且开奖都由这2个机器人中奖)
     * @return
     */
    ResultResponse systemAutoAddAttender(Integer teamId);

    /**
     * 开奖
     * @return
     */
    ResultResponse systemLuckyDraw(Integer teamId);
}