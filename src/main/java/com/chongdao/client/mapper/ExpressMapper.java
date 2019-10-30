package com.chongdao.client.mapper;

import com.chongdao.client.vo.ExpressStaticsVO;
import com.chongdao.client.vo.ExpressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExpressMapper {
    List<ExpressVO> getExpressListByAreaCodeAndName(@Param("areaCode") String areaCode, @Param("name") String name, @Param("type") Integer type, @Param("status") Integer status);

    ExpressStaticsVO getCompleteOrderStatics(@Param("expressId") Integer expressId);

    ExpressStaticsVO getCompleteOrderStaticsByType(@Param("expressId") Integer expressId, @Param("type") Integer type);
}