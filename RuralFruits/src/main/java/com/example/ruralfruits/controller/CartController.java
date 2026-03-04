package com.example.ruralfruits.controller;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Cart;
import com.example.ruralfruits.service.CartService;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public Result add(@RequestBody Cart cart, HttpServletRequest request) {
        // 1. 校验 token（必须登录才能新增）
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("新增失败：未登录（token为空）");
        }

        // 2. 解析 token 获取当前登录用户信息
        String userId = JWT.decode(token).getAudience().get(0);
        String username = ""; // 若 token 中存储了用户名，可解析：JWT.decode(token).getClaim("username").asString()

        // 3. 给关键字段赋值（解决 create_user 和 user_id 为空问题）
        cart.setUserId(Long.parseLong(userId)); // 设置用户ID
        cart.setCreateUser(username.isEmpty() ? userId : username); // createUser 填用户名或用户ID

        // 4. 校验必填字段（避免插入空数据）
        if (StrUtil.isEmpty(cart.getName())) {
            return Result.error("新增失败：商品名称不能为空");
        }
        if (cart.getGoodsId() <= 0) {
            return Result.error("新增失败：商品ID不能为空且需大于0");
        }
        if (cart.getCount() <= 0) {
            return Result.error("新增失败：购买数量不能为空且需大于0");
        }
        if (StrUtil.isEmpty(cart.getPrice())) {
            return Result.error("新增失败：商品单价不能为空");
        }

        // 5. 保存并判断是否成功
        boolean success = cartService.save(cart);
        return success ? Result.success("新增购物车成功") : Result.error("新增购物车失败");
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        // 1. 校验 token
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("查询失败：未登录（token为空）");
        }

        // 2. 解析用户ID，查询当前用户的购物车
        String userId = JWT.decode(token).getAudience().get(0);
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        Page<Cart> page = Page.of(pageNum, pageSize);
        cartService.page(page, queryWrapper);

        // 4. 组装返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal()); // 总条数
        map.put("list", page.getRecords()); // 当前页数据
        map.put("pageNum", pageNum); // 当前页码
        map.put("pageSize", pageSize); // 每页条数

        return Result.success(map);
    }

    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable Long[] ids, HttpServletRequest request) {
        // 1. 校验 token
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("删除失败：未登录（token为空）");
        }

        // 2. 校验是否选择了要删除的记录
        if (ids == null || ids.length == 0) {
            return Result.error("删除失败：请选择要删除的购物车记录");
        }

        // 3. 校验权限：只能删除自己的购物车（可选，根据需求是否保留）
        String userId = JWT.decode(token).getAudience().get(0);
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .in("id", Arrays.asList(ids));
        List<Cart> ownCarts = cartService.list(queryWrapper);
        if (ownCarts.size() != ids.length) {
            return Result.error("删除失败：包含他人购物车记录，无权操作");
        }

        // 4. 执行删除并判断结果
        boolean success = cartService.removeByIds(Arrays.asList(ids));
        return success ? Result.success("删除购物车成功") : Result.error("删除购物车失败");
    }
}