package com.july.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud
 * @ClassName: TurbineApplicatin
 * @Author: July
 * @Description: turbine
 * @url:
 * @Date: 2021/5/19 14:28
 * @Version: 1.0
 */
@Slf4j
//开启Turbine对hystrix聚合汇总
@EnableTurbine
@SpringBootApplication
public class TurbineApplicatin {
    public static void main(String[] args) {
        SpringApplication.run(TurbineApplicatin.class,args);
        log.info("Turbine服务（Turbine对多个hystrix聚合）已启动，端口：3722");
    }
}
