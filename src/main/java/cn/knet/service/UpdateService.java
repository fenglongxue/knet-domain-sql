package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.engine.SqlEngine;
import cn.knet.enums.SqlType;
import cn.knet.util.CommUtils;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlLogDetail;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行业务层
 */
@Service
@Slf4j
public class UpdateService {
    @Resource
    SqlEngine sqlEngine;
    @Resource
    LogEngineService logEngineService;
    private int logCount=50;//日志只存小于50条的数据，大于100条不存
    /**
     * 更新
     * @param sql
     * @return
     */
    public DbResult update(String sql,String type,String userId) {
        long startTime = System.currentTimeMillis();
        log.info("更新执行的sql:{}", sql);
        try {
            String selectSql=CommUtils.getSelectByUpdateSql((Update) SqlParserTool.getStatement(sql), SpringTools.getJdbcTemplate(type));
            DbResult dbResult=sqlEngine.queryDb(type, selectSql,0);
            if (dbResult.getCount() == 0) return dbResult.setCode(1002).setData(null).setSql(sql).setMsg(dbResult.getMsg()).setSqlType( SqlType.UPDATE.name());
            dbResult =CommUtils.initUpdateDateForSelect(sql, SqlType.UPDATE,dbResult);
            if (null == dbResult.getMap()) {
                return DbResult.error(1002, dbResult.getMsg(), sql);
            }
            List<KnetSqlLogDetail> details=new ArrayList<>();
            if(dbResult.getCount()<=logCount){
                CommUtils.saveLogDetail(dbResult, details);
            }
            int i = sqlEngine.updateAnalysisEngine(type,sql);
            long time = System.currentTimeMillis() - startTime;
            String msg = i + "条数据被成功更新到表" + SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0) + "，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.UPDATE.name(),msg, userId),
                    details);
            return DbResult.success(msg, sql);
        } catch (Exception e) {
            log.error("sql:{}更新出错{}",sql, SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, "更新出错" + SqlParserTool.getSqlEcception(e), sql);
        }
    }
}