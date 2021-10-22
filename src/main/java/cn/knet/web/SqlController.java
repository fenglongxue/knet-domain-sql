package cn.knet.web;

import cn.knet.service.EngineService;
import cn.knet.enums.SqlType;
import cn.knet.vo.DbResult;
import cn.knet.vo.DbResultForPl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("sql")
@Slf4j
public class SqlController {
    @Resource
    EngineService engineService;
    /***
     * 运行操作：可能是更新、查询、表操作
     * @param sqls
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping("/exc")
    @ResponseBody
    public List<DbResult> exc(String sqls[],String type,int pageNumber) throws Exception {
       /* return engineService.exc(new String[]{"update KNET_OFFICIAL_INDUSTRY_CASE set id='2' where id='4bf8025f226c4b63906a38f0ba9f76f2'",
                "update KNET_TEST  t  set t.name='科技有限责任公司董事1',t.profile_id='2' where t.profile_id= '280c96b4a9db417882d9647e347fe823'"},"wz",pageNumber);*/
        return engineService.exc(new String[]{"\n" +
                "\n" +
                "select kpi.keyword 域名,kr.registrant_id 注册人id,\n" +
                "kr.status \n" +
                "审核状态,ka.u_id 代理商id,ka.name 代理商名称,kpi.reg_date 注册时间,kpi.expire_date 到期时间 from knet_product_instance kpi \n" +
                "left join knet_registrant kr on kpi.registrant_id=kr.registrant_id \n" +
                "left join knet_agent ka on kpi.registrar_id=ka.u_id \n" +
                "where kpi.pro_type='domain' and \n" +
                "(kr.status='UNPASS' or kr.id_number='123456' or kr.audit_file is  null) \n" +
                "and (to_char(kpi.expire_date,'yyyy')='2022'  or to_char(kpi.expire_date,'yyyy')='2023' ) ","select * from KNET_TEST "},"wz",50);
    }
    /***
     * 只有更新有执行，每次是执行一条sql语句
     * 返回更新的数据量
     * @param sqls
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateExc")
    @ResponseBody
    public List<DbResult> updateExc(String[] sqls,String type){
        return engineService.updateEngine(new String[]{"update KNET_OFFICIAL_INDUSTRY_CASE set id='2' where id='4bf8025f226c4b63906a38f0ba9f76f2'","update KNET_OFFICIAL_INDUSTRY_CASE set id='3' where id='4bf8025f226c4b63906a38f0ba9f76f2'" },"wz",SqlType.UPDATE);
    }
    @RequestMapping("/updateListForSelect")
    @ResponseBody
    public DbResultForPl updateListForSelect(String sqls[], String type, int pageNumber) throws Exception {
        return engineService.updateListForSelect(new String[]{"update KNET_TEST  t  set t.name='科技有限责任公司董事1' where t.profile_id= '280c96b4a9db417882d9647e347fe823'",
        "update KNET_TEST  t  set t.name='科技有限责任公司董事2',t.profile_id='3' where t.profile_id= '1a371ceda4f8497d9e4ae796869e38a5'",
        "update KNET_TEST  t  set t.name='科技有限责任公司董事3',t.profile_id='4' where t.profile_id= 'dea5267dbaac495b9d3b39459c551f2f'"
},"wz",pageNumber, SqlType.PIUPDATESELECT);
       // return engineService.updateListForSelect(sqls,type,pageNumber, SqlType.PIUPDATESELECT);
    }
    @RequestMapping("/alert")
    @ResponseBody
    public List<DbResult> alert(String sqls[],String type){
        return engineService.alertAnalysisEngine(new String[]{"update KNET_OFFICIAL_INDUSTRY_CASE set id='2' where id='4bf8025f226c4b63906a38f0ba9f76f2'"},"wz",SqlType.UPDATE);
    }
}
