package com.example.ruralfruits.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ruralfruits.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT sum(price * `count`) from r_order where create_time between #{begin} and #{end}")
    public Double selectDailyMoney(@Param("begin") Date begin, @Param("end")Date end);



    @Select("SELECT " +
            "  DATE_FORMAT(create_time, '%Y-%m-%d') AS order_date, " +
            "  SUM(price * count) AS m " +
            "FROM r_order " +
            "WHERE create_time BETWEEN #{begin} AND #{end} " +
            "GROUP BY order_date " +
            "ORDER BY order_date ASC")
    List<Map<String, Object>> selectMonthMoney(@Param("begin") Date begin, @Param("end") Date end);

}
