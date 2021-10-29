package cn.knet.web;

import cn.knet.vo.KnetUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController extends  SuperController {

    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        KnetUser userAccount = getCurrentLoginUser(request);
        model.addAttribute("userAccount", userAccount);
        return "index";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.HEAD })
    public String welcome(HttpServletRequest request, Model model) {
        return "login";
    }

    @RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.HEAD })
    public String login(HttpServletRequest request, Model model) {
        return "login";
    }

    @RequestMapping(value = "/unauthc", method = { RequestMethod.GET, RequestMethod.HEAD })
    public String unauthc() {
        return "unauthc";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(Model model, HttpServletRequest request, String username, String password,
                                     RedirectAttributes redirectAttributes) throws Exception {
        Map<String, String> r = new HashMap<String, String>();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            KnetUser user = (KnetUser) subject.getPrincipal();
            subject.getSession().setAttribute("user", user);
            r.put("code", "0");
        } catch (Exception ae) {
            r.put("code", "1");
            r.put("msg", ae.getMessage());

        }
        return r;
    }

    @RequestMapping(value = "/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
        }
        return "redirect:/";
    }
}
