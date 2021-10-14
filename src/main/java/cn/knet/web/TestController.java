package cn.knet.web;

import cn.knet.service.AnalysisEngineService;
import cn.knet.service.EngineService;
import cn.knet.vo.DbResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class TestController {

    @Resource
    EngineService engineService;
    @Resource
    AnalysisEngineService analysisEngineService;
    @RequestMapping("/checkSql")
    @ResponseBody
    public DbResult checkSql(String sql) throws Exception {
        return  analysisEngineService.SqlCheckEngine(sql, "wz");
    }
    @RequestMapping("/testSql")
    @ResponseBody
    public DbResult testSql(String[] sqls) throws Exception {
        return  engineService.selectEngine("select * from knet_official_industry ", "wz",10);
    }
    @RequestMapping("/testCheck")
    @ResponseBody
    public DbResult testCheck(String sql) throws Exception {
         CCJSqlParserUtil.parseCondExpression(sql);

        return  null;
    }
    @RequestMapping("/test")
    @ResponseBody
    public List<DbResult> test(String[] sqls) throws Exception {
        DbResult result=engineService.selectEngine("select * from knet_official_industry ", "wz",10);
        DbResult result2=engineService.selectEngine("select * from knet_official_industry_case ", "wz",10);
        List<DbResult> list=new ArrayList<>();
        list.add(result);
        list.add(result2);
        return  list;
    }
    @RequestMapping("/update")
    @ResponseBody
    public List<DbResult> update(String[] sqls) throws Exception {
        DbResult result=engineService.updateEngine("update KNET_OFFICIAL_INDUSTRY_CASE  set domain='测试083101.网址', id='1'", "wz");
        DbResult result2=engineService.updateEngine("update KNET_OFFICIAL_INDUSTRY_CASE set id='2' where id='4bf8025f226c4b63906a38f0ba9f76f2'", "wz");
        List<DbResult> list=new ArrayList<>();
        list.add(result);
        list.add(result2);
        return  list;
    }
}
