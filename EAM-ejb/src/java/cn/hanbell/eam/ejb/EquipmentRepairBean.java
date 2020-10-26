/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentRepair;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import javax.persistence.Query;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentRepairBean extends SuperEJBForEAM<EquipmentRepair> {

    public EquipmentRepairBean() {
        super(EquipmentRepair.class);
    }

    public List<EquipmentRepair> getEquipmentRepairList(Map<String, Object> filters, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"repairuser".equals(key)) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                } else {
                    strMap.put(key, value);
                }

            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get("repairuser")).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get("repairuser")).append("')");
            }
        }

        filters = strMap;

        if (filters != null) {
            this.setQueryFilter(sb, filters);
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

    @Override
    public int getRowCount(Map<String, Object> filters) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(e) FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"repairuser".equals(key)) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                } else {
                    strMap.put(key, value);
                }

            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get("repairuser")).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get("repairuser")).append("')");
            }
        }
        filters = strMap;

        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }

        final Query query = this.getEntityManager().createQuery(sb.toString());
        if (filters != null) {
            this.setQueryParam(query, filters);
        }
        return Integer.parseInt(query.getSingleResult().toString());
    }
    
    
    @Override
    public List<EquipmentRepair> findByFilters(Map<String, Object> filters,int first, int pageSize, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"repairuser".equals(key)) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                } else {
                    strMap.put(key, value);
                }

            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get("repairuser")).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get("repairuser")).append("')");
            }
        }
        filters = strMap;
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }

        if (orderBy != null && orderBy.size() > 0) {
            sb.append(" ORDER BY ");
            for (final Map.Entry<String, String> o : orderBy.entrySet()) {
                sb.append(" e.").append(o.getKey()).append(" ").append(o.getValue()).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }

        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString()).setFirstResult(first).setMaxResults(pageSize);

        //参数赋值
        if (filters != null) {
            this.setQueryParam(query, filters);

        }
        List results = query.getResultList();
        return results;
    }
    //获取显示的进度
    public String getStateName(String str) {
        switch (str) {
            case "10":
                return "已报修";
            case "15":
                return "已受理";
            case "20":
                return "维修到达";
            case "25":
                return "维修中";
            case "28":
                return "维修暂停";
            case "30":
                return "维修完成";
            case "40":
                return "维修验收";
            case "50":
                return "责任回复";
            case "60":
                return "课长审核";
            case "70":
                return "经理审核";
            case "95":
                return "报修结案";
            case "98":
                return "已作废";
            default:
                return "";
        }
    }
    
        //获取维修紧急统计表的List
    public List<EquipmentRepair> getRepairEmergencyStatisticsList(String staDate, String endDate,String assetno,String deptname) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,A.assetDesc,A.deptname,sum(if(R.hitchtype = '03', 1, 0)) AS emergency,sum(if(R.hitchtype = '02', 1, 0)) AS urgent,sum(if(R.hitchtype = '01', 1, 0)) AS general");
        sb.append(" FROM equipmentrepair R, assetcard A");
        sb.append(" WHERE R.assetno = A.formid AND R.hitchtype IS NOT NULL");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (assetno!=null&&!"".equals(assetno)) {
            sb.append(" AND R.assetno Like ").append("'%").append(assetno).append("%'");
        }
        if (deptname!=null&&!"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }
    
    
          //获取维修费用统计表的List
    public List<EquipmentRepair> getRepairCostStatisticsList(String staDate, String endDate,String deptname,String abrasehitch) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.formid, R.assetno,A.assetDesc,A.deptname,R.hitchtime,R.abrasehitch,R.repairmethod,R.repaircost,R.laborcosts,R.sparecost,R.repaircost+R.laborcosts+R.sparecost AS tot, R.serviceusername");
        sb.append(" FROM equipmentrepair R, assetcard A WHERE R.assetno = A.formid  AND R.rstatus='95'");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch!=null&&!"".equals(abrasehitch)) {
            sb.append(" AND R.abrasehitch = ").append("'").append(abrasehitch).append("'");
        }
        if (deptname!=null&&!"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }
}
