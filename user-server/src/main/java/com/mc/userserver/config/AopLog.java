package com.mc.userserver.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-02 13:28
 * @类说明：填写类说明
 * @修改记录：
 */

@Slf4j
public class AopLog {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    ThreadLocal<Long> startTime = new ThreadLocal<>();
    //定义切点
    @Pointcut(value = "execution(* com.mc.userserver.controller.*.*(..))")
    public void aopWebLog() {
    }

    @Pointcut(value = "execution(* alter*(..))")
    public void aopAlter() {
    }


    //使用环绕通知
  /*  @Around("aopWebLog()")
    public Object myLogger(ProceedingJoinPoint pjp) throws Throwable {
        startTime.set(System.currentTimeMillis());
        //使用ServletRequestAttributes请求上下文获取方法更多
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        //使用数组来获取参数
        Object[] array = pjp.getArgs();
        ObjectMapper mapper = new ObjectMapper();
        //执行函数前打印日志
        logger.info("调用前：{}：{},传递的参数为：{}", className, methodName, mapper.writeValueAsString(array));
        logger.info("URL:{}", request.getRequestURL().toString());
        logger.info("IP地址：{}", request.getRemoteAddr());
        //调用整个目标函数执行
        Object obj = pjp.proceed();
        //执行函数后打印日志
        logger.info("调用后：{}：{},返回值为：{}", className, methodName, mapper.writeValueAsString(obj));
        logger.info("耗时：{}ms", System.currentTimeMillis() - startTime.get());
        return obj;
    }*/

    @Before("aopAlter()")
    public void afterAlter(JoinPoint joinPoint) throws Throwable{
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("<=====================================================");
        logger.info("请求来源： =》" + request.getRemoteAddr());
        logger.info("请求URL：" + request.getRequestURL().toString());
        logger.info("请求方式：" + request.getMethod());
        logger.info("响应方法：" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("请求参数：" + Arrays.toString(joinPoint.getArgs()));
        logger.info("------------------------------------------------------");
        startTime.set(System.currentTimeMillis());


    }
    @After("within(com.mc.userserver.controller.*)")
    public void after(){
        System.out.println("方法之后执行...after.");
    }
    @AfterReturning(pointcut="execution(* alter*(..))",returning = "methodResult")
    public void afterRunning(JoinPoint joinPoint, Object methodResult){
        if(startTime.get() == null){
            startTime.set(System.currentTimeMillis());
        }
        System.out.println("方法执行完执行...afterRunning");
        //获取方法返回值
        String returnJson = JSONObject.toJSONString(methodResult);
        Object[] args = joinPoint.getArgs();
        logger.info("耗时（毫秒）：" +  (System.currentTimeMillis() - startTime.get()));
        logger.info("返回数据：{}", methodResult);
        logger.info("==========================================>");
    }


}
