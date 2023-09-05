package com.zhoushengen.robot.wechat.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhoushengen.robot.wechat.dto.OpenAIChatReqDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class OpenAIRemoteApi {

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


    public JSONObject requestOpenAi(String context) {
        OpenAIChatReqDTO openAIChatReqDTO = new OpenAIChatReqDTO();
        ArrayList<OpenAIChatReqDTO.Message> messages = new ArrayList<>();
        OpenAIChatReqDTO.Message chatMessage = new OpenAIChatReqDTO.Message();
        chatMessage.setContent(context);
        messages.add(chatMessage);
        openAIChatReqDTO.setMessages(messages);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request.Builder requestbuilder = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(RequestBody.create(mediaType, JSON.toJSONString(openAIChatReqDTO)));
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
            JSONArray choices = res.getObject("choices", JSONArray.class);
            JSONObject choice = (JSONObject) choices.get(0);
            return choice.getObject("message", JSONObject.class).getObject("content", String.class);
        }
        return null;
    }

}
