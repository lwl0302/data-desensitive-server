package com.mrray.datadesensitiveserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableFeignClients
//@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DataDesensitiveServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataDesensitiveServerApplication.class, args);
    }
}