package com.mc.postserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PostServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostServerApplication.class, args);
    }

}
