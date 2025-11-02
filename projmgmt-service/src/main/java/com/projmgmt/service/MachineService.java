package com.projmgmt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.projmgmt.domain.Machine;

import java.util.List;

/**
 * 机台Service接口
 *
 * @author projmgmt
 */
public interface MachineService extends IService<Machine> {

    /**
     * 查询机台列表
     *
     * @param machine 机台信息
     * @return 机台集合
     */
    List<Machine> selectMachineList(Machine machine);

    /**
     * 新增机台
     *
     * @param machine 机台信息
     * @return 结果
     */
    boolean insertMachine(Machine machine);

    /**
     * 修改机台
     *
     * @param machine 机台信息
     * @return 结果
     */
    boolean updateMachine(Machine machine);

    /**
     * 删除机台
     *
     * @param id 机台ID
     * @return 结果
     */
    boolean deleteMachineById(Long id);
}

