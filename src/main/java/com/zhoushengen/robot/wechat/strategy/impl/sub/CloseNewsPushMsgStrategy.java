package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhoushengen.robot.wechat.entity.PushMessage;
import com.zhoushengen.robot.wechat.mapper.PushMessageMapper;
import com.zhoushengen.robot.wechat.strategy.RoomMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:50
 **/
@Component("关闭每日推送")
public class CloseNewsPushMsgStrategy implements RoomMsgStrategy {

    @Autowired
    private PushMessageMapper pushMessageMapper;

    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        QueryWrapper<PushMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ROOM_WXID", fromWxid);
        List<PushMessage> objects = pushMessageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(objects)) {
            PushMessage pushMessage = new PushMessage();
            pushMessage.setRoomWxid(fromWxid);
            pushMessage.setRegisterWxid(msg.getWxid());
            pushMessageMapper.insert(pushMessage);
        }
        return "本群已关闭每日推送[愉快]";
    }

}
