package cn.knet.service;

import cn.knet.enums.SqlType;
import cn.knet.util.SqlFormatUtil;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlLogDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量更新业务层
 */
@Service
@Slf4j
public class PlUpdateService {
    @Resource
    UpdateService updateService;
    @Resource
    LogEngineService logEngineService;
    @Resource
    WebSocketServer webSocketServer;

    /***
     * 批量更新的引擎
     * @param sqls
     * @param type
     * @param userId
     * @return
     */
    public List<DbResult> updateAnalysisEngine(String type, String userId, String[] sqls,String sigo) throws IOException, EncodeException {
        long start = System.currentTimeMillis();
        List<DbResult> list=new ArrayList<>();
        List<KnetSqlLogDetail> details=new ArrayList<KnetSqlLogDetail>();
        SqlType sqlType=null;
            for (String sql:sqls) {
                try {
                log.info("批量更新执行的sql:{}", sql);
                DbResult dbResult = SqlFormatUtil.sqlVaildate(sql, type);
                if (dbResult.getCode() != 1000) {
                    list.add(DbResult.error(dbResult.getMsg(), sql));
                }else{
                    sqlType=SqlParserTool.getSqlType(sql);
                    dbResult=getResultBySqlType(type,sqlType,sql);
                    details.addAll(dbResult.getLogDetails());
                    list.add(dbResult);
                }
               webSocketServer.sendInfo(dbResult,userId);
            } catch (Exception e) {
                String msg = "sql:" + sql + "批量更新出错（" + SqlParserTool.getSqlEcception(e) + ")";
                list.add(DbResult.error(msg, sql).setSqlType(sqlType.name()));
                webSocketServer.sendInfo(DbResult.error(msg, sql).setSqlType(sqlType.name()),userId);
            }
            }
            log.info("批量执行用"+(System.currentTimeMillis()-start));
            long startTime = System.currentTimeMillis();
            logEngineService.logSava(new KnetSqlLog("",type,SqlType.PIUPDATE.name(),"本次批量操作共输入"+sqls.length+"条sql语句。", userId,sigo),details);
         return list;
    }

    public DbResult getResultBySqlType(String type, SqlType sqlType, String sql) {
        switch (sqlType){
            case UPDATE:return updateService.update(sql, type, true);
            case INSERT:return updateService.insert(sql, type,sqlType,true);
            case DELETE: return updateService.delete(sql, type, sqlType, true);
            case ALTER:return updateService.alert(sql,type,sqlType,true);
            case CREATETABLE:return updateService.alert(sql,type,sqlType,true);
            case COMMENT:return updateService.alert(sql,type,sqlType,true);
            case DROP:return updateService.alert(sql,type,sqlType,true);
            default:return DbResult.error(sqlType+"类型不匹配", sql).setSqlType(SqlType.PIUPDATE.name());
        }
    }
}