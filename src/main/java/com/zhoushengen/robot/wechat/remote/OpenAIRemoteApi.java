package com.zhoushengen.robot.wechat.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhoushengen.robot.wechat.dto.openai.OpenAIChatReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAICompletionReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAIEmbeddingReqDTO;
import com.zhoushengen.robot.wechat.dto.openai.OpenAIEmbeddingResDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OpenAIRemoteApi {

    public static final String COMPLETION_TEXT_PATTERN = "\n.*";

    @Value("${openAI.auth-token}")
    private String authToken;

    @Value("${openAI.request-timeout}")
    private Integer requestTimeout;

    @Value("${proxy.confirm}")
    private Boolean proxyConfirm;

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private Integer proxyPort;


    /**
     * OpenAI 聊天接口
     *
     * @param openAIChatReqDTO
     * @return
     */
    public JSONObject chat(OpenAIChatReqDTO openAIChatReqDTO) {

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Builder requestbuilder = new Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(RequestBody.create(mediaType, JSON.toJSONString(openAIChatReqDTO)));
        return execute(requestbuilder);
    }

    /**
     * OpenAI 补全接口
     *
     * @param openAICompletionReqDTO
     * @return
     */
    public JSONObject completion(OpenAICompletionReqDTO openAICompletionReqDTO) {

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Builder requestbuilder = new Builder()
            .url("https://api.openai.com/v1/completions")
            .post(RequestBody.create(mediaType, JSON.toJSONString(openAICompletionReqDTO)));
        return execute(requestbuilder);
    }

    private JSONObject execute(Builder requestbuilder) {
        // 增加请求头
        requestbuilder.addHeader("Authorization", authToken);
        Request request = requestbuilder.build();
        OkHttpClient okHttpClient;
        if (proxyConfirm) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            okHttpClient = new OkHttpClient().newBuilder().proxy(proxy).callTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS).writeTimeout(requestTimeout, TimeUnit.SECONDS).build();
        } else {
            okHttpClient = new OkHttpClient().newBuilder().callTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS).writeTimeout(requestTimeout, TimeUnit.SECONDS).build();
        }
        JSONObject res = null;
        try {
            res = JSONObject.parseObject(okHttpClient.newCall(request).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    public static String formatResContext(JSONObject res) {
        if (null != res) {
            String object = res.getObject("object", String.class);
            JSONArray choices = res.getObject("choices", JSONArray.class);
            JSONObject choice = (JSONObject) choices.get(0);
            // 文本补全
            if (object.equals("text_completion")) {
                Pattern p = Pattern.compile(COMPLETION_TEXT_PATTERN);
                Matcher m = p.matcher(choice.getObject("text", String.class));
                StringBuilder result = new StringBuilder();
                while(m.find()){
                    result.append(m.group());
                }
                return result.toString().replaceAll("\n", "\r").replaceAll("\r\r", "\r");
            } else {
                // 聊天模型
                return choice.getObject("message", JSONObject.class).getObject("content", String.class).replaceAll("\n", "\r");
            }
        }
        return null;
    }


    public OpenAIEmbeddingResDTO requestEmbedding(String input) {
        OpenAIEmbeddingReqDTO openAIEmbeddingReqDTO = new OpenAIEmbeddingReqDTO();
        openAIEmbeddingReqDTO.setInput(input);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Builder requestbuilder = new Builder()
            .url("https://api.openai.com/v1/embeddings")
            .post(RequestBody.create(mediaType, JSON.toJSONString(openAIEmbeddingReqDTO)));
        // 增加请求头
        requestbuilder.addHeader("Authorization", "Bearer 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");
        Request request = requestbuilder.build();
        OkHttpClient okHttpClient;
        if (proxyConfirm) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            okHttpClient = new OkHttpClient().newBuilder().proxy(proxy).callTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS).writeTimeout(requestTimeout, TimeUnit.SECONDS).build();
        } else {
            okHttpClient = new OkHttpClient().newBuilder().callTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS).writeTimeout(requestTimeout, TimeUnit.SECONDS).build();
        }
        JSONObject res = null;
        try {
            res = JSONObject.parseObject(okHttpClient.newCall(request).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(res.toJSONString(), OpenAIEmbeddingResDTO.class);
    }

}
