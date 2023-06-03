package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.common.utils.R;
import com.mc.userserver.entity.UserProfileTable;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-03 5:16
 * @类说明：填写类说明
 * @修改记录：
 */
public interface UserProService extends IService<UserProfileTable> {
    //修改用户信息
    R<String> AlterInfo(UserProfileTable userProfileTable);
}
