package com.example.ayuan;

import com.example.ayuan.annotation.BeanAsyncInit;
import com.example.ayuan.test.AAABean;
import com.example.ayuan.test.BBBBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Just4playRuntimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Just4playRuntimeApplication.class, args);
    }

    @Bean(value = "AAA", initMethod = "init")
    @BeanAsyncInit
    public AAABean aaaBean() {
        return new AAABean();
    }


    @Bean(value = "BBB", initMethod = "init")
    @BeanAsyncInit
    public BBBBean bbbBean() {
        return new BBBBean();
    }
}
