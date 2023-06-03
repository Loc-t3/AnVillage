package com.mc.userserver.config;

import java.lang.annotation.*;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-03 9:41
 * @类说明：填写类说明
 * @修改记录：
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 日志描述
     *
     * @return 返回日志描述信息
     */
    String value();

    /**
     * 日志等级（info、debug、trace、warn、error）
     *
     * @return 返回日志等级
     */
    String level() default "info";

    /**
     * 打印方法返回结果
     *
     * @return 返回打印方法返回结果
     */
    boolean printResult() default false;
}

