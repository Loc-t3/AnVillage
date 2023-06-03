package com.mc.userserver.filter;

import cn.hutool.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-03-31 21:16
 * @类说明：填写类说明
 * @修改记录：
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否需要进行拦截（ThreadLocal是否有用户）
        if (BaseContext.getUser() == null){
            //没有 需要拦截设置状态码
            /*HashMap<Object, Object> map = new HashMap<>();
            map.put("code", HttpStatus.UNAUTHORIZED);
            map.put("msg","未授权，请检查后再次尝试");*/
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            JSONObject res = new JSONObject();
            res.put("status",HttpStatus.UNAUTHORIZED);
            res.put("msg","未授权，请检查后再次尝试");
            PrintWriter out = null ;
            out = response.getWriter();
            out.write(res.toString());
            out.flush();
            out.close();

            response.setStatus(401);
            //拦截
            return false;
        }
        //用户存在 放行
        return true;
    }


}
