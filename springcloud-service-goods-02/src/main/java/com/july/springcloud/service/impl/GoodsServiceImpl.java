package com.july.springcloud.service.impl;

import com.july.springcloud.mapper.GoodsMapper;
import com.july.springcloud.model.Goods;
import com.july.springcloud.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<Goods> getAllGoods() {
        return goodsMapper.selectAllGoods();
    }

    @Override
    public Goods getGoodsById(Integer goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public int decrByStore(Integer goodsId, Integer buyNum) {
        return goodsMapper.updateByStore(goodsId, buyNum);
    }
}