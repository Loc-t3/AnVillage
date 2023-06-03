package com.mc.userserver.dto;

import com.mc.userserver.entity.UserProfileTable;
import lombok.Data;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-16 1:13
 * @类说明：填写类说明
 * @修改记录：
 */
@Data
public class UserDTO  {
    private String userId;
    private String nickname;
    private String avatar;
}
