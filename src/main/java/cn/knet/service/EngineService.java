package cn.knet.service;

import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import cn.knet.vo.DbResultForPl;
import cn.knet.vo.OldAndNewForPlVo;
import cn.knet.vo.OldAndNewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected JdbcTemplate sealJdbcTemplate;
    @Resource
    AnalysisEngineService analysisEngineService;

    /***
     * 运行操作：具体执行那个引擎
     * @param sqls
     * @param type
     * @param pageNumber
     * @return
     * @throws Exception
     */
    public List<DbResult> exc(String[] sqls, String type, int pageNumber) throws Exception {
        List<DbResult> list = new ArrayList<>();
        if (null == sqls) {
            list.add(DbResult.error(1002, "参数不能为空！"));
            return list;
        }
        String sql = sqls[0].toUpperCase();
        SqlType opType = sql.contains("SELECT") ? SqlType.SELECT : (sql.contains("UPDATE") ? SqlType.UPDATESELECT : SqlType.ALTER);
        if (opType.equals(SqlType.SELECT)) {
            return selectEngine(sqls, type, pageNumber, opType);
        } else if (opType.equals(SqlType.UPDATESELECT)) {
            return updateForSelect(sqls, type, pageNumber, opType);
        } else {
            return alertAnalysisEngine(sqls, type, opType);
        }
    }

    /***
     * 查询引擎
     * @param sqls
     * @param pageNumber
     * @return
     */
    public List<DbResult> selectEngine(String sqls[], String type, int pageNumber, SqlType opType) {
        List<DbResult> list = new ArrayList<>();
        for (String sql : sqls) {
            DbResult dbResult = analysisEngineService.queryDb("wz".equalsIgnoreCase(type) ? jdbcTemplate : sealJdbcTemplate, sql, pageNumber);
            dbResult.setSqlType(opType.name());
            list.add(dbResult);
        }
        return list;
    }

    /***
     * 更新前的查找引擎
     * @param sqls
     * @param type
     * @param pageNumber
     * @return
     * @throws Exception
     */
    public List<DbResult> updateForSelect(String[] sqls, String type, int pageNumber, SqlType opType) {
        List<DbResult> list = new ArrayList<>();
        for (String sql : sqls) {
                DbResult dbResult = analysisEngineService.updateForSelect("wz".equalsIgnoreCase(type) ? jdbcTemplate : sealJdbcTemplate, sql, pageNumber,"");
                dbResult.setSqlType(opType.name());
                list.add(dbResult);
        }
        return list;
    }

    /**
     * 更新引擎
     *
     * @param sqls
     * @return
     */
    public List<DbResult> updateEngine(String sqls[], String type, SqlType opType) {
        List<DbResult> list = new ArrayList<>();
        for (String sql : sqls) {
            DbResult dbResult = analysisEngineService.updateAnalysisEngine("wz".equalsIgnoreCase(type) ? jdbcTemplate : sealJdbcTemplate, sql);
            dbResult.setSqlType(opType.name());
            list.add(dbResult);
        }
        return list;
    }

    /***
     * 批量更新查询引擎
     * 只返回一个结果页
     * @param sqls
     * @param type
     * @param pageNumber
     * @return
     * @throws Exception
     */
    public DbResultForPl updateListForSelect(String[] sqls, String type, int pageNumber, SqlType opType) {
        DbResultForPl dbResultForPl=new DbResultForPl();
        List<OldAndNewForPlVo> lists=new ArrayList<>();
        for (int i = 0; i < pageNumber && i < sqls.length; i++) {
            OldAndNewForPlVo vo=new OldAndNewForPlVo();
            DbResult result = analysisEngineService.updateForSelectList("wz".equalsIgnoreCase(type) ? jdbcTemplate : sealJdbcTemplate, sqls[i], pageNumber, String.valueOf(i + 1));//用i作为key值
            vo.setCode(result.getCode());
            vo.setMsg(result.getMsg());
            vo.setMap(result.getMap());
            vo.setSql(sqls[0]);
            lists.add(vo);
        }
        dbResultForPl.setList(lists);
        dbResultForPl.setSqlType(opType.name());
        return dbResultForPl;
    }

    /**
     * 日志引擎
     *
     * @param userId
     * @param sql
     * @param operType
     * @param result
     * @return
     */
    public DbResult logEngine(String userId, String sql, String operType, String result, String type) {
        return new DbResult();
    }

    /**
     * 下载引擎
     *
     * @param userId
     * @param sql
     * @param mail
     * @return
     */
    public DbResult lowerEngine(String userId, String sql, String mail, String type) {
        return new DbResult();
    }

    /****+
     * 表操作
     * @param sqls
     * @return
     */
    public List<DbResult> alertAnalysisEngine(String[] sqls, String type, SqlType opType) {
        List<DbResult> list = new ArrayList<>();
        for (String sql : sqls) {
            DbResult dbResult = analysisEngineService.alertAnalysisEngine("wz".equalsIgnoreCase(type) ? jdbcTemplate : sealJdbcTemplate, sql);
            dbResult.setSqlType(opType.name());
            list.add(dbResult);
        }
        return list;
    }
}