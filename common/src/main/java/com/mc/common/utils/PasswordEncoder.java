package com.mc.common.utils;


import cn.hutool.core.util.RandomUtil;
import org.springframework.util.DigestUtils;

import javax.lang.model.element.VariableElement;
import java.nio.charset.StandardCharsets;

public class PasswordEncoder {

    public static String encode(String password) {
        // 生成盐
        String salt = RandomUtil.randomString(20);
        // 加密
        return encode(password,salt);
    }
    private static String encode(String password, String salt) {
        // 加密
        return salt + "@" + DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
    }
    public static Boolean matches(String encodedPassword, String rawPassword) {
        if (encodedPassword == null || rawPassword == null) {
            return false;
        }
        if(!encodedPassword.contains("@")){
            throw new RuntimeException("密码格式不正确！");
        }
        String[] arr = encodedPassword.split("@");
        // 获取盐
        String salt = arr[0];
        // 比较
        return encodedPassword.equals(encode(rawPassword, salt));
    }


    public static void main(String[] args) {
        String encode = encode("123456a");
        System.out.println(encode);
        String rawpwd = "123456a";

        Boolean matches = matches(encode, rawpwd);
        if (matches){
            System.out.println("密码正确");
        }else {
            System.out.println("错误");
        }
    }
}
