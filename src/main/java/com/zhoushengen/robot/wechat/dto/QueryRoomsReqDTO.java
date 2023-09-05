package com.zhoushengen.robot.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:51
 **/
@Data
public class QueryRoomsReqDTO implements Serializable {
    private static final long serialVersionUID = 1879980416909382184L;

    /**
     * 1=从缓存中获取，2=重新遍历二叉树并刷新缓存
     */
    private String type = "2";
}
