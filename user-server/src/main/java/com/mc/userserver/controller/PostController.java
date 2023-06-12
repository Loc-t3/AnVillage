package com.mc.userserver.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.mc.common.utils.R;
import com.mc.common.utils.VillageOrAppellationEnum;
import com.mc.userserver.config.SysLog;
import com.mc.userserver.entity.PostDetailTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.service.PostDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

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

    /**
     * * 获取用户发布定位选择
     *      * 获取用户发布帖子信息 title img body label
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


}
