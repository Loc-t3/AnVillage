package com.mc.userserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.common.utils.*;
import com.mc.userserver.config.SysLog;
import com.mc.userserver.entity.UserProfileTable;
import com.mc.userserver.entity.UserTable;
import com.mc.userserver.dto.loginDTO;
import com.mc.userserver.service.UserProService;
import com.mc.userserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;

import java.util.List;

import static com.mc.common.utils.AllStringCtant.*;
import static com.mc.common.utils.PasswordEncoder.matches;
import static com.mc.common.utils.UserCommon.setNickName;
import static com.mc.common.utils.UserCommon.setUUId;
import static com.mc.common.utils.VillageOrAppellationContants.APPELATION_DESC_1;
import static com.mc.common.utils.VillageOrAppellationContants.APPELATION_NAME_1;

/**
 * 用户登录相关
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-15 16:43
 * @类说明：填写类说明
 * @修改记录：
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProService userProService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户注册
     * @param request
     * @param user
     * @param type
     * @return
     */
    @SysLog(value = "#{'用户-操作-注册账号'}", level = "info", printResult = 0)
    @PostMapping("/register/{type}")
    public R<String> userRegister(HttpServletRequest request,@RequestBody UserTable user,@PathVariable String type) {
        String requestURI = request.getRequestURI();
        String requestURL = request.getRemoteAddr();


        //密码正则验证
        if(RegexUtils.isPwdInvalid(user.getPassword())){
            String password = user.getPassword();
            String encode = PasswordEncoder.encode(password);
            user.setPassword(encode);
        } //邮箱注册则对邮箱进行正则验证
        else if (type.equals(COMMON_NUMBER_ONE)&&!RegexUtils.isEmailInvalid(user.getEmail())){

            user.setEmail(user.getEmail());
        }  //手机注册则对手机号进行正则验证
        else if (type.equals(COMMON_NUMBER_ZERO)&&!RegexUtils.isPhoneInvalid(user.getPhone())){
            user.setPhone(user.getPhone());
        }else{
            return R.error("手机号格式错误！");
        }


        user.setUserId(setUUId());
        user.setNickname(setNickName());
        user.setRegisterType(type);
        user.setAvatar("1");
        user.setUserIntroduction(AllStringCtant.DEFAULT_USER_INTRODUCTION);

        //对用户资料表创建相关信息
        UserProfileTable userProfileTable = new UserProfileTable();
        userProfileTable.setProfileId(setUUId());
        userProfileTable.setUserId(user.getUserId());
        userProfileTable.setBirthday(DEFAULT_BIRTHDAY);
        userProfileTable.setUserAppellation(APPELATION_NAME_1);
        userProfileTable.setUserAppellationDesc(APPELATION_DESC_1);
        userProfileTable.setSchool(DEFAULT_SCHOOL);


        //通过注册方式判断是否已存在注册用户
        if (type.equals(COMMON_NUMBER_ONE)||type.equals(COMMON_NUMBER_ZERO))
        {
            if (!userService.CheckPhoneIsOrNot(type,user)){
                userService.save(user);
                userProService.save(userProfileTable);
            }
            else{
                return R.error("该账号已被注册,请重试!");

            }
        }

        log.info("ip:{},访问:{},实现用户注册",requestURL,requestURI);


        return R.success("用户注册成功");
    }

    /**
     * 用户登录
     * @param login
     * @param type
     * @param session
     * @param request
     * @return
     */
    @SysLog(value = "#{'用户-操作-登录账号'}", level = "info", printResult = 0)
    @PostMapping("/login/{type}")
    public R<String> userLogin(@RequestBody loginDTO login, @PathVariable String type, HttpSession session, HttpServletRequest request){

        //1.调用service对用户进行判断是否存在,存在则登录成功
        //type-判断是密码登录还是手机验证登录 0 - 密码登录，1-手机登录
        UserTable user = null;
        LambdaQueryWrapper<UserTable> queryWrapper = new LambdaQueryWrapper<>();
        //对密码进行加密
        String encodePwd = PasswordEncoder.encode(login.getPassword());
        //判断是密码登录还是短信验证登录 0 - 密码登录，  //密码验证正确则对账号进行核对
        if (type.equals(COMMON_NUMBER_ZERO)&&matches(encodePwd, login.getPassword())){

            //1.对帐号进行正则判断 判断账号形式为手机号还是邮箱
            if (!RegexUtils.isEmailInvalid(login.getAcount())){
                queryWrapper.eq(UserTable::getEmail, login.getAcount());
                user = userService.getOne(queryWrapper);
            }else{
                queryWrapper.eq(UserTable::getPhone, login.getAcount());
                user = userService.getOne(queryWrapper);
            }
        }

        //1-短信验证登录 账号必然为手机号
        if (type.equals(COMMON_NUMBER_ONE)&&matches(encodePwd,login.getPassword())){
            //判断验证码 从reids中获取
            String CasheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + login.getAcount());
            String code = login.getCode();
            if (CasheCode == null || !CasheCode.equals(code)){
                //不一致 报错
                return R.error("验证码错误");
            }
            queryWrapper.eq(UserTable::getPhone, login.getAcount());
            user = userService.getOne(queryWrapper);
        }

        //判断用户是否存在
        if (user==null){
            return R.error("该用户账号不存在！");
        }

        return userService.login(user,request);


    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @SysLog(value = "#{'用户-操作-退出账号'}", level = "info", printResult = 0)
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        String authorization = request.getHeader("Authorization");

                //约定好 的有前缀的 bearer token
                String realToken = authorization.replaceFirst("bearer ","");
                //判断从前端获取的token不为空且存在
                if (!StringUtils.isEmpty(realToken)&&stringRedisTemplate.hasKey(realToken)){
                    //删除token 实现登出
                    stringRedisTemplate.delete(realToken);
                    }

        return R.success("退出成功");
    }


    /**
     * 发送手机验证码
     * @param phone
     * @param session
     * @return
     */
    @SysLog(value = "#{'用户-操作-发生验证码'}", level = "info", printResult = 0)
    @PostMapping("/code")
    public R<String> sendCode(@RequestParam("phone") String phone, HttpSession session) {
        //  发送短信验证码并保存验证码
        return userService.sendCode(phone,session);
    }





    @PostMapping("/getHeader")
    public void getHeader(HttpServletRequest request){
        //检查
        HttpHeaders headers = new HttpHeaders();
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(authorization)){
            if (StringUtils.hasText(authorization)){
                //约定好 的有前缀的 bearer token
                String realToken = authorization.replaceFirst("bearer ","");
                //判断从前端获取的token不为空且存在
                if (!StringUtils.isEmpty(realToken)&&stringRedisTemplate.hasKey(realToken)){
                    //继续向下执行
                    System.out.println("存在");
                }
            }

        }


    }



}
