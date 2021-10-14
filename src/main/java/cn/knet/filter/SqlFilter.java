package cn.knet.filter;

import cn.knet.util.SqlFormatUtil;
import cn.knet.vo.DbResult;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/***
 * 验证sql的格式
 */
public class SqlFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String sql=request.getParameter("sql");
        DbResult result=DbResult.success();
        if(StringUtils.isNotBlank(sql)){
            result=SqlFormatUtil.sqlFormat(sql);
        }
        String[] sqls=request.getParameterValues("sql");
        if(null!=sqls){
            result=SqlFormatUtil.sqlFormat(sqls);
        }
        if(result.getCode()!=1000){
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(result.getMsg());
            return;
        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
