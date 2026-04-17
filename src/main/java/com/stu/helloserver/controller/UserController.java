package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.UserInfo;
import com.stu.helloserver.service.UserService;
import com.stu.helloserver.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 用户注册接口 POST http://localhost:8080/api/users
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 用户登录接口 POST http://localhost:8080/api/users/login
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // 根据ID查询用户接口 GET http://localhost:8080/api/users/{id}
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // 分页查询用户列表
    @GetMapping("/list/paged")
    public Result<Object> getUserPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }

    // ==================== 新增：查询用户详情（多表联查+Redis缓存） ====================
    @GetMapping("/{id}/detail")
    public Result<UserDetailVO> getUserDetail(@PathVariable("id") Long userId) {
        return userService.getUserDetail(userId);
    }

    // ==================== 新增：更新用户扩展信息 ====================
    @PutMapping("/{id}/detail")
    public Result<String> updateUserInfo(
            @PathVariable("id") Long userId,
            @RequestBody UserInfo userInfo
    ) {
        userInfo.setUserId(userId);
        return userService.updateUserInfo(userInfo);
    }

    // ==================== 新增：删除用户 ====================
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long userId) {
        return userService.deleteUser(userId);
    }
}