package cn.knet.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SimpleWriteData implements Serializable {
    private String fileName;//文件名
    private List<?> dataList;//数据列表
}