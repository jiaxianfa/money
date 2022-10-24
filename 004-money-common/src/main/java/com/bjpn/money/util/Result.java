package com.bjpn.money.util;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//返回值
public class Result {
    //没有返回成功信息
    public static Map<String, Object> success() {
        Map<String, Object> map = new HashMap<>();
        map.put("code",1);
        map.put("message", "");
        map.put("success", true);
        return map;
    }

    //自定义返回成功信息
    public static Map<String, Object> success(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code",1);
        map.put("message", message);
        map.put("success", true);
        return map;
    }
    //没有返回错误信息
    public static Map<String, Object> error() {
        Map<String, Object> map = new HashMap<>();
        map.put("code",0);
        map.put("message", "");
        map.put("error", false);
        return map;
    }
    //自定义返回错误信息
    public static Map<String, Object> error(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code",0);
        map.put("message", message);
        map.put("error", false);
        return map;
    }

    //获取随机数 自定义长度
    //random:取0-1的随机数
    // round:取最近的正整数
    public static String generateCode(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <len; i++) {
            stringBuilder.append(Math.round(Math.random()*9));
        }
        return stringBuilder.toString();
    }
}
