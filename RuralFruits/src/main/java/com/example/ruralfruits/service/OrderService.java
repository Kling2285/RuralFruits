package com.example.ruralfruits.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruralfruits.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    public Double selectDailyMoney(@Param("begin") Date begin, @Param("end")Date end);


    public List<Map<String,Object>> selectMonthMoney(@Param("begin") Date begin, @Param("end") Date end);
}
