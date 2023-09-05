package com.zhoushengen.robot.wechat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/5 1:02
 */

@Data
@TableName("push_message")
public class PushMessage implements Serializable {
    private static final long serialVersionUID = -8942754288243530858L;
    //对应id，可不填
    @TableId(value = "ID", type = IdType.AUTO)
    private int id;

    //对应字段名，如果属性名和字段名一致，可不填
    @TableField(value = "REGISTER_WXID")
    private String registerWxid;

    //对应字段名，如果属性名和字段名一致，可不填
    @TableField(value = "ROOM_WXID")
    private String roomWxid;

}
