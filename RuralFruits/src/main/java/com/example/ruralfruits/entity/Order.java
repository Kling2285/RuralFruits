package com.example.ruralfruits.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.io.Serializable; // 需导入Serializable接口

@Data
@TableName("r_order")
// 继承BaseEntity（BaseEntity已实现Serializable，Order无需重复实现）
public class Order extends BaseEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String orderId;
  private long productId;
  private String name;
  private String image;
  private long count;
  private double price;
  private String unit;
  private String linkUser;
  private String linkAddress;
  private String linkPhone;
  private String status;
  private Date createTime;
  @TableField("user_id") // 显式映射表字段（确保拿到值）
  @JsonProperty("user_id")
  private Long userId;
  @TableField(exist = false)
  private SysUser sysUser;//数据关联
}