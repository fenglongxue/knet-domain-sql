package cn.knet.engine;

import cn.knet.dao.JdbcDao;
import cn.knet.service.LogEngineService;
import cn.knet.vo.DbResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 解析sqlServices
 */
@Service
@Slf4j
public class SqlEngine {
    @Resource
    private LogEngineService logEngineService;
    private int logCount=50;//日志只存小于50条的数据，大于100条不存
    @Resource
    private JdbcDao jdbcDao;
    /**
     * 查询引擎
     * @param sql
     * @param pageNumber
     * @return
     */
    public DbResult queryDb(String type, String sql,int pageNumber) {
        DbResult result = new DbResult();
        result.setCount(jdbcDao.getCout(type,sql));
        if(result.getCount()==0){
            return result.setMsg("0条数据").setCode(1000).setSql(sql);
        }
        List<Map<String, Object>> list = jdbcDao.query(type,sql,pageNumber);
        if (list.isEmpty()) {
            return result.setMsg("0条数据").setCode(1000).setSql(sql);
        }
         return result.setData(list).setCode(1000);
    }
    /***
     * 表操作引擎
     * @param type
     * @param sql
     * @return
     */
    public void alertAnalysisEngine(String type,String sql) {
        jdbcDao.execute(type,sql);
    }

    /***
     * 更新操作引擎
     * @param type
     * @param sql
     * @return
     */
    public int updateAnalysisEngine(String type, String sql) {
        return jdbcDao.update(type,sql);
    }
    /***
     * 插入操作引擎
     * @param type
     * @param sql
     * @return
     */
    public int insertAnalysisEngine(String type, String sql) {
        return jdbcDao.update(type,sql);
    }

    /***
     * 删除操作引擎
     * @param type
     * @param sql
     * @return
     */
    public int deleteAnalysisEngine(String type, String sql) {
        return jdbcDao.update(type,sql);
    }
}
