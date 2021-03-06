package com.chongdao.client.service.iml;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.UserBank;
import com.chongdao.client.repository.UserBankRepository;
import com.chongdao.client.service.UserBankService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/12/3
 * @Version 1.0
 **/
@Service
public class UserBankServiceImpl implements UserBankService {
    @Autowired
    private UserBankRepository userBankRepository;

    @Override
    @Transactional
    public ResultResponse addUserBankService(UserBank userBank) {
        Integer userId = userBank.getUserId();
        Integer id = userBank.getId();
        if(id != null) {
            //保存
            return ResultResponse.createBySuccess(userBankRepository.save(userBank));
        } else {
            List<UserBank> list = userBankRepository.findByUserId(userId);
            if(list.size() > 0) {
                //如果存在旧的银行卡, 那么新添加将删除以前的记录
                for(UserBank ub : list) {
                    userBankRepository.delete(ub);
                }
            }
            //新增
            UserBank newOne = new UserBank();
            BeanUtils.copyProperties(userBank, newOne);
            userBank.setCreateTime(new Date());
            return ResultResponse.createBySuccess(userBankRepository.save(userBank));
        }
    }

    @Override
    public ResultResponse getUserBankList(Integer userId) {
        return ResultResponse.createBySuccess(userBankRepository.findByUserId(userId));
    }
}
