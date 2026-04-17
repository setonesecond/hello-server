package com.stu.helloserver.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.User;
import com.stu.helloserver.entity.UserInfo;
import com.stu.helloserver.mapper.UserInfoMapper;
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.service.UserService;
import com.stu.helloserver.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 用户业务实现类，对接MyBatis-Plus真实数据库
 */
@Service
public class UserServiceImpl implements UserService {

    // ==================== 新增：Redis缓存Key前缀 ====================
    private static final String CACHE_KEY_PREFIX = "user:detail:";

    // 注入Mapper
    @Autowired
    private UserMapper userMapper;

    // ==================== 新增：注入UserInfoMapper和Redis模板 ====================
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 查询用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        // 用户名已存在，返回错误
        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 组装实体对象，插入数据库
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);

        return Result.success("注册成功!");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 校验密码是否正确
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        return Result.success("登录成功!");
    }

    @Override
    public Result<String> getUserById(Long id) {
        // 根据ID查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        return Result.success("查询成功，用户名为：" + user.getUsername());
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        // 构建分页参数
        Page<User> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        Page<User> resultPage = userMapper.selectPage(page, null);
        return Result.success(resultPage);
    }

    // ==================== 新增：查询用户详情（多表联查+Redis缓存） ====================
    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;

        // 1. 先查Redis缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isBlank()) {
            try {
                // 缓存命中，直接返回
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常，删除脏缓存，继续查数据库
                redisTemplate.delete(key);
            }
        }

        // 2. 缓存未命中，查数据库（多表联查）
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 回写Redis缓存，设置10分钟过期
        redisTemplate.opsForValue().set(
                key,
                JSONUtil.toJsonStr(detail),
                10,
                TimeUnit.MINUTES
        );

        return Result.success(detail);
    }

    // ==================== 新增：更新用户扩展信息 ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateUserInfo(UserInfo userInfo) {
        // 参数校验
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        // 更新user_info表
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId, userInfo.getUserId());
        int update = userInfoMapper.update(userInfo, wrapper);

        if (update > 0) {
            // 更新成功，删除对应的Redis缓存
            redisTemplate.delete(CACHE_KEY_PREFIX + userInfo.getUserId());
            return Result.success("更新成功");
        }

        return Result.error(ResultCode.USER_NOT_EXIST);
    }

    // ==================== 新增：删除用户 ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteUser(Long userId) {
        // 1. 删除用户扩展信息
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId, userId);
        userInfoMapper.delete(wrapper);

        // 2. 删除用户核心信息
        int delete = userMapper.deleteById(userId);

        if (delete > 0) {
            // 删除成功，清除缓存
            redisTemplate.delete(CACHE_KEY_PREFIX + userId);
            return Result.success("删除成功");
        }

        return Result.error(ResultCode.USER_NOT_EXIST);
    }
}