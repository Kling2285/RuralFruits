package com.example.ruralfruits.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruralfruits.entity.ProductComment;
import com.example.ruralfruits.mapper.ProductCommentMapper;
import com.example.ruralfruits.service.ProductCommentService;
import org.springframework.stereotype.Service;

@Service
public class ProductCommentServiceImpl extends ServiceImpl<ProductCommentMapper, ProductComment>implements ProductCommentService {
}
