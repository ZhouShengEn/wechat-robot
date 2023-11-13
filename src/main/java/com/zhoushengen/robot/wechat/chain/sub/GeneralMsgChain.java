package com.zhoushengen.robot.wechat.chain.sub;

import com.zhoushengen.robot.wechat.chain.RoomMsgChain;
import com.zhoushengen.robot.wechat.strategy.RoomMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: zhoushengen
 * @Description: 文本答复
 * @DateTime: 2023/11/13 10:25
 **/
@Component
public class GeneralMsgChain extends RoomMsgChain {

    @Autowired
    private Map<String, RoomMsgStrategy> roomMsgStrategyMap;

    @Override
    public Boolean preHandle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        return StringUtils.isNotEmpty(message) && StringUtils.isNotEmpty(message.replace(" ", ""));
    }

    @Override
    public String handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        RoomMsgStrategy roomMsgStrategy;
        return  (null != (roomMsgStrategy = roomMsgStrategyMap.get(message.replaceAll(" ", "")))
            ? roomMsgStrategy
            : roomMsgStrategyMap.get("generalMsgStrategy")).roomHandleBackContext(msg, fromWxid, message, wxidTo);
    }
}
