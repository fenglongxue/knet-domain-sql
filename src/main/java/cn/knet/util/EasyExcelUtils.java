package cn.knet.util;


import cn.knet.domain.util.DateUtils;
import com.alibaba.excel.EasyExcel;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.*;

@Slf4j
public class EasyExcelUtils {

    //不创建对象的导出
    public void noModleWrite(OutputStream outputStream, @RequestBody NoModelWriteData data)  {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
//            response.setContentType("application/vnd.ms-excel");
            // 这里需要设置不关闭流
            EasyExcel.write(outputStream).registerConverter( new LocalDateTimeConverter())
                    .useDefaultStyle(false)
                    .password(data.getPassword())
                    .head(head(data.getHeadMap()))
                    .sheet(data.getFileName())
                    .doWrite(dataList(data.getDataList(), data.getDataStrMap()));
        } catch (Exception e) {
            log.error("生成报表异常:{}",e.toString());
            // 重置response
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());

        }
    }



    //设置表头
    private List<List<String>> head(String[] headMap) {
        List<List<String>> list = new ArrayList<List<String>>();

        for (String head : headMap) {
            List<String> headList = new ArrayList<String>();
            headList.add(head);
            list.add(headList);
        }
        return list;
    }

    //设置导出的数据内容
    private List<List<Object>> dataList(List<Map<String, Object>> dataList, String[] dataStrMap) {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (Map<String, Object> map : dataList) {
            List<Object> data = new ArrayList<Object>();
            for (int i = 0; i < dataStrMap.length; i++) {
                if (map.get(dataStrMap[i]) instanceof Date) {
                    Date d = (Date) map.get(dataStrMap[i]);
                    data.add(DateUtils.formatDate(d,"yyyy-MM-dd HH:mm:ss"));
                    //data.add("时间");
                }else{
                    data.add(map.get(dataStrMap[i]));
                }
            }
            list.add(data);
        }
        return list;
    }
}
