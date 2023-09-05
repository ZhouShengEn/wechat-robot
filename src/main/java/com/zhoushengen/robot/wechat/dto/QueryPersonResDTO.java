package com.zhoushengen.robot.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/3 21:05
 */
@Data
public class QueryPersonResDTO implements Serializable {
    private static final long serialVersionUID = -2083362409585836678L;

    /**
     * 微信id
     */
    private String wxid;

    /**
     * 微信号
     */
    private String wxNum;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 备注
     */
    private String remark;

    /**
     * 备注简称
     */
    private String remarkBrief;

    /**
     * 备注全称
     */
    private String remarkWhole;

    /**
     * 昵称简拼
     */
    private String nickBrief;

    /**
     * 昵称全拼
     */
    private String nickWhole;

    /**
     * 英文简称
     */
    private String enBrief;

    /**
     * 英文全称
     */
    private String enWhole;

    /**
     * V3数据 同意好友验证时使用
     */
    private String v3;

    /**
     * V4数据 同意好友验证时使用
     */
    private String v4;

    /**
     * 签名
     */
    private String sign;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 朋友圈背景图
     */
    private String momentsBackgroudImgUrl;

    /**
     * 头像小图
     */
    private String avatarMinUrl;

    /**
     * 头像大图
     */
    private String avatarMaxUrl;



}
