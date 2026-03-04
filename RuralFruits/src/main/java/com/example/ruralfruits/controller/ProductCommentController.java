package com.example.ruralfruits.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.ProductComment;
import com.example.ruralfruits.service.ProductCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class ProductCommentController {
    @Autowired
    private ProductCommentService productCommentService;

    @GetMapping("/all")
    public Result list(ProductComment comment){

        return Result.success(productCommentService.list(new QueryWrapper<ProductComment>().eq("pid",comment.getPid())));
    }
}
