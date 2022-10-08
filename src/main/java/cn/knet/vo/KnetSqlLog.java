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
     * 查询为查询结果，日志查看时提供下载功能
     */
    @TableField("SQL_RESU")
    private String sqlResu;
    /**
     * 签报号
     */
    @TableField("SIGO")
    private String sigo;
    public KnetSqlLog(String sql, String type, String opType,String sqlResu,String userId,String sigo) {
        this.sql = sql;
        this.type = type;
        this.opType = opType;
        this.userId = userId;
        this.sqlResu=sqlResu;
        this.sigo=sigo;
    }
    public KnetSqlLog(String sql, String type, String opType,String sqlResu,String userId) {
        this.sql = sql;
        this.type = type;
        this.opType = opType;
        this.userId = userId;
        this.sqlResu=sqlResu;
    }
    public KnetSqlLog(){

    }
}
