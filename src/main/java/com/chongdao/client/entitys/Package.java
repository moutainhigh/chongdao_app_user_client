package com.chongdao.client.entitys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/** 
 * @Author onlineS
 * @Description 礼包
 * @Date 9:03 2019/4/19
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "package")
public class Package implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;
	private Integer good_id;//商品id
	private String name;//名字
	private String description;//产品描述
	private BigDecimal displayPrice;//例如360元得510元礼包, 这个值就是510
	private BigDecimal price;//价格
	private Integer status;//-1:删除;0:下架;1:上架
	private Integer type;

	private String startTime;//有效时间
	private String endTime;//失效时间
	private Date createTime;
	private Date updateTime;

	@Override
	public String toString() {
		return "Package{" +
				"id=" + id +
				", good_id=" + good_id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", displayPrice=" + displayPrice +
				", price=" + price +
				", status=" + status +
				", type=" + type +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				'}';
	}
}