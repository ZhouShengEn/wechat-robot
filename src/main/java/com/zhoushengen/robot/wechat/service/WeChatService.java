package com.zhoushengen.robot.wechat.service;


import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:21
 **/
public interface WeChatService {

    String[] timeOutMessages = new String[]{
        "外网访问超时，可能是chatgpt需要返回的数据过多，您可以在提问时限制回答字数在100字以内！[委屈]",
        "外网访问又超时了，抬头看会窗外的风景吧，或者问个幅度少点的问题呗[晕]！"};

    void handleMsg(WeChatMsgVO msg);

}
