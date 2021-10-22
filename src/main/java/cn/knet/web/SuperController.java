package cn.knet.web;


import cn.knet.vo.KnetUser;
import org.apache.shiro.SecurityUtils;

import javax.servlet.http.HttpServletRequest;

public class SuperController {
    protected KnetUser getCurrentLoginUser(HttpServletRequest request) {
        SecurityUtils.getSubject().getSession().getAttribute("");
        return (KnetUser) SecurityUtils.getSubject().getPrincipal();
    }
}
