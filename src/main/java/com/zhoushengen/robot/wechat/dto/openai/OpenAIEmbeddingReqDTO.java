package com.zhoushengen.robot.wechat.dto.openai;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: 获取嵌入向量-入参
 * @DateTime: 2023/9/1 15:51
 **/
@Data
public class OpenAIEmbeddingReqDTO implements Serializable {

    private static final long serialVersionUID = -2921764361924220228L;

    private String input;

    private String model = "text-embedding-ada-002";
}
