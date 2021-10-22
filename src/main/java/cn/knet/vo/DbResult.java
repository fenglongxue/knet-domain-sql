package cn.knet.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@Accessors(chain = true)
public class DbResult {
    int code;
    String msg;
    long count;
    List<Map<String, Object>> data;
    String sql;
    Map<String, List<OldAndNewVo>> map;
    String sqlType;
    String[] title;
    public DbResult(Map<String, List<OldAndNewVo>> map,String sql) {
        this.map = map;
        this.code = 1000;
        this.sql=sql;
    }
    public DbResult(long count, List<Map<String, Object>> data) {
        this.count = count;
        this.data = data;
        this.code = 1000;
    }
    public DbResult(long count,Map<String, List<OldAndNewVo>> map) {
        this.count = count;
        this.code = 1000;
        this.map=map;
    }
    public DbResult(long count,List<Map<String, Object>> data, String sql) {
        this.count = count;
        this.data = data;
        this.sql = sql;
        this.code = 1000;
    }
    public DbResult(int code,String msg, String sql,String sqlType) {
        this.sql = sql;
        this.msg = msg;
        this.code=code;
        this.sqlType=sqlType;
    }
    public DbResult(int code,String msg, String sql) {
        this.sql = sql;
        this.msg = msg;
        this.code=code;
    }
    public DbResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public static DbResult success(long count, List data) {
        return new DbResult(count, data);
    }
    public static DbResult success(Map<String, List<OldAndNewVo>> map,String sql) {
        return new DbResult(map,sql);
    }
    public static DbResult success(long count,Map<String, List<OldAndNewVo>> map) {
        return new DbResult(count, map);
    }
    public static DbResult success(List data) {
        return success(-1, data);
    }

    public static DbResult success() {
        return success(1000, "");
    }

    public static DbResult success(String msg) {
        return success(1000, msg);
    }
    public static DbResult success(String msg,String sql) {
        return new DbResult(1000, msg,sql);
    }
    public static DbResult success(String msg,String sql,String sqlType) {
        return new DbResult(1000, msg,sql,sqlType);
    }
    public static DbResult error(int code, String msg) {
        return new DbResult(code, msg);
    }
    public static DbResult error(int code, String msg,String sql) {
        return new DbResult(code, msg,sql);
    }
    public static DbResult error(int code, String sql,String msg,String sqlType) {
        return new DbResult(code,sql,msg,sqlType);
    }
    public static DbResult error(String msg,String sql) {
        return new DbResult(1002,msg,sql);
    }
    public static DbResult success(IPage p) {
        return success(p.getTotal(), p.getRecords());
    }
    public static DbResult success(int code, String msg){
        return new DbResult(code, msg);
    }
}
