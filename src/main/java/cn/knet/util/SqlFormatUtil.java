package cn.knet.util;

import cn.knet.conf.SpringTools;
import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 德鲁伊sql格式化错误返回错误信息
 */
@Slf4j
public class SqlFormatUtil {
private static final String CZLXMSG = "目前的操作类型是";

    private SqlFormatUtil() {
    }
    /***
     * 校验sql语法
     * @param sql
     * @return
     */
    public static DbResult sqlFormat(String sql) {
        try {
            if(StringUtils.isBlank(sql)){
                return DbResult.error(1002,"sql语句不能为空！",sql);
            }
            SQLUtils.format(sql, DbType.oracle);
            SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
            SQLStatementParser parser  = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
            parser.parseStatementList();
            return DbResult.success(1000,sql);
        } catch (ParserException e) {
            log.error("SQL:{}转换中发生了错误:{}",sql,e.getCause().getMessage());
            return DbResult.error(1001,"格式错误："+e.getCause().getMessage(),sql);
        }
    }

    /***
     * 校验sql的类型
     * @param sql
     * @return
     */
    public static DbResult sqlTypeFormat(String sql) {
        try {
            SqlType sqlType = SqlParserTool.getSqlType(sql);
            if (null != sqlType && !sqlType.equals(SqlType.NONE)) {
                return DbResult.success(1000, sql);
            }
        } catch (JSQLParserException e) {
            log.error("SQL:{}格式错误{}", sql, e.getCause().getMessage());
            return DbResult.error(1001, "格式错误：" +  e.getCause().getMessage(),sql);
        }
        return DbResult.error(1002, "格式错误，无匹配的操作类型！",sql);
    }
    /***
     * 校验是否是注释语句
     * @param sql
     * @return
     */
    public static DbResult sqlAnnotation(String sql) {
        if(sql.indexOf("--") == -1){
            return DbResult.success(1000, "").setSql(sql);
        }
        sql = sql.replaceAll("\n"," \n");
        String sqlSub = "";
        String[] split = sql.split("\n");
        for (String x:split){
            int xs = x.indexOf("--");
            if(x.indexOf("--") != -1){
                sqlSub += x.substring(0,x.indexOf("--"));
            }else{
                sqlSub += x;
            }
        }
        return DbResult.success(1000, "").setSql(sqlSub);
    }
    /***
     * sql校验
     * 格式  类型 表名等
     * @param sql
     * @param type
     * @return
     */
    public static DbResult sqlVaildate(String sql,String type){
        try {
            DbResult result = SqlFormatUtil.sqlAnnotation(sql);
            sql=result.getSql();
            if (null == result || result.getCode() != 1000) {
                return DbResult.error(1002, "注释语句不执行！" + result.getMsg(), sql);
            }
            result = SqlFormatUtil.sqlTypeFormat(sql);
            if (null == result || result.getCode() != 1000) {
                return DbResult.error(1002, "sql校验出错！" + result.getMsg(), sql);
            }
            result = SqlFormatUtil.sqlFormat(sql);
            if (null == result || result.getCode() != 1000) {
                return DbResult.error(1002, "sql校验出错！" + result.getMsg(), sql);
            }
            Statement statement = SqlParserTool.getStatement(sql);
            if (null == statement) {
                return DbResult.error(1002, "sql格式有问题！", sql);
            }
            SqlType sqlType = SqlParserTool.getSqlType(sql);
            if(sqlType.equals(SqlType.COMMENT)){
                sqlType=SqlType.CREATETABLE;
            }
            if(sqlType.equals(SqlType.SELECT)){
                return DbResult.success(1000,"校验成功！").setSqlType(sqlType.name());
            }
            List<String> tables = SqlParserTool.getTableList(statement);
            if (tables.isEmpty()) {
                return DbResult.error(1002, "表名获取失败！", sql).setSqlType(sqlType.name());
            }
            if (tables.size() > 1) {
                return DbResult.error(1002, "目前只支持单表操作！", sql).setSqlType(sqlType.name());
            }

            if (sqlType.equals(SqlType.DROP)) {
                int count = SpringTools.getJdbcTemplate(type).queryForObject("SELECT count(*) FROM " + tables.get(0).toUpperCase(), int.class);
                if (count > 0) {
                    log.error("表操作校验出错：数据表" + tables.get(0).toUpperCase() + "中有数据，无法执行此操作！");
                    return DbResult.error(1002, "表操作校验出错：数据表" + tables.get(0).toUpperCase() + "中有" + count + "条数据，无法执行此操作！", sql).setSqlType(sqlType.name());
                }
            }
            return DbResult.success(1000, tables.get(0).toUpperCase()).setSqlType(sqlType.name());
        } catch (Exception e) {
        log.error("校验出错:校验出错{}", SqlParserTool.getSqlEcception(e));
        return DbResult.error(1002,"校验出错:"+SqlParserTool.getSqlEcception(e), sql);
    }
    }
}
