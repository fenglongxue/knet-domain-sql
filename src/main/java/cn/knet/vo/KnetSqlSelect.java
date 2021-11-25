package cn.knet.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author xuxiannian
 * @since 2021-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("KNET_SQL_SELECT")
public class KnetSqlSelect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID")
    private String id;

    @TableField("SQL")
    private String sql;

    /**
     * 操作的数据库，wz。seal
     */
    @TableField("TYPE")
    private String type;

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
     * 执行结果
     */
    @TableField("SQL_RESU")
    private String sqlResu;

    /**
     * 查询出的数据
     */
    @TableField("SQL_DETAIL")
    private List sqlDetail;
    /**
     * 查询出的数据
     */
    @TableField("TITLE")
    private List title;


}
