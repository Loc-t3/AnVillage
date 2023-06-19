package com.mc.userserver.dto;

import com.mc.userserver.entity.PostCommentTable;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-17 23:55
 * @类说明：填写类说明
 * @修改记录：
 */
public class CommentDTO extends UserDTO {
    private PostCommentTable postComment;
    private String commentCount;
}
