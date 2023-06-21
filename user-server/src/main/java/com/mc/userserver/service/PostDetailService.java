package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.common.utils.R;
import com.mc.userserver.entity.PostCommentTable;
import com.mc.userserver.entity.PostDetailTable;

import java.util.HashMap;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-11 14:48
 * @类说明：填写类说明
 * @修改记录：
 */
public interface PostDetailService extends IService<PostDetailTable> {
    Boolean editPost(PostDetailTable postDetail,String type);

    Boolean deletePost(String type, String postDetailId);

    R<String> activeLike(String postDetailId);

    R<String> activeFavorite(String postDetailId);

    R<String> activeShare(String postDetailId);

    HashMap<String,Object> getCommentList(PostCommentTable postComment);

    HashMap<String,Object> getPostList(String userId);

    HashMap<String,Object> getLikeList(String userId);

    HashMap<String,Object> getFavoriteList(String userId);
}
