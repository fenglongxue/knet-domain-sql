package cn.knet.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class NoModelWriteData implements Serializable {
    private String fileName;//文件名
    private String password;//文件密码
    private String[] headMap;//表头数组
    private String[] dataStrMap;//对应数据字段数组
    private List<Map<String, Object>> dataList;//数据集合
}