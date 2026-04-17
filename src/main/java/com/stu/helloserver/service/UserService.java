package com.stu.helloserver.service;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;

/**
 * 用户业务接口
 */
public interface UserService {
    // 用户注册
    Result<String> register(UserDTO userDTO);
    // 用户登录
    Result<String> login(UserDTO userDTO);
    // 根据ID查询用户
    Result<String> getUserById(Long id);
}