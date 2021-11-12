package cn.knet.web;

import cn.knet.enums.SqlType;
import cn.knet.service.ExcService;
import cn.knet.service.PlUpdateService;
import cn.knet.service.UpdateService;
import cn.knet.util.SqlFormatUtil;
import cn.knet.util.SqlParserTool;
import cn.knet.vo.DbResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("sql")
@Slf4j
public class SqlController extends SuperController{
    @Resource
    ExcService excService;
    @Resource
    PlUpdateService plUpdateService;
    @Resource
    UpdateService updateService;
    private static final String SQLS = "sqls[]";
    private static final String MSG = "参数不能为空!";
    /***
     * 运行操作：可能是更新、查询、表操作
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/exc",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public List<DbResult> exc(HttpServletRequest request,String type, @RequestParam(value = "pageNumber",defaultValue ="1") int pageNumber){
        List<DbResult> list = new ArrayList<>();
        String[] sqls=request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <=0) {
            list.add(DbResult.error(1002, MSG));
            return list;
        }
        for (String  sql:sqls) {
                //先校验语法和格式等
                DbResult dbResult= SqlFormatUtil.sqlVaildate(sql,type);
                if(dbResult.getCode()!=1000) {
                    list.add(dbResult);
                }else{
                    list.add(excService.exc(sql, type, pageNumber,getCurrentLoginUser().getId()));
                }
        }
    return list;
    }

    /***
     * 只有更新有执行，每次是执行一条sql语句
     * 返回更新的数据量
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateExc",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public List<DbResult> updateExc(HttpServletRequest request, String type) {
        List<DbResult> list = new ArrayList<>();
        String[] sqls=request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <=0) {
            list.add(DbResult.error(1002, MSG));
            return list;
        }
        for (String sql : sqls) {
            DbResult dbResult=SqlFormatUtil.sqlVaildate(sql,type);
            dbResult.setSqlType(SqlType.UPDATE.name());
            dbResult.setSql(sql);
            if (dbResult.getCode() == 1000) {
                SqlType opType = null;
                try {
                    opType = SqlParserTool.getSqlType(sql);
                    if(opType.name().equalsIgnoreCase(SqlType.UPDATE.name())){
                        list.add(updateService.update(sql, type,getCurrentLoginUser().getId()));
                    }else{
                        list.add(dbResult);
                    }
                } catch (JSQLParserException e) {
                    list.add(dbResult.setCode(1002).setMsg(SqlParserTool.getSqlEcception(e)));
                }
            }else{
                list.add(dbResult);
            }
        }
        return list;
    }
    /***
     * 批量更新
     * @param request
     * @param type
     * @return
     */
    @RequestMapping(value = "/updateForPl",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public List<DbResult> updateForPl(HttpServletRequest request,String type) {
        List<DbResult> list = new ArrayList<>();
        String[] sqls=request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <=0) {
            list.add(DbResult.error(1002, MSG));
            return list;
        }

        return plUpdateService.updateForPLAnalysisEngine(type,sqls,getCurrentLoginUser().getId());
    }
}
