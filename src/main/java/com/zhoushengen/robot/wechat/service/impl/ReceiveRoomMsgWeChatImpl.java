package com.zhoushengen.robot.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.entity.PushMessage;
import com.zhoushengen.robot.wechat.mapper.PushMessageMapper;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:21
 **/
@Service("10008")
public class ReceiveRoomMsgWeChatImpl implements WeChatService {

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Value("${wechat.gaoChangWxid}")
    private String gaoChangWxid;

    @Value("${wechat.ourChatId}")
    private String ourChatId;

    @Value("${wechat.registerName}")
    private String registerName;

    @Autowired
    private PushMessageMapper pushMessageMapper;


    private final String[] girlFriendMessages = new String[]{"怎么啦我的大宝贝！[玫瑰]"
        , "今天也要开心一整天呀！[憨笑]", "不许偷偷看帅哥，没事多想想我[抠鼻]", "记得多喝点热水，身体不好多运动，牢记于心！[白眼]"
        , "夸夸我的大宝贝，大宝贝今天又是美美的一天[旺柴]", "天空一声巨响，明哥闪亮登场，偷偷亲一口小媳妇[亲亲]",
        "最近有谣言说我喜欢你，我要澄清一下，那不是谣言。", "我发现昨天很喜欢你，今天也很喜欢你，而且有预感明天也会喜欢你",
        "这是我的手背，这是我的脚背，你是我的宝贝", "n 55lw ! n paau ! 把你的手机倒过来看看"};


    @Override
    public void handleMsg(WeChatMsgVO msg) {
        WeChatMsgVO.InnerData data = msg.getData().getData();
        String fromWxid = data.getFromWxid();
        String message = data.getMsg();
        Object atWxidList = data.getAtWxidList();
        if (atWxidList instanceof ArrayList && ((ArrayList) atWxidList).contains(msg.getWxid())) {
            handle(msg, data.getFinalFromWxid(), fromWxid, message);
        } else if (message.contains("@" + registerName)) {
            handle(msg, data.getFinalFromWxid(), fromWxid, message);
        }

    }

    private void handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        message = message.replace("@明哥的小跟班", "");
        String context;
        if (StringUtils.isNotEmpty(message) && StringUtils.isNotEmpty(message.replace(" ", ""))) {
            // 询问内容
            context = assembleContext(msg, fromWxid, message, wxidTo);
        } else if (gaoChangWxid.equals(wxidTo)) {
            int random_index = (int) (Math.random() * girlFriendMessages.length);
            context = girlFriendMessages[random_index];
        } else {
            context = "大家好，我是明哥呕心沥血四个小时做的机器人，未来将会上线更多功能，尽请期待哈！[哇]";
        }

        weChatRemoteApi.sendTextMsg(msg.getWxid(), new SendTextMsgReqDTO(fromWxid, context));
    }

    private String assembleContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        String context;
        if (message.contains("关闭每日推送")) {
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
        if (message.contains("开启每日推送")) {
            QueryWrapper<PushMessage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ROOM_WXID", fromWxid);
            pushMessageMapper.delete(queryWrapper);
            return "本群开启成功，将在每日早晨七点半准点为您播报[愉快]";
        }
        if (ourChatId.equals(fromWxid) && gaoChangWxid.equals(wxidTo)) {
            if (message.contains("歉") && message.contains("道")) {
                return "乖乖媳妇不气不气, 道个歉，哄哄媳妇[可怜]";
            } else if (message.contains("哄")) {
                return "哄一哄，给小媳妇十个亲亲，啵唧一大口[亲亲]，再哄一哄，乖乖，给你花花[玫瑰]";
            }
        }
        HashMap<String, String> requestOpenAiMap = new HashMap<>();
        requestOpenAiMap.put(wxidTo, " 稍等，还在拼命搜索哈！");
        String text = handleOpenAIRequest(msg.getWxid(), new SendTextMsgReqDTO(fromWxid
                , weChatRemoteApi.assembleRoomSendText(msg.getWxid(), requestOpenAiMap)),
            message.replace("@明哥的小跟班", ""), weChatRemoteApi);
        HashMap<String, String> map = new HashMap<>();
        map.put(wxidTo, text);
        context = weChatRemoteApi.assembleRoomSendText(msg.getWxid(), map);
        if (gaoChangWxid.equals(wxidTo)) {
            context = context.concat("[玫瑰]");
        }
        return context;
    }

}
