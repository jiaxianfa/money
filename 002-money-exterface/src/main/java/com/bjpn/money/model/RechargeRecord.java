package com.bjpn.money.model;

import java.io.Serializable;
import java.util.Date;

public class RechargeRecord implements Serializable {
    //用户充值记录表id
    private Integer id;
    //用户id
    private Integer uid;
    //充值订单号
    private String rechargeNo;
    //充值订单状态（0充值中，1充值成功，2充值失败）
    private String rechargeStatus;
    //充值金额
    private Double rechargeMoney;
    //充值时间
    private Date rechargeTime;
    //充值描述
    private String rechargeDesc;

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

    public String getRechargeNo() {
        return rechargeNo;
    }

    public void setRechargeNo(String rechargeNo) {
        this.rechargeNo = rechargeNo;
    }

    public String getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(String rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

    public Double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(Double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public String getRechargeDesc() {
        return rechargeDesc;
    }

    public void setRechargeDesc(String rechargeDesc) {
        this.rechargeDesc = rechargeDesc;
    }
}