package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.zhoushengen.robot.component.MilvusService;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAIChatReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAIChatReqDTO.Message;
import com.zhoushengen.robot.wechat.strategy.impl.AbstractMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 18:08
 **/
@Component
@Slf4j
public class GeneralMsgStrategy extends AbstractMsgStrategy {

    @Autowired
    private MilvusService milvusService;

    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        String schema = groupSchemaMap.get(fromWxid);
        // 专家模式下
        if (StringUtils.isNotEmpty(schema)) {
            groupSchemaMap.put(fromWxid, "专家模式");
            HashMap<String, String> map = new HashMap<>();
            map.put(wxidTo, milvusService.embeddingSearch(wxidTo, message));
            return weChatRemoteApi.assembleRoomSendText(msg.getWxid(), map);
        }

        // 聊天模式下
        HashMap<String, String> requestOpenAiMap = new HashMap<>();
        requestOpenAiMap.put(wxidTo, " 稍等，还在拼命搜索哈！");
        OpenAIChatReqDTO openAIChatReqDTO = new OpenAIChatReqDTO();
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new Message(message));
        openAIChatReqDTO.setMessages(messages);
        String text = handleOpenAIRequest(msg.getWxid(), new SendTextMsgReqDTO(fromWxid
            , weChatRemoteApi.assembleRoomSendText(msg.getWxid(), requestOpenAiMap)), openAIChatReqDTO, weChatRemoteApi);
        HashMap<String, String> map = new HashMap<>();
        map.put(wxidTo, text);
        return weChatRemoteApi.assembleRoomSendText(msg.getWxid(), map);
    }

    @Override
    public String personalHandleBackContext(WeChatMsgVO msg, String fromWxid, String message) {
        String schema = personalSchemaMap.get(fromWxid);
        // 专家模式下
        if (StringUtils.isNotEmpty(schema)) {
            personalSchemaMap.put(fromWxid, "专家模式");
            return milvusService.embeddingSearch(fromWxid, message);
        }

        // 聊天模式下
        OpenAIChatReqDTO openAIChatReqDTO = new OpenAIChatReqDTO();
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new Message(message));
        openAIChatReqDTO.setMessages(messages);
        return handleOpenAIRequest(msg.getWxid(),
            new SendTextMsgReqDTO(fromWxid, " 稍等，还在拼命搜索哈！"), openAIChatReqDTO, weChatRemoteApi);

    }
}
