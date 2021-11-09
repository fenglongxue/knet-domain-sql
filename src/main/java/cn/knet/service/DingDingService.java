package cn.knet.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiProcessListbyuseridRequest;
import com.dingtalk.api.request.OapiProcessinstanceListidsRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiProcessListbyuseridResponse;
import com.dingtalk.api.response.OapiProcessinstanceListidsResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DingDingService {

    //账号AK信息请填写(必选)
    @Value(value="${accessKeyId}")
    private String accessKeyId = "ding88cieqzfmpaak1ka";
    //账号AK信息请填写(必选)
    @Value(value="${accessKeySecret}")
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

        //获取审批实例ID列表
        try {
            DingTalkClient clientList = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/listids");
            OapiProcessinstanceListidsRequest reqList = new OapiProcessinstanceListidsRequest();
            reqList.setHttpMethod("GET");
            reqList.setProcessCode("PROC-EF6YLU2SO2-710KOOWCV1SSLPCFO53X1-8L7Z6H1J-4");
            reqList.setStartTime(1496678400000L);
            reqList.setEndTime(1686879999000L);
            OapiProcessinstanceListidsResponse rspList = client.execute(reqList, "4365f8c1a81d317f85d7edbd7e29209d");
            System.out.println("审批列表"+rspList.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        //获取用户id
        try {
            DingTalkClient clientUser = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
            OapiUserGetuserinfoRequest reqUser = new OapiUserGetuserinfoRequest();
            req.setHttpMethod("POST");
            OapiUserGetuserinfoResponse rspUser = client.execute(reqUser, "4365f8c1a81d317f85d7edbd7e29209d");
            System.out.println("用户id:"+rspUser.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        //获取所有可见签报
        try {
            DingTalkClient clients = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/listbyuserid");
            OapiProcessListbyuseridRequest reqs = new OapiProcessListbyuseridRequest();
            reqs.setHttpMethod("POST");
            reqs.setOffset(0L);
            reqs.setSize(10L);
            OapiProcessListbyuseridResponse rsps = clients.execute(reqs, "4365f8c1a81d317f85d7edbd7e29209d");
            System.out.println("所有签报"+rsps.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
