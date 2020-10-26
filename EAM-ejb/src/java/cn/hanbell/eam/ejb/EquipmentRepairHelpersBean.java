/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentRepairHelpersBean extends SuperEJBForEAM<EquipmentRepairHelpers> {

    public EquipmentRepairHelpersBean() {
        super(EquipmentRepairHelpers.class);
    }

    //获取维修工时明细
    public List<EquipmentRepairHelpers> getEquipmentRepairHelpersList(String staDate, String endDate, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e.id,e.curnode2,e.pid,r.assetno,c.assetDesc,r.hitchtime,r.repairmethod,c.deptname,r.servicearrivetime,r.completetime,e.rtype,e.userno From ");
        sb.append(" EquipmentRepairHelpers e, equipmentrepair r, assetcard c");
        sb.append(" WHERE e.pid = r.formid AND c.formid = r.assetno ");
        if (!"".equals(sql)) {
            sb.append(" AND (").append(sql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND r.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND r.hitchtime< ").append("'").append(endDate).append("'");
        }
        sb.append(" ORDER BY curnode2 ");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    public List<EquipmentRepairHelpers> getEquipmentRepairHelpersList(Map<String, Object> filters, Map<String, String> orderBy, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        filters = strMap;
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }
        if (!"".equals(sql)) {
            sb.append(" AND (").append(sql).append(" )");
        }
        if (orderBy != null && orderBy.size() > 0) {
            sb.append(" ORDER BY ");
            for (final Map.Entry<String, String> o : orderBy.entrySet()) {
                sb.append(" e.").append(o.getKey()).append(" ").append(o.getValue()).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }

        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString());

        //参数赋值
        if (filters != null) {
            this.setQueryParam(query, filters);

        }
        List results = query.getResultList();
        return results;
    }

    //获取维修工时汇总的List
    public List<EquipmentRepairHelpers> getRepairManHourSummaryList(String staDate, String endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT  A.curnode2,COUNT(curnode2),sum( if(rtype=0,userno,'0' ))AS Maintenancehours,sum( if(rtype=1,userno,'0' )) AS auxiliary ,SUM(userno)");
        sb.append(" FROM equipmentrepairhelpers A,equipmentrepair B");
        sb.append(" WHERE A.pid=B.formid");
        if (!"".equals(staDate)) {
            sb.append(" AND B.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND B.hitchtime< ").append("'").append(endDate).append("'");
        }
        sb.append(" GROUP BY A.curnode2  ORDER BY curnode2 DESC");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

}
