package com.zhoushengen.robot.wechat.service.impl;

import com.zhoushengen.robot.wechat.dto.BaseWeChatResDTO;
import com.zhoushengen.robot.wechat.dto.QueryRoomsResDTO;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:21
 **/
@Service("10014")
public class AccountModifyWeChatImpl implements WeChatService {

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Value("${wechat.register}")
    private String registerWxid;

    @Override
    public void handleMsg(WeChatMsgVO msg) {
        String type = msg.getData().getType();
        if ("1".equals(type)) {
            BaseWeChatResDTO<List<QueryRoomsResDTO>> roomRes = weChatRemoteApi.queryRooms(registerWxid);
            roomRes.getResult().forEach(room -> {
                String context = "天空一声巨响，明总的小跟班强势上线，机器人服务已开启！[转圈]";
                weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(room.getWxid(), context));
            });
        }

    }
}
