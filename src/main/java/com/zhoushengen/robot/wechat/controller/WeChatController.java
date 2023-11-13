package com.zhoushengen.robot.wechat.controller;

import com.zhoushengen.robot.util.IpUtil;
import com.zhoushengen.robot.wechat.service.ExpendService;
import com.zhoushengen.robot.wechat.service.WeChatService;
import com.zhoushengen.robot.wechat.vo.WeChatMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("chat")
public class WeChatController {


    @Autowired
    private Map<String, WeChatService> weChatServiceMap;

    @Autowired
    private ExpendService expendService;


    @RequestMapping(value = "/msg", method = RequestMethod.POST)
    void getMsg(@RequestBody WeChatMsgVO msg) {
        WeChatService weChatService = weChatServiceMap.get(String.valueOf(msg.getEvent()));
        if (null != weChatService) {
            weChatService.handleMsg(msg);
        }

    }

    @RequestMapping(value = "/location", method = RequestMethod.GET)
    String getLocation(@RequestParam(required = false) String latitude, @RequestParam(required = false) String longitude
        , @RequestParam String msg, @RequestParam String sign, @RequestParam(required = false) String accuracy, HttpServletRequest request) {
        expendService.getLocation(latitude, longitude, msg, sign, accuracy, IpUtil.getIpAddress(request));
        return "end";
    }


}
