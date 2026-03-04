package com.example.ruralfruits.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("r_cart")
public class Cart {

  @TableId(type = IdType.AUTO)
  private long id;
  private String name;
  private String type;
  private String price;
  private String unit;
  private long userId;
  private Date createTime =new Date();
  private String createUser;
  private long status;
  private String updateTime;
  private String updateUser;
  private long goodsId;
  private java.sql.Timestamp payTime;
  private long count;
  private String image;
}
