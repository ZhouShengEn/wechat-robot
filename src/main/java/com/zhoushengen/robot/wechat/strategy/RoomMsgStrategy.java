package com.zhoushengen.robot.wechat.strategy;

import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:55
 **/
public interface RoomMsgStrategy {

    ExpiringMap<String, String> groupSchemaMap = ExpiringMap.builder()
        .expiration(1, TimeUnit.HOURS)
        .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();


    String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo);
}
