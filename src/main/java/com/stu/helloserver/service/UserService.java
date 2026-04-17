package com.stu.helloserver.service;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.UserInfo;
import com.stu.helloserver.vo.UserDetailVO;

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

    // 获取分页用户列表
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);

    /**
     * 查询用户详情（多表联查+Redis缓存）
     */
    Result<UserDetailVO> getUserDetail(Long userId);

    /**
     * 更新用户扩展信息
     */
    Result<String> updateUserInfo(UserInfo userInfo);

    /**
     * 删除用户
     */
    Result<String> deleteUser(Long userId);
}
