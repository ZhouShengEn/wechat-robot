package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.zhoushengen.robot.wechat.dto.BaseWeChatResDTO;
import com.zhoushengen.robot.wechat.dto.QueryRoomsResDTO;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.strategy.PersonalMsgStrategy;
import com.zhoushengen.robot.wechat.strategy.StartWithMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.zhoushengen.robot.wechat.strategy.impl.sub.GroupSendMsgStrategy.GROUP_SEND_PREFIX;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 18:19
 **/
@Component(GROUP_SEND_PREFIX)
public class GroupSendMsgStrategy implements PersonalMsgStrategy, StartWithMsgStrategy {

    public static final String GROUP_SEND_PREFIX = "群发-";

    @Autowired
    public WeChatRemoteApi weChatRemoteApi;

    @Value("${wechat.masterWxid}")
    private String masterWxid;

    @Override
    public String personalHandleBackContext(WeChatMsgVO msg, String fromWxid, String message) {
        if (masterWxid.equals(fromWxid) && message.replace(" ", "").startsWith(GROUP_SEND_PREFIX)) {
            String substring = message.substring(GROUP_SEND_PREFIX.length());
            String registerWxid = msg.getWxid();
            BaseWeChatResDTO<List<QueryRoomsResDTO>> roomRes = weChatRemoteApi.queryRooms(registerWxid);
            roomRes.getResult().forEach(
                room -> weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(room.getWxid(), substring)));
        }
        return "发送完成";
    }
}
