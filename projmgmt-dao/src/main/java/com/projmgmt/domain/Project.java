package com.projmgmt.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目信息表
 *
 * @author projmgmt
 */
@Data
@TableName("t_project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 项目ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目名称 */
    private String projectName;

    /** 机台数量 */
    private Integer machineCount;

    /** 创建用户ID */
    private Long userId;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}

