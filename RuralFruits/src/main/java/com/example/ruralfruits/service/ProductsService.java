package com.example.ruralfruits.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruralfruits.entity.Products;

import java.util.List;
import java.util.Map;


public interface ProductsService extends IService<Products> {
    List<Products> list(IPage<Products> page, Wrapper<Products> queryWrapper);

    public List<Map<String,Object>> selectCategoriesCount();

    //查询给用户推荐的商品
    public List<Products> selectRecommend(int count);
}
