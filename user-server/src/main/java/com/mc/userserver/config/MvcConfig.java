package com.mc.userserver.config;

import com.mc.userserver.filter.LoginInterceptor;
import com.mc.userserver.filter.TokenRefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-03-31 21:22
 * @类说明：填写类说明
 * @修改记录：
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                //设置放行路径
                .excludePathPatterns(
                        "/user/code",
                        "/user/login/{type}",
                        "/user/register/{type}"

                ).order(1);
        registry.addInterceptor(new TokenRefreshInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }
}
