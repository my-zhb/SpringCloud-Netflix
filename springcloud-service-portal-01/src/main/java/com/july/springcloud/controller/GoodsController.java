package com.july.springcloud.controller;


import com.july.springcloud.constants.Constant;
import com.july.springcloud.model.Goods;
import com.july.springcloud.model.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GoodsController {

    //普通方式调用
    private static final  String GOODS_SERVER_URL="http://localhost:9100/service/goods";
    //使用Ribbon调用
    private static final  String GOODS_SERVER_URL_RIBBON="http://springcloud-service-goods-01/service/goods";

    @Resource
    private RestTemplate restTemplate;

    /**
     * 查询所有商品
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
}