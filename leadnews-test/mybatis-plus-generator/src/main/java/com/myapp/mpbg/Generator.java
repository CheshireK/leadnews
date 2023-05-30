package com.myapp.mpbg;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

public class Generator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://myvm.site:3306/leadnews_user?useUnicode=true&useSSL=false&characterEncoding=utf8", "root", "admin")
                .globalConfig(builder -> {
                    builder.author("luosa") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            // .fileOverride() // 覆盖已生成文件
                            .outputDir("D://data//generator"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("com.myapp.model.common.pojo") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://data//generator")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("ap_user") ;// 设置需要生成的表名
                            // .addTablePrefix("t_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
