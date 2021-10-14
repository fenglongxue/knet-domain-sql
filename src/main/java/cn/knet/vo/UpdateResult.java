package cn.knet.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.chart.PieChart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class UpdateResult extends DbResult {
    int code;
    String msg;
    long count;
    Map<String, List<OldAndNewVo>> list;
    String sql;
    public UpdateResult(long count,Map<String, List<OldAndNewVo>> list, Object totalRow) {
        this.count = count;
        this.list = list;
        this.code = 1000;
    }

    public UpdateResult(long count, Map<String, List<OldAndNewVo>> list, String sql) {
        this.count = count;
        this.list = list;
        this.sql = sql;
        this.code = 1000;
    }

    public UpdateResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public UpdateResult(long count, List data) {
    }

    public static UpdateResult success(long count, Map<String, List<OldAndNewVo>> data, Object totalRow) {
        return new UpdateResult(count, data, totalRow);
    }


    public static UpdateResult success(long count, List data) {
        return new UpdateResult(count, data);
    }

    public static UpdateResult success(List data) {
        return success(-1, data);
    }

    public static UpdateResult success() {
        return success(1000, "");
    }

    public static UpdateResult error(int code, String msg) {
        return new UpdateResult(code, msg);
    }

    public static UpdateResult success(IPage p) {
        return success(p.getTotal(), p.getRecords());
    }
    public static UpdateResult success(int code, String msg){
        return new UpdateResult(code, msg);
    }
}
