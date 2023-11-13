package com.zhoushengen.robot.wechat.service.impl;

import com.zhoushengen.robot.wechat.chain.sub.EmptyMsgChain;
import com.zhoushengen.robot.wechat.chain.sub.GeneralMsgChain;
import com.zhoushengen.robot.wechat.chain.sub.GirlFriendMsgChain;
import com.zhoushengen.robot.wechat.chain.sub.HeadMsgChain;
import com.zhoushengen.robot.wechat.dto.BaseWeChatResDTO;
import com.zhoushengen.robot.wechat.dto.QueryPersonReqDTO;
import com.zhoushengen.robot.wechat.dto.QueryPersonResDTO;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:21
 **/
@Service("10008")
@Slf4j
public class ReceiveRoomMsgWeChatImpl implements WeChatService {


    private HashMap<String, String> registerMap = new HashMap<>(1);

    private HeadMsgChain headMsgChain;

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Value("${wechat.chunkSize}")
    private Integer chunkSize;


    public ReceiveRoomMsgWeChatImpl(EmptyMsgChain emptyMsgChain, GeneralMsgChain generalMsgChain,
        GirlFriendMsgChain girlFriendMsgChain) {
        headMsgChain = new HeadMsgChain();
        headMsgChain.setNext(generalMsgChain).setNext(girlFriendMsgChain).setNext(emptyMsgChain);
    }

    @Override
    public void handleMsg(WeChatMsgVO msg) {
        WeChatMsgVO.InnerData data = msg.getData().getData();
        String fromWxid = data.getFromWxid();
        String message = data.getMsg();
        Object atWxidList = data.getAtWxidList();
        String registerWxid = msg.getWxid();
        String registerName = registerMap.get(registerWxid);
        if (StringUtils.isEmpty(registerName)) {
            BaseWeChatResDTO<QueryPersonResDTO> registerInfo = weChatRemoteApi.queryPerson(
                registerWxid, new QueryPersonReqDTO(registerWxid));
            registerName = registerInfo.getResult().getNick();
            registerMap.put(registerWxid, registerName);
        }
        if ((atWxidList instanceof ArrayList && ((ArrayList) atWxidList).contains(registerWxid))
            || (message.contains("@" + registerName))) {
            handle(msg, data.getFinalFromWxid(), fromWxid, message, registerName);
        }
    }

    private void handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message, String registerName) {
        message = message.replace("@" + registerName, "");
        String context = headMsgChain.start(msg, wxidTo, fromWxid, message);
        if (StringUtils.isNotEmpty(context)) {
            String[] contextArray = com.zhoushengen.robot.util.StringUtils.splitByNumber(context, chunkSize);
            for (String s : contextArray) {
                weChatRemoteApi.sendTextMsg(msg.getWxid(), new SendTextMsgReqDTO(fromWxid, s));
            }
        }
    }


}
