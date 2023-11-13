package com.zhoushengen.robot.wechat.chain.sub;

import com.zhoushengen.robot.wechat.chain.RoomMsgChain;
import com.zhoushengen.robot.wechat.strategy.RoomMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author: zhoushengen
 * @Description: 空消息处理
 * @DateTime: 2023/11/13 10:33
 **/
@Component
public class EmptyMsgChain extends RoomMsgChain {
    @Override
    public Boolean preHandle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        return StringUtils.isEmpty(message.replaceAll(" ", ""));
    }

    @Override
    public String handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        String schema = (null != RoomMsgStrategy.groupSchemaMap.get(fromWxid)) ? "专家模式" : "聊天模式";
        return "大家好，我是智能AI机器人，本群当前处于 " + schema;
    }
}
