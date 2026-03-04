package com.example.ruralfruits;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ruralfruits.mapper")
public class RuralFruitsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuralFruitsApplication.class, args);
    }

}
