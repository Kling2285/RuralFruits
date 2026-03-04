package com.example.ruralfruits.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("r_products")
public class Products extends BaseEntity{
  @TableId(type = IdType.AUTO)
  private Long id;
  private String name;
  private Double price;
  private String description;
  private String category;
  @TableField("image_url")
  @JsonProperty("image_url")
  private String imageUrl;
  private String producer;
  private String unit;
  private long sellCount;
  private String content;
  private Long stock;
  private String status="0";
  // 关键修改：把 long 改成 Long（包装类）
  @TableField("user_id") // 显式映射表字段（确保拿到值）
  @JsonProperty("user_id")
  private Long userId;
  @TableField(exist = false)
  private SysUser sysUser;//数据关联
  private String createUser;
  private Date createTime=new Date();
  private Date updateTime=new Date();
  private long deleted=0;


}
