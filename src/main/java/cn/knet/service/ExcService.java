package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.engine.SqlEngine;
import cn.knet.enums.SqlType;
import cn.knet.util.CommUtils;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlLogDetail;
import cn.knet.vo.OldAndNewVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 运行业务层
 */
@Service
@Slf4j
public class ExcService {
    @Resource
    SqlEngine sqlEngine;
    @Resource
    LogEngineService logEngineService;
    private int logCount=50;//日志只存小于50条的数据，大于100条不存
    /***
     * 运行操作：具体执行那个方法
     * @param type
     * @param pageNumber
     * @return
     * @throws Exception
     */
    public DbResult exc(String sql, String type, int pageNumber, String userId) {
                try {
                    SqlType opType = SqlParserTool.getSqlType(sql);
                    switch (opType){
                        case SELECT: return select(sql, type, pageNumber, opType);
                        case UPDATE: return updateForSelect(sql, type, SqlType.UPDATESELECT);
                        case INSERT:  return insert(sql, type, opType, userId);
                        case DELETE: return delete(sql, type, opType, userId);
                        case COMMENT:   return  alert(sql, type, opType, userId);
                        case ALTER:   return  alert(sql, type, opType, userId);
                        case CREATETABLE:   return  alert(sql, type, opType, userId);
                        case DROP:   return  alert(sql, type, opType, userId);
                        default:  return  DbResult.error(1002, opType+"操作类型不支持！",sql);
                    }
                } catch (JSQLParserException e) {
                    return  DbResult.error(1002, "出现异常，" + SqlParserTool.getSqlEcception(e),sql);
                }
    }

    /***
     * 查询方法
     * @param sql
     * @param pageNumber
     * @return
     */
    public DbResult select(String sql, String type, int pageNumber, SqlType opType) {
        long startTime = System.currentTimeMillis();
            DbResult result =sqlEngine.queryDb(type, sql, pageNumber);
            if (result.getData().isEmpty()) {
                return result.setSql(sql).setSqlType(opType.name());
            }
            List<String> listKey=new ArrayList<>();
            result.getData().get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            long time = System.currentTimeMillis() - startTime;
            String msg = "执行" + SqlType.SELECT.name() + "操作成功,共查询到" + result.getCount() + "条数据，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            return result.setMsg(msg).setSql(sql).setTitle(listKey).setSqlType(opType.name());
    }

    /***
     * 更新前的查找方法
     * 返回多个查询页
     * @param sql
     * @param type
     * @return
     * @throws Exception
     */
    public DbResult updateForSelect(String sql, String type, SqlType sqlType) {
        //只取前100条展示
        long startTime = System.currentTimeMillis();
        try {
            String selectSql = CommUtils.getSelectByUpdateSql((Update) SqlParserTool.getStatement(sql),SpringTools.getJdbcTemplate(type));
            DbResult dbResult = sqlEngine.queryDb(type, selectSql,0);
            if (dbResult.getCount() == 0) return dbResult.setCode(1002).setData(null).setSql(sql).setMsg(dbResult.getMsg()).setSqlType(sqlType.name());
            List<String> listKey=new ArrayList<>();
            dbResult.getData().get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            dbResult=CommUtils.initUpdateDateForSelect(sql,sqlType,dbResult);
            long time = System.currentTimeMillis() - startTime;
            String msg =dbResult.getMsg()+"执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            return dbResult.setMsg(msg).setTitle(listKey);
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, e.getCause().getMessage(), sql).setSqlType(sqlType.name());
        }
    }
    /****
     * 表操作
     * @param sql
     * @return
     */
    public DbResult alert(String sql, String type, SqlType opType, String userId) {
        long startTime = System.currentTimeMillis();
        log.info("表操作执行的sql:{}", sql);
        try {
            String table = SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0);
            sqlEngine.alertAnalysisEngine(type,sql);
            long time= System.currentTimeMillis() - startTime;
            String msg="表"+table+"执行"+opType.name()+"操作成功"+table+"，执行时间"+time+"毫秒。";
            log.info("sql{}:"+msg,sql);
            logEngineService.logSava(new KnetSqlLog(sql, type,opType.name(), msg, userId),new ArrayList<KnetSqlLogDetail>());
            return new DbResult().setSql(sql).setCode(1000).setSqlType(opType.name()).setMsg(msg);
        } catch (Exception e) {
            log.error("sql:{}表操作执行出错{}",sql,SqlParserTool.getSqlEcception(e));
            return new DbResult().setSql(sql).setCode(1002).setSqlType(opType.name()).setMsg(SqlParserTool.getSqlEcception(e));
        }
    }

    /**
     * 插入操作
     * @param sql
     * @param type
     * @param opType
     * @param userId
     * @return
     */
    public DbResult insert(String sql, String type, SqlType opType, String userId) {
        long startTime = System.currentTimeMillis();
        try {
            int i =sqlEngine.insertAnalysisEngine(type,sql);
            List<String> tables = SqlParserTool.getTableList(SqlParserTool.getStatement(sql));
            String msg = i + "条数据被成功插入到表" + tables.get(0) + "，执行时间" +(System.currentTimeMillis() - startTime)+ "毫秒。";
            log.info(msg);
            logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.INSERT.name(), msg, userId),new ArrayList<KnetSqlLogDetail>());
            return new DbResult().setSql(sql).setCode(1000).setSqlType(opType.name()).setMsg(msg);
        } catch (Exception e) {
            log.error("表插入sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return new DbResult().setSql(sql).setCode(1002).setSqlType(opType.name()).setMsg(SqlParserTool.getSqlEcception(e));
        }
    }

    /***
     * 删除操作
     * @param sql
     * @param type
     * @param opType
     * @param userId
     * @return
     */
    public DbResult delete(String sql, String type, SqlType opType, String userId) {
        long startTime = System.currentTimeMillis();
        try{
        DbResult dbResult=sqlEngine.queryDb(type,CommUtils.getSelectByDeleteSql((Delete) SqlParserTool.getStatement(sql),SpringTools.getJdbcTemplate(type)),0);
        //保存删除日志的明细
        List<KnetSqlLogDetail> details=new ArrayList<>();
        if (dbResult.getCount()>0){
            Map<String, List<OldAndNewVo>> map = new HashMap<>();
            AtomicInteger n = new AtomicInteger(0);
            List<Map<String, Object>> data=dbResult.getData();
            data.stream().forEach(d -> {
                List<OldAndNewVo> list = new ArrayList<>();
                d.forEach((k,v)->{
                    OldAndNewVo vo = new OldAndNewVo();
                    vo.setKey(k).setOldValue(v);
                    list.add(vo);
                });
                map.put(String.valueOf(n.get()), list);
                n.set(n.get() + 1);
            });
            if (null != dbResult && null != dbResult.getMap()&&dbResult.getCount()<=logCount) {
                CommUtils.saveLogDetail(dbResult, details);
            }
        }
            long time = System.currentTimeMillis() - startTime;
            String msg = sqlEngine.deleteAnalysisEngine(type, sql) + "条数据被成功从表"+SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0)+"中删除，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.DELETE.name(), msg, userId),details);
            return new DbResult().setSql(sql).setCode(1000).setSqlType(opType.name()).setMsg(msg);
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return new DbResult().setSql(sql).setCode(1002).setSqlType(opType.name()).setMsg(SqlParserTool.getSqlEcception(e));
        }
    }
}