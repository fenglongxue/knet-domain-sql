package cn.knet.service;

import cn.knet.util.SqlParserTool;
import cn.knet.util.SqlType;
import cn.knet.vo.DbResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 引擎接口
 */
@Service
@Slf4j
public class EngineService {

    @Autowired
    @Qualifier("wzJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("sealJdbcTemplate")
    protected JdbcTemplate  sealJdbcTemplate;
    @Resource
    AnalysisEngineService analysisEngineService;
    /**
     * 解析引擎
     * @param sql
     * @param type
     * @return
     */
    public DbResult analysisEngine(String sql,String type)  throws Exception{
        DbResult dbResult = new DbResult();
        SqlType sqlType = SqlParserTool.getSqlType(sql);
        if(sqlType.equals(SqlType.UPDATE)){
            return analysisEngineService.updateAnalysisEngine(sql,type);
        }
        return dbResult;
    }


    /**
     * 查询引擎
     * @param sql
     * @param type
     * @param pageNumber
     * @return
     */
    public DbResult selectEngine(String sql,String type,int pageNumber){
        return  analysisEngineService.queryDb(sql, type, new DbResult(), pageNumber);
    }

    /**
     * 更新引擎
     * @param sql
     * @param type
     * @return
     */
    public DbResult updateEngine(String sql,String type) throws Exception {
        return  analysisEngineService.updateAnalysisEngine(sql,type);
    }

    /**
     * 批量更新更新引擎
     * @param sql
     * @param type
     * @return
     */
    public DbResult updateListEngine(String sql,String type){
        return new DbResult();
    }

    /**
     * 日志引擎
     * @param userId
     * @param sql
     * @param type
     * @param operType
     * @param result
     * @return
     */
    public DbResult logEngine(String userId,String sql,String type,String operType,String result){
        return new DbResult();
    }

    /**
     * 下载引擎
     * @param userId
     * @param sql
     * @param mail
     * @return
     */
    public DbResult lowerEngine(String userId,String sql,String mail){
        return new DbResult();
    }
}
