package com.zhoushengen.robot.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/3 21:06
 */
@Data
public class BaseWeChatResDTO<K> implements Serializable {
    private static final long serialVersionUID = -4350803139155441306L;

    private Integer code;

    private String msg;

    private String wxid;

    private Integer port;

    private Integer pid;

    private String flag;

    private String timestamp;

    private K result;
}
