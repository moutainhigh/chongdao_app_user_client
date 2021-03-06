package com.chongdao.client.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/10/18
 * @Version 1.0
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopBillVO {
    private Integer id;
    private Integer shopId;
    private Integer orderId;
    private BigDecimal price;
    private String note;
    private Integer type;//1:订单消费, 2:订单退款, 3:店铺提现, 4:医疗费用订单, 5: 追加订单
    private Date createTime;
    private String userName;
}
