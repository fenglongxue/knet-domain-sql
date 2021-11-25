package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.engine.SqlEngine;
import cn.knet.enums.SqlType;
import cn.knet.util.CommUtils;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlLogDetail;
import cn.knet.vo.KnetSqlSelect;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/***
 * 运行服务
 */
@Service
@Slf4j
public class RunService {
    @Resource
    SqlEngine sqlEngine;
    @Resource
    LogEngineService logEngineService;
    @Resource
    UpdateService updateService;
    private int logCount = 50;//日志只存小于50条的数据，大于100条不存

    /***
     * 运行操作：具体执行那个方法
     * @param type
     * @param pageNumber
     * @return
     * @throws Exception
     */
    public DbResult running(String sql, String type, int pageNumber, String userId) {
        try {
            SqlType opType = SqlParserTool.getSqlType(sql);
            switch (opType) {
                case SELECT:
                    return select(sql,type, pageNumber,opType,userId);
                case UPDATE:
                    return updateForSelect(sql, type, SqlType.UPDATESELECT);
                case INSERT:
                    return insert(sql, type, opType, userId);
                case DELETE:
                    return deleteForSelect(sql, type, SqlType.DELETESELECT);
                case COMMENT:
                    return alert(sql, type, SqlType.CREATETABLE,userId);
                case ALTER:
                    return alert(sql, type, opType, userId);
                case CREATETABLE:
                    return alert(sql, type, opType, userId);
                case DROP:
                    return alert(sql, type, opType, userId);
                default:
                    return DbResult.error(1002, opType + "操作类型不支持！", sql);
            }
        } catch (JSQLParserException e) {
            return DbResult.error(1002, "出现异常，" + SqlParserTool.getSqlEcception(e), sql);
        }
    }

