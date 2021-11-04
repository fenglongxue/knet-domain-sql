package cn.knet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class KnetDomainSqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnetDomainSqlApplication.class, args);
    }

}
