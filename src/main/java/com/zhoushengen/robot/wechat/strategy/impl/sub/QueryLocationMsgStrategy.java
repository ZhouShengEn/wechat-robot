package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhoushengen.robot.wechat.consts.WechatRedisKeyConst;
import com.zhoushengen.robot.wechat.dto.CacheLocationDTO;
import com.zhoushengen.robot.wechat.strategy.impl.AbstractMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLongAdder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:39
 **/
@Component("查询位置")
public class QueryLocationMsgStrategy extends AbstractMsgStrategy {

    private final static String EXCEED_QUERY_NUM_MSG = "您今天的查询次数用完了哈，有需要的话可以找明哥帮你[呲牙]";
    private final static String LOCATION_URL_PREFIX = "https://zhoushengen.link/taobao.html?sign=";
    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        String base64WxidTo = Base64Utils.encodeToString(wxidTo.getBytes(StandardCharsets.UTF_8));
        RBucket<Object> bucket =
            redissonClient.getBucket(WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(base64WxidTo));
        Object cache = bucket.get();
        String registerWxid = msg.getWxid();
        CacheLocationDTO cacheLocationDTO;
        if (null != cache && StringUtils.isNotEmpty(
            (cacheLocationDTO = JSONObject.parseObject(cache.toString(), CacheLocationDTO.class)).getAddress())
            && ((new Date().getTime() - cacheLocationDTO.getDate()) <= 300000)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 5分钟内
            HashMap<String, String> map = new HashMap<>();
            map.put(wxidTo, "\n您所在位置：".concat(cacheLocationDTO.getAddress()).concat("\n经度：")
                .concat(cacheLocationDTO.getLongitude()).concat("\r纬度：").concat(cacheLocationDTO.getLatitude())
                .concat("\n位置精准度偏差：").concat(cacheLocationDTO.getAccuracy()).concat("米").concat("\n绑定IP：")
                .concat(cacheLocationDTO.getIp()).concat("\n记录时间：")
                .concat(simpleDateFormat.format(new Date(cacheLocationDTO.getDate()))));
            return weChatRemoteApi.assembleRoomSendText(registerWxid, map);
        }
        cacheLocationDTO = new CacheLocationDTO();
        cacheLocationDTO.setFromId(wxidTo);
        cacheLocationDTO.setRoomId(fromWxid);
        if (masterWxid.equals(wxidTo)) {
            return assembleUrl(base64WxidTo, cacheLocationDTO, registerWxid, Boolean.TRUE);
        }
        String key = WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(wxidTo);
        RLongAdder longAdder = redissonClient.getLongAdder(key);
        long sum = longAdder.sum();
        if (sum == 0) {
            longAdder.expire(24, TimeUnit.HOURS);
        } else if (sum >= 5) {
            HashMap<String, String> map = new HashMap<>();
            map.put(wxidTo, EXCEED_QUERY_NUM_MSG);
            return weChatRemoteApi.assembleRoomSendText(registerWxid, map);
        }
        longAdder.increment();
        return assembleUrl(base64WxidTo, cacheLocationDTO, registerWxid, Boolean.TRUE);
    }

    @Override
    public String personalHandleBackContext(WeChatMsgVO msg, String fromWxid, String message) {
        String base64WxidTo = Base64Utils.encodeToString(fromWxid.getBytes(StandardCharsets.UTF_8));
        RBucket<Object> bucket =
            redissonClient.getBucket(WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(base64WxidTo));
        String registerWxid = msg.getWxid();
        Object cache = bucket.get();
        CacheLocationDTO cacheLocationDTO;
        if (null != cache && StringUtils.isNotEmpty(
            (cacheLocationDTO = JSONObject.parseObject(cache.toString(), CacheLocationDTO.class)).getAddress())
            && ((new Date().getTime() - cacheLocationDTO.getDate()) <= 300000)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 5分钟内
            return "您所在位置：".concat(cacheLocationDTO.getAddress()).concat("\n经度：")
                .concat(cacheLocationDTO.getLongitude()).concat("\r纬度：").concat(cacheLocationDTO.getLatitude())
                .concat("\n位置精准度偏差：").concat(cacheLocationDTO.getAccuracy()).concat("米").concat("\n绑定IP：")
                .concat(cacheLocationDTO.getIp()).concat("\n记录时间：")
                .concat(simpleDateFormat.format(new Date(cacheLocationDTO.getDate())));
        }
        cacheLocationDTO = new CacheLocationDTO();
        cacheLocationDTO.setFromId(fromWxid);
        if (masterWxid.equals(fromWxid)) {
            return assembleUrl(base64WxidTo, cacheLocationDTO, registerWxid, Boolean.FALSE);
        }
        String key = WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(fromWxid);
        RLongAdder longAdder = redissonClient.getLongAdder(key);
        long sum = longAdder.sum();
        if (sum == 0) {
            longAdder.expire(24, TimeUnit.HOURS);
        } else if (sum >= 5) {
            return EXCEED_QUERY_NUM_MSG;
        }
        longAdder.increment();
        return assembleUrl(base64WxidTo, cacheLocationDTO, registerWxid, Boolean.FALSE);
    }

    private String assembleUrl(String base64WxidTo, CacheLocationDTO cacheLocationDTO, String registerWxid,
        Boolean isRoom) {
        String url = "\n" + LOCATION_URL_PREFIX + base64WxidTo;
        RBucket<Object> bucket =
            redissonClient.getBucket(WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(base64WxidTo));
        // 链接五分钟内有效
        bucket.set(JSON.toJSONString(cacheLocationDTO), 5, TimeUnit.MINUTES);
        if (isRoom) {
            HashMap<String, String> map = new HashMap<>();
            map.put(cacheLocationDTO.getFromId(), url);
            return weChatRemoteApi.assembleRoomSendText(registerWxid, map);
        } else {
            return url;
        }

    }
}
