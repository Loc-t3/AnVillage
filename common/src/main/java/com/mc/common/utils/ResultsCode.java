package com.mc.common.utils;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-03 11:09
 * @类说明：填写类说明
 * @修改记录：
 */
public enum ResultsCode {


        SUCCESS(200, "成功"),
        FAIL(-1, "失败"),
        ERROR(500, "服务器异常"),
        UNAUTHORIZED(401, "未认证（签名错误）"),
        FORBIDDEN(403, "禁止访问"),
        NOT_FOUND(404, "接口不存在"),
        AUTH_ERROR(-10000, "鉴权登陆失败，请重新登录！");

    public int code;
    public String message;

    ResultsCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }



}
