package com.july.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud
 * @ClassName: DashboardApplication
 * @Author: July
 * @Description: dashboard
 * @url:
 * @Date: 2021/5/18 11:35
 * @Version: 1.0
 */
@Slf4j
@EnableHystrixDashboard
@SpringBootApplication
public class DashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class,args);
        log.info("Dashboard服务（Hystrix仪表盘）已启动，端口：3721");
    }
}
