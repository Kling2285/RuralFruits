package com.example.ruralfruits.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.mapper.SysUserMapper;
import com.example.ruralfruits.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUsrServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public boolean save(SysUser entity) {
        //对密码进行MD5算法加密
        entity.setPassword(SecureUtil.md5(entity.getPassword()));
        return super.save(entity);
    }


}
