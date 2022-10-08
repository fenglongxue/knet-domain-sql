package cn.knet.util;

import cn.knet.dao.JdbcDao;
import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLogDetail;
import cn.knet.vo.OldAndNewVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CommUtils {
    private CommUtils() {
    }

    /***
     * 包装更新和删除前后的数据
     * @param dbResult
     */
    public static List<KnetSqlLogDetail> saveLogDetail(DbResult dbResult,boolean isPl,String sql,String sqlType,String msg){
        List<KnetSqlLogDetail> details=new ArrayList<>();
        List<Map<String, Object>> oldData = getUpdateValueByType(dbResult.getMap(), false);//更新前的数据
        List<Map<String, Object>> newData = getUpdateValueByType(dbResult.getMap(), true);//更新后的数据
        if(!oldData.isEmpty()){
            for (int i = 0; i < oldData.size(); i++) {
                KnetSqlLogDetail logDetail=new KnetSqlLogDetail();
                Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
                logDetail.setOld(gson.toJson(oldData.get(i)));
                if(!newData.isEmpty()&&sqlType.equals(SqlType.UPDATE.name())){
                    logDetail.setNow(gson.toJson(newData.get(i)));
                }
                if(isPl){
                    logDetail.setOpType(sqlType);
                    logDetail.setSqlResu(msg);
                    logDetail.setSql(sql.replaceFirst("\n",""));
                }
                details.add(logDetail);
            }
        }else if(isPl){
            KnetSqlLogDetail logDetail=new KnetSqlLogDetail();
            logDetail.setSql(sql.replaceFirst("\n",""));
            details.add(logDetail);
        }
        return details;
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
                v.forEach(l ->objectMap.put(l.getKey(), isNew ? l.getNewValue() : l.getOldValue()));
                list.add(objectMap);
            });
        } catch (Exception e) {
            log.error("操作执行出错{}",SqlParserTool.getSqlEcception(e));
        }
        return list;
    }
    /***
     * 获取update的查询sql
     * @param updateStatement
     * @return
     */
    public static String getSelectByUpdateSql(Update updateStatement, JdbcTemplate jdbcTemplate) {
        log.info("获取update的查询sql");
        try {
            StringBuilder selectSql = new StringBuilder("select ");
            String table = updateStatement.getTable().getName();
            selectSql.append(JdbcDao.getClumnsForUpdate(updateStatement, jdbcTemplate, table)).append(" from " + table);
            Expression where = updateStatement.getWhere();
            if (null!=where) {
                if(null != updateStatement.getTable().getAlias()){
                    String where2=where.toString().replaceFirst(updateStatement.getTable().getAlias().getName()+"\\.","");
                    selectSql.append(" where " +where2);//去掉别名
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

    /***
     * 获取Delete的查询sql
     * @param deleteStatement
     * @return
     */
    public static String getSelectByDeleteSql(Delete deleteStatement) {
        log.info("获取delete的查询sql");
        try {
            String table = deleteStatement.getTable().getName();
            StringBuilder selectSql = new StringBuilder("select * from "+table);
            Expression where = deleteStatement.getWhere();
            if (null!=where) {
                if((null != deleteStatement.getTable().getAlias())){
                    String where2=where.toString().replaceFirst(deleteStatement.getTable().getAlias().getName()+"\\."," ");
                    selectSql.append(" where " +where2);//去掉别名
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
    public static DbResult initUpdateForSelect(String sql, SqlType sqlType, DbResult dbResult) {
        try {
            Update updateStatement = (Update) SqlParserTool.getStatement(sql);
            Map<String, List<OldAndNewVo>> map = new HashMap<>();
            AtomicInteger n = new AtomicInteger(0);
            List<Map<String, Object>> data=dbResult.getData();
            data.stream().forEach(d -> {
                    List<OldAndNewVo> list = new ArrayList<>();
                    d.forEach((k,v)->{
                        OldAndNewVo vo = new OldAndNewVo();
                        vo.setKey(k).setOldValue(v).setNewValue(v);
                        List<Expression> expressionList=updateStatement.getExpressions();//获取所有的set是表达式类型的值
                        List<Column> columnList=updateStatement.getColumns();
                        if(!columnList.isEmpty()){
                            for (int i = 0; i <columnList.size() ; i++) {
                                if(columnList.get(i).getColumnName().toUpperCase().equals(k)){
                                    vo.setUpdate(true);
                                    String newValue=expressionList.get(i).toString();
                                    vo.setNewValue(null!=newValue?newValue.replace("'",""):newValue);
                                }
                            }
                        }
                        list.add(vo);
                    });
                 map.put(String.valueOf(n.get()), list);
                n.set(n.get() + 1);
            });
            String msg = "本次预更新操作共查询出"+dbResult.getCount()+"条数据"+(dbResult.getCount()>100?"其中前100条如下":"");
            log.info("sql{}:"+msg,sql);
            return dbResult.setCode(1000).setMsg(msg).setData(null).setMap(map).setSql(sql).setSqlType(sqlType.name());
        } catch (JSQLParserException e) {
            log.error("查询sql:{}执行出错{}", sql,SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002, e.getCause().getMessage(), sql).setSqlType(sqlType.name());
        }
    }

    public static Map<String, List<OldAndNewVo>> initOldMap(DbResult dbResult) {
        Map<String, List<OldAndNewVo>> map = new HashMap<>();
        AtomicInteger n = new AtomicInteger(0);
        List<Map<String, Object>> data = dbResult.getData();
        data.stream().forEach(d -> {
            List<OldAndNewVo> list = new ArrayList<>();
            d.forEach((k, v) -> {
                OldAndNewVo vo = new OldAndNewVo();
                vo.setKey(k).setOldValue(v);
                list.add(vo);
            });
            map.put(String.valueOf(n.get()), list);
            n.set(n.get() + 1);
        });
        return map;
    }
 /*  public static void main(String args[]) {
        String Str =new String("update knet_registrant_rm t set t.address='河北',t.mobile='123' where t.registrant_id='linyurt'");
        System.out.println("返回值 2:" +Str);
        Str=Str.replaceFirst("t\\.", "" );
        System.out.println("返回值 3:" +Str);
    }*/
}
