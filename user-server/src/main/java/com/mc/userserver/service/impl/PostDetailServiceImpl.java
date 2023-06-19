package com.mc.userserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.common.utils.VillageOrAppellationEnum;
import com.mc.userserver.dto.UserDTO;
import com.mc.userserver.entity.*;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.mapper.*;
import com.mc.userserver.service.PostDetailService;
import com.mc.userserver.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static com.mc.common.utils.AllStringCtant.*;
import static com.mc.common.utils.UserCommon.setUUId;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-11 14:49
 * @类说明：填写类说明
 * @修改记录：
 */
@Service
@Slf4j
public class PostDetailServiceImpl extends ServiceImpl<PostDetailMapper, PostDetailTable> implements PostDetailService {
    @Autowired
    private PostDetailService postDetailService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostLikeMapper postLikeMapper;
    @Autowired
    private PostFavoriteMapper postFavoriteMapper;
    @Autowired
    private PostShareMapper postShareMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PostCommentMapper postCommentMapper;

    @Override
    public Boolean editPost(PostDetailTable postDetail,String type) {
        boolean success = false;

            String userId = BaseContext.getUser().getUserId();
            postDetail.setUserId(userId);

            postDetail.setPostTypeCode(RandomUtil.randomString(5));
            postDetail.setPostPublishPosition(VillageOrAppellationEnum.getByCode(postDetail.getPostPublishPosition()).getName());
        //获取旧数据
        /*LambdaQueryWrapper<PostDetailTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostDetailTable::getPostDetailId,postDetail.getPostDetailId());
        PostDetailTable detail = postDetailService.getOne(queryWrapper);*/

        try {
            if (type.equals(COMMON_NUMBER_ZERO)){
                postDetail.setPostDetailId(setUUId());
            success  = save(postDetail);
            }else {
                PostDetailTable detail = this.getById(postDetail.getPostDetailId());

                postDetail.setVersion(detail.getVersion()+1);
                success = this.updateById(postDetail);
            }

        }catch (Exception e){

        }


        return success;
    }

    @Override
    public Boolean deletePost(String type, String postDetailId) {
        Boolean success = false;
        if (type.equals(COMMON_NUMBER_ONE)){
            success = removeById(postDetailId);
        }
        //逻辑删除
        //判断当前逻辑删除状态
        if (type.equals(COMMON_NUMBER_ZERO)){
            PostDetailTable postDetail = getById(postDetailId);
            if (postDetail.getIsDelete().equals(COMMON_NUMBER_ZERO)){
                postDetail.setIsDelete(COMMON_NUMBER_ONE);
            }else {
                postDetail.setIsDelete(COMMON_NUMBER_ZERO);
            }
            success = updateById(postDetail);

        }
        return success;
    }

    @Override
    @Transactional()
    public R<String> activeLike(String postDetailId) {
        //获取当前用户
        String userId = BaseContext.getUser().getUserId();
        boolean success = false;
        //点赞
        //通过id获取当前数据
        String key = ACTIVE_LIKE + postDetailId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId);
        //在redis中进行判断
        //不存在，进行添加-实现用户已点赞
        if (BooleanUtil.isFalse(isMember)){
            //post_detail_table点赞数量添加
            success = update().setSql("post_like_count = post_like_count + 1").eq("post_detail_id", postDetailId).update();
            if (success) {
                //点赞维护表数据新增
                PostLikeTable postLikeTable = new PostLikeTable();
//                postLikeTable.setLikeId(setUUId());
                postLikeTable.setLikePostId(postDetailId);
                postLikeTable.setLikeUserId(userId);
                postLikeMapper.insert(postLikeTable);

                //redis添加
                stringRedisTemplate.opsForSet().add(key, userId);
            }


        }

