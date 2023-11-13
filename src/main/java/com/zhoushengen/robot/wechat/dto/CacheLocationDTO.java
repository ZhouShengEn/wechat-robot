package com.zhoushengen.robot.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/7 23:03
 */
@Data
public class CacheLocationDTO implements Serializable {
    private static final long serialVersionUID = 7557052618693552321L;

    /**
     * 房间id
     */
    private String roomId;
    /**
     * 微信id
     */
    private String fromId;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 准确度
     */
    private String accuracy;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 时间
     */
    private long date;
    /**
     * 地址
     */
    private String address;
}
