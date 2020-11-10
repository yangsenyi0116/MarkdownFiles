package com.kermi.security.jdbccertification;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kermi.security.jdbccertification.mapper")
public class JDBCCertificationStarter {
    public static void main(String[] args) {
        SpringApplication.run(JDBCCertificationStarter.class, args);
    }
}

