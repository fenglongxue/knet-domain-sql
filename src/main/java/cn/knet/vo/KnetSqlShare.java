package cn.knet.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("KNET_SQL_SHARE")
public class KnetSqlShare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID")
    private String id;

    @TableField("SQL")
    private String sql;

    /**
     * SHARE（分享）、SAVE（保存） 
     */
    @TableField("STOR_TYPE")
    private String storType;
    /**
     * SHARE（分享）、SAVE（保存）
     */
    @TableField("type")
    private String type;

    /**
     * 保存sql用途
     */
    @TableField("TITLE")
    private String title;

    /**
     * 操作用户
     */
    @TableField("USER_ID")
    private String userId;


    /**
     * 创建日期
     */
    @TableField("CREATE_DATE")
    private Date createDate;
    /**
     * 是否分享Y|N
     */
    @TableField("IS_SHARE")
    private String isShare;

}
