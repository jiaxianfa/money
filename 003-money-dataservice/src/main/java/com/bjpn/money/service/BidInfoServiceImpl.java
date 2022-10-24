package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.BidInfoMapper;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.util.Constant;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * 投资业务实现类
 */
@Service(interfaceClass = BidInfoService.class, version = "1.0.0", timeout = 20000)
@Component
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    BidInfoMapper bidInfoMapper;
    @Autowired(required = false)
    RedisTemplate redisTemplate;
    @Autowired
    LoanInfoMapper loanInfoMapper;
    @Autowired(required = false)
    FinanceAccountMapper financeAccountMapper;

    //首页：累计成交额
    @Override
    public Double queryBidMoneySum() {
        Double bidMoneySum = (Double) redisTemplate.opsForValue().get(Constant.BID_MONEY_SUM);
        //如果不为空，可以直接返回值，不需要多线程了，效率更高
        if (null == bidMoneySum) {
            //解决缓存穿透问题
            synchronized (this) {
                //因为多线程，需要再查询一下
                bidMoneySum = (Double) redisTemplate.opsForValue().get(Constant.BID_MONEY_SUM);
                if (null == bidMoneySum) {
                    //从数据库中查询
                    bidMoneySum = bidInfoMapper.selectBidMoneySum();
                    //将查到的值赋值到redis中
                    redisTemplate.opsForValue().set(Constant.BID_MONEY_SUM, bidMoneySum);
                }
            }
        }
        return bidMoneySum;
    }

    //产品详情页：根据id查询投资记录  还需要客户手机号    所以需要两表联查
    @Override
    public List<BidInfo> queryBidInfosByLoanId(Integer loanId) {
        return bidInfoMapper.selectBidInfosByLoanId(loanId);
    }

    //小金库：根据用户id和数量查询投资信息
    @Override
    public List<BidInfo> queryBidInfoByUidAndNumber(Map<String, Object> parasMap) {
        return bidInfoMapper.selectBidInfoByUidAndNumber(parasMap);
    }

    //投资：根据产品id，用户id，投资金额进行投资
    @Transactional
    @Override
    public int invest(Map<String, Object> parasMap) {
        //剩余可投金额减少  部分修改
        int num = loanInfoMapper.updateLeftProductMoneyReduceForInvest(parasMap);
        if (num != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constant.BID_LOAN_LEFTMONEY_FAIL;
        }

        //账户余额减少    部分修改
        num = financeAccountMapper.updateAvailableMoneyReduceForInvest(parasMap);
        if (num!=1 ) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constant.BID_FINANCE_AVAILABLEMONEY_FAIL;
        }
        //判断是否满标
        //先查询一下产品剩余可投金额，然后再更改产品状态
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) parasMap.get("loanId"));
        //0未满标，1已满标     剩余可投金额为0    但是产品状态还不是0
        if (loanInfo.getLeftProductMoney() == 0 && loanInfo.getProductStatus() == 0) {
            //更新数据库的产品状态
            loanInfo.setProductStatus(0);
            loanInfo.setProductFullTime(new Date());
            //全修改
            num = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if (num != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Constant.BID_LOAN_STATUS_FAIL;
            }
        }
        //投资记录表添加一条记录   不能用上面的，需要用parasMpa集合里面的
        BidInfo bidInfo = new BidInfo();
        bidInfo.setLoanId((Integer) parasMap.get("loanId"));
        bidInfo.setUid((Integer) parasMap.get("userId"));
        bidInfo.setBidMoney((Double) parasMap.get("bidMoney"));
        bidInfo.setBidTime(new Date());
        //正常业务需要先设置为0   人工审核之后再设置为1
        bidInfo.setBidStatus(1);
        num = bidInfoMapper.insertSelective(bidInfo);
        if (num != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constant.BID_INFO_FAIL;
        }
        return Constant.BID_INFO_SUCCESS;
    }
}
