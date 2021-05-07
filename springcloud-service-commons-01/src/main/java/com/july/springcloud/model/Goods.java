package com.july.springcloud.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Goods {

    private Integer id;

    private String name;

    private String nameDesc;

    private BigDecimal price;

    private Integer store;

    private String imageUrl;

    private Date startTime;

    private Date endTime;
}