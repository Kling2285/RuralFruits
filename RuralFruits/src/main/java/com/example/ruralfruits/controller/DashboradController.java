package com.example.ruralfruits.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Order;
import com.example.ruralfruits.entity.Products;
import com.example.ruralfruits.entity.SysUser;

import com.example.ruralfruits.service.OrderService;
import com.example.ruralfruits.service.ProductsService;
import com.example.ruralfruits.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboradController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ProductsService productsService; // 注入的实例对象

    @GetMapping("/daily")
    public Result daily_data(){
        Date begin= DateUtil.beginOfDay(new Date());
        Date end= DateUtil.endOfDay(new Date());
        //查询今日营业额
        Double total=orderService.selectDailyMoney(begin,end);

        //统计今日订单数
        long order_count=orderService.count(new QueryWrapper<Order>().between("create_time",begin,end));

        //今日新增用户
        long user_count=sysUserService.count(new QueryWrapper<SysUser>().between("create_time",begin,end));

        //今日新增农产品
        long product_count=productsService.count(new QueryWrapper<Products>().between("create_time",begin,end));

        Map<String,Object> map=new HashMap<>();
        map.put("total",total);
        map.put("order_count",order_count);
        map.put("user_count",user_count);
        map.put("product_count",product_count);

        return Result.success(map);
    }


    @GetMapping("/month_order")
    public Result month_order(){
        Date begin=DateUtil.beginOfMonth(new Date());
        Date end=DateUtil.endOfMonth(new Date());

        List<Map<String,Object>> list=orderService.selectMonthMoney(begin,end);

        return Result.success(list);
    }

    @GetMapping("/categories")
    public Result categories(){
        return Result.success(productsService.selectCategoriesCount());
    }
}