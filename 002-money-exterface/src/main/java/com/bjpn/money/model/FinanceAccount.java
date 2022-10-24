package com.bjpn.money.model;

import java.io.Serializable;

public class FinanceAccount implements Serializable {
    //用户财务资金账户表id
    private Integer id;
    //用户ID
    private Integer uid;
    //用户可用资金
    private Double availableMoney;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(Double availableMoney) {
        this.availableMoney = availableMoney;
    }
}