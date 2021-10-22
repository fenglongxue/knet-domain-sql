package cn.knet.web;

import cn.knet.service.LogEngineService;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlLog;
import cn.knet.vo.KnetSqlShare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("log")
@Slf4j
public class logController extends SuperController {
    @Resource
    private LogEngineService logEngineService;

    @RequestMapping("/logList")
    @ResponseBody
    public DbResult logList(HttpServletRequest request, String sqls, String type){
        return logEngineService.logList(this.getCurrentLoginUser(request).getId());
    }

    @RequestMapping("/logSava")
    @ResponseBody
    public DbResult logSava(HttpServletRequest request, KnetSqlLog log){
        // String sql, String opType,String title
        log.setUserId(this.getCurrentLoginUser(request).getId());
        return logEngineService.logSava(log);
    }
    @RequestMapping("/logShare")
    @ResponseBody
    public DbResult logShare(HttpServletRequest request, KnetSqlShare share){
        //String sql, String storType,String title
        share.setUserId(this.getCurrentLoginUser(request).getId());
        return logEngineService.logShare(share);
    }
}
