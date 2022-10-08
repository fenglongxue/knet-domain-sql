package cn.knet.service;

import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 执行业务层
 */
@Service
@Slf4j
public class ExcService {
    @Resource
    LogEngineService logEngineService;
    @Resource
    UpdateService updateService;
    /***
     * 删除操作
     * @param sql
     * @param type
     * @param userId
     * @return
     */
    public DbResult delete(String sql,String type,String userId,String sigo) {
            DbResult dbResult=updateService.delete(sql,type,SqlType.DELETE,false);
            if(dbResult.getCode()==1000){
            logEngineService.logSava(new KnetSqlLog(sql,type,SqlType.DELETE.name(),dbResult.getMsg(), userId,sigo),dbResult.getLogDetails());
            }
            return dbResult;
    }
     /**
     * 更新
     * @param sql
     * @return
     */
    public DbResult update(String sql,String type,String userId,String sigo) {
        DbResult dbResult=updateService.update(sql,type,false);
        if(dbResult.getCode()==1000){
            logEngineService.logSava(new KnetSqlLog(sql,type,SqlType.UPDATE.name(),dbResult.getMsg(), userId,sigo),dbResult.getLogDetails());
        }
        return dbResult;
    }
}