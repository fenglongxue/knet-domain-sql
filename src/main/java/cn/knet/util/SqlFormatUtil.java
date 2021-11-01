package cn.knet.util;

import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;

/**
 * 德鲁伊sql格式化错误返回错误信息
 */
@Slf4j
public class SqlFormatUtil {
    public static DbResult sqlFormat(String sql) {
        try {
            if(StringUtils.isBlank(sql)){
                return DbResult.error(1002,"sql语句不能为空！");
            }
            SQLUtils.format(sql, DbType.oracle);
            SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
            SQLStatementParser parser  = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
            parser.parseStatementList();
            return DbResult.success(1000,sql);
        } catch (ParserException e) {
            log.error("SQL:{}转换中发生了错误:{}",sql,e.getCause().getMessage());
            return DbResult.error(1001,"格式错误："+e.getCause().getMessage());
        }
    }
    public static DbResult sqlTypeFormat(String sql) {
        try {
            SqlType sqlType = SqlParserTool.getSqlType(sql);
            if (null != sqlType && !sqlType.equals(SqlType.NONE)) {
                return DbResult.success(1000, sql);
            }
        } catch (JSQLParserException e) {
            log.error("SQL:{}格式错误{}", sql, e.getCause().getMessage());
            return DbResult.error(1001, "格式错误：" +  e.getCause().getMessage());
        }
        return DbResult.success(1002, "格式错误，无匹配的操作类型！");
    }
    public static DbResult sqlValidate(String[] sqls) {
        for (String sql:sqls) {
            DbResult result=sqlValidate(sql);
            if(result.getCode()!=1000)
                return DbResult.error(1002,sql+"格式错误:"+result.getMsg());
        }
        return DbResult.success();
    }
    public static DbResult sqlValidate(String sql) {
        DbResult result=SqlFormatUtil.sqlTypeFormat(sql);
        if(null!=result&&result.getCode()==1000)
            result=SqlFormatUtil.sqlFormat(sql);
        return result;
    }
}
