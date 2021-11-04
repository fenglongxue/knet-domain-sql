package cn.knet.vo;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2021-11-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("KNET_SQL_LOG_DETAIL")
@Accessors(chain = true)
public class KnetSqlLogDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private String id;

    @TableField("LOG_ID")
    private String logId;

    @TableField("OLD")
    private String old;

    @TableField("NOW")
    private String now;

    @TableField("CREATE_DATE")
    private Date createDate;
    public KnetSqlLogDetail() {}

    public KnetSqlLogDetail(String old,String now) {
        this.old = old;
        this.now=now;
    }

}
