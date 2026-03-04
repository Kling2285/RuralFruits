package com.example.ruralfruits.controller;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Order;
import com.example.ruralfruits.service.OrderService;
import com.example.ruralfruits.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    // 批量新增订单（核心修改：添加 @Transactional 事务注解，确保原子性）
    @PostMapping
    @Transactional(rollbackFor = Exception.class) // 新增事务：异常时回滚所有操作
    public Result addBatch(@RequestBody List<Order> orders, HttpServletRequest request) {
        if (orders == null || orders.isEmpty()) {
            return Result.error("批量新增失败：订单列表不能为空");
        }

        // 从token获取当前登录用户ID，自动填充（避免手动传参）
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("新增失败：未登录（token为空）");
        }
        String userId = JWT.decode(token).getAudience().get(0);
        orders.forEach(order -> order.setUserId(Long.parseLong(userId)));

        boolean success = orderService.saveBatch(orders);
        return success ? Result.success("批量新增订单成功") : Result.error("批量新增订单失败");
    }

    // 单条新增订单（补充接口）
    @PostMapping("/add")
    public Result addSingle(@RequestBody Order order, HttpServletRequest request) {
        // 校验必填字段
        if (StrUtil.isEmpty(order.getOrderId())) {
            return Result.error("新增失败：订单编号（orderId）不能为空");
        }
        if (order.getProductId() <= 0) {
            return Result.error("新增失败：商品ID（productId）不能为空且需大于0");
        }
        if (StrUtil.isEmpty(order.getName())) {
            return Result.error("新增失败：商品名称（name）不能为空");
        }
        if (order.getCount() <= 0) {
            return Result.error("新增失败：购买数量（count）不能为空且需大于0");
        }
        if (order.getPrice() <= 0) {
            return Result.error("新增失败：商品单价（price）不能为空且需大于0");
        }

        // 从token获取当前登录用户ID，自动填充
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("新增失败：未登录（token为空）");
        }
        String userId = JWT.decode(token).getAudience().get(0);
        order.setUserId(Long.parseLong(userId));

        boolean success = orderService.save(order);
        return success ? Result.success("单条新增订单成功") : Result.error("单条新增订单失败");
    }

    // 修改订单（管理员可改所有，普通用户只能改自己的）
    @PutMapping("/update")
    public Result update(@RequestBody Order order, HttpServletRequest request) {
        // 校验必填参数
        if (Objects.isNull(order.getId())) {
            return Result.error("修改失败：订单ID不能为空");
        }
        if (StrUtil.isEmpty(order.getOrderId())) {
            return Result.error("修改失败：订单编号（orderId）不能为空");
        }

        // 解析Token获取用户信息
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("修改失败：未登录（token为空）");
        }
        String userId = JWT.decode(token).getAudience().get(0);
        Claim userTypeClaim = JWT.decode(token).getClaim("type");
        String userType = userTypeClaim.isNull() ? "user" : userTypeClaim.asString();

        // 校验订单是否存在
        Order existOrder = orderService.getById(order.getId());
        if (Objects.isNull(existOrder)) {
            return Result.error("修改失败：该订单不存在");
        }

        // 权限校验：普通用户只能修改自己的订单
        if (!"manager".equals(userType) && !"admin".equals(userType)) {
            if (!String.valueOf(existOrder.getUserId()).equals(userId)) {
                return Result.error("修改失败：无权修改他人订单");
            }
        }

        // 保留创建时间，不允许修改
        order.setCreateTime(existOrder.getCreateTime());

        boolean success = orderService.updateById(order);
        return success ? Result.success("修改订单成功") : Result.error("修改订单失败");
    }

    // 单条删除订单（管理员可删所有，普通用户只能删自己的）
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        if (Objects.isNull(id)) {
            return Result.error("删除失败：订单ID不能为空");
        }

        // 解析Token获取用户信息
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("删除失败：未登录（token为空）");
        }
        String userId = JWT.decode(token).getAudience().get(0);
        Claim userTypeClaim = JWT.decode(token).getClaim("type");
        String userType = userTypeClaim.isNull() ? "user" : userTypeClaim.asString();

        // 校验订单是否存在
        Order existOrder = orderService.getById(id);
        if (Objects.isNull(existOrder)) {
            return Result.error("删除失败：该订单不存在");
        }

        // 权限校验：普通用户只能删除自己的订单
        if (!"manager".equals(userType) && !"admin".equals(userType)) {
            if (!String.valueOf(existOrder.getUserId()).equals(userId)) {
                return Result.error("删除失败：无权删除他人订单");
            }
        }

        boolean success = orderService.removeById(id);
        return success ? Result.success("删除订单成功") : Result.error("删除订单失败");
    }

    // 批量删除订单（管理员可批量删所有，普通用户只能删自己的）
    @DeleteMapping("/batch/{ids}")
    public Result deleteBatch(@PathVariable Long[] ids, HttpServletRequest request) {
        if (ids == null || ids.length == 0) {
            return Result.error("删除失败：请选择要删除的订单");
        }

        // 解析Token获取用户信息
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("删除失败：未登录（token为空）");
        }
        String userId = JWT.decode(token).getAudience().get(0);
        Claim userTypeClaim = JWT.decode(token).getClaim("type");
        String userType = userTypeClaim.isNull() ? "user" : userTypeClaim.asString();

        // 校验所有订单是否存在
        List<Order> existOrders = orderService.listByIds(List.of(ids));
        if (existOrders.size() != ids.length) {
            return Result.error("删除失败：部分订单不存在");
        }

        // 权限校验：普通用户只能删除自己的订单
        if (!"manager".equals(userType) && !"admin".equals(userType)) {
            for (Order order : existOrders) {
                if (!String.valueOf(order.getUserId()).equals(userId)) {
                    return Result.error("删除失败：包含他人订单，无权操作");
                }
            }
        }

        boolean success = orderService.removeByIds(List.of(ids));
        return success ? Result.success("批量删除订单成功") : Result.error("批量删除订单失败");
    }

    // 查看订单详情（管理员可看所有，普通用户只能看自己的）
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id, HttpServletRequest request) {
        if (Objects.isNull(id)) {
            return Result.error("查询失败：订单ID不能为空");
        }

        // 解析Token获取用户信息
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            return Result.error("查询失败：未登录（token为空）");
        }
        String userId = JWT.decode(token).getAudience().get(0);
        Claim userTypeClaim = JWT.decode(token).getClaim("type");
        String userType = userTypeClaim.isNull() ? "user" : userTypeClaim.asString();

        // 校验订单是否存在
        Order order = orderService.getById(id);
        if (Objects.isNull(order)) {
            return Result.error("查询失败：该订单不存在");
        }

        // 权限校验：普通用户只能查看自己的订单
        if (!"manager".equals(userType) && !"admin".equals(userType)) {
            if (!String.valueOf(order.getUserId()).equals(userId)) {
                return Result.error("查询失败：无权查看他人订单");
            }
        }

        return Result.success(order);
    }

    // 订单列表（管理员看所有，普通用户看自己的）
    @GetMapping("/list")
    public Result list(HttpServletRequest request, Order order) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        String token = request.getHeader("token");

        // 1. 校验 Token
        if (StrUtil.isEmpty(token)) {
            return Result.error("查询失败：未登录（token为空）");
        }

        // 2. 解析用户信息（用修改后的 TokenUtils）
        String userId = TokenUtils.getUserId(token);
        String userType = TokenUtils.getUserType(token); // 解析角色类型
        userType = StrUtil.isEmpty(userType) ? "user" : userType; // 默认普通用户
        // 4. 权限判断：管理员看所有，普通用户看自己的
        boolean isAdmin = "manager".equalsIgnoreCase(userType) || "admin".equalsIgnoreCase(userType);
        if (!isAdmin) {
            queryWrapper.eq("user_id", userId);
        }

        // 5. 其他筛选条件
        queryWrapper.eq(order.getStatus() != null && !"-1".equals(order.getStatus()), "status", order.getStatus());
        queryWrapper.ge(StrUtil.isNotBlank(order.getParams().get("begin")), "create_time", order.getParams().get("begin"));
        queryWrapper.le(StrUtil.isNotBlank(order.getParams().get("end")), "create_time", order.getParams().get("end") + " 23:59:59");
        queryWrapper.like(StrUtil.isNotBlank(order.getOrderId()), "order_id", order.getOrderId());
        queryWrapper.like(StrUtil.isNotBlank(order.getName()), "name", order.getName());

        // 6. 分页查询
        Page<Order> page = Page.of(
                order.getPageNum() != null ? order.getPageNum() : 1,
                order.getPageSize() != null ? order.getPageSize() : 10
        );
        orderService.page(page, queryWrapper);

        // 7. 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("list", page.getRecords());
        return Result.success(data);
    }

    // 新增：查询所有订单（不做任何权限判断、不筛选，仅分页）
    @GetMapping("/listAll")
    public Result listAll(Order order) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        Page<Order> page = Page.of(
                order.getPageNum() != null ? order.getPageNum() : 1,  // 默认第1页
                order.getPageSize() != null ? order.getPageSize() : 10 // 默认每页10条
        );

        orderService.page(page, queryWrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("list", page.getRecords());
        return Result.success(data);
    }
}