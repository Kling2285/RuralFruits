package com.example.ruralfruits.controller;

import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.exception.CustomException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @RequestMapping("/index")
    public Result index() {
        System.out.println("Welcome to the index page");
        if(2>1){
            throw new CustomException(500,"账号冻结");
        }
        return Result.success("Welcome to the index page");
    }

    @RequestMapping("/list")
    public Result list(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("message", "Welcome to the list page");
        return Result.success(map);
    }
}
