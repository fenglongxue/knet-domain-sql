package cn.knet.service;

import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.OldAndNewVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 解析sqlServices
 */
@Service
@Slf4j
public class AnalysisEngineService {
    /**
     * 更新前的查找引擎
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public DbResult updateForSelect(JdbcTemplate jdbcTemplate, String sql, int pageNumber,String i) {
        log.info("解析sql为：{}", sql);
        try {
            Update updateStatement = getUpdateStatement(sql);
            DbResult dbResult = queryDb(jdbcTemplate, getSelectSql(updateStatement), pageNumber);
            //没有可设置的数据
            if (dbResult.getCount() == 0 || null == dbResult.getData()) {
                dbResult.setCode(1002).setData(null).setSql(sql);//单个查询统一返回一条sql
                return dbResult;
            }
            Map<String, List<OldAndNewVo>> map = new HashMap<>();
            dbResult.getData().stream().forEach(d -> {
                List<OldAndNewVo> list = new ArrayList<>();
                updateStatement.getColumns().forEach(c -> {
                    if (d.containsKey(c.getColumnName().toUpperCase())) {
                        OldAndNewVo vo = new OldAndNewVo();
                        vo.setKey(c.getColumnName());
                        vo.setNewValue(c.getASTNode().jjtGetLastToken().next.next.image);
                        vo.setOldValue(d.get(c.getColumnName().toUpperCase()));
                        list.add(vo);
                    }
                });
                if(StringUtils.isNotBlank(i)){
                    map.put(i, list);
                }else{
                    map.put(d.get("ROW_ID").toString(), list);
                }

            });
            dbResult.setCode(1000).setMsg("查询成功！").setData(null).setMap(map).setSql(sql);//单个查询统一返回一条sql
            return dbResult;
        } catch (JSQLParserException e) {
            return DbResult.error(1002, e.getCause().getMessage(), sql);
        }
    }
    public DbResult updateForSelectList(JdbcTemplate jdbcTemplate, String sql, int pageNumber,String i){
        return updateForSelect(jdbcTemplate,sql,pageNumber,i);
    }
    /***
     * 获取update的查询sql
     * @param updateStatement
     * @return
     */
    public String getSelectSql(Update updateStatement) {
        StringBuffer selectSql = new StringBuffer("select ");
        updateStatement.getColumns().forEach(x -> {
            selectSql.append(x.getColumnName() + ",");
        });
        if (updateStatement.getColumns().size() > 0) {
            selectSql.deleteCharAt(selectSql.length() - 1);
        }
        String table = updateStatement.getTable().getName();
        table=(null!=updateStatement.getTable().getAlias())?table+" "+updateStatement.getTable().getAlias().toString():table+" ";
        selectSql.append(" from " + table);
        Expression where = updateStatement.getWhere();
        if (where instanceof BinaryExpression) {
            log.info("where 条件为:{}", ((BinaryExpression) where).getLeftExpression());
            selectSql.append(" where " + where.toString());
        }
        log.info("拼接的查询sql为：{}", selectSql.toString());
        return selectSql.toString();
    }

    /**
     * oracle查询默认增加分页增加分页
     *
     * @param sql
     * @param pageNumber
     * @return
     */
    public DbResult queryDb(JdbcTemplate jdbcTemplate, String sql, int pageNumber) {
        try {
            DbResult result = new DbResult();
            log.info("计数sql:{}", SqlParserTool.getCount(sql));
            result.setCount(jdbcTemplate.queryForObject(SqlParserTool.getCount(sql), int.class));
            log.info("分页sql:{}", SqlParserTool.setRowNum(sql, pageNumber));
            List list = jdbcTemplate.queryForList(SqlParserTool.setRowNum(sql, pageNumber));
            if (null != list && list.size() > 0) {
                AtomicReference<AtomicReference<String>> titleAtomic = new AtomicReference<>();
                Map<String, Object> map= (Map<String, Object>) list.get(0);
                map.forEach((k,v)->{
                    titleAtomic.set(new AtomicReference<>(null!=titleAtomic.get()?titleAtomic.get()+","+k:k));
                    });
                result.setData(list).setCode(1000).setMsg("查询成功！").setSql(sql).setTitle(titleAtomic.get().toString().split(","));
            } else {
                result.setMsg("没有查询出符合条件的数据").setCode(1002).setSql(sql);
            }
            return result;
        } catch (Exception e) {
            log.error("操作执行出错{}", e.getCause().getMessage());
            return DbResult.error(1002, e.getCause().getMessage(),sql);
        }

    }

    /***
     * 表操作
     * @param jdbcTemplate
     * @param sql
     * @return
     */
    public DbResult alertAnalysisEngine(JdbcTemplate jdbcTemplate, String sql) {
        log.info("表操作执行的sql{}", sql);
        try {
            String table = getUpdateStatement(sql).getTable().getName();
            if (sql.toUpperCase().contains("DROP")) {
                DbResult result = queryDb(jdbcTemplate, sql, 50);//只是为了查询有没有数据
                if (result.getCount() != 0) {
                    log.error("数据表" + table + "中有数据，无法执行此操作！");
                    return DbResult.error(1002, "数据表" + table + "中有数据，无法执行此操作！");
                }
            }
            jdbcTemplate.execute(sql);
            log.info(table + "表操作执行成功");
            return DbResult.success(table + "表操作执行成功");
        } catch (Exception e) {
            log.error("表操作执行出错{}", e.getCause().getMessage());
            return DbResult.error(1002, e.getCause().getMessage());
        }
    }

    /***
     * 更新操作
     * @param jdbcTemplate
     * @param sql
     * @return
     */
    public DbResult updateAnalysisEngine(JdbcTemplate jdbcTemplate, String sql) {
        log.info("更新执行的sql{}", sql);
        try {
            String table = getUpdateStatement(sql).getTable().getName();
            int i = jdbcTemplate.update(sql);
            log.info("表" + table + "一共更新数据{}条", i);
            return DbResult.success("表" + table + "一共更新数据" + i + "条", sql);
        } catch (Exception e) {
            log.error("更新出错{}", e.getCause().getMessage());
            return DbResult.error(1002, e.getCause().getMessage(), sql);
        }
    }

    /***
     * 获取UpdateStatement对象
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    public Update getUpdateStatement(String sql) throws JSQLParserException {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Statement statement = pm.parse(new StringReader(sql));
        return (Update) statement;
    }
}
