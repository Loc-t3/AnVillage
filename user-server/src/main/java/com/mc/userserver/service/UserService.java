package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.common.utils.R;
import com.mc.userserver.dto.loginDTO;
import com.mc.userserver.entity.UserTable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-15 18:50
 * @类说明：填写类说明
 * @修改记录：
 */
public interface UserService extends IService<UserTable> {
    /**
     * 通过注册方式判断该注册账号是否已被注册
     * @param type
     * @param user
     * @return
     */
    Boolean CheckPhoneIsOrNot(String type,UserTable user);

    /**
     * 用户登录
     * @param user
     * @return
     */

    R<String> login(UserTable user, HttpServletRequest request);


    /**
     * 发送手机验证码
     * @param phone
     * @param session
     * @return
     */
    R<String> sendCode(String phone, HttpSession session);
}
