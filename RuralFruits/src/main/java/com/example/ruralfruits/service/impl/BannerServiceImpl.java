package com.example.ruralfruits.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruralfruits.entity.Banner;
import com.example.ruralfruits.mapper.BannerMapper;
import com.example.ruralfruits.service.BannerService;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
}
