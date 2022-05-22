package com.changgou.order.pojo;

public class OrderVo extends Order {
    //普通 1; 秒杀 2
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
