package com.projmgmt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projmgmt.domain.Project;
import com.projmgmt.mapper.ProjectMapper;
import com.projmgmt.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 项目Service实现
 *
 * @author projmgmt
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public List<Project> selectProjectList(Project project, String orderByColumn, String isAsc) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        // 物理删除模式下，不需要过滤 delFlag
        if (StringUtils.hasText(project.getProjectName())) {
            queryWrapper.like(Project::getProjectName, project.getProjectName());
        }
        if (project.getUserId() != null) {
            queryWrapper.eq(Project::getUserId, project.getUserId());
        }

        // 处理排序
        boolean asc = "asc".equalsIgnoreCase(isAsc);
        if (StringUtils.hasText(orderByColumn)) {
            if ("createTime".equals(orderByColumn)) {
                queryWrapper.orderBy(true, asc, Project::getCreateTime);
            } else if ("updateTime".equals(orderByColumn)) {
                queryWrapper.orderBy(true, asc, Project::getUpdateTime);
            } else {
                // 默认按创建时间倒序
                queryWrapper.orderByDesc(Project::getCreateTime);
            }
        } else {
            // 没有指定排序字段时，默认按创建时间倒序
            queryWrapper.orderByDesc(Project::getCreateTime);
        }

        return this.list(queryWrapper);
    }

    @Override
    public boolean insertProject(Project project) {
        project.setMachineCount(0);
        // 物理删除模式下，不需要设置 delFlag
        return this.save(project);
    }

    @Override
    public boolean updateProject(Project project) {
        return this.updateById(project);
    }

    @Override
    public boolean deleteProjectById(Long id) {
        // 物理删除：直接从数据库删除记录
        return this.removeById(id);
    }
}

