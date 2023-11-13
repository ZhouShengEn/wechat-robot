package com.zhoushengen.robot.wechat.dto.openai;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:51
 **/
@Data
public class OpenAICompletionReqDTO implements Serializable {


    private static final long serialVersionUID = 8101668234866356253L;
    private String model = "gpt-3.5-turbo-instruct";

    private Integer temperature = 1;

    private Integer max_tokens = 1000;

    private String prompt;

}
