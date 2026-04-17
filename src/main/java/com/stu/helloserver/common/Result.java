package com.stu.helloserver.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局统一接口返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // 成功响应-带数据
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    // 成功响应-自定义消息
    public static <T> Result<T> success(String msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, null);
    }

    // 失败响应-自定义状态码
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    // 失败响应-自定义消息
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}