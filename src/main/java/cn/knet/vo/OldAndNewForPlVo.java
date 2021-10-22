package cn.knet.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class OldAndNewForPlVo{
    Map<String, List<OldAndNewVo>> map;
    String sql;
    int code;
    String msg;
}
