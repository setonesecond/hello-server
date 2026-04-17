package com.stu.helloserver.vo;

import lombok.Data;

/**
 * 多表联查用户详情返回对象
 */
@Data
public class UserDetailVO {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String address;
}