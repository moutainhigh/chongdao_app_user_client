package com.chongdao.client.service.iml;

import com.chongdao.client.common.ResultResponse;
import com.chongdao.client.entitys.PetBreed;
import com.chongdao.client.enums.ResultEnum;
import com.chongdao.client.repository.PetBreedRepository;
import com.chongdao.client.service.PetBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/8/1
 * @Version 1.0
 **/
@Service
public class PetBreedServiceImpl implements PetBreedService {
    @Autowired
    private PetBreedRepository petBreedRepository;

    @Override
    public ResultResponse getPetBreedByType(Integer type, String name) {
        List<PetBreed> dataByTypeAndName = petBreedRepository.getDataByTypeAndName(type, name);
        return ResultResponse.createBySuccess(ResultEnum.SUCCESS.getMessage(), petBreedRepository.getDataByTypeAndName(type, name));
    }
}
