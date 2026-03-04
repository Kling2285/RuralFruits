package com.example.ruralfruits.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser extends BaseEntity implements Serializable {
    @TableId(value = "user_id",type = IdType.AUTO)
    @Alias("编号")
    private Integer userId;
    @Alias("用户名")
    private String username;
    private String password;
    @Alias("昵称")
    private String nickname;
    private String avatar;
    private Integer sex;
    private String phone;
    private String email;
    private Integer status=0;
    private Integer deleted=0;
    private Date createTime=new Date();
    private Date updateTime=new Date();
    private String type;
    private BigDecimal restMoney = BigDecimal.ZERO;
    @TableField(exist = false)
    private String token;
}
