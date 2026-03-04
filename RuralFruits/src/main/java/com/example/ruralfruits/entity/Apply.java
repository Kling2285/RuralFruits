package com.example.ruralfruits.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("r_apply")
public class Apply {
  @TableId(type = IdType.AUTO)
  private long id;
  private long userId;
  private long auditorId;
  private Date applyTime=new Date();
  private Date auditTime=new Date();
  private long auditStatus;
}
