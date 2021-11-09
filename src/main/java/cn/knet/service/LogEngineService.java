package cn.knet.service;

import cn.knet.conf.SpringTools;
import cn.knet.domain.vo.MapBuilder;
import cn.knet.oss.UploadUtils;
import cn.knet.util.EasyExcelUtils;
import cn.knet.util.NoModelWriteData;
import cn.knet.util.SqlParserTool;
import cn.knet.util.UUIDGenerator;
import cn.knet.vo.*;
import com.alibaba.excel.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class LogEngineService {
    @Autowired
    @Qualifier("wzJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${spring.profiles.active}")
    private String path;
    /**
     *
     * 日志保存
     * @param logs
     * @return
     */
    public DbResult logSava(KnetSqlLog logs,List<KnetSqlLogDetail> details)  {
        DbResult result = new DbResult();
        String insertSql = "INSERT INTO KNET_SQL_LOG (ID,SQL,TYPE,OP_TYPE,USER_ID,SQL_RESU) VALUES (?,?,?,?,?,?)";
        log.info("日志保存数据为：{}",logs);
        logs.setId(UUIDGenerator.getUUID());
        jdbcTemplate.update(insertSql, logs.getId(),logs.getSql(),logs.getType(),logs.getOpType(),logs.getUserId(),logs.getSqlResu());
        result.setCode(1000);
        result.setMsg("插入成功");
        if(!details.isEmpty()){
            String detailsSql = "INSERT INTO KNET_SQL_LOG_DETAIL (ID,LOG_ID,NOW,OLD)  VALUES (?,?,?,?)";
            try {
                jdbcTemplate.batchUpdate(detailsSql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, UUIDGenerator.getUUID());
                        ps.setString(2, logs.getId());
                        ps.setString(3, details.get(i).getNow());
                        ps.setString(4, details.get(i).getOld());
                    }
                    @Override
                    public int getBatchSize() {
                        return details.size();
                    }
                });
                log.info("日志详情共：{}条",details.size());
            } catch (DataAccessException e) {
                jdbcTemplate.update(detailsSql,UUIDGenerator.getUUID(),logs.getId(),"因为次sql涉及长字段不给予存储","因为次sql涉及长字段不给予存储");
                e.printStackTrace();
            }
        }

        return  result;
    }
    /**
     *
     * 下载保存
     * @param download
     * @return
     */
    public DbResult logDownload(KnetSqlDownload download) {
        String insertSql = "INSERT INTO KNET_SQL_DOWNLOAD (ID,USER_ID,SIGO,EMAIL,DOWNLOAD_URL,SQL,type) VALUES (?,?,?,?,?,?,?)";
        log.info("sql保存数据为：{}",download);
        jdbcTemplate.update(insertSql, UUIDGenerator.getUUID(),
                download.getUserId(),download.getSigo(),download.getEmail(),download.getDownloadUrl(),download.getSql(),download.getType());
        return  new DbResult(1000,"保存成功");
    }
    /**
     *
     * sql保存
     * @param share
     * @return
     */
    public DbResult logShare(KnetSqlShare share) {
        String insertSql = "INSERT INTO KNET_SQL_SHARE (ID,SQL,STOR_TYPE,TITLE,USER_ID,type) VALUES (?,?,?,?,?,?)";
        log.info("sql保存数据为：{}",share);
        try {
            jdbcTemplate.update(insertSql, UUIDGenerator.getUUID(),share.getSql(),share.getStorType(),share.getTitle(),share.getUserId(),share.getType());
        } catch (DataAccessException e) {
            return  new DbResult(1000,"sql保存失败："+e.toString());
        }
        return  new DbResult(1000,"保存成功");
    }
    /**
     * 查询日志
     * @param userId
     * @return
     */
    public DbResult logList(String storType,String type,String userId,String title,int pageNumber) {
        DbResult result = new DbResult();
        StringBuilder listSql =  new StringBuilder("select l.id,sql,title,stor_type,u.name from KNET_SQL_SHARE l , KNET_USER u where l.USER_ID=U.ID  ");
        if(StringUtils.isNotBlank(storType)){
            if("SAVE".equals(storType)){
                listSql.append("and  l.user_id=?  and stor_Type = 'SAVE'");
            }else{
                listSql.append("and  l.user_id!=?  and stor_Type = 'SHARE'");
            }
        }else {
            listSql.append(" and (l.user_id=? or l.stor_type='SHARE' )");
        }
        if(StringUtils.isNotBlank(title)){
            listSql.append(" and title like '%"+title+"%'");
        }
        if(StringUtils.isNotBlank(type)){
            listSql.append(" and type = '"+type+"'");
        }
        listSql.append(" order by l.CREATE_DATE desc");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(SqlParserTool.setRowNum(listSql.toString(),pageNumber),userId);
        List<String> listKey = new ArrayList<>();
        if(!list.isEmpty()){
            list.get(0).forEach((x,y)->listKey.add(x));
        }

        result.setData(list).setCode(1000).setMsg("查询成功！").setSql(listSql.toString()).setTitle(listKey);
        return  result;
    }
    /**
     *
     * sql删除
     * @param id
     * @return
     */
    public DbResult delet(String id) {
        String deleteSql = "delete from KNET_SQL_SHARE where id=?";
        log.info("sql删除数据为：{}",id);
        jdbcTemplate.update(deleteSql, id);
        return  new DbResult(1000,"删除成功");
    }

    /**
     * 日志导出
     * @param sqllog
     * @return
     */
    @Async
    public DbResult logImp(KnetSqlDownload sqllog)  {
        if(sqllog.getType()!=null && "wz".equals(sqllog.getType())){
            sqllog.setTitle(".网址->"+sqllog.getTitle()+"-"+sqllog.getSigo()) ;
        }else{
            sqllog.setTitle("可信->"+sqllog.getTitle()+"-"+sqllog.getSigo()) ;
        }
        DbResult result = new DbResult();
        long startTime=System.currentTimeMillis();   //获取开始时间

        List<Map<String, Object>> list = SpringTools.getJdbcTemplate(sqllog.getType()).queryForList(SqlParserTool.setRowNum(sqllog.getSql(),10000,10000));
        List<String> listKey ;
        if ( !list.isEmpty()) {
             listKey = new ArrayList<>(list.get(0).keySet());
            result.setData(list).setSql(sqllog.getSql()).setTitle(listKey);
        } else {
            return  result.setMsg("没有查询出符合条件的数据").setCode(1002).setSql(sqllog.getSql());
        }
        String[] strs =  listKey.toArray(new String[]{});
        long sqlendTime=System.currentTimeMillis(); //获取结束时间

        log.info("sql程序运行时间： {} s",(sqlendTime-startTime));
        log.info("总共生成：{}条",result.getData().size());
        //附件生成
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        NoModelWriteData d = new NoModelWriteData();
        d.setFileName(sqllog.getTitle());
        d.setHeadMap(strs);
        d.setDataStrMap(strs);
        d.setDataList(list);
        d.setPassword(DateUtils.format(new Date(),DateUtils.DATE_FORMAT_14)+new Random().nextInt(100));
        EasyExcelUtils easyExcelUtils = new EasyExcelUtils();
        easyExcelUtils.noModleWrite(os,d);
        long excendTime=System.currentTimeMillis(); //获取结束时间
        log.info("文件生成程序运行时间： {}ns",(excendTime-sqlendTime));
        log.info("{}生成附件密码：{}",sqllog.getTitle(),d.getPassword());
        //上传到阿里云
        String url = this.updateFile(os, sqllog);
        sqllog.setDownloadUrl(url);
        //邮件发送
        this.sendMail(sqllog);
        long sendendTime=System.currentTimeMillis(); //获取结束时间
        log.info("文件生成程序运行时间： {}s",(sendendTime-excendTime));
        log.info("总执行时间： {}s",(sendendTime-startTime));
        log.info("{}邮件发送成功",sqllog.getEmail());
        //保存下载
        this.logDownload(sqllog);
        //保存导出结果
        return new DbResult(1000,"已发送邮件密码："+d.getPassword());
    }

    private String updateFile(ByteArrayOutputStream os,KnetSqlDownload sqllog){
        String finalTitle = sqllog.getTitle();
        String url = null;
        try {
            url = UploadUtils.uploadFile(os.toByteArray(), finalTitle +".xlsx", path);
            sqllog.setDownloadUrl(url);
            log.info("生成文件返回路径为:{}",url);
            return url;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    private DbResult sendMail(KnetSqlDownload sqllog){
        MapBuilder map = MapBuilder.build("subject",sqllog.getTitle())
                .ad("to",sqllog.getEmail())
                .ad("content","文件下载地址："+sqllog.getDownloadUrl());
        try {
            Map<String, Object> results= restTemplate.postForObject("http://knet-cloud-mail/mail/send",map,Map.class);
            if (results!=null&&!"1000".equals(results.get("code").toString())) {
                return new DbResult(1002,"发送邮件中台异常");
            }
        } catch (RestClientException e) {
            log.error("发送邮件密码异常:{}",e.getMessage());
            return new DbResult(1002,"发送邮件本地异常");
        }
        return new DbResult(1002,"发送邮件异常");
    }
}
