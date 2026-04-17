package com.stu.helloserver.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    USER_HAS_EXISTED(4001, "用户名已存在"),
    USER_NOT_EXIST(4002, "用户不存在"),
    PASSWORD_ERROR(4003, "密码错误"),
    PARAM_ERROR(400, "参数错误"),
    SYSTEM_ERROR(500, "系统异常");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}