        //存在，进行删除-实现用户取消点赞
        if (BooleanUtil.isTrue(isMember)){
            //数据库点赞数量添加
            success = update().setSql("post_like_count = post_like_count - 1").eq("post_detail_id", postDetailId).update();
            if (success) {
                //点赞维护表数据删除
                LambdaQueryWrapper<PostLikeTable> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PostLikeTable::getLikePostId,postDetailId).eq(PostLikeTable::getLikeUserId,userId);
                Integer likeId = postLikeMapper.selectOne(queryWrapper).getLikeId();
                postLikeMapper.deleteById(likeId);
                //redis添加
                stringRedisTemplate.opsForSet().remove(key, userId);
            }
            return R.success("取消点赞成功");

        }
        return R.success("点赞成功");
    }

    @Override
    @Transactional
    public R<String> activeFavorite(String postDetailId) {
        //获取当前用户
        String userId = BaseContext.getUser().getUserId();
        boolean success = false;
        //点赞
        //通过id获取当前数据
        String key = ACTIVE_FAVORITE + postDetailId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId);
        //在redis中进行判断
        //不存在，进行添加-实现用户已收藏
        if (BooleanUtil.isFalse(isMember)){
            //post_detail_table收藏数量添加
            success = update().setSql("post_favorite_count = post_favorite_count + 1").eq("post_detail_id", postDetailId).update();
            if (success) {
                //收藏维护表数据新增
                PostFavoriteTable postFavoriteTable = new PostFavoriteTable();
//                postLikeTable.setLikeId(setUUId());
                postFavoriteTable.setFavoritePostId(postDetailId);
                postFavoriteTable.setFavoriteUserId(userId);
                postFavoriteMapper.insert(postFavoriteTable);

                //redis添加
                stringRedisTemplate.opsForSet().add(key, userId);
            }


        }

        //存在，进行删除-实现用户取消收藏
        if (BooleanUtil.isTrue(isMember)){
            //数据库点赞数量添加
            success = update().setSql("post_favorite_count = post_favorite_count - 1").eq("post_detail_id", postDetailId).update();
            if (success) {
                //点赞维护表数据删除
                LambdaQueryWrapper<PostFavoriteTable> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PostFavoriteTable::getFavoritePostId,postDetailId).eq(PostFavoriteTable::getFavoriteUserId,userId);
                Integer likeId = postFavoriteMapper.selectOne(queryWrapper).getFavoriteId();
                postFavoriteMapper.deleteById(likeId);
                //redis删除
                stringRedisTemplate.opsForSet().remove(key, userId);
            }
            return R.success("取消收藏成功");

        }
        return R.success("收藏成功");
    }

    @Override
    @Transactional
    public R<String> activeShare(String postDetailId) {
        //获取当前用户
        String userId = BaseContext.getUser().getUserId();
        boolean success = false;
        //点赞
        //通过id获取当前数据
        String key = ACTIVE_SHARE + postDetailId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId);
        //在redis中进行判断
        //不存在，进行添加-实现用户已分享
        if (BooleanUtil.isFalse(isMember)){
            //post_detail_table收藏数量添加
            success = update().setSql("post_share_count = post_share_count + 1").eq("post_detail_id", postDetailId).update();
            if (success) {
                //收藏维护表数据新增
                PostShareTable postShareTable = new PostShareTable();
//                postLikeTable.setLikeId(setUUId());
                postShareTable.setSharePostId(postDetailId);
                postShareTable.setShareUserId(userId);
                postShareMapper.insert(postShareTable);

                //redis添加
                stringRedisTemplate.opsForSet().add(key, userId);
            }

        }

        //存在，进行删除-实现用户取消分享 可不实现
       /* if (BooleanUtil.isTrue(isMember)){
            //数据库点赞数量添加
            success = update().setSql("post_share_count = post_share_count - 1").eq("post_detail_id", postDetailId).update();
            if (success) {
                //点赞维护表数据删除
                LambdaQueryWrapper<PostShareTable> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PostShareTable::getSharePostId,postDetailId).eq(PostShareTable::getShareUserId,userId);
                Integer shareId = postShareMapper.selectOne(queryWrapper).getShareId();
                postFavoriteMapper.deleteById(shareId);
                //redis删除
                stringRedisTemplate.opsForSet().remove(key, userId);
            }

        }*/
        return R.success("分享成功");
    }

    @Override
    public HashMap<String, Object> getCommentList(PostCommentTable postComment) {
        HashMap<String, Object> map = new HashMap<>();
        //获取当前用户 存入 map- currentUser
        //依据post_comment_table中的user_id查询该用户基础信息并存入评论信息中 foreach
        //返回数据
        String userId = BaseContext.getUser().getUserId();
        UserTable user = userService.getById(userId);
        UserDTO currentUser = BeanUtil.copyProperties(user, UserDTO.class);
        map.put("currentUser",currentUser);

        //获取查看帖子数据
        LambdaQueryWrapper<PostCommentTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCommentTable::getCommentPostId,postComment.getCommentPostId())
                .eq(PostCommentTable::getCommentLevel,COMMON_NUMBER_ZERO)
                .orderByDesc(PostCommentTable::getCreateTime);
        List<PostCommentTable> commentlist = postCommentMapper.selectList(queryWrapper);

        map.put("commentList",getdata(commentlist));

        return map;
    }
    public List<PostCommentTable> getdata(List<PostCommentTable> list){
        for (PostCommentTable lists:list) {
            String userId1 = lists.getUserId();
            UserTable byId = userService.getById(userId1);
            lists.setUserDTO(BeanUtil.copyProperties(byId,UserDTO.class));
            LambdaQueryWrapper<PostCommentTable> children = new LambdaQueryWrapper<>();
            children.eq(PostCommentTable::getCommentLevel,lists.getPostCommentId());
            List<PostCommentTable> childrenlists = postCommentMapper.selectList(children);
            getdata(childrenlists);
            lists.setCommentChList(childrenlists);
        }
        return list;
    }

}
