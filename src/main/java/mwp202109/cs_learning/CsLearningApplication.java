package mwp202109.cs_learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "mwp202109.cs_learning.dao.mapper")
public class CsLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsLearningApplication.class, args);
    }

}
