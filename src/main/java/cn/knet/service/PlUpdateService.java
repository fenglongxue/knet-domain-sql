package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.engine.SqlEngine;
import cn.knet.enums.SqlType;
import cn.knet.util.CommUtils;
import cn.knet.util.SqlFormatUtil;
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
 * 批量更新业务层
 */
@Service
@Slf4j
public class PlUpdateService {
    @Resource
    SqlEngine sqlEngine;
    @Resource
    LogEngineService logEngineService;
    private int logCount=50;//日志只存小于50条的数据，大于100条不存
    /***
     * 批量更新的引擎
     * @param sqls
     * @param type
     * @param userId
     * @return
     */
    public List<DbResult> updateForPLAnalysisEngine(String type, String[] sqls,String userId) {
        List<DbResult> list = new ArrayList<>();
        for (String sql : sqls) {
            log.info("更新执行的sql:{}", sql);
            long startTime = System.currentTimeMillis();
            try {
                DbResult dbResult=SqlFormatUtil.sqlVaildate(sql,type);
                if(SqlFormatUtil.sqlVaildate(sql,type).getCode()==1000){
                    SqlType sqlType = SqlParserTool.getSqlType(sql);
                    if (sqlType.equals(SqlType.UPDATE)) {
                        String table=dbResult.getMsg();
                        String selectSql= CommUtils.getSelectByUpdateSql((Update) SqlParserTool.getStatement(sql), SpringTools.getJdbcTemplate(type));
                        dbResult=sqlEngine.queryDb(type, selectSql,0);
                        if(dbResult.getCount()<=0){
                            list.add(DbResult.error(dbResult.getMsg(),sql));
                        }else{
                            dbResult =CommUtils.initUpdateDateForSelect(sql, SqlType.PIUPDATE,dbResult);
                        List<KnetSqlLogDetail> details=new ArrayList<>();
                        if (null != dbResult && null != dbResult.getMap()&&dbResult.getCount()<=logCount) {
                            CommUtils.saveLogDetail(dbResult, details);
                        }
                        int i=sqlEngine.updateAnalysisEngine(type,sql);
                        long time = System.currentTimeMillis() - startTime;
                        String msg = i + "条数据被成功更新到表" + table+ "，执行时间" + time + "毫秒。";
                        log.info("sql{}:"+msg,sql);
                        logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.PIUPDATE.name(),msg, userId),
                                details);
                        list.add(DbResult.success(msg,sql));
                        }
                    }else{
                        list.add(DbResult.error("类型不匹配，不是"+SqlType.UPDATE,sql));
                    }
                }else{
                    list.add(DbResult.error(dbResult.getMsg(),sql));
                }
            } catch (Exception e) {
                String msg =  "sql:"+sql + "更新出错（" + SqlParserTool.getSqlEcception(e) + ")";
                log.error("SQl{}更新出错{}",sql, msg);
                list.add(DbResult.error(msg,sql));
            }
        }
        return list;
    }
}