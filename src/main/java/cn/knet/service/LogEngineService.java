package cn.knet.service;

import cn.knet.util.SqlParserTool;
import cn.knet.util.UUIDGenerator;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlShare;
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
     * 
     * @param sqsl
     * @param type
     * @param opType
     * @param sigo
     * @param email
     * @param userId
     * @param sqlResu
     * @param old
     * @param now
     * @return
     * @throws Exception
     */
    public DbResult logSava(KnetSqlLog log)  {
        String insertSql = "INSERT INTO KNET_SQL_LOG (ID,SQL,TYPE,OP_TYPE,SIGO,EMAIL,USER_ID,SQL_RESU,old,now) VALUES (?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(insertSql, UUIDGenerator.getUUID(),log.getSql(),log.getType(),log.getOpType(),log.getSigo(),log.getEmail(),log.getUserId(),log.getSqlResu(),log.getOld(),log.getNow());
        return  new DbResult();
    }

    /**
     *
     * @param sql
     * @param storType
     * @param title
     * @param userId
     * @return
     * @throws Exception
     */
    public DbResult logShare(KnetSqlShare share) {
        String insertSql = "INSERT INTO KNET_SQL_SHARE (ID,SQL,STOR_TYPE,TITLE,USER_ID) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(insertSql, UUIDGenerator.getUUID(),share.getSql(),share.getStorType(),share.getTitle(),share.getUserId());
        return  new DbResult();
    }
    /**
     * 查询日志
     * @param userId
     * @return
     */
    public DbResult logList(String userId) {
        StringBuffer listSql =  new StringBuffer("select sql,type,op_type,u.name from KNET_SQL_LOG l left join KNET_USER u where l.USER_ID=U.ID ");
        if(StringUtils.isNotBlank(userId)){
            listSql.append("and USER_ID=?");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(SqlParserTool.setRowNum(listSql.toString(),50));
        return  new DbResult(1000,list);
    }
}
