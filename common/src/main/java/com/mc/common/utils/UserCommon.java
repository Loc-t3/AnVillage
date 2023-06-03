package com.mc.common.utils;

import cn.hutool.Hutool;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import org.springframework.util.StringUtils;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-15 19:18
 * @类说明：填写类说明
 * @修改记录：
 */
public class UserCommon {

    /**
     * 生成UUID并除去 -
     * @return
     */
    public static String setUUId(){
        UUID uuid = UUID.randomUUID();
        String ID = uuid.toString().replace("-","");
        return ID;
    }

    /**
     * 随机初始化用户名
     * @return
     */
    public static String setNickName(){
        String nickname = RandomUtil.randomString(9);
        return nickname;
    }

    /*public static void main(String[] args) {
        String s = setUUId();

        System.out.println(s);

        String s1 = setNickName();
        System.out.println(s1);
    }*/
}
