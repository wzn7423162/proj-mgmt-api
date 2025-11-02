package com.projmgmt.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.projmgmt.common.core.domain.AjaxResult;
import com.projmgmt.common.core.page.TableDataInfo;
import com.projmgmt.domain.Machine;
import com.projmgmt.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机台管理控制器
 *
 * @author projmgmt
 */
@RestController
@RequestMapping("/front/machine")
public class MachineController {

    @Autowired
    private MachineService machineService;

    /**
     * 查询机台列表
     */
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long projectId,
            @RequestParam(required = false) String machineName) {

        PageHelper.startPage(pageNum, pageSize);
        Machine machine = new Machine();
        machine.setProjectId(projectId);
        machine.setMachineName(machineName);
        List<Machine> list = machineService.selectMachineList(machine);
        PageInfo<Machine> pageInfo = new PageInfo<>(list);

        TableDataInfo dataInfo = new TableDataInfo(list, pageInfo.getTotal());
        dataInfo.setPageNum(pageNum);
        dataInfo.setPageSize(pageSize);
        return dataInfo;
    }

    /**
     * 获取机台详情
     */
    @GetMapping("/detail")
    public AjaxResult getInfo(@RequestParam Long id) {
        Machine machine = machineService.getById(id);
        return AjaxResult.success(machine);
    }

    /**
     * 新增机台
     */
    @PostMapping("/create")
    public AjaxResult add(@RequestBody Machine machine) {
        boolean result = machineService.insertMachine(machine);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改机台
     */
    @PutMapping("/update")
    public AjaxResult edit(@RequestBody Machine machine) {
        boolean result = machineService.updateMachine(machine);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除机台
     */
    @DeleteMapping("/delete")
    public AjaxResult remove(@RequestParam Long id) {
        boolean result = machineService.deleteMachineById(id);
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}

