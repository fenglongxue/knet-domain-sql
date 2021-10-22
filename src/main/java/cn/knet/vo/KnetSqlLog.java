package cn.knet.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("KNET_SQL_LOG")
public class KnetSqlLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private String id;

    @TableField("SQL")
    private String sql;

    /**
     * 操作的数据库，wz。seal
     */
    @TableField("TYPE")
    private String type;

    @TableField("OP_TYPE")
    private String opType;

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
     * 操作人ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 操作时间
     */
    @TableField("CREATE_DATE")
    private Date createDate;

    /**
     * 查询为查询结果，更新为更新记录（字段前后对比），日志查看时提供下载功能
     */
    @TableField("SQL_RESU")
    private String sqlResu;

    /**
     * 修改前记录
     */
    @TableField("OLD")
    private String old;

    /**
     * 修改后记录
     */
    @TableField("NOW")
    private String now;


}
