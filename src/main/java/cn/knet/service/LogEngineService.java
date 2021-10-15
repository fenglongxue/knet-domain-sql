package cn.knet.service;

import cn.knet.util.UUIDGenerator;
import cn.knet.vo.DbResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LogEngineService {
    @Autowired
    @Qualifier("wzJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("sealJdbcTemplate")
    protected JdbcTemplate  sealJdbcTemplate;
    /**
     * 插入日志
     * @param sql
     * @param type
     * @return
     */
    public DbResult logSava(String sql, String type,String opType,String sigo,String email,String userId,String sqlResu) throws Exception {
        String insertSql = "INSERT INTO KNET_SQL_LOG (ID,SQL,TYPE,OP_TYPE,SIGO,EMAIL,USER_ID,SQL_RESU) VALUES (?,?,?,?,?,?,?,?);";
        jdbcTemplate.update(insertSql, UUIDGenerator.getUUID(),sql,type,opType,sigo,email,userId,sqlResu);
        return  new DbResult();
    }
    /**
     * 保存分享日志
     * @param sql
     * @param type
     * @return
     */
    public DbResult logShare(String sql, String storType,String title,String userId) throws Exception {
        String insertSql = "INSERT INTO KNET_SQL_LOG (ID,SQL,STOR_TYPE,TITLE,USER_ID) VALUES (?,?,?,?,?);";
        jdbcTemplate.update(insertSql, UUIDGenerator.getUUID(),sql,storType,title,userId);
        return  new DbResult();
    }
    /**
     * 查询日志
     * @param sql
     * @param type
     * @return
     */
    public DbResult logList(String userId) throws Exception {
        StringBuffer listSql =  new StringBuffer("select * from KNET_SQL_LOG ");
        if(StringUtils.isNotBlank(userId)){
            listSql.append("where USER_ID=?");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(listSql.toString(), userId);
        return  new DbResult(1000,list);
    }
}
