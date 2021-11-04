package cn.knet.web;

import cn.knet.enums.SqlType;
import cn.knet.service.EngineService;
import cn.knet.vo.DbResult;
import lombok.extern.slf4j.Slf4j;
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
    EngineService engineService;
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
        return engineService.exc(sqls, type, pageNumber,getCurrentLoginUser().getId());
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
        return engineService.update(sqls, type, SqlType.UPDATE,getCurrentLoginUser().getId());
    }
    /***
     * 批量更新
     * @param request
     * @param type
     * @return
     */
    @RequestMapping(value = "/updateForPl",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DbResult updateForPl(HttpServletRequest request,String type) {
        String[] sqls=request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <=0) {
            return DbResult.error(1002, MSG);
        }
        return engineService.updateForPl(sqls,type,SqlType.PIUPDATESELECT,getCurrentLoginUser().getId());
    }
}
