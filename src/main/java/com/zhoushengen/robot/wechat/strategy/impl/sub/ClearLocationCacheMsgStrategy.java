package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.zhoushengen.robot.wechat.consts.WechatRedisKeyConst;
import com.zhoushengen.robot.wechat.strategy.impl.AbstractMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.redisson.api.RBucket;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:35
 **/
@Component("清除位置缓存")
public class ClearLocationCacheMsgStrategy extends AbstractMsgStrategy {
    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        String base64WxidTo = Base64Utils.encodeToString(wxidTo.getBytes(StandardCharsets.UTF_8));
        RBucket<Object> bucket =
            redissonClient.getBucket(WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(base64WxidTo));
        bucket.delete();
        HashMap<String, String> map = new HashMap<>();
        map.put(wxidTo,"位置信息已清除！");
        return weChatRemoteApi.assembleRoomSendText(msg.getWxid(), map);
    }

    @Override
    public String personalHandleBackContext(WeChatMsgVO msg, String fromWxid, String message) {
        String base64WxidTo = Base64Utils.encodeToString(fromWxid.getBytes(StandardCharsets.UTF_8));
        RBucket<Object> bucket =
            redissonClient.getBucket(WechatRedisKeyConst.WECHAT_LOCATION_REQUEST_PREFIX.concat(base64WxidTo));
        bucket.delete();
        return "位置信息已清除！";
    }
}
