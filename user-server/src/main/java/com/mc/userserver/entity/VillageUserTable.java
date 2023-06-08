package com.mc.userserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-06 10:17
 * @类说明：填写类说明
 * @修改记录：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageUserTable {

    @TableId
    private String id;
    private String villageId;
    private String userId;
    private String status;
    private String isAdd;
    private String villageUserContribute;
    private String villageUserAppellation;
    private String villageUserAppellationDesc;

    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private LocalDateTime updateTime;



    private String attribute01;
    private String attribute02;
    private String attribute03;
    private String attribute04;
    private String attribute05;
}
