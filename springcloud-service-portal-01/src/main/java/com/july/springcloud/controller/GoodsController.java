package com.july.springcloud.controller;


import com.july.springcloud.constants.Constant;
import com.july.springcloud.model.ResultObject;
import com.july.springcloud.service.GoodsClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RefreshScope
@RestController
public class GoodsController {

    /**
     * 普通方式调用
     */
    private static final  String GOODS_SERVER_URL="http://localhost:9100/service/goods";
    /**
     * 使用Ribbon调用
     */
    private static final  String GOODS_SERVER_URL_RIBBON="http://springcloud-service-goods-01/service/goods";
    /**
     * 使用Fegin 调用
     */
    @Resource
    private GoodsClient goodsClient;

    @Resource
    private RestTemplate restTemplate;

    @Value("${info.address}")
    private String address;

    /**
     * Fegin查询所有商品
     *
     * @return
     */
    @RequestMapping(value = "/service/goodsFegin", method = RequestMethod.GET)
    public ResultObject goodsFegin() {
        System.out.println("/service/goodsFegin -->8080 被执行..........");
        ResultObject goods = goodsClient.goods();
        return new ResultObject(Constant.ZERO, "查询成功", goods);
    }

    /**
     * 普通 查询所有商品
     *
     * @return
     */
    @RequestMapping(value = "/service/goods", method = RequestMethod.GET)
    public ResultObject goods() {
        System.out.println("/service/goods -->8080 被执行..........");
        ResultObject forObject = restTemplate.getForObject(GOODS_SERVER_URL_RIBBON, ResultObject.class);
        assert forObject != null;
        return new ResultObject(Constant.ZERO, "查询成功", forObject.getData());
    }

    /**
     * Hystrix 查询所有商品
     *
     * @return
     */
    @HystrixCommand(fallbackMethod = "fallback",
            threadPoolKey = "goods",
            threadPoolProperties = {
                    //可用线程数量（这里设置的是2个）
                    @HystrixProperty(name = "coreSize", value = "2"),
                    //队列 （这里设置的是1 所以可以方一个），如果第4个就限流了
                    @HystrixProperty(name = "maxQueueSize", value = "1")
            }
    )
    @RequestMapping(value = "/service/goodsHystrix", method = RequestMethod.GET)
    public ResultObject goodsHystrix() throws InterruptedException {
        System.out.println("/service/goodsHystrix -->8080 被执行..........");
        ResultObject goods = goodsClient.goods();
        if(goods != null){
            Thread.sleep(2000);
        }
        return new ResultObject(Constant.ZERO, "查询成功", goods.getData());
    }

    /**
     * 服务降级了
     * @return
     */
    public ResultObject fallback(Throwable throwable){
        throwable.printStackTrace();
        System.out.println(throwable.getMessage());
        return new ResultObject(Constant.ONE,"服务降级了...");
    }

    /**
     * Fegin整合Hystrix 查询所有商品
     *
     * @return
     */
    @RequestMapping(value = "/service/goodsFeginHystrix", method = RequestMethod.GET)
    public ResultObject goodsFeginHystrix() {
        System.out.println("/service/goodsHystrix -->8080 被执行..........");
        return goodsClient.goods();
    }

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public Object config() {
        return address;
    }
}