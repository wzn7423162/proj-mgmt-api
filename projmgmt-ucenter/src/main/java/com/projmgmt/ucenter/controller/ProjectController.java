package com.projmgmt.ucenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.projmgmt.ucenter.core.AjaxResult;
import com.projmgmt.ucenter.domain.Project;
import com.projmgmt.ucenter.core.JwtUtil;
import com.projmgmt.ucenter.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/front/project")
public class ProjectController {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public AjaxResult list(@RequestParam int pageNum,
                           @RequestParam int pageSize,
                           @RequestParam(required = false) String projectName,
                           @RequestParam(required = false) String orderByColumn,
                           @RequestParam(required = false) String isAsc) {
        // 兜底保护
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(pageSize, 1);

        // 先统计总数（不带排序）
        LambdaQueryWrapper<Project> countQw = new LambdaQueryWrapper<>();
        if (projectName != null && !projectName.isEmpty()) {
            countQw.like(Project::getProjectName, projectName);
        }
        long total = projectMapper.selectCount(countQw);

        // 查询当前页数据（带排序 + limit）
        LambdaQueryWrapper<Project> listQw = new LambdaQueryWrapper<>();
        if (projectName != null && !projectName.isEmpty()) {
            listQw.like(Project::getProjectName, projectName);
        }
        boolean asc = "asc".equalsIgnoreCase(isAsc);
        if ("createTime".equals(orderByColumn)) {
            listQw.orderBy(true, asc, Project::getCreateTime);
        } else if ("updateTime".equals(orderByColumn)) {
            listQw.orderBy(true, asc, Project::getUpdateTime);
        } else {
            // 默认按创建时间倒序
            listQw.orderByDesc(Project::getCreateTime);
        }
        int offset = (pageNum - 1) * pageSize;
        listQw.last("LIMIT " + offset + ", " + pageSize);

        var list = projectMapper.selectList(listQw);

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return AjaxResult.success(data);
    }

    @PostMapping("/create")
    public AjaxResult create(@RequestBody Project body, HttpServletRequest request) {
        String frontToken = request.getHeader("FrontToken");
        if (frontToken != null && frontToken.startsWith("Bearer ")) {
            String token = frontToken.substring(7);
            var claims = jwtUtil.parseToken(token);
            Object uid = claims.get("userId");
            if (uid != null) {
                body.setUserId(String.valueOf(uid));
            }
        }

        if (body.getUserId() == null || body.getUserId().isEmpty()) {
            return AjaxResult.error("缺少用户标识");
        }
        if (body.getProjectName() == null || body.getProjectName().isEmpty()) {
            return AjaxResult.error("项目名称不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        body.setCreateTime(now);
        body.setUpdateTime(now);
        if (body.getMachineCount() == null) {
            body.setMachineCount(0);
        }
        projectMapper.insert(body);
        return AjaxResult.success(body);
    }

    @GetMapping("/detail")
    public AjaxResult detail(@RequestParam String id) {
        Project p = projectMapper.selectById(id);
        if (p == null) {
            return AjaxResult.error("未找到项目");
        }
        return AjaxResult.success(p);
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody Project body) {
        if (body.getId() == null) {
            return AjaxResult.error("缺少项目ID");
        }
        body.setUpdateTime(LocalDateTime.now());
        int c = projectMapper.updateById(body);
        return c > 0 ? AjaxResult.success() : AjaxResult.error("更新失败");
    }

    @DeleteMapping("/delete")
    public AjaxResult delete(@RequestParam(value = "id", required = true) String id) {
        try {
            // 验证ID不能为空
            if (id == null || id.trim().isEmpty()) {
                return AjaxResult.error("项目ID不能为空");
            }

            // 检查项目是否存在
            Project project = projectMapper.selectById(id.trim());
            if (project == null) {
                return AjaxResult.error("项目不存在");
            }

            // 执行物理删除（直接从数据库删除记录）
            // 由于外键约束 ON DELETE CASCADE，删除项目时数据库会自动删除关联的机台
            int result = projectMapper.deleteById(id.trim());
            if (result > 0) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }
}


