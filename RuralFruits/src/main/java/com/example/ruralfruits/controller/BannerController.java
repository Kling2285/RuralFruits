package com.example.ruralfruits.controller;


import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    @GetMapping("/all")
    public Result listAll(){
        return Result.success(bannerService.list());
    }
}
