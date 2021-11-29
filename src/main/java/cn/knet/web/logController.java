package cn.knet.web;

import cn.knet.service.LogEngineService;
import cn.knet.vo.DbResult;
import cn.knet.vo.KnetSqlDownload;
import cn.knet.vo.KnetSqlSelect;
import cn.knet.vo.KnetSqlShare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("log")
@Slf4j
public class logController extends SuperController {
    @Resource
    private LogEngineService logEngineService;

    @RequestMapping("/list")
    @ResponseBody
    public DbResult logList(HttpServletRequest request, String storType,String sqls, String type,String title,int pageNumber){
                                          return logEngineService.logList(storType,type,this.getCurrentLoginUser().getId(),title,pageNumber);
    }

    @RequestMapping("/sava")
    @ResponseBody
    public DbResult logSava(HttpServletRequest request, KnetSqlSelect log){
        // String sql, String opType,String title
        log.setUserId(this.getCurrentLoginUser().getId());
        log.setSql("select * from ");

        return logEngineService.logSelectSava(log);
    }
    @RequestMapping("/share")
    @ResponseBody
    public DbResult logShare(HttpServletRequest request, KnetSqlShare share){
        //String sql, String storType,String title
        share.setUserId(this.getCurrentLoginUser().getId());
        return logEngineService.logShare(share);
    }
    @RequestMapping("/imp")
    @ResponseBody
    public DbResult logImp(HttpServletRequest request, String sql, String type, String sigo, String email,String copyEmail,String title){
        return logEngineService.logImp(new KnetSqlDownload(sql,type,sigo,email,copyEmail,this.getCurrentLoginUser().getId(),title));
    }
    @RequestMapping("/delete")
    @ResponseBody
    public DbResult delete(String id){
        return logEngineService.delet(id);
    }
}
