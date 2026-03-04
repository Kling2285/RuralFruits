package com.example.ruralfruits.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("r_notice")
public class Notice {
  @TableId(type = IdType.AUTO)
  private long id;
  private String title;
  private String content;
  private String time;
  private Long open;
  @TableField(exist = false)
  private String username;
  private Date updateTime=new Date();
}