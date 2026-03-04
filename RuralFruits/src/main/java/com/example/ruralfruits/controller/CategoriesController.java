package com.example.ruralfruits.controller;

import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Categories;
import com.example.ruralfruits.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("/all")
    public Result listAll(){
        return Result.success(categoriesService.list());
    }
}
