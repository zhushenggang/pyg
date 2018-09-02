package com.pyg.fm;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.WriteAbortedException;
import java.io.Writer;
import java.util.*;

/**
 * Created by on 2018/8/22.
 */
public class MyFreemaker {


    /**
     * freemarker入门案例
     * 获取基本数据：${} 花括号中填写是map的key,获取map中value值。
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("hello.ftl");

        //创建map对象，存储ftl模板页面需要的数据
        Map maps = new HashMap();
        maps.put("hello", "欢迎来到freemarker世界!");

        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\first.html"));

        //生成静态页面
        template.process(maps,out);

        out.close();


    }

    /**
     * freemarker指令：assain常量指令
     * @throws Exception
     */
    @Test
    public void test02() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("assian.ftl");


        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\assain.html"));

        //生成静态页面
        template.process(null,out);

        out.close();


    }

    /**
     * freemarker ifelse指令
     * @throws Exception
     */
    @Test
    public void test03() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("ifelse.ftl");

        //创建map对象，存储ftl模板页面需要的数据
        Map maps = new HashMap();
        maps.put("flag", 3);

        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\ifelse.html"));

        //生成静态页面
        template.process(maps,out);

        out.close();


    }

    /**
     * freemarker list指令--循环list集合数据
     * @throws Exception
     */
    @Test
    public void test04() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("list.ftl");

        //创建map对象，存储ftl模板页面需要的数据
        Map maps = new HashMap();

        //创建集合对象，封装数据
        List<Person> pList = new ArrayList<>();
        //创建person对象
        Person p1 = new Person();
        p1.setId(1);
        p1.setName("张三丰");
        p1.setSex("男");
        p1.setAge(12);
        p1.setAddress("武当山");

        //创建person对象
        Person p2 = new Person();
        p2.setId(2);
        p2.setName("张翠山");
        p2.setSex("男");
        p2.setAge(1);
        p2.setAddress("武当山");


        //创建person对象
        Person p3 = new Person();
        p3.setId(3);
        p3.setName("张翠花");
        p3.setSex("男");
        p3.setAge(10);
        p3.setAddress("武当山");

        pList.add(p1);
        pList.add(p2);
        pList.add(p3);

        maps.put("pList",pList);


        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\list.html"));

        //生成静态页面
        template.process(maps,out);

        out.close();


    }

    /**
     * freemarker函数：eval内建函数
     * @throws Exception
     */
    @Test
    public void test05() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("eval.ftl");


        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\eval.html"));

        //生成静态页面
        template.process(null,out);

        out.close();


    }


    /**
     * freemarker函数：时间函数
     * @throws Exception
     */
    @Test
    public void test06() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("date.ftl");

        Map maps = new HashMap();
        maps.put("today", new Date());


        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\date.html"));

        //生成静态页面
        template.process(maps,out);

        out.close();


    }

    @Test
    public void test07() throws Exception {

        //创建freemarker核心对象
        Configuration cf = new Configuration(Configuration.getVersion());
        //设置模板页面所在路径
        cf.setDirectoryForTemplateLoading(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\com\\pyg\\ftl"));
        //设置模板文件编码
        cf.setDefaultEncoding("utf-8");

        //读取模板文件，获取模板对象
        Template template = cf.getTemplate("null.ftl");

        //创建map对象，存储ftl模板页面需要的数据
        Map maps = new HashMap();
        maps.put("name", "唐僧");

        //创建输出流对象，把生成静态页面写入磁盘
        Writer out = new FileWriter(new File("C:\\workspace\\heima84\\" +
                "pyg-html-utils\\src\\main\\resources\\out\\null.html"));

        //生成静态页面
        template.process(maps,out);

        out.close();


    }



}
