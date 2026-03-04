package com.example.ruralfruits.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("r_categories")
public class Categories {
  @TableId(type = IdType.AUTO)
  private long id;
  private String name;
  private long deleted;
  private String createTime;
  private String updateTime;

}
