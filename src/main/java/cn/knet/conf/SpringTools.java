package cn.knet.conf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringTools implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Map<String, JdbcTemplate> jdbcTemplateMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringTools.applicationContext == null) {
            SpringTools.applicationContext = applicationContext;
        }
        if (jdbcTemplateMap == null) {
            jdbcTemplateMap = applicationContext.getBeansOfType(JdbcTemplate.class);
        }

    }

    public static JdbcTemplate getJdbcTemplate(String type) {
        if (StringUtils.isBlank(type)) {
            type = "wz";
        }
        JdbcTemplate j = jdbcTemplateMap.get(type + "JdbcTemplate");
        if (j != null) return j;
        throw new RuntimeException("无法找到对应的数据源");
    }
}
