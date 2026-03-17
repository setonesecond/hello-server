package com.stu.helloserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// 关键：添加 ComponentScan 扫描 controller 和 entity 包
@ComponentScan(basePackages = {"com.stu.helloserver", "controller", "entity"})
@SpringBootApplication
public class HelloServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloServerApplication.class, args);
    }
}