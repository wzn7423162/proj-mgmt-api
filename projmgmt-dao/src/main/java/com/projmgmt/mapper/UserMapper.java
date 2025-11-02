package com.projmgmt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projmgmt.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author projmgmt
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

