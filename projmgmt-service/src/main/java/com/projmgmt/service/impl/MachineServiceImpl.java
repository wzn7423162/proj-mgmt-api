package com.projmgmt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projmgmt.domain.Machine;
import com.projmgmt.mapper.MachineMapper;
import com.projmgmt.service.MachineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 机台Service实现
 *
 * @author projmgmt
 */
@Service
public class MachineServiceImpl extends ServiceImpl<MachineMapper, Machine> implements MachineService {

    @Override
    public List<Machine> selectMachineList(Machine machine) {
        LambdaQueryWrapper<Machine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Machine::getDelFlag, "0");
        if (machine.getProjectId() != null) {
            queryWrapper.eq(Machine::getProjectId, machine.getProjectId());
        }
        if (StringUtils.hasText(machine.getMachineName())) {
            queryWrapper.like(Machine::getMachineName, machine.getMachineName());
        }
        queryWrapper.orderByDesc(Machine::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public boolean insertMachine(Machine machine) {
        machine.setDelFlag("0");
        return this.save(machine);
    }

    @Override
    public boolean updateMachine(Machine machine) {
        return this.updateById(machine);
    }

    @Override
    public boolean deleteMachineById(Long id) {
        Machine machine = new Machine();
        machine.setId(id);
        machine.setDelFlag("2");
        return this.updateById(machine);
    }
}

