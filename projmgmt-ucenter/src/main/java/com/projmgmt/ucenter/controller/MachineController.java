package com.projmgmt.ucenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.projmgmt.ucenter.core.AjaxResult;
import com.projmgmt.ucenter.domain.Machine;
import com.projmgmt.ucenter.mapper.MachineMapper;
import com.projmgmt.ucenter.mapper.ProjectMapper;
import com.projmgmt.ucenter.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/front/machine")
public class MachineController {

    @Autowired
    private MachineMapper machineMapper;
    @Autowired
    private ProjectMapper projectMapper;

    @GetMapping("/list")
    public AjaxResult list(@RequestParam int pageNum,
                           @RequestParam int pageSize,
                           @RequestParam Long projectId,
                           @RequestParam(required = false) String machineName,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate) {

        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(pageSize, 1);

        // 统计总数
        LambdaQueryWrapper<Machine> countQw = new LambdaQueryWrapper<>();
        countQw.eq(Machine::getProjectId, projectId);
        if (machineName != null && !machineName.isEmpty()) {
            countQw.like(Machine::getMachineName, machineName);
        }
        applyTimeRange(countQw, startDate, endDate);
        long total = machineMapper.selectCount(countQw);

        // 查询当前页
        LambdaQueryWrapper<Machine> listQw = new LambdaQueryWrapper<>();
        listQw.eq(Machine::getProjectId, projectId);
        if (machineName != null && !machineName.isEmpty()) {
            listQw.like(Machine::getMachineName, machineName);
        }
        applyTimeRange(listQw, startDate, endDate);
        listQw.orderByDesc(Machine::getCreateTime);
        int offset = (pageNum - 1) * pageSize;
        listQw.last("LIMIT " + offset + ", " + pageSize);

        List<Machine> list = machineMapper.selectList(listQw);

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return AjaxResult.success(data);
    }

    @GetMapping("/detail")
    public AjaxResult detail(@RequestParam Long id) {
        Machine m = machineMapper.selectById(id);
        return AjaxResult.success(m);
    }

    @PostMapping("/create")
    public AjaxResult create(@RequestBody Machine body) {
        if (body.getProjectId() == null) {
            return AjaxResult.error("缺少项目ID");
        }
        // 先校验项目是否存在，避免外键错误
        Project proj = projectMapper.selectById(String.valueOf(body.getProjectId()));
        if (proj == null) {
            return AjaxResult.error("项目不存在");
        }

        String namesRaw = body.getMachineName();
        if (namesRaw == null || namesRaw.trim().isEmpty()) {
            return AjaxResult.error("机台名称不能为空");
        }

        // 支持英文逗号/中文逗号分隔，自动去重去空格
        String[] nameArr = namesRaw.split("[,，]");
        int created = 0;
        List<String> failed = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (String n : nameArr) {
            String name = n == null ? "" : n.trim();
            if (name.isEmpty()) {
                continue;
            }
            Machine m = new Machine();
            m.setProjectId(body.getProjectId());
            m.setMachineName(name);
            // 默认值
            m.setImportTime(now);
            m.setOnlineTime(null);
            m.setOnlineVerified(0);
            m.setDelFlag("0");
            m.setCreateTime(now);
            m.setUpdateTime(now);
            try {
                created += machineMapper.insert(m);
            } catch (Exception ex) {
                failed.add(name);
            }
        }

        // 更新项目的机台数量统计
        updateProjectMachineCount(body.getProjectId());

        Map<String, Object> data = new HashMap<>();
        data.put("created", created);
        if (!failed.isEmpty()) {
            data.put("failed", failed);
        }
        return AjaxResult.success(data);
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody Machine body) {
        if (body.getId() == null) {
            return AjaxResult.error("缺少机台ID");
        }
        body.setUpdateTime(LocalDateTime.now());
        int c = machineMapper.updateById(body);
        return c > 0 ? AjaxResult.success() : AjaxResult.error("更新失败");
    }

    @DeleteMapping("/delete")
    public AjaxResult delete(@RequestParam Long id) {
        // 先获取机台信息，以便知道所属项目
        Machine machine = machineMapper.selectById(id);
        if (machine == null) {
            return AjaxResult.error("机台不存在");
        }

        int c = machineMapper.deleteById(id);
        if (c > 0) {
            // 更新项目的机台数量统计
            updateProjectMachineCount(machine.getProjectId());
            return AjaxResult.success();
        }
        return AjaxResult.error("删除失败");
    }

    private void applyTimeRange(LambdaQueryWrapper<Machine> qw, String start, String end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (start != null && !start.isEmpty()) {
            try {
                LocalDateTime s = LocalDateTime.parse(start, fmt);
                qw.ge(Machine::getImportTime, s);
            } catch (Exception ignored) {}
        }
        if (end != null && !end.isEmpty()) {
            try {
                LocalDateTime e = LocalDateTime.parse(end, fmt);
                qw.le(Machine::getImportTime, e);
            } catch (Exception ignored) {}
        }
    }

    /**
     * 更新项目的机台数量统计
     */
    private void updateProjectMachineCount(Long projectId) {
        if (projectId == null) {
            return;
        }
        // 统计该项目下有效的机台数量（未删除的）
        LambdaQueryWrapper<Machine> countQw = new LambdaQueryWrapper<>();
        countQw.eq(Machine::getProjectId, projectId);
        countQw.eq(Machine::getDelFlag, "0");
        long count = machineMapper.selectCount(countQw);

        // 更新项目表的 machineCount 字段
        Project project = projectMapper.selectById(String.valueOf(projectId));
        if (project != null) {
            project.setMachineCount((int) count);
            project.setUpdateTime(LocalDateTime.now());
            projectMapper.updateById(project);
        }
    }
}


