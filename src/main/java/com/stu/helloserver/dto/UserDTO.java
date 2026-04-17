package com.stu.helloserver.dto;

import lombok.Data;

/**
 * 用户接口入参DTO
 */
@Data
public class UserDTO {
    private String username;
    private String password;
}