package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.userserver.entity.UserActiveTable;

import javax.servlet.http.HttpServletRequest;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-04 7:40
 * @类说明：填写类说明
 * @修改记录：
 */
public interface UserActiveService extends IService<UserActiveTable> {
    /**
     * 实现关注用户
     * @param activedid
     * @param request
     * @return
     */
    Boolean toActiveUser(String activedid, HttpServletRequest request);

    /**
     * 实现用户取关
     * @param activedid 当前用户粉丝id
     * @param request  获取当前用户id
     * @return
     */
    Boolean delActiveUser(String activedid, HttpServletRequest request);
}
