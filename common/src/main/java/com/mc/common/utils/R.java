package com.mc.common.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/***
 * 通用返回对象，在前端返回的数据都会封装成该对象
 * 设置泛型的原因是增强通用性
 * @param <T>
 */

public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败



    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    /****
     * 成功响应Msg中的信息
     **/
    private final static String SUCCESS = "success";

    /****
     * 失败响应Msg中的信息
     **/
    private final static String FAILED = "failed";


    public void Result() {
        this.code = ResultsCode.SUCCESS.code;
        this.msg = ResultsCode.SUCCESS.message;
    }

    public R<T> setCode(ResultsCode resultsCode) {
        this.code = resultsCode.code;
        return this;
    }
    public int getCode() {
        return code;
    }

    public R<T> setCode(int code) {
        this.code = code;
        return this;
    }


        public String getMsg() {
        return msg;
    }

    public R<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", map=" + map +
                '}';
    }

    public static <T> R<T> success(T object) {

        return new R<T>().setCode(ResultsCode.SUCCESS).setMsg(SUCCESS).setData(object);
    }
    public static  <T> R<T> success(String message) {

        return new R<T>().setCode(ResultsCode.SUCCESS).setMsg(message);
    }

    public static  <T> R<T> error(String message) {

        return new R<T>().setCode(ResultsCode.FAIL).setMsg(message);
    }




}
