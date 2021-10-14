package cn.knet.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "wzDataSource")
    @Qualifier("wzDataSource")
    @ConfigurationProperties(prefix="spring.datasource.wz")
    public DataSource wzDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "sealDataSource")
    @Qualifier("sealDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.seal")
    public DataSource sealDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name="wzJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate (
            @Qualifier("wzDataSource")  DataSource dataSource ) {

        return new JdbcTemplate(dataSource);
    }

    @Bean(name="sealJdbcTemplate")
    public JdbcTemplate  secondaryJdbcTemplate(
            @Qualifier("sealDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}