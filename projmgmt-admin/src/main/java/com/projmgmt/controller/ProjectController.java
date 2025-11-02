package com.projmgmt.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.projmgmt.common.core.domain.AjaxResult;
import com.projmgmt.common.core.page.TableDataInfo;
import com.projmgmt.domain.Project;
import com.projmgmt.domain.Machine;
import com.projmgmt.service.ProjectService;
import com.projmgmt.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 项目管理控制器
 *
 * @author projmgmt
 */
@RestController
@RequestMapping("/front/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MachineService machineService;

    @Value("${token.header:Authorization}")
    private String tokenHeader;

    private static final String FRONT_TOKEN_HEADER = "FrontToken";

    /**
     * 查询项目列表
     */
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String orderByColumn,
            @RequestParam(required = false) String isAsc) {

        PageHelper.startPage(pageNum, pageSize);
        Project project = new Project();
        project.setProjectName(projectName);
        List<Project> list = projectService.selectProjectList(project, orderByColumn, isAsc);
        PageInfo<Project> pageInfo = new PageInfo<>(list);

        TableDataInfo dataInfo = new TableDataInfo(list, pageInfo.getTotal());
        dataInfo.setPageNum(pageNum);
        dataInfo.setPageSize(pageSize);
        return dataInfo;
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/detail")
    public AjaxResult getInfo(@RequestParam Long id) {
        Project project = projectService.getById(id);
        return AjaxResult.success(project);
    }

    /**
     * 新增项目
     */
    @PostMapping("/create")
    public AjaxResult add(@RequestBody Project project, HttpServletRequest request) {
        // 验证项目名称不能为空
        if (project == null || !StringUtils.hasText(project.getProjectName())) {
            return AjaxResult.error("项目名称不能为空");
        }

        // 从请求头获取token并解析用户ID（优先使用FrontToken，兼容Authorization）
        String token = request.getHeader(FRONT_TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            token = request.getHeader(tokenHeader);
        }
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return AjaxResult.error("缺少用户标识，请先登录");
        }

        // 设置用户ID
        project.setUserId(userId);

        // 设置创建时间和更新时间
        Date now = new Date();
        project.setCreateTime(now);
        project.setUpdateTime(now);

        // 如果机台数量为空，设置为0
        if (project.getMachineCount() == null) {
            project.setMachineCount(0);
        }

        // 调用Service保存项目
        boolean result = projectService.insertProject(project);
        if (result) {
            return AjaxResult.success("项目创建成功", project);
        } else {
            return AjaxResult.error("项目创建失败");
        }
    }

    /**
     * 从token中提取用户ID
     * token格式: token_123456 或 Bearer token_123456
     *
     * @param token token字符串
     * @return 用户ID，如果解析失败返回null
     */
    private Long extractUserIdFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        // 去掉Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 去掉token_前缀
        if (token.startsWith("token_")) {
            try {
                String userIdStr = token.substring(6);
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * 修改项目
     */
    @PutMapping("/update")
    public AjaxResult edit(@RequestBody Project project) {
        boolean result = projectService.updateProject(project);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/delete")
    public AjaxResult remove(@RequestParam(value = "id", required = true) Long id) {
        try {
            // 验证ID不能为空
            if (id == null) {
                return AjaxResult.error("项目ID不能为空");
            }

            // 检查项目是否存在
            Project project = projectService.getById(id);
            if (project == null) {
                return AjaxResult.error("项目不存在");
            }

            // 检查是否有关联的机台
            List<Machine> machines = machineService.list(
                new LambdaQueryWrapper<Machine>()
                    .eq(Machine::getProjectId, id)
            );

            // 如果有关联的机台，先删除所有关联机台（物理删除）
            // 注意：由于外键约束 ON DELETE CASCADE，删除项目时数据库也会自动删除关联机台
            // 但为了确保数据一致性，我们先手动删除机台
            if (!machines.isEmpty()) {
                for (Machine machine : machines) {
                    machineService.removeById(machine.getId());
                }
            }

            // 执行物理删除项目（直接从数据库删除记录）
            boolean result = projectService.deleteProjectById(id);
            if (result) {
                int machineCount = machines.size();
                if (machineCount > 0) {
                    return AjaxResult.success("删除成功，已同时删除 " + machineCount + " 个关联机台");
                } else {
                    return AjaxResult.success("删除成功");
                }
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

