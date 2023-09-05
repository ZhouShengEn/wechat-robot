package com.zhoushengen.robot.wechat.remote;

import com.alibaba.fastjson.JSON;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.zhoushengen.robot.util.orika.MappingUtils;
import com.zhoushengen.robot.wechat.dto.BaseWeChatReqDTO;
import com.zhoushengen.robot.wechat.dto.BaseWeChatResDTO;
import com.zhoushengen.robot.wechat.dto.QueryPersonReqDTO;
import com.zhoushengen.robot.wechat.dto.QueryPersonResDTO;
import com.zhoushengen.robot.wechat.dto.QueryRoomsReqDTO;
import com.zhoushengen.robot.wechat.dto.QueryRoomsResDTO;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RetrofitClient(baseUrl = "http://127.0.0.1:7777")
public interface WeChatRemoteApi {

    Logger log = LoggerFactory.getLogger(WeChatRemoteApi.class);

    @POST("/DaenWxHook/httpapi/")
    Response<BaseWeChatResDTO> sendMsg(@Query("wxid") String wxid, @Body BaseWeChatReqDTO reqDTO);

    /**
     * 查询对象信息（Q0004）
     *
     * @param wxid
     * @param reqDTO
     * @return
     */
    default BaseWeChatResDTO<QueryPersonResDTO> queryPerson(String wxid, QueryPersonReqDTO reqDTO) {
        Response<BaseWeChatResDTO> res = sendMsg(wxid, new BaseWeChatReqDTO<>("Q0004", reqDTO));
        BaseWeChatResDTO body = res.body();
        if (res.isSuccessful() && null != body) {
            body.setResult(MappingUtils.beanConvert(body.getResult(), QueryPersonResDTO.class));
            return body;
        }
        log.error("wechat queryPerson request res:{}, wxid:{}, req:{}", JSON.toJSONString(res), wxid,
            JSON.toJSONString(reqDTO));
        return null;
    }

    /**
     * 获取群聊列表（Q0006）
     *
     * @param wxid
     * @return
     */
    default BaseWeChatResDTO<List<QueryRoomsResDTO>> queryRooms(String wxid) {

        Response<BaseWeChatResDTO> res = sendMsg(wxid, new BaseWeChatReqDTO<>("Q0006", new QueryRoomsReqDTO()));
        BaseWeChatResDTO body = res.body();
        if (res.isSuccessful() && null != body) {
            body.setResult(MappingUtils.beanListConvert((ArrayList) body.getResult(), QueryRoomsResDTO.class));
            return body;
        }
        log.error("wechat queryRooms request res:{}, wxid:{}", JSON.toJSONString(res), wxid);
        return null;
    }

    /**
     * 发送文本消息（Q0001）
     *
     * @param wxid
     * @param reqDTO
     * @return
     */
    default BaseWeChatResDTO<Void> sendTextMsg(String wxid, SendTextMsgReqDTO reqDTO) {
        Response<BaseWeChatResDTO> res = sendMsg(wxid, new BaseWeChatReqDTO<>("Q0001", reqDTO));
        BaseWeChatResDTO body = res.body();
        if (res.isSuccessful() && null != body) {
            return body;
        }
        log.error("wechat sendTextMsg request res:{}, wxid:{}, req:{}", JSON.toJSONString(res), wxid,
            JSON.toJSONString(reqDTO));
        return null;
    }

    default String assembleRoomSendText(String registerWxid, HashMap<String, String> toWxidMap) {
        StringBuilder context = new StringBuilder();
        toWxidMap.keySet().forEach(key -> {
            String msg = toWxidMap.get(key);
            BaseWeChatResDTO<QueryPersonResDTO> res = queryPerson(registerWxid, new QueryPersonReqDTO(key));
            context.append("[@,wxid=").append(key).append(",nick=")
                .append(res.getResult().getNick()).append(",isAuto=true]").append(msg);
        });
        return context.toString();
    }

}
