package com.example.ruralfruits.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.exception.CustomException;
import com.example.ruralfruits.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class JWTInterceptor implements HandlerInterceptor {
    @Autowired
    private SysUserService sysUserService;

    // 1. 定义白名单（登录、注册、文件上传接口）
    private final List<String> WHITE_LIST = Arrays.asList(
            "/login", "/register", "/file/upload"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 2. 白名单路径直接放行（核心新增逻辑）
        String requestUri = request.getRequestURI();
        for (String whitePath : WHITE_LIST) {
            if (requestUri.equals(whitePath) || requestUri.startsWith(whitePath + "/")) {
                System.out.println("白名单路径放行：" + requestUri);
                return true;
            }
        }

        // 放过OPTIONS预检请求（原有逻辑保留）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取客户端传递过来的token信息（原有逻辑保留）
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)){
            token = request.getParameter("token");
        }
        if (StrUtil.isBlank(token)){
            throw new CustomException(401,"你没有权限访问该资源");
        }

        // 解析token中的载荷信息（原有逻辑保留）
        String audience = JWT.decode(token).getAudience().get(0);
        SysUser sysUser=sysUserService.getById(Integer.parseInt(audience));
        if (sysUser==null){
            throw new CustomException(401,"你没有权限访问该资源");
        }

        // 验证签名（原有逻辑保留）
        try{
            JWT.require(Algorithm.HMAC256(sysUser.getPassword())).build().verify(token);
        } catch (Exception e) {
            throw new CustomException(401,"你没有权限访问该资源");
        }

        return true;
    }
}