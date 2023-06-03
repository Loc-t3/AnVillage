package com.mc.userserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mc.userserver.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-03 4:55
 * @类说明：填写类说明
 * @修改记录：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileTable implements Serializable {
    @TableId
    private String profileId;

    private String userId;

    private String school;
    private String gender;

    private String birthday;

    private String userFavorability;

    private String userAppellation;

    private String userAppellationDesc;

    private String profileFansNum;

    private String profileActiveNum;

    private String profileFavoriteNum;

    private String profileLikeNum;

    private String profilePostNum;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String attribute01;
    private String attribute02;
    private String attribute03;
    private String attribute04;
    private String attribute05;

    @TableField(exist = false)
    @Transient
    private UserDTO userDTO;

}
