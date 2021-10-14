package cn.knet.util;

import cn.knet.vo.DbResult;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 德鲁伊sql格式化错误返回错误信息
 */
@Slf4j
public class SqlFormatUtil {
    public static DbResult sqlFormat(String sql) {
        try {
            SQLUtils.format(sql, DbType.oracle);
            SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
            SQLStatementParser parser  = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
            parser.parseStatementList();
            return DbResult.success(1000,sql);
        } catch (ParserException e) {
            log.error("SQL:{}转换中发生了错误:{}",sql,e.getMessage());
            return DbResult.error(1001,"格式错误："+e.getMessage());
        }
    }
    public static DbResult sqlFormat(String[] sqls) {
        for (String sql:sqls) {
            DbResult dbResult=sqlFormat(sql);
            if(dbResult.getCode()!=1000)
                return dbResult;
        }
        return DbResult.success();
    }
}
