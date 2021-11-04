package cn.knet.util;

import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.validation.constraints.NotEmpty;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcUtil {
private static final String COLUMN_NAME = "COLUMN_NAME";
    private JdbcUtil() {
    }

    /***
     * 获取主键
     * @param jdbcTemplate
     * @param table
     * @return
     */
    public static String getPriKey(JdbcTemplate jdbcTemplate,String table){
        try {
            String sql="select cu.COLUMN_NAME\n" +
                    "  from user_cons_columns cu, user_constraints au\n" +
                    " where cu.constraint_name = au.constraint_name\n" +
                    "   and au.constraint_type = 'P'\n" +
                    "   and au.table_name =  '"+table.toUpperCase()+"'";
            return jdbcTemplate.queryForObject(sql, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString(COLUMN_NAME);
                }
            });
        }catch (Exception e){
            return null;
        }

    }

    /***
     * 获取表中所有的列
     * @param jdbcTemplate
     * @param table
     * @return
     */
    public static List<Map<String, Object>> getAllClumns(JdbcTemplate jdbcTemplate, String table){
        List<Map<String, Object>> list = new ArrayList<>();
        try {
        String sql="select t.COLUMN_NAME\n" +
                "     from user_tab_columns t, user_col_comments c\n" +
                "    where t.table_name = c.table_name\n" +
                "      and t.column_name = c.column_name\n" +
                "      and t.table_name = '"+table.toUpperCase()+"'";
        return jdbcTemplate.queryForList(sql);
        }catch (Exception e){
            return list;
        }
    }
    /***
     * 根据更新的列表，查找要更新表的主键（没有主键直接读取所有的列）
     * @param updateStatement
     * @param jdbcTemplate
     * @param table
     * @return
     */
    public static String getClumnsForUpdate(Update updateStatement, JdbcTemplate jdbcTemplate, String table){
            StringBuilder clumns = new StringBuilder();
            //获取主键
            String pk = JdbcUtil.getPriKey(jdbcTemplate, table);
            if (StringUtils.isNotBlank(pk)) {
                clumns.append(pk + ",");
                updateStatement.getColumns().forEach(x -> {
                    if (!pk.toUpperCase().equalsIgnoreCase(x.getColumnName())) {
                        clumns.append(x.getColumnName().toUpperCase() + ",");
                    }
                });
            }
            if (StringUtils.isBlank(pk)) {
                updateStatement.getColumns().forEach(x -> clumns.append(x.getColumnName().toUpperCase() + ","));//变化的列名在最前面
                @NotEmpty
                List<Map<String, Object>> list =JdbcUtil.getAllClumns(jdbcTemplate, table);
                if (!list.isEmpty())
                    list.forEach(x -> updateStatement.getColumns().forEach(c -> clumns.append((!x.get(COLUMN_NAME).toString().equalsIgnoreCase(c.getColumnName())) ? x.get(COLUMN_NAME) + "," : "")));//未变化的列名在最后
            }
            if (clumns.toString().endsWith(",")) {
                clumns.deleteCharAt(clumns.length() - 1);
            }
            return clumns.toString();
    }
}
