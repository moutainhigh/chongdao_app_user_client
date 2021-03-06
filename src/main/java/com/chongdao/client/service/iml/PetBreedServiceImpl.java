package com.chongdao.client.service.iml;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.mapper.PetBreedMapper;
import com.chongdao.client.service.PetBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/8/1
 * @Version 1.0
 **/
@Service
public class PetBreedServiceImpl implements PetBreedService {
    @Autowired
    private PetBreedMapper petBreedMapper;

    @Override
    public ResultResponse getPetBreedByType(Integer type, String name, Integer pageNum, Integer pageSize) {
        return ResultResponse.createBySuccess(ResultEnum.SUCCESS.getMessage(), petBreedMapper.getDataByTypeAndName(type, name, pageNum, pageSize));
    }
}
