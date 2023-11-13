package com.zhoushengen.robot.wechat.chain;

import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/13 10:23
 **/
public abstract class RoomMsgChain {
    public RoomMsgChain roomMsgChain;

    public abstract Boolean preHandle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message);
    public abstract String handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message);

    public RoomMsgChain setNext(RoomMsgChain next) {
        this.roomMsgChain = next;
        return next;
    }

    public String start(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        if (preHandle(msg, wxidTo, fromWxid, message)) {
            return handle(msg, wxidTo, fromWxid, message);
        } else {
            return null != roomMsgChain ? roomMsgChain.start(msg, wxidTo, fromWxid, message) : null;
        }
    }

}
