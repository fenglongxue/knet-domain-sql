package cn.knet.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


    @Primary
    @Bean(name = "wzDataSourceProperties")
    @Qualifier("wzDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.wz")
    public DataSourceProperties wzDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "wzDataSource")
    @Qualifier("wzDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.wz")
    public DataSource wzDataSource() {

        return wzDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name="wzJdbcTemplate")
    public JdbcTemplate wzJdbcTemplate (
            @Qualifier("wzDataSource")  DataSource dataSource ) {

        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "sealDataSourceProperties")
    @Qualifier("sealDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.seal")
    public DataSourceProperties sealDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "sealDataSource")
    @Qualifier("sealDataSource")
//    @ConfigurationProperties(prefix="spring.datasource.seal")
    public DataSource sealDataSource() {
        return sealDataSourceProperties().initializeDataSourceBuilder().build();
    }




    @Bean(name="sealJdbcTemplate")
    public JdbcTemplate  secondaryJdbcTemplate(
            @Qualifier("sealDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}