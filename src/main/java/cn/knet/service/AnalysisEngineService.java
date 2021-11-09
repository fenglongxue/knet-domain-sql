package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.enums.SqlType;
import cn.knet.util.JdbcUtil;
import cn.knet.util.SqlFormatUtil;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlLogDetail;
import cn.knet.vo.OldAndNewVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解析sqlServices
 */
@Service
@Slf4j
public class AnalysisEngineService {
    @Resource
    private LogEngineService logEngineService;
    private int logCount=50;//日志只存小于50条的数据，大于100条不存
    /**
     * 更新前的查找引擎
     *i=n 只取前n条
     * i为空不限制
     * @param sql
     * @return
     * @throws Exception
     */
    public DbResult updateForSelect(String type, String sql,int i) {
        long startTime = System.currentTimeMillis();
        log.info("解析sql为：{}", sql);
        DbResult dbResult = new DbResult();
        try {
            Update updateStatement = (Update) SqlParserTool.getStatement(sql);
            String selectSql = getSelectByUpdateSql(updateStatement,SpringTools.getJdbcTemplate(type));
            dbResult = queryDb(type, selectSql);
            if (dbResult.getCount() == 0) return dbResult.setCode(1002).setData(null).setSql(sql).setMsg(dbResult.getMsg());
            Map<String, List<OldAndNewVo>> map = new HashMap<>();
            AtomicInteger n = new AtomicInteger(0);
            dbResult.getData().stream().forEach(d -> {
                if(i==0||n.get()<=i){
                    List<OldAndNewVo> list = new ArrayList<>();
                    d.forEach((k,v)->{
                        OldAndNewVo vo = new OldAndNewVo();
                        vo.setKey(k).setOldValue(v).setNewValue(v);
                        updateStatement.getColumns().forEach(c -> {
                            if (k.equalsIgnoreCase(c.getColumnName())) {
                                vo.setUpdate(true);
                                String value=c.getASTNode().jjtGetLastToken().next.next.image;
                                vo.setNewValue(StringUtils.isNotBlank(value)?value.replace("'", ""):"");
                            }
                        });
                        list.add(vo);
                    });
                    map.put(String.valueOf(n.get()), list);
                }
                n.set(n.get() + 1);
            });
            long time = System.currentTimeMillis() - startTime;
            String msg ="本次更新将影响"+dbResult.getCount()+"条数据,其中前100条如下。执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            return dbResult.setCode(1000).setMsg(msg).setData(null).setMap(map).setSql(sql);//单个查询统一返回一条sql
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, e.getCause().getMessage(), sql);
        }
    }
    /**
     * 查询带分页
     * @param sql
     * @param pageNumber
     * @return
     */
    public DbResult queryDb(String type, String sql, int pageNumber) {
        long startTime = System.currentTimeMillis();
        try {
            DbResult result = new DbResult();
            log.info("计数sql:{}", SqlParserTool.getCount(sql));
            JdbcTemplate jdbcTemplate = SpringTools.getJdbcTemplate(type);
            result.setCount(jdbcTemplate.queryForObject(SqlParserTool.getCount(sql), int.class));
            log.info("分页sql:{}", SqlParserTool.setRowNum(sql, pageNumber));
            List<Map<String, Object>> list = jdbcTemplate.queryForList(SqlParserTool.setRowNum(sql, pageNumber));
            if (list.isEmpty()) {
                return result.setMsg("没有查询到符合条件的数据").setCode(1002).setSql(sql);
            }
            List<String> listKey=new ArrayList<>();
            list.get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            long time = System.currentTimeMillis() - startTime;
            String msg = "执行" + SqlType.SELECT.name() + "操作成功,共查询到" + result.getCount() + "条数据，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            return result.setData(list).setCode(1000).setMsg(msg).setSql(sql).setTitle(listKey);
        } catch (Exception e) {
            log.error("sql:{}操作执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, SqlParserTool.getSqlEcception(e), sql);
        }

    }
    public DbResult queryDb(String type, String sql) {
        log.info("查询的sql:{}", sql);
        try {
            JdbcTemplate jdbcTemplate = SpringTools.getJdbcTemplate(type);
            int count = jdbcTemplate.queryForObject(SqlParserTool.getCount(sql), int.class);
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.isEmpty()) {
                return new DbResult().setMsg("没有查询到符合条件的数据").setCode(1002).setSql(sql);
            }
            List<String> listKey =new ArrayList<>();
            list.get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            return new DbResult().setCount(count).setData(list).setCode(1000).setMsg("查询成功！").setSql(sql).setTitle(listKey);
        } catch (Exception e) {
            log.error("sql:{}操作执行出错{}",sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, SqlParserTool.getSqlEcception(e), sql);
        }

    }

    /***
     * 表操作
     * @param type
     * @param sql
     * @param userId
     * @return
     */
    public DbResult alertAnalysisEngine(String type, String sql, String userId) {
        long startTime = System.currentTimeMillis();
        log.info("表操作执行的sql:{}", sql);
        try {
            SqlType sqlType = SqlParserTool.getSqlType(sql);
            List<String> tables = SqlParserTool.getTableList(SqlParserTool.getStatement(sql));
            String table = tables.get(0);//目前只支持单表
            SpringTools.getJdbcTemplate(type).execute(sql);
            long time = System.currentTimeMillis() - startTime;
            String msg = "表" + table + "执行" + sqlType.name()+"操作成功"+table+"，执行时间"+time+"毫秒。";
            log.info("sql{}:"+msg,sql);
            logEngineService.logSava(new KnetSqlLog(sql, type,sqlType.name(), msg, userId),new ArrayList<KnetSqlLogDetail>());
            return DbResult.success(msg, sql);
        } catch (Exception e) {
            log.error("sql:{}表操作执行出错{}",sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, SqlParserTool.getSqlEcception(e),sql);
        }
    }

    /***
     * 更新操作
     * @param type
     * @param sql
     * @return
     */
    @Transactional
    public DbResult updateAnalysisEngine(String type, String sql, String userId) {
        long startTime = System.currentTimeMillis();
        log.info("更新执行的sql:{}", sql);
        try {
            DbResult dbResult = updateForSelect(type, sql,0);
            if (null == dbResult.getMap()) {
                return DbResult.error(1002, dbResult.getMsg(), sql);
            }
            List<KnetSqlLogDetail> details=new ArrayList<>();
            if(dbResult.getCount()<=logCount){
                saveLogDetail(dbResult, details);
            }
            int i = SpringTools.getJdbcTemplate(type).update(sql);
            long time = System.currentTimeMillis() - startTime;
            List<String> tables = SqlParserTool.getTableList(SqlParserTool.getStatement(sql));
            String msg = i + "条数据被成功更新到表" + tables.get(0) + "，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            if(i>0){
                logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.UPDATE.name(),msg, userId),
                        details);
            }
            return DbResult.success(msg, sql);
        } catch (Exception e) {
            log.error("sql:{}更新出错{}",sql, SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, "更新出错" + SqlParserTool.getSqlEcception(e), sql);
        }
    }

    public void saveLogDetail(DbResult dbResult, List<KnetSqlLogDetail> details) {
        List<Map<String, Object>> oldData = getUpdateValueByType(dbResult.getMap(), false);//更新前的数据
        List<Map<String, Object>> newData = getUpdateValueByType(dbResult.getMap(), true);//更新后的数据
        KnetSqlLogDetail logDetail=new KnetSqlLogDetail();
        oldData.forEach(o->{
            logDetail.setOld(com.alibaba.fastjson.JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(o)).toString());
            newData.forEach(n->logDetail.setNow(com.alibaba.fastjson.JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(n)).toString()));
            details.add(logDetail);
        });
    }

    /***
     * 批量更新操作
     * @param type
     * @param sqls
     * @param userId
     * @return
     */
    public List<DbResult> updateForPLAnalysisEngine(String type, String[] sqls,String userId) {
        List<DbResult> list = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        for (String sql : sqls) {
            log.info("更新执行的sql:{}", sql);
            try {
                DbResult dbResult=SqlFormatUtil.updateValidate(sql);
                if(SqlFormatUtil.sqlValidate(sql).getCode()==1000&&dbResult.getCode()==1000){
                    String table=dbResult.getMsg();
                    dbResult = updateForSelect(type, sql,0);
                    List<KnetSqlLogDetail> details=new ArrayList<>();
                    if (null != dbResult && null != dbResult.getMap()&&dbResult.getCount()<=logCount) {
                        saveLogDetail(dbResult, details);
                    }
                    int i=SpringTools.getJdbcTemplate(type).update(sql);
                    long time = System.currentTimeMillis() - startTime;
                    String msg = i + "条数据被成功更新到表" + table+ "执行时间" + time + "毫秒。";
                    log.info("sql{}:"+msg,sql);
                    if(i>0){
                        logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.PIUPDATE.name(),msg, userId),
                                details);
                    }
                    list.add(DbResult.success(msg,sql));
                }
            } catch (Exception e) {
                String msg =  "sql:"+sql + "更新出错（" + SqlParserTool.getSqlEcception(e) + ")";
                log.error("SQl{}更新出错{}",sql, msg);
                list.add(DbResult.error(msg,sql));
            }
        }
        return list;
    }

    /***
     * 插入操作
     * @param type
     * @param sql
     * @param userId
     * @return
     */
    public DbResult insertAnalysisEngine(String type, String sql, String userId) {
        long startTime = System.currentTimeMillis();
        log.info("插入执行的sql:{}", sql);
        try {
            int i = SpringTools.getJdbcTemplate(type).update(sql);
            long time = System.currentTimeMillis() - startTime;
            List<String> tables = SqlParserTool.getTableList(SqlParserTool.getStatement(sql));
            String table = tables.get(0);//目前只支持单表
            String msg = i + "条数据被成功插入到表" + table + "，执行时间" + time + "毫秒。";
            log.info(msg);
            logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.INSERT.name(), msg, userId),new ArrayList<KnetSqlLogDetail>());
            return DbResult.success(msg, sql);
        } catch (Exception e) {
            log.error("表插入sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, SqlParserTool.getSqlEcception(e),sql);
        }
    }
    public DbResult deleteAnalysisEngine(String type, String sql, String userId) {
        long startTime = System.currentTimeMillis();
        log.info("删除执行的sql:{}", sql);
        try {
            DbResult dbResult=deleteForSelect(type,sql);
            List<KnetSqlLogDetail> details=new ArrayList<>();
            if (null != dbResult && null != dbResult.getMap()&&dbResult.getCount()<=logCount) {
                saveLogDetail(dbResult, details);
            }
            int i = SpringTools.getJdbcTemplate(type).update(sql);
            long time = System.currentTimeMillis() - startTime;
            List<String> tables = SqlParserTool.getTableList(SqlParserTool.getStatement(sql));
            String table = tables.get(0);//目前只支持单表
            String msg = i + "条数据被成功从表"+table+"中删除，执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            if(i>0){
                logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.DELETE.name(), msg, userId),details);
            }
            return DbResult.success(msg, sql);
        } catch (Exception e) {
            log.error("表删除sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, SqlParserTool.getSqlEcception(e),sql);
        }
    }
    public DbResult deleteForSelect(String type, String sql) {
        long startTime = System.currentTimeMillis();
        log.info("解析sql为：{}", sql);
        DbResult dbResult = new DbResult();
        try {
            Delete deleteStatement= (Delete) SqlParserTool.getStatement(sql);
            String selectSql = getSelectByDeleteSql(deleteStatement,SpringTools.getJdbcTemplate(type));
            dbResult = queryDb(type, selectSql);
            if (dbResult.getCount() == 0) return dbResult.setCode(1002).setData(null).setSql(sql).setMsg(dbResult.getMsg());
            Map<String, List<OldAndNewVo>> map = new HashMap<>();
            AtomicInteger n = new AtomicInteger(0);
            dbResult.getData().stream().forEach(d -> {
                List<OldAndNewVo> list = new ArrayList<>();
                d.forEach((k,v)->{
                    OldAndNewVo vo = new OldAndNewVo();
                    vo.setKey(k).setOldValue(v);
                    list.add(vo);
                });
                map.put(String.valueOf(n.get()), list);
                n.set(n.get() + 1);
            });
            long time = System.currentTimeMillis() - startTime;
            String msg ="本次删除将影响"+dbResult.getCount()+"条数据,执行时间" + time + "毫秒。";
            log.info("sql{}:"+msg,sql);
            return dbResult.setCode(1000).setMsg(msg).setData(null).setMap(map).setSql(sql);//单个查询统一返回一条sql
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, e.getCause().getMessage(), sql);
        }
    }
    /***
     * 批量更新查询
     * @param type
     * @param sql
     * @return
     */
    public DbResult updateForSelectList(String type, String sql) {
        return updateForSelect(type, sql,1000);
    }

    /***
     * 获取update的查询sql
     * @param updateStatement
     * @return
     */
    public String getSelectByUpdateSql(Update updateStatement,JdbcTemplate jdbcTemplate) {
        log.info("获取update的查询sql");
        try {
            StringBuilder selectSql = new StringBuilder("select ");
            String table = updateStatement.getTable().getName();
            selectSql.append(JdbcUtil.getClumnsForUpdate(updateStatement, jdbcTemplate, table)).append(" from " + table);
            Expression where = updateStatement.getWhere();
            if (where instanceof BinaryExpression) {
                log.info("where 条件为:{}", ((BinaryExpression) where).getLeftExpression());
                if((null != updateStatement.getTable().getAlias())){
                    selectSql.append(" where " +where.toString().replaceAll(updateStatement.getTable().getAlias().getName()+"."," "));//去掉别名
                }else{
                    selectSql.append(" where " + where.toString());
                }
            }
            log.info("获取update的查询sql为：{}", selectSql.toString());
            return selectSql.toString();
        } catch (Exception e) {
            log.error("获取update的查询sql操作执行出错{}", SqlParserTool.getSqlEcception(e));
            return  SqlParserTool.getSqlEcception(e);
        }
    }
    public String getSelectByDeleteSql(Delete deleteStatement, JdbcTemplate jdbcTemplate) {
        log.info("获取delete的查询sql");
        try {
            String table = deleteStatement.getTable().getName();
            StringBuilder selectSql = new StringBuilder("select * from "+table);
            Expression where = deleteStatement.getWhere();
            if (where instanceof BinaryExpression) {
                if((null != deleteStatement.getTable().getAlias())){
                    selectSql.append(" where " +where.toString().replaceAll(deleteStatement.getTable().getAlias().getName()+"."," "));//去掉别名
                }else{
                    selectSql.append(" where " + where.toString());
                }
            }
            log.info("获取delete的查询sql为：{}", selectSql.toString());
            return selectSql.toString();
        } catch (Exception e) {
            log.error("获取delete的查询sql操作执行出错{}", SqlParserTool.getSqlEcception(e));
            return  SqlParserTool.getSqlEcception(e);
        }
    }
    /***
     * 获取新旧值
     * @param map
     * @param isNew
     * @return
     */
    public static List<Map<String, Object>> getUpdateValueByType(Map<String, List<OldAndNewVo>> map, boolean isNew) {
        List<Map<String, Object>> list=new ArrayList<>();
        try {
            map.forEach((k, v) -> {
                Map<String, Object> objectMap = new HashMap<>();
                v.forEach(l -> {
                    objectMap.put(l.getKey(), isNew ? l.getNewValue() : l.getOldValue());
                });
                list.add(objectMap);
            });
        } catch (Exception e) {
            log.error("操作执行出错{}",SqlParserTool.getSqlEcception(e));
        }
        return list;
    }
}
