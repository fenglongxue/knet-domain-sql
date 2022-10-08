package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.engine.SqlEngine;
import cn.knet.enums.SqlType;
import cn.knet.util.CommUtils;
import cn.knet.util.SqlFormatUtil;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLogDetail;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.delete.Delete;
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
    private int logCount=50;//日志只存小于50条的数据，大于100条不存
    /****
     * 表操作
     * @param sql
     * @return
     */
    public DbResult alert(String sql, String type, SqlType sqlType,boolean isPl) {
        long startTime = System.currentTimeMillis();
        log.info("表操作执行的sql:{}", sql);
        try {
            String table = SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0).toUpperCase();
            sqlEngine.alertAnalysisEngine(type, sql);
            long time = System.currentTimeMillis() - startTime;
            String msg = "表" + table + "执行" + sqlType.name() + "操作成功，执行时间" + time + "毫秒。";
            log.info("sql{}:" + msg, sql);
            if(isPl){
                List<KnetSqlLogDetail> details = new ArrayList<>();
                details.add(new KnetSqlLogDetail().setOpType(sqlType.name()).setSql(sql).setSqlResu(msg));
                return new DbResult().setCode(1000).setMsg(msg).setLogDetails(details).setSql(sql).setSqlType(sqlType.name());
            }
            return new DbResult().setCode(1000).setMsg(msg).setSql(sql).setSqlType(sqlType.name());
        } catch (Exception e) {
            return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg(SqlParserTool.getSqlEcception(e)).setSql(sql);
        }
    }

    /**
     * 插入操作
     *
     * @param sql
     * @param type
     * @param sqlType
     * @return
     */
    public DbResult insert(String sql,String type,SqlType sqlType,boolean isPl) {
        long startTime = System.currentTimeMillis();
        try {
            int i = sqlEngine.insertAnalysisEngine(type, sql);
            List<String> tables = SqlParserTool.getTableList(SqlParserTool.getStatement(sql));
            String msg = i + "条数据被成功插入到表" + tables.get(0).toUpperCase() + "，执行时间" + (System.currentTimeMillis() - startTime) + "毫秒。";
            log.info(msg);
            if(isPl){
                List<KnetSqlLogDetail> details = new ArrayList<>();
                details.add(new KnetSqlLogDetail().setOpType(sqlType.name()).setSql(sql).setSqlResu(msg));
                return new DbResult().setCode(1000).setMsg(msg).setLogDetails(details).setSql(sql).setSqlType(sqlType.name());
            }
            return new DbResult().setCode(1000).setMsg(msg).setSql(sql).setSqlType(sqlType.name());
        } catch (Exception e) {
            return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg(SqlParserTool.getSqlEcception(e)).setSql(sql);
        }
    }
    /***
     * 删除操作
     * @param sql
     * @param type
     * @param sqlType
     * @return
     */
    public DbResult delete(String sql,String type,SqlType sqlType,boolean isPl) {
        long startTime = System.currentTimeMillis();
        try {
            String selectSql=CommUtils.getSelectByDeleteSql((Delete) SqlParserTool.getStatement(sql));
            int total=sqlEngine.queryCount(type,selectSql);
            //保存删除日志的明细
            List<KnetSqlLogDetail> details = new ArrayList<>();
            if(total<=0){
                //批量更新要保存sql到明细中
                if(isPl){
                    details.add(new KnetSqlLogDetail().setSql(sql.replaceFirst("\n","")).setOpType(sqlType.name()));
                }
                return new DbResult().setCode(1002).setSql(sql).setMsg("共删除0条数据").setSqlType(sqlType.name()).setLogDetails(details);
            }
            if(isPl&&total>1){
                details.add(new KnetSqlLogDetail().setSql(sql.replaceFirst("\n","")).setOpType(sqlType.name()));
                return new DbResult().setCode(1002).setMsg("将删除"+total+"条数据，不支持批量操作").setSqlType(sqlType.name()).setLogDetails(details);
            }
            if (total<=logCount) {
                //保存日志要查询全部
                DbResult dbResult = sqlEngine.queryDb(type,selectSql,-1);
                if(dbResult.getData().isEmpty()){
                    log.error("查询sql:{}执行出错{}", sql,"将删除的数据为0条");
                    return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg("共删除0条数据").setSql(sql);
                }
                dbResult.setMap(CommUtils.initOldMap(dbResult));
                details=CommUtils.saveLogDetail(dbResult,isPl,sql,sqlType.name(),dbResult.getCount()+"条数据被成功从表" + SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0).toUpperCase() + "中删除，执行时间" +(System.currentTimeMillis()-startTime) + "毫秒。");
            }
            long time = System.currentTimeMillis() - startTime;
            String msg = sqlEngine.deleteAnalysisEngine(type, sql) + "条数据被成功从表" + SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0).toUpperCase() + "中删除，执行时间" + time + "毫秒。";
            log.info("sql{}:" + msg, sql);
            return new DbResult().setLogDetails(details).setCode(1000).setMsg(msg).setSql(sql).setSqlType(sqlType.name());
        } catch (JSQLParserException e) {
            return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg(SqlParserTool.getSqlEcception(e)).setSql(sql);
        }
    }
    /**
     * 更新
     * @param sql
     * @return
     */
    public DbResult update(String sql,String type,boolean isPl) {
        long startTime = System.currentTimeMillis();
        String sqlType=SqlType.UPDATE.name();
        log.info("更新执行的sql:{}", sql);
        try {
            DbResult dbResult = SqlFormatUtil.sqlVaildate(sql, type);
            if (dbResult.getCode() != 1000) {
                return dbResult.setSql(sql).setSqlType(sqlType);
            }
            String selectSql=CommUtils.getSelectByUpdateSql((Update) SqlParserTool.getStatement(sql), SpringTools.getJdbcTemplate(type));
            int total=sqlEngine.queryCount(type,selectSql);
            List<KnetSqlLogDetail> details=new ArrayList<>();
            if(total<=0){
                //批量更新要保存sql到明细中
                if(isPl){
                    details.add(new KnetSqlLogDetail().setSql(sql.replaceFirst("\n","")).setOpType(sqlType));
                }
                return new DbResult().setCode(1002).setSql(sql).setMsg("共更新0条数据").setSqlType(sqlType).setLogDetails(details);
            }
            if(isPl&&total>1){
                details.add(new KnetSqlLogDetail().setSql(sql.replaceFirst("\n","")).setOpType(sqlType));
                return new DbResult().setCode(1002).setMsg("将更新"+total+"条数据，不支持批量操作").setLogDetails(details).setSql(sql);
            }
            //保存日志详情
            if(total<=logCount){
                dbResult=sqlEngine.queryDb(type, selectSql,-1);//-1不分页 取全部
                if (dbResult.getCount() == 0) return dbResult.setCode(1002).setData(null).setSql(sql).setMsg(dbResult.getMsg()).setSqlType(sqlType);
                dbResult =CommUtils.initUpdateForSelect(sql,SqlType.UPDATE,dbResult);
                if (null == dbResult.getMap()) {
                    return DbResult.error(1002,dbResult.getMsg(),sql).setSqlType(sqlType);
                }
                details=CommUtils.saveLogDetail(dbResult,isPl,sql,sqlType,
                        dbResult.getCount()+ "条数据被成功更新到表" + SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0).toUpperCase()+ "，执行时间" +(System.currentTimeMillis()-startTime)+ "毫秒。");
            }
            int i = sqlEngine.updateAnalysisEngine(type,sql);
            long time = System.currentTimeMillis() - startTime;
            String msg = i + "条数据被成功更新到表" + SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0).toUpperCase()+ "，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            return new DbResult().setLogDetails(details).setCode(1000).setMsg(msg).setSql(sql).setSqlType(sqlType);
        } catch (Exception e) {
            return DbResult.error(1002, "更新出错" + SqlParserTool.getSqlEcception(e), sql).setSqlType(sqlType);
        }
    }
}