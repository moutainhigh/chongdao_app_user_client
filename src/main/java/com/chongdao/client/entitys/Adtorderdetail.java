package com.chongdao.client.entitys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the adtorderdetail database table.
 * 
 */
@Entity
@Setter
@NoArgsConstructor
@Getter
@Table(name="adtorderdetail")
public class Adtorderdetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Integer id;
	private Integer adtorderid;
	private Integer createby;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdate;
	private Double discount;
	private BigDecimal discountprice;
	private Integer goodsamount;
	private Integer goodsid;
	private BigDecimal goodsprice;
	private String note;
	private BigDecimal price;
	private Integer shopid;
	private Integer updateby;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedate;
	private Integer version;

	@Override
	public String toString() {
		return "Adtorderdetail{" +
				"id=" + id +
				", adtorderid=" + adtorderid +
				", createby=" + createby +
				", createdate=" + createdate +
				", discount=" + discount +
				", discountprice=" + discountprice +
				", goodsamount=" + goodsamount +
				", goodsid=" + goodsid +
				", goodsprice=" + goodsprice +
				", note='" + note + '\'' +
				", price=" + price +
				", shopid=" + shopid +
				", updateby=" + updateby +
				", updatedate=" + updatedate +
				", version=" + version +
				'}';
	}
}