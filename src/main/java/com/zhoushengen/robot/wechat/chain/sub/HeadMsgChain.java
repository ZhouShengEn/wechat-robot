package com.zhoushengen.robot.wechat.chain.sub;

import com.zhoushengen.robot.wechat.chain.RoomMsgChain;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/13 10:38
 **/
public class HeadMsgChain extends RoomMsgChain {
    @Override
    public Boolean preHandle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        return roomMsgChain.preHandle(msg, wxidTo, fromWxid, message);
    }

    @Override
    public String handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        return roomMsgChain.handle(msg, wxidTo, fromWxid, message);
    }

}
