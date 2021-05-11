package com.july.springcloud.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud.config
 * @ClassName: MyIRule
 * @Author: July
 * @Description: 自定义负载均衡算法
 * @url:
 * @Date: 2021/5/11 17:06
 * @Version: 1.0
 */
public class MyIRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        //具体实现
        return null;
    }
}
