package com.zhoushengen.robot.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/1 15:51
 **/
@Data
public class QueryRoomsResDTO implements Serializable {

    private static final long serialVersionUID = 8049479292077883130L;

    /**
     * 微信id
     */
    private String wxid;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 备注
     */
    private String remark;

    /**
     * 昵称简拼
     */
    private String nickBrief;

    /**
     * 昵称全拼
     */
    private String nickWhole;

    /**
     * 备注简拼
     */
    private String remarkBrief;

    /**
     * 备注全拼
     */
    private String remarkWhole;

    /**
     * V3数据
     */
    private String v3;

    /**
     * 头像小图
     */
    private String avatarMinUrl;

    /**
     * 头像大图
     */
    private String avatarMaxUrl;
}
