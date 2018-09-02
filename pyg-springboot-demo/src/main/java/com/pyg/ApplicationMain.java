package com.pyg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by on 2018/8/23.
 */
@SpringBootApplication
public class ApplicationMain {

    /**
     * 需求：开发一个自定义命令
     * 输入：
     * 1,java abc 执行数据库数据一键导入索引库
     * 2,java 123 实现删除索引库
     * 说出你的思路？
     * 输入：java -jar xx.jar
     *
     * @param args
     */

    public static void main(String[] args) {
        //入口代码
        //执行入口函数后
        //1,加载内容tomcat服务器
        //2,加载web项目服务环境
        SpringApplication.run(ApplicationMain.class,args);

    }

}
