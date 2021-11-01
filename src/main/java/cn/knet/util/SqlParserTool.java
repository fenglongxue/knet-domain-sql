package cn.knet.util;

import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * jsqlparser解析SQL工具类
 * PlainSelect类不支持union、union all等请使用SetOperationList接口
 *
 */
public class SqlParserTool {



    /**
     * 由于jsqlparser没有获取SQL类型的原始工具，并且在下面操作时需要知道SQL类型，所以编写此工具方法
     * 目前只支持以下几个类型
     * @param sql sql语句
     * @return sql类型，
     * @throws JSQLParserException
     */
    public static SqlType getSqlType(String sql) throws JSQLParserException {
        Statement sqlStmt = CCJSqlParserUtil.parse(sql);
        if (sqlStmt instanceof Alter) {
            return SqlType.ALTER;
        } else if (sqlStmt instanceof CreateTable) {
            return SqlType.CREATETABLE;
        }else if (sqlStmt instanceof Drop) {
            return SqlType.DROP;
        } else if (sqlStmt instanceof Insert) {
            return SqlType.INSERT;
        }  else if (sqlStmt instanceof Select) {
            return SqlType.SELECT;
        } else if (sqlStmt instanceof Update) {
            return SqlType.UPDATE;
        }else {
            return SqlType.NONE;
        }
    }
    public static Statement getStatement(String sql) throws JSQLParserException {
        DbResult result=SqlFormatUtil.sqlValidate(sql);
        if(result.getCode()==1000){
            return CCJSqlParserUtil.parse(sql);
        }
        return null;
    }
    /**
     * 获取Statement
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    public static Statement getStatementByType(String sql) throws JSQLParserException {
        SqlType sqlType = SqlParserTool.getSqlType(sql);
        if(sqlType.equals(SqlType.ALTER)){
         return (Alter) SqlParserTool.getStatement(sql);
       }else if(sqlType.equals(SqlType.SELECT)){
           return (Select) SqlParserTool.getStatement(sql);
        }else if(sqlType.equals(SqlType.CREATETABLE)){
            return (CreateTable) SqlParserTool.getStatement(sql);
        }else if(sqlType.equals(SqlType.DROP)){
            return (Drop) SqlParserTool.getStatement(sql);
        }else if(sqlType.equals(SqlType.INSERT)){
            return (Insert) SqlParserTool.getStatement(sql);
        }else if(sqlType.equals(SqlType.UPDATE)){
            return (Update) SqlParserTool.getStatement(sql);
        }else{
            return null;
        }
    }

    /**
     * 获取tables的表名
     * @param statement
     * @return
     */
    public static List<String> getTableList(Statement statement){
        List<String> tableList=new ArrayList<>();
        if (statement instanceof Drop) {
            Drop drop= (Drop) statement;
            tableList.add(drop.getName().getName());
            return tableList;
        }
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        tableList = tablesNamesFinder.getTableList(statement);
        return tableList;
    }

    /**
     * 获取join层级
     * @param selectBody
     * @return
     */
    public static List<Join> getJoins(SelectBody selectBody){
        if(selectBody instanceof PlainSelect){
            List<Join> joins =((PlainSelect) selectBody).getJoins();
            return joins;
        }
        return new ArrayList<Join>();
    }
    /**
     * 添加分页
     * @param sql
     * @return
     */
    public static String setRowNum(String sql,int pageNumber){
        return "SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM (" + sql +" ) TMP WHERE ROWNUM <="+pageNumber+") WHERE ROW_ID > "+((pageNumber-50)>0?pageNumber-50:0)+"";
    }
    /**
     * 添加分页
     * @param sql
     * @return
     */
    public static String setRowNum(String sql,int pageNumber,int pageSize){
        return "SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM (" + sql +" ) TMP WHERE ROWNUM <="+pageNumber+") WHERE ROW_ID > "+((pageNumber-pageSize)>0?pageNumber-pageSize:0)+"";
    }
    /**
     * 添加分页
     * @param sql
     * @return
     */
    public static String getCount(String sql){
        return "SELECT count(*) FROM ( " + sql +" ) TMP ";
    }
    /**
     * 获取FromItem不支持子查询操作
     * @param selectBody
     * @return
     */
    public static FromItem getFromItem(SelectBody selectBody){
        if(selectBody instanceof PlainSelect){
            FromItem fromItem = ((PlainSelect) selectBody).getFromItem();
            return fromItem;
        }else if(selectBody instanceof WithItem){
            SqlParserTool.getFromItem(((WithItem) selectBody).getSelectBody());
        }
        return null;
    }

    /**
     * 获取子查询
     * @param selectBody
     * @return
     */
    public static SubSelect getSubSelect(SelectBody selectBody){
        if(selectBody instanceof PlainSelect){
            FromItem fromItem = ((PlainSelect) selectBody).getFromItem();
            if(fromItem instanceof SubSelect){
                return ((SubSelect) fromItem);
            }
        }else if(selectBody instanceof WithItem){
            SqlParserTool.getSubSelect(((WithItem) selectBody).getSelectBody());
        }
        return null;
    }

    /**
     * 判断是否为多级子查询
     * @param selectBody
     * @return
     */
    public static boolean isMultiSubSelect(SelectBody selectBody){
        if(selectBody instanceof PlainSelect){
            FromItem fromItem = ((PlainSelect) selectBody).getFromItem();
            if(fromItem instanceof SubSelect){
                SelectBody subBody = ((SubSelect) fromItem).getSelectBody();
                if(subBody instanceof PlainSelect){
                    FromItem subFromItem = ((PlainSelect) subBody).getFromItem();
                    if(subFromItem instanceof SubSelect){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取查询字段
     * @param selectBody
     * @return
     */
    public static List<SelectItem> getSelectItems(SelectBody selectBody){
        if(selectBody instanceof PlainSelect){
            List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
            return selectItems;
        }
        return null;
    }

    public static void main(String[] args) throws JSQLParserException {
        String sql = "select * from (select userid from a) a";
        SqlType sqlType = SqlParserTool.getSqlType(sql);
        if(sqlType.equals(SqlType.SELECT)){
            Select statement = (Select) SqlParserTool.getStatement(sql);
            SubSelect subSelect = SqlParserTool.getSubSelect(statement.getSelectBody());
            System.out.println(subSelect.getSelectBody());
        }
    }
    public static String getSqlEcception(Exception e) {
        if(e instanceof NullPointerException){
            return "空指针异常！";
        }
        String message;
        if(StringUtils.isNotBlank(e.getMessage())){
            message=e.getMessage();
        }else if(StringUtils.isNotBlank(e.getCause().getMessage())){
            message=e.getCause().getMessage();
        }else{
            message="没有获取到异常的具体信息";
        }
        return message;
    }
}
