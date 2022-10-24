package com.bjpn.money.mapper;

import com.bjpn.money.model.BidInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);
    //非空更改  如果是传进来的是空值，就不更改
    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    Double selectBidMoneySum();
    // 产品详情页：根据id查询投资记录  还需要客户手机号
    List<BidInfo> selectBidInfosByLoanId(Integer loanId);
    //小金库：根据用户id和数量查询投资信息
    List<BidInfo> selectBidInfoByUidAndNumber(Map<String, Object> parasMap);
}