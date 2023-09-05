package com.zhoushengen.robot.wechat.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:43
 **/
@Data
public class WeChatMsgVO implements Serializable {
    private static final long serialVersionUID = 5623871736274834328L;

    private Integer event;

    private String wxid;

    private MessageData data;


    @Data
    public static class MessageData implements Serializable{
        private static final long serialVersionUID = -1106822895458243811L;
        private String des;
        private String flag;
        private Integer port;
        private Integer pid;
        private String wxid;
        private String type;
        private String timestamp;
        private InnerData data;

    }

    @Data
    public static class InnerData implements Serializable{

        private static final long serialVersionUID = -1300821874667251886L;

        private String timeStamp;

        private Integer fromType;

        private Integer msgType;

        private Integer msgSource;

        private String fromWxid;

        private String finalFromWxid;

        private Object atWxidList;

        private Integer silence;

        private Integer membercount;

        private String signature;

        private String msg;

        private String msgBase64;

    }


}
