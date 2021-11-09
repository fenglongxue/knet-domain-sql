package cn.knet.rm;

import cn.knet.conf.SpringTools;
import cn.knet.domain.enums.StatusEnum;
import cn.knet.vo.KnetUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

@Slf4j
@Configuration
public class MyRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        KnetUser user = (KnetUser) principal.fromRealm(getName()).iterator().next();
        if (user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            String [] rs=user.getRoles().split(",");
            for(String r:rs){
                info.addRole(r);
            }
            return info;
        } else {
            return null;
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        List<KnetUser> l = SpringTools.getJdbcTemplate("wz").query("select  * from knet_user where  username=? and password =? and status =? "
                ,new BeanPropertyRowMapper<>(KnetUser.class),token.getUsername(),DigestUtils.md5Hex(new String(token.getPassword())),StatusEnum.ABLE.getValue());
        if (!l.isEmpty()) {
            return new SimpleAuthenticationInfo(l.get(0), token.getPassword() , getName());
        }
        throw new AuthenticationException("用户名密码错误");
    }
}