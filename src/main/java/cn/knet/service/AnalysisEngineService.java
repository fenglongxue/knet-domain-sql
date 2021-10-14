package cn.knet.service;

import cn.knet.util.SqlFormatUtil;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.OldAndNewVo;
import cn.knet.vo.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.parser.Token;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析sqlServices
 */
@Service
@Slf4j
public class AnalysisEngineService {
    @Autowired
    @Qualifier("wzJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("sealJdbcTemplate")
    protected JdbcTemplate  sealJdbcTemplate;
    /**
     * sql正确性校验
     * @param sql
     * @param type
     * @return
     * @throws Exception
     */
    public DbResult SqlCheckEngine(String sql, String type)  throws Exception{
        DbResult dbResult = new DbResult();
        Boolean s = SqlFormatUtil.sqlFormat(sql).getCode()==1000;
        if(s){
            dbResult.setMsg("格式正确！");
            dbResult.setCode(1000);
        }else {
            dbResult.setMsg("格式错误！");
            dbResult.setCode(500);
        }
        log.info("sql校验结果为：{}",s);
        return dbResult;
    }
    /**
     * 修改解析并返回修改前结果集
     * @param sql
     * @param type
     * @return
     * @throws Exception
     */
    public DbResult selectAnalysisEngine(String sql, String type)  throws Exception{
        DbResult dbResult = new DbResult();

        Select Select = (Select)SqlParserTool.getStatement(sql);
        SelectBody selectBody = Select.getSelectBody();
        log.info("解析查询sql为：{}",sql);
        if(SqlParserTool.getLimit(selectBody).isLimitNull()){
            SqlParserTool.setLimit(selectBody,10);
        }
        this.queryDb(sql,type,dbResult,10);
        return dbResult;
    }

    /**
     * 修改解析并返回修改前结果集
     * @param sql
     * @param type
     * @return
     * @throws Exception
     */
    public UpdateResult updateAnalysisEngine(String sql, String type)  throws Exception{
        UpdateResult dbResult = new UpdateResult();
        log.info("解析sql为：{}",sql);
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Statement upStatement = pm.parse( new StringReader(sql));
        //获得Update对象
        Update updateStatement = (Update) upStatement;
        //活的修改值
        StringBuffer getSelectSql = new StringBuffer("select ");
        List<Expression> expressions=updateStatement.getExpressions();
        List<Column> columns = updateStatement.getColumns();
        columns.forEach(x->{
            getSelectSql.append(x.getColumnName()+",");
        });
        if(columns.size()>0){
            getSelectSql.deleteCharAt(getSelectSql.length()-1);
        }
        //获得表名
        log.info("修改表名为"+updateStatement.getTable().getName());
        Table table = updateStatement.getTable();
        updateStatement.getTable().getName();
        getSelectSql.append(" from "+table.getName() +" ");
        //获得where条件表达式
        Expression where = updateStatement.getWhere();
        //初始化接收获得到的字段信息
        StringBuffer allColumnNames = new StringBuffer();
        if(where instanceof BinaryExpression){
            log.info("where 条件为:{}",((BinaryExpression)where).toString());
            getSelectSql.append(" where "+((BinaryExpression)where).toString());
        }
        log.info("拼接的查询sql为：{}",getSelectSql.toString());
        dbResult = this.queryDbByWhere(sql,type,dbResult,10,expressions,columns);
        return dbResult;
    }

    /**
     * oracle查询默认增加分页增加分页
     * @param sql
     * @param type
     * @param result
     * @param pageNumber
     * @return
     */
    public DbResult queryDb(String sql,String type,DbResult result,int pageNumber){
        List list;
        result.setCount(jdbcTemplate.queryForObject(SqlParserTool.getCount(sql),int.class));
        result.setSql(sql);
        if("wz".equals(type)){
            list = jdbcTemplate.queryForList(SqlParserTool.setRowNum(sql,pageNumber));
        }else{
            list = sealJdbcTemplate.queryForList(SqlParserTool.setRowNum(sql,pageNumber));
        }
        if(null!=list&&list.size()>0){
            result.setData(list);
            result.setMsg("成功执行");
            result.setCode(1000);
        }else {
            result.setMsg("未解析到相关数据");
            result.setCode(500);
        }
        return result;
    }
    public UpdateResult queryDbByWhere(String sql, String type, DbResult result, int pageNumber, List<Expression> expressions, List<Column> columns){
        DbResult dbResult=queryDb(sql,type,result,pageNumber);
        Map<String, List<OldAndNewVo>> map=new HashMap<>();
        dbResult.getData().stream().forEach(d->{
            List<OldAndNewVo> list=new ArrayList<>();
          columns.forEach(c->{
              if(d.containsKey(c.getColumnName().toUpperCase())){
                  OldAndNewVo vo=new OldAndNewVo();
                  vo.setKey(c.getColumnName());
                  vo.setNewValue(c.getASTNode().jjtGetLastToken().next.next.image);
                  vo.setOldValue(d.get(c.getColumnName().toUpperCase()));
                  list.add(vo);
              }
          });
            map.put(d.get("ID").toString(),list);
        });
        UpdateResult updateResult=new UpdateResult();
        BeanUtils.copyProperties(dbResult,updateResult);
        updateResult.setData(null);
        updateResult.setList(map);
        updateResult.setSql(sql);
        return updateResult;
    }
}
