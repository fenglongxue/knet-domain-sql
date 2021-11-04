package cn.knet.util;

import cn.knet.conf.SpringTools;
import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import lombok.SneakyThrows;
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
     * sql校验-包括sql语法和sql的类型
     * @param sqls
     * @return
     */
    public static DbResult sqlValidate(String[] sqls) {
        for (String sql:sqls) {
            DbResult result=sqlValidate(sql);
            if(null!=result&&result.getCode()!=1000){
                return DbResult.error(1002,sql+"格式错误:"+result.getMsg(),sql);
            }
        }
        return DbResult.success();
    }
    public static DbResult sqlValidate(String sql) {
        DbResult result=SqlFormatUtil.sqlTypeFormat(sql);
        if(null!=result&&result.getCode()==1000)
            result=SqlFormatUtil.sqlFormat(sql);
        return result;
    }
    @SneakyThrows
    public static DbResult commonVaildate(String sql){
        Statement statement=SqlParserTool.getStatement(sql);
        if(null==statement){
            return DbResult.error(1002, "sql格式有问题！",sql);
        }
        List<String> tables=SqlParserTool.getTableList(statement);
        if(tables.isEmpty()){
            return DbResult.error(1002, "表名获取失败！",sql);
        }
        if(tables.size()>1){
            return DbResult.error(1002, "目前只支持单表操作！",sql);
        }
        return DbResult.success(1000,tables.get(0));
    }
    /***
     * 更新操作的校验
     * @param sqls
     * @param isPL
     * @return
     */
    public static DbResult updateValidate(String[] sqls,Boolean isPL){
        String table = null;
        for (String sql:sqls) {
            log.info("更新执行的sql:{}", sql);
            try {
                SqlType sqlType= SqlParserTool.getSqlType(sql);
                if(!sqlType.equals(SqlType.UPDATE)) return DbResult.error(1002, "更新校验出错:"+sql+CZLXMSG+sqlType+",不是更新数据类型，不支持！",sql);
                DbResult dbResult=commonVaildate(sql);
                if(null==dbResult||dbResult.getCode()!=1000) return DbResult.error(1002, "更新校验出错",sql);
                if(Boolean.TRUE.equals(isPL)){
                    table= StringUtils.isBlank(table)?dbResult.getMsg():table;//目前只支持单表
                    if(!table.equals(dbResult.getMsg())) return DbResult.error(1002, "更新校验出错:目前批量更新只支持同一张表的操作！",sql);
                }else{
                    table=dbResult.getMsg();
                }
            } catch (JSQLParserException e) {
                log.error("更新校验出错:校验出错{}", SqlParserTool.getSqlEcception(e));
                return DbResult.error(1002,"更新校验出错:"+SqlParserTool.getSqlEcception(e), sql);
            }
        }
        return DbResult.success(1000,table);
    }

    /***
     * 表操作的校验
     * @param sql
     * @param type
     * @return
     */
    public static DbResult alertValidate(String sql,String type) {
                log.info("表操作执行的sql:{}", sql);
                try {
                SqlType sqlType=SqlParserTool.getSqlType(sql);
                if(!(sqlType.equals(SqlType.COMMENT) ||sqlType.equals(SqlType.ALTER)||sqlType.equals(SqlType.DROP)||sqlType.equals(SqlType.CREATETABLE))){
                    return DbResult.error(1002, sql+"表操作校验出错：目前的操作类型是"+sqlType+",不是表操作类型，不支持！",sql);
                }
                DbResult dbResult=commonVaildate(sql);
                if(null==dbResult||dbResult.getCode()!=1000) return DbResult.error(1002, "表操作校验出错",sql);
               String table=dbResult.getMsg();
                if (sqlType.equals(SqlType.DROP)) {
                    int count= SpringTools.getJdbcTemplate(type).queryForObject("SELECT count(*) FROM "+table, int.class);
                    if (count>0) {
                        log.error("表操作校验出错：数据表" + table + "中有数据，无法执行此操作！");
                        return DbResult.error(1002, "表操作校验出错：数据表" + table + "中有"+count+"条数据，无法执行此操作！",sql);
                    }
                }
                } catch (Exception e) {
                    log.error("表操作校验出错：执行出错{}", SqlParserTool.getSqlEcception(e));
                    return DbResult.error(1002,SqlParserTool.getSqlEcception(e),sql);
                }
        return DbResult.success(1000,"表操作校验通过！");
    }

    /***
     * 插入操作的校验
     * @param sql
     * @return
     */
    public static DbResult insertValidate(String sql) {
        try {
            log.info("插入执行的sql{}", sql);
            SqlType sqlType=SqlParserTool.getSqlType(sql);
            if(!sqlType.equals(SqlType.INSERT)){
                return DbResult.error(1002, sql+CZLXMSG+sqlType+",不是插入类型，不支持！",sql);
            }
            DbResult dbResult=commonVaildate(sql);
            if(null==dbResult||dbResult.getCode()!=1000) return DbResult.error(1002, "插入操作校验出错",sql);
        } catch (Exception e) {
            log.error("插入操作校验出错{}", SqlParserTool.getSqlEcception(e));
            return DbResult.error(1002,SqlParserTool.getSqlEcception(e),sql);
        }
        return DbResult.success(1000,"插入操作校验通过！");
    }

    /***
     * 查询操作校验
     * @param sql
     * @return
     */
    public static DbResult queryValidate(String sql) {
        try{
            SqlType sqlType = SqlParserTool.getSqlType(sql);
            if (!sqlType.equals(SqlType.SELECT)) {
                return DbResult.error(1002, sql + CZLXMSG + sqlType + ",不是查询数据类型，不支持！",sql);
            }
        } catch (Exception e) {
            log.error("查询操作校验出错{}", SqlParserTool.getSqlEcception(e),sql);
            return DbResult.error(1002,SqlParserTool.getSqlEcception(e));
        }
        return DbResult.success(1000,"查询操作校验通过！");
    }
}
