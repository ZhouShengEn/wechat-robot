package com.zhoushengen.robot.wechat.service;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/7 12:43
 **/
public interface ExpendService {

    void getLocation(String latitude, String longitude, String msg, String sign, String accuracy, String ip);
}
