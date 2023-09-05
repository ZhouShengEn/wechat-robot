package com.zhoushengen.robot.wechat.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:51
 **/
@Data
public class OpenAIChatReqDTO implements Serializable {

    private static final long serialVersionUID = -263968593604777341L;

    private String model = "gpt-3.5-turbo";

    private ArrayList<Message> messages;

    @Data
    public static class Message implements Serializable {
        private static final long serialVersionUID = 6047230470960857303L;
        private String role = "user";
        private String content;
    }
}
