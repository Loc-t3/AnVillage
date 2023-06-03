package com.mc.userserver.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.common.utils.R;
import com.mc.userserver.dto.UserDTO;
import com.mc.userserver.entity.UserActiveTable;
import com.mc.userserver.entity.UserFansTable;
import com.mc.userserver.entity.UserProfileTable;
import com.mc.userserver.entity.UserTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.service.UserActiveService;
import com.mc.userserver.service.UserFansService;
import com.mc.userserver.service.UserProService;
import com.mc.userserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户关注相关
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-04 7:25
 * @类说明：填写类说明
 * @修改记录：
 */
@RestController
@RequestMapping("/active")
public class UserActiveController {
    @Autowired
    private UserActiveService userActiveService;
    @Autowired
    private UserFansService userFansService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProService userProService;

    /*@GetMapping("/getuserinfo/{userid}")
    public R<UserProfileTable> getUserPro(){



    }*/

    /**
     *关注用户
     * @param activedid  被关注者id
     * @param request 从token中获取当前登录用户id
     * @return
     */
    @PostMapping("/activeUser/{activedid}")
    public R<String> avtiveUser(@PathVariable String activedid, HttpServletRequest request){
        Boolean aBoolean = userActiveService.toActiveUser(activedid, request);

        if (!aBoolean){
            return R.error("关注失败，请稍后重试");
        }


        return R.success("关注成功");

    }





    /**
     *
     *
     * 实现取消关注功能
     * @param activedid 当前用户粉丝id
     * @param request  获取当前用户id
     * @return
     */
    @PostMapping("/delActiveUser/{activedid}")
    public R<String> delActiveUser(@PathVariable String activedid, HttpServletRequest request){
        Boolean aBoolean = userActiveService.delActiveUser(activedid, request);

        if (!aBoolean){
            return R.error("取关失败，请稍后重试");
        }


        return R.success("取关成功");
    }

    /**
     * 获取用户关注列表
     * @param userId 获取个人用户id
     * @return
     */
    @GetMapping("/getActiveList/{userId}")
    @ResponseBody
    public R<List<UserDTO>> getActiveList(@PathVariable String userId){
        //获取个人用户id
//        String userId = BaseContext.getUser().getUserId();
        //在关注表中进行查询 返回List
        LambdaQueryWrapper<UserActiveTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActiveTable::getUserId,userId);
        List<UserActiveTable> activeList = userActiveService.list(queryWrapper);
        //利用activeList中的useractiveid在用户表中查询
        //接收查询到的关注列表的用户
        List<UserDTO> userDTOS = new ArrayList<>();
        //index自增
       /* AtomicInteger index=new AtomicInteger(0);
        userDTOS.stream().map((item) -> {
            item.setUserId(activeList.get(index.getAndIncrement()).getUserActiveId());
            return item;
        }).collect(Collectors.toList());*/
        UserTable user = null;

        for (UserActiveTable userdtos:activeList) {
            LambdaQueryWrapper<UserTable> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(UserTable::getUserId,userdtos.getUserActiveId());
            user = userService.getOne(queryWrapper1);
            //将UserTable中的数据拷贝至UserDTO中（相同字段）
            UserDTO users = BeanUtil.copyProperties(user, UserDTO.class);
            userDTOS.add(users);

        }


        return R.success(userDTOS);
    }

    /**
     *  获取用户粉丝列表
     * @param userId 获取个人用户id
     * @return
     */
    @GetMapping("/getFansList/{userId}")
    @ResponseBody
    private  R<List<UserDTO>> getFansList(@PathVariable String userId){
        //获取个人用户id
//        String userId = BaseContext.getUser().getUserId();
        //在关注表中进行查询 返回List
        LambdaQueryWrapper<UserFansTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFansTable::getUserId,userId);
        List<UserFansTable> activeList = userFansService.list(queryWrapper);
        //利用activeList中的useractiveid在用户表中查询
        //接收查询到的关注列表的用户
        List<UserDTO> userDTOS = new ArrayList<>();

        UserTable user = null;

        for (UserFansTable userdtos:activeList) {
            //一个条件构造器占用一个内存 放在foreach外边则只能执行一个 我们需要放在循环里面已达到目的
            LambdaQueryWrapper<UserTable> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(UserTable::getUserId,userdtos.getUserFansId());
            user = userService.getOne(queryWrapper1);
            //将UserTable中的数据拷贝至UserDTO中（相同字段）
            UserDTO users = BeanUtil.copyProperties(user, UserDTO.class);
            userDTOS.add(users);

        }

        return R.success(userDTOS);

    }


}
