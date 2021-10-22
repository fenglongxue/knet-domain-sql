package cn.knet.web;

import cn.knet.service.EngineService;
import cn.knet.service.LogEngineService;
import cn.knet.vo.DbResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class TestController extends SuperController {
    @Resource
    EngineService engineService;
    @Resource
    LogEngineService logEngineService;


    @RequestMapping("/testCheck")
    @ResponseBody
    public DbResult testCheck(String sql) throws Exception {
        CCJSqlParserUtil.parseCondExpression(sql);
        return  null;
    }
    @RequestMapping("/testlog")
    @ResponseBody
    public DbResult testlog(HttpServletRequest request,String sql, String type, String opType, String sigo, String email,String sqlResu) throws Exception {
        DbResult dbResult = logEngineService.logList("");
        //logEngineService.logSava(sql,type,opType,sigo,email,"1",sqlResu,"","");
        this.getCurrentLoginUser(request);
        //logEngineService.logShare(sql,"Share","测试",this.getCurrentLoginUser(request).getId());
        return  null;
    }



}
