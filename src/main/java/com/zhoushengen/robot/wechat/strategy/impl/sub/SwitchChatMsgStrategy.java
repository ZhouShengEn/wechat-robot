package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.zhoushengen.robot.wechat.strategy.impl.AbstractMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:15
 **/
@Component("切换聊天模式")
public class SwitchChatMsgStrategy extends AbstractMsgStrategy {


    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        String schema = groupSchemaMap.get(fromWxid);
        if (StringUtils.isNotEmpty(schema)) {
            groupSchemaMap.remove(fromWxid);
        } else {
            return "本群当前已处于聊天模式哈";
        }
        return "本群已切换至聊天模式[旺柴]";
    }

    @Override
    public String personalHandleBackContext(WeChatMsgVO msg, String fromWxid, String message) {
        String schema = personalSchemaMap.get(fromWxid);
        if (StringUtils.isNotEmpty(schema)) {
            personalSchemaMap.remove(fromWxid);
        } else {
            return "当前已处于聊天模式哈";
        }
        return "已切换至聊天模式[旺柴]";
    }
}
