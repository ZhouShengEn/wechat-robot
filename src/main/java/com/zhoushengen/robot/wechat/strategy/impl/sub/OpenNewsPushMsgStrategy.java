package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhoushengen.robot.wechat.entity.PushMessage;
import com.zhoushengen.robot.wechat.mapper.PushMessageMapper;
import com.zhoushengen.robot.wechat.strategy.RoomMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:51
 **/
@Component("开启每日推送")
public class OpenNewsPushMsgStrategy implements RoomMsgStrategy {

    @Autowired
    private PushMessageMapper pushMessageMapper;
    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        QueryWrapper<PushMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ROOM_WXID", fromWxid);
        pushMessageMapper.delete(queryWrapper);
        return "本群开启成功，将在每日早晨七点半准点为您播报[愉快]";
    }

}
