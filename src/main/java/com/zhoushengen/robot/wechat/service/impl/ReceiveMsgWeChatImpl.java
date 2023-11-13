package com.zhoushengen.robot.wechat.service.impl;

import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.strategy.PersonalMsgStrategy;
import com.zhoushengen.robot.wechat.strategy.StartWithMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:21
 **/
@Service("10009")
public class ReceiveMsgWeChatImpl implements WeChatService {

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Autowired
    private Map<String, PersonalMsgStrategy> personalMsgStrategyMap;

    @Autowired
    private Map<String, StartWithMsgStrategy> startWithMsgStrategyMap;

    @Value("${wechat.chunkSize}")
    private Integer chunkSize;

    @Override
    public void handleMsg(WeChatMsgVO msg) {
        WeChatMsgVO.InnerData data = msg.getData().getData();
        String message = data.getMsg();
        if (StringUtils.isNotEmpty(message)) {
            String fromWxid = data.getFromWxid();
            PersonalMsgStrategy personalMsgStrategy;
            if (null == (personalMsgStrategy = personalMsgStrategyMap.get(message))) {
                String msgStrategy = startWithMsgStrategyMap.keySet().stream().filter(message::startsWith).findFirst()
                    .orElse("generalMsgStrategy");
                personalMsgStrategy = personalMsgStrategyMap.get(msgStrategy);
            }
            String resMsg = personalMsgStrategy.personalHandleBackContext(msg, fromWxid, message);
            if (StringUtils.isNotEmpty(resMsg)) {
                String[] textArray = com.zhoushengen.robot.util.StringUtils.splitByNumber(resMsg, chunkSize);
                for (String s : textArray) {
                    weChatRemoteApi.sendTextMsg(msg.getWxid(), new SendTextMsgReqDTO(fromWxid, s));
                }
            }
        }
    }
}
