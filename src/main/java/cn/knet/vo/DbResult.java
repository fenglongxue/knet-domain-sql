package cn.knet.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class DbResult {
    int code;
    String msg;
    long count;
    List<Map<String, Object>> data;
    String sql;
    public DbResult(long count, List<Map<String, Object>> data, Object totalRow) {
        this.count = count;
        this.data = data;
        this.code = 1000;
    }

    public DbResult(long count,List<Map<String, Object>> data, String sql) {
        this.count = count;
        this.data = data;
        this.sql = sql;
        this.code = 1000;
    }

    public DbResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public DbResult(long count, List data) {
    }

    public static DbResult success(long count, List data, Object totalRow) {
        return new DbResult(count, data, totalRow);
    }


    public static DbResult success(long count, List data) {
        return new DbResult(count, data);
    }

    public static DbResult success(List data) {
        return success(-1, data);
    }

    public static DbResult success() {
        return success(1000, "");
    }

    public static DbResult error(int code, String msg) {
        return new DbResult(code, msg);
    }

    public static DbResult success(IPage p) {
        return success(p.getTotal(), p.getRecords());
    }
    public static DbResult success(int code, String msg){
        return new DbResult(code, msg);
    }
}
