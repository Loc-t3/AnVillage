package com.mc.userserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.common.utils.VillageOrAppellationEnum;
import com.mc.userserver.entity.PostDetailTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.mapper.PostDetailMapper;
import com.mc.userserver.service.PostDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.mc.common.utils.AllStringCtant.COMMON_NUMBER_ONE;
import static com.mc.common.utils.AllStringCtant.COMMON_NUMBER_ZERO;
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
}
