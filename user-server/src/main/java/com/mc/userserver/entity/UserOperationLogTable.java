package com.mc.userserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.awt.*;
import java.time.LocalDateTime;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-03 14:28
 * @类说明：填写类说明
 * @修改记录：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOperationLogTable {
    @TableId
    private String operationId;

    private String UserId;

    private String operationAddr;

    private String operationUrl;
    private String operationContent;
    private String operationDev;
    private String operationResult;
    private String operationDesc;
    private String operationCost;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    private String attribute01;
    private String attribute02;
    private String attribute03;
    private String attribute04;
    private String attribute05;
}
