package com.example.ruralfruits.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.service.SysUserService;
import com.example.ruralfruits.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        // 根据用户名查询用户信息
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", map.get("username")));
        if (sysUser != null) {
            // 密码 MD5 比对
            if (!sysUser.getPassword().equals(SecureUtil.md5(map.get("password")))) {
                return Result.error("密码错误");
            }
            String token = TokenUtils.createToken(
                    sysUser.getUserId() + "", // 第1个参数：userId
                    sysUser.getPassword(),    // 第2个参数：password（作为密钥）
                    sysUser.getType()         // 第3个参数：角色类型（manager/user/merchant）
            );
            sysUser.setToken(token);
            return Result.success(sysUser); // 返回给前端（包含 token 和 type）
        }
        return Result.error("账号不存在");
    }

    @PostMapping("/register")
    public Result register(@RequestBody SysUser sysUser) {
        // 密码 MD5 加密后存储
        String md5Password = SecureUtil.md5(sysUser.getPassword());
        sysUser.setPassword(md5Password);
        // 🟢 可选优化：注册时默认角色为普通用户（user），避免 type 为空
        if (sysUser.getType() == null || sysUser.getType().trim().isEmpty()) {
            sysUser.setType("user"); // 普通用户默认值，管理员需手动在数据库设为 manager
        }
        sysUserService.save(sysUser);
        return Result.success("注册成功");
    }
}