package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.common.utils.R;
import com.mc.userserver.entity.PostDetailTable;

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
}
