package com.mc.userserver.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.common.utils.PasswordEncoder;
import com.mc.common.utils.R;
import com.mc.common.utils.RegexUtils;
import com.mc.userserver.config.SysLog;
import com.mc.userserver.dto.UserDTO;
import com.mc.userserver.entity.UserAddressTable;
import com.mc.userserver.entity.UserProfileTable;
import com.mc.userserver.entity.UserTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.service.UserAddressService;
import com.mc.userserver.service.UserProService;
import com.mc.userserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mc.common.utils.AllStringCtant.USER_INFO_KEY;
import static com.mc.common.utils.AllStringCtant.USER_INFO_TTL;
import static com.mc.common.utils.UserCommon.setUUId;

/**
 * 用户信息
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-03 5:01
 * @类说明：填写类说明
 * @修改记录：
 */
@RestController
@RequestMapping("/info")
@Slf4j
public class InfoController {

    @Resource
    private UserProService userProService;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/getInfo")
    @ResponseBody
    public R<UserProfileTable> getInfo(){
        //获取用户的基础信息 昵称 头像 id
       UserDTO user = BaseContext.getUser();
        String userId = BaseContext.getUser().getUserId();

        //通过id获取用户资料 UserProfileTable
        LambdaQueryWrapper<UserProfileTable> proqueryWrapper = new LambdaQueryWrapper<>();
        proqueryWrapper.eq(UserProfileTable::getUserId,userId);

        UserProfileTable userProServiceOne = userProService.getOne(proqueryWrapper);
        userProServiceOne.setUserDTO(user);
        String userInfoKey = USER_INFO_KEY+userId;
        //将用户信息存入redis缓存中  用户数据信息较少可不存在redis 缓存中，此处只作为练习
        //如果真要做缓存 可以先判断缓存中是否已存在该用户的资料  命中则从缓存中获取数据 没有命中则进行查询并加入缓存
        //在用户资料进行修改时也需要对用户资料的缓存进行更新 实现双写一致
        String infoStr = JSONObject.toJSONString(userProServiceOne);
        stringRedisTemplate.opsForValue().set(userInfoKey,infoStr);
        stringRedisTemplate.expire(userInfoKey,USER_INFO_TTL, TimeUnit.DAYS);

        return R.success(userProServiceOne);

    }

    /**
     * 修改用户信息
     * 用户id暂时从线程中获取,
     * @param userProfileTable
     * @param request
     * @return
     */
    @SysLog(value = "#{'用户-操作-修改地址成功'}", level = "info", printResult = 0)
    @PostMapping("/alterInfo")
    @ResponseBody
    public R<String> alterInfo(@RequestBody UserProfileTable userProfileTable, HttpServletRequest request){

        return  userProService.AlterInfo(userProfileTable);

    }

    /**
     * 修改密码
     * @param data
     * @return
     */
    @SysLog(value = "#{'用户-操作-修改密码成功'}", level = "info", printResult = 0)
    @PutMapping("/alterPass")
    public R<String> alterPass(@RequestBody Map<Object,String>  data){
        //获取用户信息
        String userId = data.get("userId");
        String oldPassWord = data.get("OldPassWord");
        String newPassWord = data.get("NewPassWord");

        LambdaQueryWrapper<UserTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTable::getUserId,userId);

        UserTable user = userService.getOne(queryWrapper);
        //对比用户信息
        String oldenCode = PasswordEncoder.encode(oldPassWord);
        if (!(PasswordEncoder.matches(user.getPassword(),oldPassWord))){
            return R.error("原始密码错误,请重试");
        }
        //密码正则验证加密
        if(RegexUtils.isPwdInvalid(newPassWord)){
            String encode = PasswordEncoder.encode(newPassWord);
            LambdaUpdateWrapper<UserTable> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserTable::getPassword,PasswordEncoder.encode(newPassWord))
                    .in(UserTable::getUserId,userId);
            userService.update(updateWrapper);
        }else {
            return R.error("密码格式不符合规范，请重试");
        }


        //更新用户数据
//        userService.updateById(user);


        return R.success("修改成功");
    }

    /**
     *
     * @param userAddressTable
     * @return
     */
    @SysLog(value = "#{'用户:-操作-新增地址成功'}", level = "info", printResult = 0)
    @RequestMapping("/addAddress")
    public R<Object> addAddress(@RequestBody UserAddressTable userAddressTable){
        Map<String, String> dataMap = userAddressService.addAddress(userAddressTable);
        if (StringUtils.isEmpty(dataMap.get("addressId"))){
            return R.error("新增失败，请稍后重试");
        }
//        log.info("用户{}新增地址成功",userAddressTable.getAddressId());
        return R.success(dataMap);
    }

    /**
     * 修改地址信息
     * @param userAddressTable
     * @return
     */
    @SysLog(value = "#{'用户-操作-修改地址成功'}", level = "info", printResult = 0)
    @RequestMapping("/alterAddress")
    public R<String> alterAddress(@RequestBody UserAddressTable userAddressTable){
        Boolean upd = userAddressService.alterAddress(userAddressTable);
        if (!upd){
            return R.error("修改失败,请重试");
        }
        return R.success("修改成功");

    }



}
