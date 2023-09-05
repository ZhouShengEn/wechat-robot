package com.zhoushengen.robot.wechat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:51
 **/
@Data
@AllArgsConstructor
public class QueryPersonReqDTO implements Serializable {

    private static final long serialVersionUID = -263968593604777341L;

    private String wxid;

}
