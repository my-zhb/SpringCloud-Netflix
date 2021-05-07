package com.july.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ProjectName: 01
 * @Package: com.july.springcloud
 * @ClassName: ProtalApplicatin
 * @Author: July
 * @Description: 消费者模块
 * @url:
 * @Date: 2021/4/28 11:25
 * @Version: 1.0
 */
@Slf4j
@EnableEurekaClient
@SpringBootApplication
public class PortalApplicatin {
    public static void main(String[] args) {
        SpringApplication.run(PortalApplicatin.class,args);
        log.info("portal服务（消费者）已启动，端口：8080");
    }
}
