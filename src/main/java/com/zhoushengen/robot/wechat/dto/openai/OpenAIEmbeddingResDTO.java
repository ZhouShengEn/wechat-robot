package com.zhoushengen.robot.wechat.dto.openai;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushengen
 * @Description: 获取嵌入向量-出参
 * @DateTime: 2023/9/1 15:51
 **/
@Data
public class OpenAIEmbeddingResDTO implements Serializable {

    private static final long serialVersionUID = 6807442449739127108L;

    private String object;

    private String model;

    private Usage usage;

    private List<EmbeddingData> data;

    @Data
    public static class Usage implements Serializable {
        private static final long serialVersionUID = -7172961563341617601L;
        private Integer prompt_tokens;
        private Integer total_tokens;
    }

    @Data
    public static class EmbeddingData implements Serializable {
        private static final long serialVersionUID = -9123381113021454364L;
        private String object;
        private Integer index;
        private ArrayList<Float> embedding;
    }
}
