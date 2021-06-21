package com.july.springcloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud.config
 * @ClassName: EurekaSecurityConfig
 * @Author: July
 * @Description: 劫持CSRF
 * @url:
 * @Date: 2021/6/21 16:19
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        super.configure(http);
    }
}
