package com.mc.userserver.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mc.common.utils.AllStringCtant;
import com.mc.userserver.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-03-31 21:16
 * @类说明：填写类说明
 * @修改记录：
 */
public class TokenRefreshInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    //构造注入 在配置类中引入 stringRedisTemplate
    public TokenRefreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请头求中的token
        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        //2.基于token获取redis中的用户
        //约定好 的有前缀的 bearer token
        String key = token.replaceFirst("bearer ","");

        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().
                entries(key);
        //3.判断用户是否存在
        if (userMap.isEmpty()){
            return true;
        }
        //5.将查询到的Hash数据转为UserDTO对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        //6.存在，保存用户信息到ThreadLocal
        BaseContext.saveUser(userDTO);
        //7.刷新token有效期
        stringRedisTemplate.expire(key,AllStringCtant.LOGIN_USER_TTL, TimeUnit.DAYS);
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除用户
        BaseContext.removeUser();
    }
}
