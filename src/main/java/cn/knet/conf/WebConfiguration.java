package cn.knet.conf;

import cn.knet.filter.SqlFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration{
    /***
     * 拦截sql,校验格式
     * @return
     */
    @Bean
    public FilterRegistrationBean registerFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SqlFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}
