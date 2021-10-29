package cn.knet.web;

import cn.knet.enums.SqlType;
import cn.knet.service.EngineService;
import cn.knet.vo.DbResult;
import cn.knet.vo.DbResultForPl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("sql")
@Slf4j
public class SqlController extends SuperController{
    @Resource
    EngineService engineService;

    /***
     * 运行操作：可能是更新、查询、表操作
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping("/exc")
    @ResponseBody
    public List<DbResult> exc(HttpServletRequest request,String type, @RequestParam(value = "pageNumber",defaultValue ="50") int pageNumber){
        return engineService.exc(request.getParameterValues("sqls[]"), type, pageNumber,getCurrentLoginUser(request).getId());
    }

    /***
     * 只有更新有执行，每次是执行一条sql语句
     * 返回更新的数据量
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateExc")
    @ResponseBody
    public List<DbResult> updateExc(HttpServletRequest request, String type) {
        return engineService.update(request.getParameterValues("sqls[]"), type, SqlType.UPDATE,getCurrentLoginUser(request).getId());
    }

    /***
     * 批量更新前的查询
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateListForSelect")
    @ResponseBody
    public DbResultForPl updateListForSelect(HttpServletRequest request,String type) {
        return engineService.updateListForSelect(request.getParameterValues("sqls[]"),type,SqlType.PIUPDATESELECT);
    }
}
