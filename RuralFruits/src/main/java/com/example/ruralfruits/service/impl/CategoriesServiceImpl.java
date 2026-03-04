package com.example.ruralfruits.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruralfruits.entity.Categories;
import com.example.ruralfruits.mapper.CategoriesMapper;
import com.example.ruralfruits.service.CategoriesService;
import org.springframework.stereotype.Service;

@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoriesService {

}