    /***
     * 查询方法
     * @param sql
     * @param pageNumber
     * @return
     */
    public DbResult select(String sql, String type, int pageNumber, SqlType sqlType,String userId) {
        try{
            long startTime = System.currentTimeMillis();
            DbResult dbResult = sqlEngine.queryDb(type, sql, pageNumber);
            if (dbResult.getCode()!=1000||dbResult.getData().isEmpty()) {
                return dbResult.setSql(sql).setSqlType(sqlType.name());
            }
            List<String> listKey = new ArrayList<>();
            dbResult.getData().get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            long time = System.currentTimeMillis() - startTime;
            String msg = "执行" + SqlType.SELECT.name() + "操作成功,共查询到" + dbResult.getCount() + "条数据，执行时间" + time + "毫秒。";
            log.info("sql{}:" + msg, sql);
            //保存日志
            logEngineService.logSelectSava(new KnetSqlSelect().setSql(sql).setUserId(userId).setType(type).setSqlResu("执行" + SqlType.SELECT.name()+"操作成功,共查询到" +dbResult.getData().size() + "条数据，执行时间" + time + "毫秒。").setSqlDetail(dbResult.getData()).setTitle(listKey));
            return dbResult.setMsg(msg).setSql(sql).setTitle(listKey).setSqlType(sqlType.name());
        }catch (Exception e){
            log.error("查询sql:{}执行出错{}", sql, SqlParserTool.getSqlEcception(e));
            return new DbResult().setCode(1002).setSql(sql).setMsg(SqlParserTool.getSqlEcception(e)).setSqlType(sqlType.name());
        }

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
        long startTime = System.currentTimeMillis();
        try {
            String selectSql = CommUtils.getSelectByUpdateSql((Update) SqlParserTool.getStatement(sql), SpringTools.getJdbcTemplate(type));
            int total=sqlEngine.queryCount(type,selectSql);
            if(total<0){
                return new DbResult().setCode(1002).setSql(sql).setMsg("将更新数据条数为0，请核对").setSqlType(sqlType.name());
            }
            //只取前100条展示
            DbResult dbResult = sqlEngine.queryDb(type, selectSql, -2);//-2是按钮更新查询显示的条数来显示
            if (dbResult.getCount()==0)
                return dbResult.setCode(1002).setData(null).setSql(sql).setMsg("请核对"+dbResult.getMsg()).setSqlType(sqlType.name());
            List<String> listKey = new ArrayList<>();
            dbResult.getData().get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            dbResult = CommUtils.initUpdateForSelect(sql, sqlType, dbResult);
            long time = System.currentTimeMillis() - startTime;
            String msg = dbResult.getMsg() + "执行时间" + time + "毫秒。";
            log.info("sql{}:" + msg, sql);
            return dbResult.setMsg(msg).setTitle(listKey).setSql(sql).setSqlType(sqlType.name());
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql, SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, e.getCause().getMessage(), sql).setSqlType(sqlType.name());
        }
    }

    /****
     * 表操作
     * @param sql
     * @return
     */
    public DbResult alert(String sql, String type, SqlType sqlType, String userId) {
        log.info("表操作执行的sql:{}", sql);
        try {
            DbResult dbResult=updateService.alert(sql,type,sqlType,false);
            if(dbResult.getCode()==1000){
                logEngineService.logSava(new KnetSqlLog(sql,type, sqlType.name(),dbResult.getMsg(), userId),new ArrayList<KnetSqlLogDetail>());
            }
            return dbResult;
        } catch (Exception e) {
            log.error("sql:{}表操作执行出错{}", sql, SqlParserTool.getSqlEcception(e));
            return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg(SqlParserTool.getSqlEcception(e));
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
    public DbResult insert(String sql,String type,SqlType sqlType,String userId) {
        try {
            DbResult dbResult=updateService.insert(sql,type,sqlType,false);
            if(dbResult.getCode()==1000){
                logEngineService.logSava(new KnetSqlLog(sql, type, SqlType.INSERT.name(), dbResult.getMsg(), userId), new ArrayList<KnetSqlLogDetail>());
            }
            return dbResult;
        } catch (Exception e) {
            log.error("表插入sql:{}执行出错{}", sql, SqlParserTool.getSqlEcception(e));
            return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg(SqlParserTool.getSqlEcception(e));
        }
    }

    /***
     * 删除前查找
     * @param sql
     * @param type
     * @param sqlType
     * @return
     */
    public DbResult deleteForSelect(String sql, String type, SqlType sqlType) {
        long startTime = System.currentTimeMillis();
        try {
            String selectSql=CommUtils.getSelectByDeleteSql((Delete) SqlParserTool.getStatement(sql));
            int total=sqlEngine.queryCount(type,selectSql);
            if(total<0){
                return new DbResult().setCode(1002).setSql(sql).setMsg("将删除的数据为0条，请核对").setSqlType(sqlType.name());
            }
            //删除也只取前100条显示
            DbResult dbResult=sqlEngine.queryDb(type,selectSql,-2);
            if(dbResult.getCode()!=1000||dbResult.getData().isEmpty()){
                log.error("查询sql:{}执行出错{}", sql,"将删除的数据为0条");
                return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg("请核对,"+dbResult.getMsg());
            }
            List<String> listKey = new ArrayList<>();
            dbResult.getData().get(0).forEach((x, y) -> {
                listKey.add(x);
            });
            dbResult.setMap(CommUtils.initOldMap(dbResult));
            long time = System.currentTimeMillis() - startTime;
            String msg = "本次删除将有"+total+"条数据从表" + SqlParserTool.getTableList(SqlParserTool.getStatement(sql)).get(0).toUpperCase()+"中删除"+(total>100?"其中前100条如下":"")+"，执行时间"+time+"毫秒。";
            log.info("sql{}:" + msg, sql);
            return dbResult.setSql(sql).setCode(1000).setSqlType(sqlType.name()).setMsg(msg).setMap(dbResult.getMap()).setData(null).setTitle(listKey);
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql, SqlParserTool.getSqlEcception(e));
            return new DbResult().setSql(sql).setCode(1002).setSqlType(sqlType.name()).setMsg(SqlParserTool.getSqlEcception(e));
        }
    }
}
