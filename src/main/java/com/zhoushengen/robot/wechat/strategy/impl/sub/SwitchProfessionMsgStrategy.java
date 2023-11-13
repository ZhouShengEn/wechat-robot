package com.zhoushengen.robot.wechat.strategy.impl.sub;

import com.zhoushengen.robot.wechat.strategy.impl.AbstractMsgStrategy;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/10 17:32
 **/
@Component("切换专家模式")
public class SwitchProfessionMsgStrategy extends AbstractMsgStrategy {
    @Override
    public String roomHandleBackContext(WeChatMsgVO msg, String fromWxid, String message, String wxidTo) {
        String schema = groupSchemaMap.get(fromWxid);
        if (StringUtils.isNotEmpty(schema)) {
            return "本群当前已处于专家模式哈";
        } else {
            groupSchemaMap.put(fromWxid, "专家模式");
        }
        return "本群已切换至专家模式[得意]，一小时内没有cue我将自动转换成聊天模式呀";
    }

    @Override
    public String personalHandleBackContext(WeChatMsgVO msg, String fromWxid, String message) {
        String schema = personalSchemaMap.get(fromWxid);
        if (StringUtils.isNotEmpty(schema)) {
            return "当前已处于专家模式哈";
        } else {
            personalSchemaMap.put(fromWxid, "专家模式");
        }
        return "已切换至专家模式[得意]，一小时内没有cue我将自动转换成聊天模式呀";
    }
}
