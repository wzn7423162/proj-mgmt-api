package com.projmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author projmgmt
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ProjMgmtApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ProjMgmtApplication.java, args);
        System.out.println("========== ProjMgmt项目管理系统启动成功 ==========");
    }
}

