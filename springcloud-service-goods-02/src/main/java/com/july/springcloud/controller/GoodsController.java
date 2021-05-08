package com.july.springcloud.controller;


import com.july.springcloud.constants.Constant;
import com.july.springcloud.model.Goods;
import com.july.springcloud.model.ResultObject;
import com.july.springcloud.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 查询所有商品
     *
     * @return
     */
    @RequestMapping(value = "/service/goods", method = RequestMethod.GET)
    public ResultObject goods() {
        System.out.println("/service/goods -->9200 被执行..........");
        List<Goods> goodsList = goodsService.getAllGoods();
        return new ResultObject(Constant.ZERO, "查询成功", goodsList);
    }

    /**
     * 查询商品详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value="/service/goods/{id}", method = RequestMethod.GET)
    public ResultObject goodsDetail(@PathVariable("id") Integer id) {
        System.out.println("/service/goods/{id} -->9100 被执行..........id=" + id);
        Goods goods = goodsService.getGoodsById(id);
        return new ResultObject(Constant.ZERO, "查询成功", goods);
    }

    /**
     * 减库存
     *
     * @param id
     * @param buyNum
     * @return
     */
    @RequestMapping(value = "/service/goods/{id}", method = RequestMethod.POST)
    public ResultObject decrByStore (@PathVariable("id") Integer id, @RequestParam("buyNum") Integer buyNum) {
        System.out.println("/service/decrByStore -->9100 被执行..........id=" + id + ", buyNum=" + buyNum);
        //减库存操作
        int result = goodsService.decrByStore(id, buyNum);
        return new ResultObject(result == 1 ? Constant.ZERO : Constant.ONE, result == 1 ? "减库存成功" : "减库存失败");
    }
}