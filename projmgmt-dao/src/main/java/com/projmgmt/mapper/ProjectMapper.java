package com.projmgmt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projmgmt.domain.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目Mapper接口
 *
 * @author projmgmt
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

}

