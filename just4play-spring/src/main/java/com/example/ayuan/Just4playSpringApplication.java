package com.example.ayuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//开启异步
@EnableAsync
//开启事务
@EnableTransactionManagement
//多数据源用这个排除
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan(basePackages = {"com.example.ayuan.mapper"})
@Configuration
public class Just4playSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(Just4playSpringApplication.class, args);
    }

}
