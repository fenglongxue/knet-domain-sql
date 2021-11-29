package cn.knet.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xuxiannian
 * @since 2021-11-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("KNET_SQL_DOWNLOAD")
public class KnetSqlDownload implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private String id;

    /**
     * 操作id
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 签报号
     */
    @TableField("SIGO")
    private String sigo;

    /**
     * 邮件
     */
    @TableField("EMAIL")
    private String email;
    /**
     * 抄送邮件
     */
    @TableField("EMAIL")
    private String copyEmail;
    /**
     * 下载地址
     */
    @TableField("DOWNLOAD_URL")
    private String downloadUrl;

    /**
     * 操作时间
     */
    @TableField("CREATE_DATE")
    private Date createDate;
    /**
     * 操作时间
     */
    @TableField("CREATE_DATE")
    private String sql;
    /**
     * 操作的数据库，wz。seal
     */
    @TableField("TYPE")
    private String type;
    /**
     * 操作的数据库，wz。seal
     */
    @TableField("TITLE")
    private String title;

    public KnetSqlDownload(String sql, String type,String sigo,String email,String copyEmail,String userId,String title) {
        this.sql = sql;
        this.type = type;
        this.sigo = sigo;
        this.email = email;
        this.copyEmail = copyEmail;
        this.userId = userId;
        this.title = title;
    }
}
