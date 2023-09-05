package com.zhoushengen.robot.wechat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:49
 **/
@Data
@AllArgsConstructor
public class BaseWeChatReqDTO<K> implements Serializable {
    private static final long serialVersionUID = -7440846534318260368L;

    private String type;

    private K data;

}
