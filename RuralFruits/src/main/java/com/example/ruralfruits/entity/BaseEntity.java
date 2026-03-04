package com.example.ruralfruits.entity;

import cn.hutool.core.annotation.PropIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class BaseEntity implements Serializable {
    @TableField(exist = false)
    @PropIgnore
    @JsonIgnore
    private Integer pageNum=1;
    @TableField(exist = false)
    @PropIgnore
    @JsonIgnore
    private Integer pageSize=10 ;
    @TableField(exist = false)
    @PropIgnore
    @JsonIgnore
    private String column;//排序的列
    @TableField(exist = false)
    @PropIgnore
    @JsonIgnore
    private String sort;//排序方式
    @TableField(exist = false)
    @PropIgnore
    @JsonIgnore
    private Map<String,String>params=new HashMap<>();
}
