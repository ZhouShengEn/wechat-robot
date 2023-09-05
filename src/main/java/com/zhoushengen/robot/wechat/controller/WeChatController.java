package com.zhoushengen.robot.wechat.controller;

import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("chat")
public class WeChatController {


    @Autowired
    private Map<String, WeChatService> weChatServiceMap;


    @RequestMapping(value = "/msg", method = RequestMethod.POST)
    void getMsg(@RequestBody WeChatMsgVO msg) {
        WeChatService weChatService = weChatServiceMap.get(String.valueOf(msg.getEvent()));
        if (null != weChatService) {
            weChatService.handleMsg(msg);
        }

    }


}
