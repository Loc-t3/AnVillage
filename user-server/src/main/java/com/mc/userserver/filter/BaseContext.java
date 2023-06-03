package com.mc.userserver.filter;


import com.mc.userserver.dto.UserDTO;

/**
 * 基于ThreadLocal 封装工具类 用户保存和获取当前登录用户id
 * @author XJM
 * @date 2022/10/24 6:53
 */
public class BaseContext {
    private static ThreadLocal<UserDTO> threadLocal = new ThreadLocal<>();
    //设置值
    /*public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }*/
    public static void saveUser(UserDTO user){
        threadLocal.set(user);
    }

    public static UserDTO getUser(){
        return threadLocal.get();
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}
