package com.example.ruralfruits.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ruralfruits.entity.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.removeById;

@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

}