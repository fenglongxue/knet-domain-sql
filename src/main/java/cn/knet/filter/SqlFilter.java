package cn.knet.filter;

import cn.knet.util.SqlFormatUtil;
import cn.knet.vo.DbResult;

import javax.servlet.*;
import java.io.IOException;
/***
 * 验证sql的格式
 */
/*@Order(2)
@WebFilter(filterName="sqlFilter", urlPatterns="/sql/*")*/
public class SqlFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String[] sqls=request.getParameterValues("sqls[]");
        if(null==sqls){
            response.getWriter().write("sql语句不能为空！");
            return;
        }
        DbResult result=SqlFormatUtil.sqlValidate(sqls);
        if(result.getCode()!=1000){
            response.getWriter().write(result.getMsg());
            return;
        }
        filterChain.doFilter(request,response);
    }
}
