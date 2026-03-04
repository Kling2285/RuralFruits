package com.example.ruralfruits.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("r_banner")
public class Banner extends BaseEntity{

  @TableId(type = IdType.AUTO)
  private long id;
  private String bannerImg;
  private String createUser;
  private String createTime;

}
