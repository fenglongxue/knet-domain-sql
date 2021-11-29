package cn.knet.web;

import cn.knet.enums.SqlType;
import cn.knet.service.ExcService;
import cn.knet.service.PlUpdateService;
import cn.knet.service.RunService;
import cn.knet.service.UpdateService;
import cn.knet.util.SqlFormatUtil;
import cn.knet.vo.DbResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("sql")
@Slf4j
public class SqlController extends SuperController {
    @Resource
    RunService runService;
    @Resource
    PlUpdateService plUpdateService;
    @Resource
    ExcService excService;
    private static final String SQLS = "sqls[]";
    private static final String MSG = "参数不能为空!";

    /***
     * 运行操作：可能是更新、查询、表操作
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/exc", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<DbResult> exc(HttpServletRequest request, String type, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber) {
        List<DbResult> list = new ArrayList<>();
        String[] sqls = request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <= 0) {
            list.add(DbResult.error(1002, MSG));
            return list;
        }
        for (String sql : sqls) {
            //先校验语法和格式等
            DbResult dbResult = SqlFormatUtil.sqlVaildate(sql, type);
            if (dbResult.getCode() != 1000) {
                list.add(dbResult);
            } else {
                list.add(runService.running(sql, type, pageNumber, getCurrentLoginUser().getId()));
            }
        }
        return list;
    }

    /***
     * 执行操作(更新和删除)
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateExc", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<DbResult> updateExc(HttpServletRequest request,String type,String sqlType) {
        List<DbResult> list = new ArrayList<>();
        String[] sqls = request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <= 0) {
            list.add(DbResult.error(1002, MSG));
            return list;
        }
        if(StringUtils.isBlank(sqlType)){
            list.add(DbResult.error(1002, "操作类型不能为空"));
            return list;
        }
        for (String sql : sqls) {
            DbResult dbResult = SqlFormatUtil.sqlVaildate(sql, type);
            dbResult.setSqlType(sqlType);
            dbResult.setSql(sql);
            if (dbResult.getCode() == 1000) {
                if (sqlType.equalsIgnoreCase(SqlType.UPDATESELECT.name())) {
                    list.add(excService.update(sql,type,getCurrentLoginUser().getId()));
                } else {
                    list.add(excService.delete(sql,type,getCurrentLoginUser().getId()));
                }
            } else {
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
    @RequestMapping(value = "/plUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<DbResult> updateForPl(String type,HttpServletRequest request) throws IOException, EncodeException {
        List<DbResult> list = new ArrayList<>();
        String[] sqls = request.getParameterValues(SQLS);
        if (null == sqls || sqls.length <= 0) {
            list.add(DbResult.error(1002, MSG));
            return list;
        }
        return plUpdateService.updateAnalysisEngine(type,getCurrentLoginUser().getId(),sqls);
    }
}
