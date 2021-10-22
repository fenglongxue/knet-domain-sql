package cn.knet.vo;

import cn.knet.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xuxiannian
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("KNET_USER")
public class KnetUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID")
    private String id;

    @TableField("USERNAME")
    private String username;

    @TableField("PASSWORD")
    private String password;

    @TableField("NAME")
    private String name;

    @TableField("CREATE_DATE")
    private Date createDate;

    @TableField("ROLES")
    private String roles;

    @TableField("STATUS")
    private StatusEnum status;

    @TableField("EMAIL")
    private String email;

    @TableField("UPDATE_DATE")
    private Date updateDate;

    @TableField("PHONE")
    private String phone;

    @TableField("USER_OPENID")
    private String userOpenid;


}
