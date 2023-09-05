package com.zhoushengen.robot.wechat.service.impl;

import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:21
 **/
@Service("10009")
public class ReceiveMsgWeChatImpl implements WeChatService {

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Override
    public void handleMsg(WeChatMsgVO msg) {
        String registerWxid = msg.getWxid();
        WeChatMsgVO.InnerData data = msg.getData().getData();
        if (StringUtils.isNotEmpty(data.getMsg())) {
            String resMsg = handleOpenAIRequest(registerWxid,
                new SendTextMsgReqDTO(data.getFromWxid(), " 稍等，还在拼命搜索哈！"), data.getMsg(), weChatRemoteApi);
            weChatRemoteApi.sendTextMsg(msg.getWxid(), new SendTextMsgReqDTO(data.getFromWxid(), resMsg));
        }
    }
}
