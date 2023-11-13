package com.zhoushengen.robot.wechat.chain.sub;

import com.zhoushengen.robot.wechat.chain.RoomMsgChain;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: zhoushengen
 * @Description: 女票发消息处理
 * @DateTime: 2023/11/13 10:30
 **/
@Component
public class GirlFriendMsgChain extends RoomMsgChain {

    private final String[] girlFriendMessages = new String[]{"怎么啦我的大宝贝！[玫瑰]"
        , "今天也要开心一整天呀！[憨笑]", "不许偷偷看帅哥，没事多想想我[抠鼻]", "记得多喝点热水，身体不好多运动，牢记于心！[白眼]"
        , "夸夸我的大宝贝，大宝贝今天又是美美的一天[旺柴]", "天空一声巨响，明哥闪亮登场，偷偷亲一口小媳妇[亲亲]",
        "最近有谣言说我喜欢你，我要澄清一下，那不是谣言。", "我发现昨天很喜欢你，今天也很喜欢你，而且有预感明天也会喜欢你",
        "这是我的手背，这是我的脚背，你是我的宝贝", "n 55lw ! n paau ! 把你的手机倒过来看看"};

    @Value("${wechat.girlFriendWxid}")
    private String girlFriendWxid;

    @Override
    public Boolean preHandle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        return girlFriendWxid.equals(wxidTo);
    }

    @Override
    public String handle(WeChatMsgVO msg, String wxidTo, String fromWxid, String message) {
        int random_index = (int) (Math.random() * girlFriendMessages.length);
        return girlFriendMessages[random_index];
    }
}
