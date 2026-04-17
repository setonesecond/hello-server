package com.stu.helloserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.helloserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper，继承MyBatis-Plus的BaseMapper，自带单表CRUD方法
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}