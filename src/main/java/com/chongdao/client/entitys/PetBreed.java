package com.chongdao.client.entitys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description 宠物品种
 * @Author onlineS
 * @Date 2019/5/30
 * @Version 1.0
 **/
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetBreed implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String name;
    private Integer type;//1:狗;2:猫;
}
