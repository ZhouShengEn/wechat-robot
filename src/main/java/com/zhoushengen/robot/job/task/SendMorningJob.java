package com.zhoushengen.robot.job.task;

import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/2 19:37
 */
@Component
@Slf4j
public class SendMorningJob extends QuartzJobBean {

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Value("${wechat.register}")
    private String registerWxid;

    @Value("${wechat.ourChatId}")
    private String ourChatId;

    @Value("${wechat.gaoChangWxid}")
    private String gaoChangWxid;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        HashMap<String, String> map = new HashMap<>();
        map.put(gaoChangWxid, " 早安我的大宝贝![玫瑰]");
        weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(ourChatId, weChatRemoteApi.assembleRoomSendText(registerWxid, map)));
    }
}
