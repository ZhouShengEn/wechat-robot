package com.zhoushengen.robot.wechat.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAIChatReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAICompletionReqDTO;
import com.zhoushengen.robot.wechat.remote.OpenAIRemoteApi;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.strategy.PersonalMsgStrategy;
import com.zhoushengen.robot.wechat.strategy.RoomMsgStrategy;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 16:59
 **/
@Component
public abstract class AbstractMsgStrategy implements PersonalMsgStrategy, RoomMsgStrategy {

    String[] timeOutMessages = new String[]{
        "外网访问超时，可能是chatgpt需要返回的数据过多，您可以在提问时限制回答字数在100字以内！[委屈]",
        "外网访问又超时了，抬头看会窗外的风景吧，或者问个幅度少点的问题呗[晕]！"};

    @Value("${wechat.masterWxid}")
    public String masterWxid;

    @Autowired
    public RedissonClient redissonClient;

    @Autowired
    public WeChatRemoteApi weChatRemoteApi;

    @Autowired
    public OpenAIRemoteApi openAIRemoteApi;


    public String handleOpenAIRequest(String registerWxid, SendTextMsgReqDTO timeoutResMsg, Object openAIReqDTO,
        WeChatRemoteApi weChatRemoteApi) {
        AtomicBoolean flag = new AtomicBoolean(Boolean.TRUE);
        Date start = new Date();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            while (flag.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (new Date().getTime() - start.getTime() >= 25000) {
                    weChatRemoteApi.sendTextMsg(registerWxid, timeoutResMsg);
                    flag.set(Boolean.FALSE);
                }
            }
        });
        JSONObject res;
        if (openAIReqDTO instanceof OpenAICompletionReqDTO) {
            res = openAIRemoteApi.completion((OpenAICompletionReqDTO) openAIReqDTO);
        } else{
            res = openAIRemoteApi.chat((OpenAIChatReqDTO) openAIReqDTO);
        }

        flag.set(Boolean.FALSE);
        future.cancel(Boolean.TRUE);
        //            Response<JSONObject> chatRes = openAIRemoteApi.chat(chatReq);
        String text = null;
        if (null == res) {
            int random_index = (int) (Math.random() * timeOutMessages.length);
            text = timeOutMessages[random_index];
        } else if (null != res.get("error")){
            JSONObject error = res.getObject("error", JSONObject.class);
            text = error.getObject("message", String.class);
        } else {
            text = OpenAIRemoteApi.formatResContext(res);
        }
        return text;
    }

}
