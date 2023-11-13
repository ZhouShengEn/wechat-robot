package com.zhoushengen.robot.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhoushengen.robot.wechat.consts.WechatRedisKeyConst;
import com.zhoushengen.robot.wechat.dto.CacheLocationDTO;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.BaiduRemoteApi;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.service.ExpendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/7 12:43
 **/
@Service
@Slf4j
public class ExpendServiceImpl implements ExpendService {

    @Autowired
    private BaiduRemoteApi baiduRemoteApi;

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Value("${wechat.register}")
    private String registerWxid;

    @Value("${ak.baidu}")
    private String baiduAk;

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public void getLocation(String latitude, String longitude, String msg, String sign, String accuracy, String ip) {
        RBucket<Object> bucket =
            redissonClient.getBucket(WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(sign));
        Object signStr = bucket.get();
        log.info("getLocation latitude:{}, longitude:{}, msg:{}, signStr:{}", latitude, longitude, msg, signStr);
        if (null == signStr) {
            return;
        }

        if ("success".equals(msg)) {
            CacheLocationDTO cacheLocationDTO = JSONObject.parseObject(signStr.toString(), CacheLocationDTO.class);
            // 不同ip人点击不允许通过，提前判断以免损失百度api调用次数
            if (StringUtils.isNotEmpty(cacheLocationDTO.getIp()) && !cacheLocationDTO.getIp().equals(ip)) {
                return;
            }
            if (StringUtils.isNotEmpty(cacheLocationDTO.getLatitude()) && StringUtils.isNotEmpty(cacheLocationDTO.getLongitude())
                && cacheLocationDTO.getLatitude().equals(latitude) && cacheLocationDTO.getLongitude().equals(longitude)) {
                // 位置不变更新时间，更新缓存状态
                cacheLocationDTO.setDate(new Date().getTime());
                bucket.set(JSON.toJSONString(cacheLocationDTO), 5, TimeUnit.HOURS);
                return;
            }
            Response<JSONObject> res = baiduRemoteApi.locationTransform(baiduAk, "json", "bd09ll"
                , latitude.concat(",").concat(longitude));
            log.info("getLocation request baidu res:{}", JSON.toJSONString(res));

            String address;
            if (res.isSuccessful()){
                LinkedHashMap addressComponent =
                    (LinkedHashMap) res.body().getObject("result", LinkedHashMap.class).get("addressComponent");
                address =
                    (String)addressComponent.get("country") + addressComponent.get("province") + addressComponent.get(
                        "city") + addressComponent.get("district") + addressComponent.get("town") + addressComponent.get("street") + addressComponent.get("street_number");
                if (StringUtils.isNotEmpty(address)) {
                    String message = address.concat("\n经度：")
                        .concat(longitude).concat("\r纬度：").concat(latitude).concat("\n位置精准度偏差：")
                        .concat(accuracy).concat("米").concat("\n绑定IP：").concat(ip);
                    if (new BigDecimal(accuracy).compareTo(new BigDecimal(1000)) > 0) {
                        message = message.concat("\n您的位置偏差比较大，位于出口网络的基站位置，系统检测到非手机端访问，建议您使用手机访问以提高位置精确度");
                    }
                    cacheLocationDTO.setLatitude(latitude);
                    cacheLocationDTO.setLongitude(longitude);
                    cacheLocationDTO.setDate(new Date().getTime());
                    cacheLocationDTO.setAccuracy(accuracy);
                    cacheLocationDTO.setAddress(address);
                    if (StringUtils.isNotEmpty(cacheLocationDTO.getIp()) && cacheLocationDTO.getIp().equals(ip)) {
                        // ip不变，用于更新缓存状态
                        bucket.set(JSON.toJSONString(cacheLocationDTO), 5, TimeUnit.HOURS);
                        return;
                    }
                    cacheLocationDTO.setIp(ip);
                    bucket.set(JSON.toJSONString(cacheLocationDTO), 5, TimeUnit.HOURS);
                    sendAddress(cacheLocationDTO.getRoomId(), cacheLocationDTO.getFromId(), message);
                } else {
                    sendAddress(cacheLocationDTO.getRoomId(), cacheLocationDTO.getFromId(), "暂未查询到位置信息！".concat("\n查询IP：").concat(ip));
                }
            } else {
                sendAddress(cacheLocationDTO.getRoomId(), cacheLocationDTO.getFromId(), "经纬度转换异常！".concat("\n查询IP：").concat(ip));
            }
        }

    }

    private void sendAddress(String roomId, String fromId, String address) {
        if (StringUtils.isNotEmpty(roomId)){
            HashMap<String, String> map = new HashMap<>();
            map.put(fromId, "\n您当前所在位置: "+ address);
            weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(
                roomId, weChatRemoteApi.assembleRoomSendText(registerWxid, map)));
        } else {
            weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(fromId, "您当前所在位置: "+ address));
        }
    }
}
