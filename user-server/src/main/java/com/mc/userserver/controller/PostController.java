package com.mc.userserver.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.mc.common.utils.R;
import com.mc.common.utils.VillageOrAppellationEnum;
import com.mc.userserver.config.SysLog;
import com.mc.userserver.dto.CommentDTO;
import com.mc.userserver.dto.UserDTO;
import com.mc.userserver.entity.PostCommentTable;
import com.mc.userserver.entity.PostDetailTable;
import com.mc.userserver.entity.UserTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.mapper.PostCommentMapper;
import com.mc.userserver.service.PostDetailService;
import com.mc.userserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.mc.common.utils.AllStringCtant.COMMON_NUMBER_ONE;
import static com.mc.common.utils.AllStringCtant.COMMON_NUMBER_ZERO;
import static com.mc.common.utils.UserCommon.setUUId;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-09 11:21
 * @类说明：填写类说明
 * @修改记录：
 */
@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostDetailService postDetailService;

    @Autowired
    private PostCommentMapper postCommentMapper;

    /**
     * * 获取用户发布定位选择
     *      * 获取用户发布帖子信息 title img body label
     *      type 0- 新增
     *      type 1 - 修改
     * @param data
     * @return
     */
    @PostMapping("/edit")
    @SysLog(value = "#{'用户-操作-编辑帖子'}", level = "info", printResult = 0)
    public R<String> editPost(@RequestBody HashMap<String,Object> data){
        Object postDetailTable =  data.get("postDetail");
        PostDetailTable postDetail= BeanUtil.copyProperties(postDetailTable, PostDetailTable.class);

        Boolean success = postDetailService.editPost((PostDetailTable) postDetail,(String)data.get("type"));
        if (!success){
            return R.error("发生异常，请稍后重试");
        }
        if (((String) data.get("type")).equals(COMMON_NUMBER_ONE)){
            return R.success("更新成功");
        }
        return R.success("发布成功");
    }

    /**
     * 通过帖子id和类型判断删除情况
     * 并依据已有的数据判断是否隐藏
     * 如果愿意数据 isdelete 是 0，进行逻辑删除时修改为 1
     * isdelete 是 1，进行逻辑删除时修改为 0
     * @param type
     * @param postDetailId
     * @return
     */
    @PutMapping("/{postDetailId}/{type}")
    @SysLog(value = "#{'用户-操作-删除帖子'}",level = "info", printResult = 0)
    public R<String> deletePost(@PathVariable String type,@PathVariable String postDetailId){
        Boolean success = postDetailService.deletePost(type, postDetailId);
        if (!success){
            return R.error("删除失败，请稍后再试");
        }

        return R.success("删除成功");
    }

    /**
     * 对用户的发帖进行点赞
     * @param postDetailId
     * @return
     */
    @PostMapping("/like/{postDetailId}")
    @SysLog(value = "#{'用户-操作-点赞帖子'}",level = "info", printResult = 0)
    public R<String> activeLike(@PathVariable String postDetailId){

        return postDetailService.activeLike(postDetailId);
    }

    /**
     * 对用户的发帖进行收藏
     * @param postDetailId
     * @return
     */
    @PostMapping("/favorite/{postDetailId}")
    @SysLog(value = "#{'用户-操作-收藏帖子'}",level = "info", printResult = 0)
    public R<String> activeFavorite(@PathVariable String postDetailId){

        return postDetailService.activeFavorite(postDetailId);
    }

    /**
     * 对用户的发帖进行分享
     * @param postDetailId
     * @return
     */
    @PostMapping("/share/{postDetailId}")
    @SysLog(value = "#{'用户-操作-分享帖子'}",level = "info", printResult = 0)
    public R<String> activeShare(@PathVariable String postDetailId){

        return postDetailService.activeShare(postDetailId);
    }


    @PostMapping("/comment")
    @SysLog(value = "#{'用户-操作-评论帖子'}",level = "info", printResult = 0)
    public R<String> activeComment(@RequestBody PostCommentTable postComment){

        postComment.setUserId(BaseContext.getUser().getUserId());
        postCommentMapper.insert(postComment);

        return R.success("评论成功");
    }

    /**
     * 获取查看帖子的相关评论
     * @param postComment
     * @return
     */
    @PostMapping("/commentList")
    public R<HashMap<String,Object>> getCommentList(@RequestBody PostCommentTable postComment){

        HashMap<String, Object> commentList = postDetailService.getCommentList(postComment);
        return R.success(commentList);
    }

    /**
     * 获取用户主页帖子数据
     * @return
     */
    @PostMapping("/list")
    public R<Map<String,Object>> getPostList(){
        String userId = BaseContext.getUser().getUserId();

        return R.success(postDetailService.getPostList(userId));

    }

    /**
     * 获取用户主页点赞过的数据
     * @return
     */
    @PostMapping("/getLike")
    public R<Map<String,Object>> getLikeList(){
        String userId = BaseContext.getUser().getUserId();

        return R.success(postDetailService.getLikeList(userId));

    }

    /**
     * 获取用户主页收藏的数据
     * @return
     */
    @PostMapping("/getFavorite")
    public R<Map<String,Object>> getFavoriteList(){
        String userId = BaseContext.getUser().getUserId();

        return R.success(postDetailService.getFavoriteList(userId));

    }












}
