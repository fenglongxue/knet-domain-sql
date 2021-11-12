package cn.knet.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DingDingService {

    //账号AK信息请填写(必选)
    //@Value(value="${accessKeyId}")
    private String accessKeyId = "ding88cieqzfmpaak1ka";
    //账号AK信息请填写(必选)
   // @Value(value="${accessKeySecret}")
    private String accessKeySecret = "qCmoZM-zwsWoWNNmJW6vksL-Wfuj_7oTcnxnk2adHGcSfHKT1iRNt9Xxs-yIINsN";

    public static void main(String[] args) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest req = new OapiGettokenRequest();
        req.setAppkey("ding88cieqzfmpaak1ka");
        req.setAppsecret("qCmoZM-zwsWoWNNmJW6vksL-Wfuj_7oTcnxnk2adHGcSfHKT1iRNt9Xxs-yIINsN");
        req.setHttpMethod("GET");
        OapiGettokenResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());


        try {
            DingTalkClient clientByname = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/get_by_name");
            OapiProcessGetByNameRequest reqByname = new OapiProcessGetByNameRequest();
            reqByname.setName("数据操作审批");
            OapiProcessGetByNameResponse rspByname = clientByname.execute(reqByname, "914b37cea3913d00a8b82d1fe0160a63");
            System.out.println("数据操作审批获取模板code:"+rspByname.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        //获取审批实例ID列表 不通
        try {
            DingTalkClient clientList = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/listids");
            OapiProcessinstanceListidsRequest reqList = new OapiProcessinstanceListidsRequest();
            reqList.setHttpMethod("GET");
            reqList.setProcessCode("PROC-8DC13715-0EE3-43AB-9D30-BCF68CA56826");
            reqList.setStartTime(1686879999000L);
            reqList.setEndTime(1687879999000L);
            reqList.setSize(10L);
            reqList.setCursor(0L);
            OapiProcessinstanceListidsResponse rspList = client.execute(reqList, "914b37cea3913d00a8b82d1fe0160a63");
            System.out.println("审批列表"+rspList.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        //获取用户id 不通
        try {
            DingTalkClient clientUser = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
            OapiUserGetuserinfoRequest reqUser = new OapiUserGetuserinfoRequest();
            req.setHttpMethod("POST");
            OapiUserGetuserinfoResponse rspUser = client.execute(reqUser, "914b37cea3913d00a8b82d1fe0160a63");
            System.out.println("用户id:"+rspUser.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        //获取所有可见签报 通
        try {
            DingTalkClient clients = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/listbyuserid");
            OapiProcessListbyuseridRequest reqs = new OapiProcessListbyuseridRequest();
            reqs.setHttpMethod("POST");
            reqs.setOffset(0L);
            reqs.setSize(100L);
            OapiProcessListbyuseridResponse rsps = clients.execute(reqs, "914b37cea3913d00a8b82d1fe0160a63");
            System.out.println("所有签报"+rsps.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
