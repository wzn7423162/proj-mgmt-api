package com.projmgmt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.projmgmt.domain.Project;

import java.util.List;

/**
 * 项目Service接口
 *
 * @author projmgmt
 */
public interface ProjectService extends IService<Project> {

    /**
     * 查询项目列表
     *
     * @param project 项目信息
     * @param orderByColumn 排序字段
     * @param isAsc 排序方向
     * @return 项目集合
     */
    List<Project> selectProjectList(Project project, String orderByColumn, String isAsc);

    /**
     * 新增项目
     *
     * @param project 项目信息
     * @return 结果
     */
    boolean insertProject(Project project);

    /**
     * 修改项目
     *
     * @param project 项目信息
     * @return 结果
     */
    boolean updateProject(Project project);

    /**
     * 删除项目
     *
     * @param id 项目ID
     * @return 结果
     */
    boolean deleteProjectById(Long id);
}

