package com.mc.userserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mc.userserver.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDateTime;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-15 18:10
 * @类说明：填写类说明
 * @修改记录：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTable {

    @TableId
    private String userId;

    private String nickname;

    private String password;

    private String registerType;

    private String email;

    private String phone;



    private String avatar;

    private String inVillage;

    private String userIntroduction;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String isDelete;

    private String attribute01;
    private String attribute02;
    private String attribute03;
    private String attribute04;
    private String attribute05;




}
