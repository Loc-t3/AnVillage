package com.mc.userserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;


//SpringBootApplication 默认只扫描当前包下的bean 要想将其他包的bean进行注入 我们需要手动扫描得以添加
@SpringBootApplication(scanBasePackages = {"com.mc.common","com.mc.userserver"})
@EnableEurekaClient
public class UserServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
    }

}
