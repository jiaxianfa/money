package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品业务实现类
 */
@Service(interfaceClass = LoanInfoService.class, version = "1.0.0", timeout = 20000)
@Component
public class LoanInfoServiceImpl implements LoanInfoService {
    @Autowired
    LoanInfoMapper loanInfoMapper;
    @Autowired(required = false)
    RedisTemplate redisTemplate;

    //动力金融网历史年化收益率
    @Override
    public Double queryLoanInfoHistoryRateAvg() {
        //设置redis对象key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Double loanInfoHistoryRateAvg = (Double) redisTemplate.opsForValue().get(Constant.LOAN_INFO_HISTORY_RATE_AVG);
        //如果不为空，可以直接返回值，不需要多线程了，效率更高
        if (null == loanInfoHistoryRateAvg) {
            synchronized (this) {
                //因为多线程，需要再查询一下
                loanInfoHistoryRateAvg = (Double) redisTemplate.opsForValue().get(Constant.LOAN_INFO_HISTORY_RATE_AVG);
                if (null == loanInfoHistoryRateAvg) {
                    //从数据库中查询
                    loanInfoHistoryRateAvg = loanInfoMapper.selectLoanInfoHistoryRateAvg();
                    //将查到的值存入redis中
                    redisTemplate.opsForValue().set(Constant.LOAN_INFO_HISTORY_RATE_AVG, loanInfoHistoryRateAvg);
                }
            }
        }
        return loanInfoHistoryRateAvg;
    }

    //首页：根据产品类型和数量 查询产品信息
    @Override
    public List<LoanInfo> queryLoanInfosByTypeAndNumber(Map<String, Object> parasMap) {
        return loanInfoMapper.selectLoanInfosByTypeAndNumber(parasMap);
    }

    //列表：根据产品类型和分页模型 查询产品信息
    @Override
    public List<LoanInfo> queryLoanInfosByTypeAndPageModel(Integer ptype, PageModel pageModel) {
        Map<String, Object> parasMap = new HashMap<>();
        parasMap.put("ptype", ptype);
        //start=当前页-1*步长
        parasMap.put("start", (pageModel.getCunPage() - 1) * pageModel.getPageSize());
        parasMap.put("content", pageModel.getPageSize());
        //还可以用之前查询产品列表的mapper方法
        return loanInfoMapper.selectLoanInfosByTypeAndNumber(parasMap);
    }
    //根据产品类型查询总记录数
    @Override
    public Long queryLoanInfoCountbyType(Integer ptype) {
        return loanInfoMapper.selectLoanInfoCountbyType(ptype);
    }
    //产品详情：根据产品编号，查询产品信息
    @Override
    public LoanInfo queryLoanInfoById(Integer loanId) {
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }
}
