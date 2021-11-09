package cn.knet.service;

import cn.knet.enums.SqlType;
import cn.knet.util.SqlFormatUtil;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import cn.knet.vo.DbResultForPl;
import cn.knet.vo.OldAndNewForPlVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 引擎接口
 */
@Service
@Slf4j
public class EngineService {
    @Resource
    AnalysisEngineService analysisEngineService;
    @Resource
    LogEngineService logEngineService;

    /***
     * 运行操作：具体执行那个引擎
     * @param sqls
     * @param type
     * @param pageNumber
     * @return
     * @throws Exception
     */
    public List<DbResult> exc(String[] sqls, String type, int pageNumber, String userId) {
        List<DbResult> list = new ArrayList<>();
        for (String  sql:sqls) {
            //先校验语法和格式
            DbResult dbResult=SqlFormatUtil.sqlValidate(sql);
            if(dbResult.getCode()!=1000){
                list.add(dbResult);
            }else{
                try {
                    SqlType opType = SqlParserTool.getSqlType(sql);
                    if (opType.equals(SqlType.SELECT)) {
                        list.add(select(sql, type, pageNumber, opType));
                    } else if (opType.equals(SqlType.UPDATE)) {
                        list.add(updateForSelect(sql, type, SqlType.UPDATESELECT));
                    } else if (opType.equals(SqlType.INSERT)) {
                        list.add(insert(sql, type, opType, userId));
                    } else if (opType.equals(SqlType.DELETE)) {
                        list.add(delete(sql, type, opType, userId));
                    } else if (opType.equals(SqlType.COMMENT) ||opType.equals(SqlType.ALTER) || opType.equals(SqlType.CREATETABLE) || opType.equals(SqlType.DROP)) {
                        list.add(alert(sql, type, opType, userId));
                    } else {
                        list.add(DbResult.error(1002, "操作类型不支持！",sql));
                    }
                } catch (JSQLParserException e) {
                    list.add(DbResult.error(1002, "出现异常，" + SqlParserTool.getSqlEcception(e),sql));
                }
            }
        }
        return list;
    }

    /***
     * 查询引擎
     * @param sql
     * @param pageNumber
     * @return
     */
    public DbResult select(String sql, String type, int pageNumber, SqlType opType) {
        DbResult dbResult =SqlFormatUtil.queryValidate(sql);
        if (dbResult.getCode() != 1000) return dbResult.setSqlType(opType.name());
        return analysisEngineService.queryDb(type, sql, pageNumber).setSqlType(opType.name());
    }

    /***
     * 更新前的查找引擎
     * 返回多个查询页
     * @param sql
     * @param type
     * @return
     * @throws Exception
     */
    public DbResult updateForSelect(String sql, String type, SqlType opType) {
        DbResult dbResult = SqlFormatUtil.updateValidate(sql);
        if (dbResult.getCode() != 1000) return dbResult.setSqlType(opType.name());
        dbResult=analysisEngineService.updateForSelect(type, sql,100);
        //只取前100条展示
        return dbResult.setSqlType(opType.name());
    }

    /**
     * 更新引擎
     * @param sqls
     * @return
     */
    public List<DbResult> update(String[] sqls,String type,SqlType opType,String userId) {
        List<DbResult> list = new ArrayList<>();
        for (String sql : sqls) {
            DbResult dbResult=SqlFormatUtil.updateValidate(sql);
            dbResult.setSqlType(opType.name());
            if (dbResult.getCode() != 1000) {
                list.add(dbResult);
            }else{
                list.add(analysisEngineService.updateAnalysisEngine(type,sql,userId));
            }
        }
        return list;
    }
    /***
     * 批量更新的引擎
     * @param sqls
     * @param type
     * @param userId
     * @return
     */
    public List<DbResult> updateForPl(String[] sqls, String type,String userId) {
        return analysisEngineService.updateForPLAnalysisEngine(type,sqls,userId);
    }

    /***
     * 批量更新前的查询引擎
     * 只返回一个结果页
     * @param sqls
     * @param type
     * @return
     * @throws Exception
     */
    public DbResultForPl updateListForSelect(String[] sqls, String type, SqlType opType) {
        DbResultForPl dbResultForPl = new DbResultForPl();
        List<OldAndNewForPlVo> lists = new ArrayList<>();
        for (int i = 0; i < sqls.length; i++) {
            OldAndNewForPlVo vo = new OldAndNewForPlVo();
            DbResult result = analysisEngineService.updateForSelectList(type, sqls[i]);
            vo.setCode(result.getCode());
            vo.setMsg(result.getMsg());
            vo.setMap(result.getMap());
            vo.setSql(sqls[i]);
            vo.setTitle(result.getTitle());
            lists.add(vo);
        }
        dbResultForPl.setList(lists);
        dbResultForPl.setSqlType(opType.name());
        return dbResultForPl;
    }

    /****
     * 表操作
     * @param sql
     * @return
     */
    public DbResult alert(String sql, String type, SqlType opType, String userId) {
        DbResult dbResult=SqlFormatUtil.alertValidate(sql,type);
        if (dbResult.getCode() != 1000) return dbResult.setSqlType(opType.name());
        return analysisEngineService.alertAnalysisEngine(type, sql, userId).setSqlType(opType.name());
    }

    /**
     * 插入操作
     * @param sql
     * @param type
     * @param opType
     * @param userId
     * @return
     */
    public DbResult insert(String sql, String type, SqlType opType, String userId) {
        DbResult dbResult=SqlFormatUtil.insertValidate(sql);
        if (dbResult.getCode() != 1000) return dbResult.setSqlType(opType.name());
        return  analysisEngineService.insertAnalysisEngine(type, sql, userId).setSqlType(opType.name());
    }

    /***
     * 删除操作
     * @param sql
     * @param type
     * @param opType
     * @param userId
     * @return
     */
    public DbResult delete(String sql, String type, SqlType opType, String userId) {
        DbResult dbResult=SqlFormatUtil.deleteValidate(sql);
        if (dbResult.getCode() != 1000) return dbResult.setSqlType(opType.name());
        return  analysisEngineService.deleteAnalysisEngine(type, sql, userId).setSqlType(opType.name());
    }
}