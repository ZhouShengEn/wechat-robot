package com.zhoushengen.robot.wechat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:58
 **/
@Data
@AllArgsConstructor
public class SendTextMsgReqDTO implements Serializable {
    private static final long serialVersionUID = -8595125505346154211L;

    private String wxid;

    private String msg;
}
