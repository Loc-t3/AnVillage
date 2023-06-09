package com.mc.userserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.google.common.collect.PeekingIterator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.PipedReader;
import java.time.LocalDateTime;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-09 11:33
 * @类说明：填写类说明
 * @修改记录：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailTable {
    @TableId
    private String postDtailId;
    private String postTypeCode;
    private String userId;
    private String postTitle;
    private String postContent;
    private String postImage;

    private String postPublishPosition;
    private String postLikeCount;
    private String postCommentCount;
    private String postFavoriteCount;
    private String postShareCount;
    private String postWeight;
    private String isDelete;
    private String version;


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
