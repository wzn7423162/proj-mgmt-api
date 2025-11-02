package com.projmgmt.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 机台信息表
 *
 * @author projmgmt
 */
@Data
@TableName("t_machine")
public class Machine implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 机台ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 机台名称 */
    private String machineName;

    /** 所属项目ID */
    private Long projectId;

    /** 导入机台的时间 */
    private Date importTime;

    /** 上线时间 */
    private Date onlineTime;

    /** 上线验证（0-未验证，1-已验证） */
    private Integer onlineVerified;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}

