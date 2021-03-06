/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.comm.SuperEJBForMES;
import cn.hanbell.eam.entity.EquipmentRepair;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

    protected SuperEJBForMES lookupSuperEJBForMES() {
        try {
            Context c = new InitialContext();
            return (SuperEJBForMES) c.lookup("java:global/EAM/EAM-ejb/SuperEJBForMES!cn.hanbell.eam.comm.SuperEJBForMES");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

        public List<EquipmentRepair> getEquipmentRepairList(Map<String, Object> filters, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        StringBuilder exFilterStr = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE (1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
      
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"repairuser".equals(key)) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                }
                else if("repairdeptno".equals(key)){
                    String deptnoTemp = "";
                    if(value.toString().contains("000"))
                    {
                        deptnoTemp = value.toString().substring(0,2);
                    }
                    else
                    {
                        deptnoTemp = value.toString().substring(0,3);
                    }
                    sb.append("  AND e.repairdeptno LIKE '").append(deptnoTemp).append("%'");
                }
                else if("ManagerCheck".equals(key)){
                    exFilterStr.append(" OR e.rstatus = '60'");
                }
                else if("ExtraFilter".equals(key)){
                    sb.append(MessageFormat.format(" AND (e.formid LIKE ''%{0}%'' OR e.hitchalarm LIKE ''%{0}%'' OR e.repairusername LIKE ''%{0}%'' OR e.serviceusername LIKE ''%{0}%'')", value.toString()));
                }
                else {
                    strMap.put(key, value);
                }
            }
            else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get("repairuser")).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get("repairuser")).append("')");
            }
        }
        sb.append(")").append(exFilterStr);

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
        Query query = getEntityManager().createQuery(sb.toString()).setMaxResults(50);

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
            if (!("repairuser".equals(key) || "repairmanager".equals(key))) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                } else {
                    strMap.put(key, value);
                }

            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get(key)).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get(key)).append("'");
                if("repairmanager".equals(key))
                {
                    sb.append("  OR e.rstatus = '70'");
                }
                sb.append(")");
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
    public List<EquipmentRepair> findByFilters(Map<String, Object> filters, int first, int pageSize, Map<String, String> orderBy) {
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
    public List<EquipmentRepair> getRepairEmergencyStatisticsList(String staDate, String endDate, String assetno, String deptname, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,A.deptname,sum(if(R.hitchtype = '03', 1, 0)) AS emergency,sum(if(R.hitchtype = '02', 1, 0)) AS urgent,sum(if(R.hitchtype = '01', 1, 0)) AS general,sum(if(R.hitchtype IS NOT NULL , 1, 0)) AS totCount");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid");
        sb.append(" WHERE R.rstatus='95' AND  R.hitchtype IS NOT NULL AND R.assetno IS NOT NULL");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND R.assetno Like ").append("'%").append(assetno).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取维修费用统计表的List
    public List<EquipmentRepair> getRepairCostStatisticsList(String staDate, String endDate, String deptname, String abrasehitch, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.formid,R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname, R.hitchtime,R.abrasehitch,R.repairmethod,R.repaircost,R.laborcosts,R.sparecost,R.repaircost + R.laborcosts + R.sparecost AS tot,R.serviceusername");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE  R.rstatus = '95'");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno Like ").append("'%").append(abrasehitch).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" ORDER BY R.itemno");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取维修费用汇总表的List
    public List<EquipmentRepair> getRepairCostSummaryList(String staDate, String endDate, String deptname, String abrasehitch, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,if(A.deptname IS NULL, R.repairdeptname, A.deptname)  AS deptname,sum(R.repaircost) A ,sum(R.laborcosts) B,sum(R.sparecost) C,sum(R.repaircost+R.laborcosts+R.sparecost) D,COUNT(R.assetno) C");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE   R.rstatus='95'");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno Like ").append("'%").append(abrasehitch).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,if(A.deptname IS NULL, R.repairdeptname, A.deptname)");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取故障责任统计表的List
    public List<EquipmentRepair> getFaultDutyStatisticalList(String staDate, String endDate, String deptname, String abrasehitch, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,if(A.deptname IS NULL, R.repairdeptname, A.deptname)  AS deptname,sum(if(R.abrasehitch = '01', 1, 0)) A ,sum(if(R.abrasehitch = '02', 1, 0)) B,sum(if(R.abrasehitch = '03', 1, 0)) C,sum(if(R.abrasehitch = '04', 1, 0)) D,sum(if(R.abrasehitch = '05', 1, 0)) E");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE R.rstatus='95' AND  R.abrasehitch LIKE '%0%'");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno Like ").append("'%").append(abrasehitch).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,if(A.deptname IS NULL, R.repairdeptname, A.deptname)  ");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取故障类型统计表的List
    public List<EquipmentRepair> getFaultTypeStatisticalList(String staDate, String endDate, String deptname, String abrasehitch, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,if(A.deptname IS NULL, R.repairdeptname, A.deptname),sum(if(R.hitchsort1 = '01', 1, 0)) A ,sum(if(R.hitchsort1 = '02', 1, 0)) B,sum(if(R.hitchsort1 = '03', 1, 0)) C,sum(if(R.hitchsort1 = '04', 1, 0)) D");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE   R.abrasehitch LIKE '%0%' AND R.rstatus='95'");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno Like ").append("'%").append(abrasehitch).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,if(A.deptname IS NULL, R.repairdeptname, A.deptname)");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取现场维修统计表的List
    public List<EquipmentRepair> getRepairSceneStatisticslList(String staDate, String endDate, String deptname, String abrasehitch, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,R.hitchtime,R.hitchdesc,R.repairmethod,R.repairusername");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno=A.formid WHERE R.rstatus='95' AND R.repairmethodtype='2'");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno Like ").append("'%").append(abrasehitch).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,R.repairdeptname");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }
//    获取MTBF和MTTR

    public List<Object[]> getMTBFAndMTTR(String staDate, String endDate, String deptname, String abrasehitch, String sql, String companySql) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT A.EQPID,A.counts,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast((B.AVAILABLEMINS*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB  FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN FROM EQP_RESULT_ALARM");
        sbMES.append(" WHERE ALARMSTARTTIME >= '").append(staDate).append("'AND ALARMSTARTTIME <= '").append(endDate).append("'");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10");
        sbMES.append(" GROUP BY EQPID)A LEFT JOIN (");
        sbMES.append(" SELECT EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS FROM EQP_AVAILABLETIME_SCHEDULE A");
        sbMES.append(" WHERE A.PLANDATE>='").append(staDate).append("' and A.PLANDATE<='").append(endDate).append("' GROUP BY EQPID) B ON A.EQPID=B.EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //生成SQL
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        if (resultsMES.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String EQPID = "";
        EQPID = resultsMES.stream().map(objects -> "'" + objects[0].toString() + "'" + ",").reduce(EQPID, String::concat);
        EQPID = EQPID.substring(0, EQPID.length() - 1);//获取所有该时段下的加工机
        sb.append(" SELECT A.remark,A.formid,A.assetDesc,A.deptname,if(R.time IS NULL ,0,R.time) time FROM");
        sb.append(" (SELECT A.remark,A.formid,A.assetDesc,A.deptname FROM assetcard A WHERE  A.remark IN (").append(EQPID).append(")) A");
        sb.append(" LEFT JOIN (SELECT R.assetno, SUM(TIMESTAMPDIFF(MINUTE, R.servicearrivetime, R.completetime) - R.excepttime) time");
        sb.append(" from equipmentrepair R WHERE R.hitchtime >= '").append(staDate).append("' AND R.hitchtime <= '").append(endDate).append("' GROUP BY R.assetno) R ON  A.formid=R.assetno");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> resultsEAM = query1.getResultList();
        List list = new ArrayList();
        for (Object[] mes : resultsMES) {
            for (Object[] eam : resultsEAM) {
                if (eam[0].equals(mes[0])) {
                    Object[] obj = new Object[9];
                    obj[0] = eam[1];
                    obj[1] = eam[2];
                    obj[2] = eam[3];
                    obj[3] = mes[3];
                    obj[4] = mes[2];
                    obj[5] = mes[1];
                    obj[6] = eam[4];
                    obj[7] = mes[4];
                    obj[8] = mes[5];
                    list.add(obj);
                }
            }
        }
        return list;
    }

    //    获取设备故障率统计表数据
    public List<Object[]> getRepairFaultRateStatistics(String staDate, String endDate, String deptname, String abrasehitch, String companySql) throws ParseException {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT A.EQPID,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN * 1.0 / CASE WHEN B.AVAILABLEMINS=0 then null ELSE  (B.AVAILABLEMINS ) END * 1.0 * 100.0 AS DECIMAL(10, 2)) AS Failure FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN FROM EQP_RESULT_ALARM");
        sbMES.append(" WHERE ALARMSTARTTIME >= '").append(staDate).append("'AND ALARMSTARTTIME <= '").append(endDate).append("'");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10");
        sbMES.append(" GROUP BY EQPID)A LEFT JOIN (");
        sbMES.append(" SELECT EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS FROM EQP_AVAILABLETIME_SCHEDULE A");
        sbMES.append(" WHERE A.PLANDATE>='").append(staDate).append("' and A.PLANDATE<='").append(endDate).append("'  GROUP BY EQPID) B ON A.EQPID=B.EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //生成SQL
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        if (resultsMES.isEmpty()) {
            return null;
        }
        String EQPID = "";
        EQPID = resultsMES.stream().map(objects -> "'" + objects[0].toString() + "'" + ",").reduce(EQPID, String::concat);
        EQPID = EQPID.substring(0, EQPID.length() - 1);//获取所有该时段下的加工机
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT A.remark, A.formid,A.assetDesc,A.deptname FROM assetcard A ");
        sb.append(" WHERE A.remark in (").append(EQPID).append(")");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sb.toString());

        List<Object[]> resultsEAM = query1.getResultList();
        List list = new ArrayList();
        resultsEAM.stream().map(eam -> {
            Object[] obj = new Object[6];
            obj[0] = eam[1];
            obj[1] = eam[2];
            obj[2] = eam[3];
            resultsMES.forEach(mes -> {
                if (eam[0].equals(mes[0])) {
                    obj[3] = mes[2];
                    obj[4] = mes[1];
                    obj[5] = mes[3];
                }
            });
            return obj;
        }).forEachOrdered(obj -> {
            list.add(obj);
        });
        return list;
    }

    //获取一年中每个月的详细数据
    public List<Object[]> getYearMTBFAndMTTR(String year) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT A.EQPID,A.counts,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN * 1.0 / CASE WHEN B.AVAILABLEMINS=0 then null ELSE  (B.AVAILABLEMINS ) END * 1.0 * 100.0 AS DECIMAL(10, 2)) AS Failure,cast((B.AVAILABLEMINS*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB,A.MONTH FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,month(ALARMSTARTTIME) MONTH FROM EQP_RESULT_ALARM WHERE ALARMSTARTTIME LIKE '").append(year).append("%' ");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10 GROUP BY month(ALARMSTARTTIME),EQPID)A LEFT JOIN (");
        sbMES.append(" SELECT EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS,month(PLANDATE) MONTH FROM EQP_AVAILABLETIME_SCHEDULE A");
        sbMES.append(" WHERE PLANDATE LIKE '").append(year).append("%' GROUP BY month(PLANDATE),EQPID) B ON A.EQPID=B.EQPID AND A.MONTH=B.MONTH ORDER BY EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //生成SQL
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        if (resultsMES.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String EQPID = "";
        for (Object[] objects : resultsMES) {
            EQPID += "'" + objects[0] + "'" + ",";
        }
        EQPID = EQPID.substring(0, EQPID.length() - 1);//获取所有该时段下的加工机
        sb.append(" SELECT A.remark, A.formid,A.assetDesc,A.deptname FROM assetcard A ");
        sb.append(" WHERE A.remark in (").append(EQPID).append(")");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> resultsEAM = query1.getResultList();
        List<Object[]> list = new ArrayList();

        for (Object[] mes : resultsMES) {
            for (Object[] eam : resultsEAM) {
                if (eam[0].equals(mes[0])) {
                    Object[] obj = new Object[8];
                    obj[0] = eam[1];
                    obj[1] = eam[2];
                    obj[2] = eam[3];
                    obj[3] = mes[1];
                    obj[4] = mes[4];
                    obj[5] = mes[5];
                    obj[6] = mes[6];
                    obj[7] = mes[7];
                    list.add(obj);
                }
            }
        }
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将资产编号相同的数据存在一起
        list.forEach(objects -> {
            List<Object> assetno = new ArrayList<>();
            String anKey = objects[0].toString();
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        assetno = entry.getValue();
                        assetno.add(objects);
                        moMap.put(anKey, assetno);

                    }
                }
            } else {
                assetno.add(objects);
                moMap.put(anKey, assetno);
            }
        });
        List resultList = new ArrayList();
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            List<?> itemList = entry.getValue();
            List<Object[]> itemList1 = (List<Object[]>) itemList;
            Object[] entity = new Object[51];
            for (int i = 1; i <= 12; i++) {
                for (Object[] obj : itemList1) {
                    entity[0] = obj[0];
                    entity[1] = obj[1];
                    entity[2] = obj[2];
                    if (Integer.parseInt(obj[7].toString()) == i) {
                        entity[3 + (4 * (i - 1))] = obj[3];
                        entity[4 + (4 * (i - 1))] = obj[4];
                        entity[5 + (4 * (i - 1))] = obj[5];
                        entity[6 + (4 * (i - 1))] = obj[6];
                        break;
                    } else {
                        entity[3 + (4 * (i - 1))] = 0;
                        entity[4 + (4 * (i - 1))] = 0;
                        entity[5 + (4 * (i - 1))] = 0;
                        entity[6 + (4 * (i - 1))] = 0;
                    }
                }
            }
            resultList.add(entity);
        }
        return resultList;
    }

//获取传入的年份中的数据
    public List<Object[]> getEveryYearMTBFAndMTTR(String staYear, String endYear) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT A.EQPID,A.counts,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN * 1.0 / CASE WHEN B.AVAILABLEMINS=0 then null ELSE  (B.AVAILABLEMINS ) END * 1.0 * 100.0 AS DECIMAL(10, 2)) AS Failure,cast((B.AVAILABLEMINS*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB,A.MONTH FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,year(ALARMSTARTTIME) MONTH FROM EQP_RESULT_ALARM WHERE ALARMSTARTTIME >= '").append(staYear).append("' AND ALARMSTARTTIME<'").append(endYear).append("'");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10 GROUP BY year(ALARMSTARTTIME),EQPID)A LEFT JOIN (");
        sbMES.append(" SELECT EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS,year(PLANDATE) MONTH FROM EQP_AVAILABLETIME_SCHEDULE A");
        sbMES.append(" WHERE PLANDATE >= '").append(staYear).append("' AND PLANDATE<='").append(endYear).append("'").append(" GROUP BY year(PLANDATE),EQPID) B ON A.EQPID=B.EQPID AND A.MONTH=B.MONTH ORDER BY EQPID,A.MONTH");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //生成SQL
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        if (resultsMES.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String EQPID = "";
        for (Object[] objects : resultsMES) {
            EQPID += "'" + objects[0] + "'" + ",";
        }
        if (!"".equals(EQPID)) {
            EQPID = EQPID.substring(0, EQPID.length() - 1);//获取所有该时段下的加工机 
        }
        sb.append(" SELECT A.remark, A.formid,A.assetDesc,A.deptname FROM assetcard A ");
        sb.append(" WHERE A.remark in (").append(EQPID).append(")");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> resultsEAM = query1.getResultList();
        List<Object[]> list = new ArrayList();

        for (Object[] mes : resultsMES) {
            for (Object[] eam : resultsEAM) {
                if (eam[0].equals(mes[0])) {
                    Object[] obj = new Object[8];
                    obj[0] = eam[1];
                    obj[1] = eam[2];
                    obj[2] = eam[3];
                    obj[3] = mes[1];
                    obj[4] = mes[4];
                    obj[5] = mes[5];
                    obj[6] = mes[6];
                    obj[7] = mes[7];
                    list.add(obj);
                }
            }
        }
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将资产编号相同的数据存在一起
        list.forEach(objects -> {
            List<Object> assetno = new ArrayList<>();
            String anKey = objects[0].toString();
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        assetno = entry.getValue();
                        assetno.add(objects);
                        moMap.put(anKey, assetno);

                    }
                }
            } else {
                assetno.add(objects);
                moMap.put(anKey, assetno);
            }
        });
        List resultList = new ArrayList();
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            List<?> itemList = entry.getValue();
            List<Object[]> itemList1 = (List<Object[]>) itemList;
            int sum = 3 + (Integer.parseInt(endYear) - Integer.parseInt(staYear)) * 3;
            Object[] entity = new Object[sum];
            for (int i = Integer.parseInt(staYear); i < Integer.parseInt(endYear); i++) {
                for (Object[] obj : itemList1) {
                    entity[0] = obj[0];
                    entity[1] = obj[1];
                    entity[2] = obj[2];
                    if (Integer.parseInt(obj[7].toString()) == i) {
                        entity[3 + (3 * (i - Integer.parseInt(staYear)))] = obj[4];
                        entity[4 + (3 * (i - Integer.parseInt(staYear)))] = obj[5];
                        entity[5 + (3 * (i - Integer.parseInt(staYear)))] = obj[6];
                        break;
                    } else {
                        entity[3 + (3 * (i - Integer.parseInt(staYear)))] = 0;
                        entity[4 + (3 * (i - Integer.parseInt(staYear)))] = 0;
                        entity[5 + (3 * (i - Integer.parseInt(staYear)))] = 0;

                    }
                }
            }
            resultList.add(entity);
        }
        return resultList;
    }

    /**
     * 获取车间月报数据
     *
     * @param year 获取数据的年份
     * @param type 获取的车间
     * @param reportType 根据选择的版本调换取值数据及计算方法
     * @return
     */
    public List<Object[]> getMonthlyReport(String year, String type, String reportType) {
        StringBuilder sb = new StringBuilder();
        String str = type.substring(3, type.length());//获取不良数的类型
        sb.append(" SELECT A.AVAILABLEMINS, A.AVA,B.counts,C.count60 count60 ,B.ALARMTIME_LEN,   cast(ROUND(((A.AVAILABLEMINS*1.0-B.ALARMTIME_LEN*1.0)/A.AVAILABLEMINS*1.0 * 100.0 ),2)AS DECIMAL(10, 2))  AS Hmovable ,cast(ROUND(((A.AVA*1.0-B.ALARMTIME_LEN*1.0)/A.AVA*1.0 * 100.0 ),2)AS DECIMAL(10, 2)) AS Gmovable,cast(round((B.ALARMTIME_LEN*1.0/A.AVAILABLEMINS*1.0*100),2)AS DECIMAL(10, 2)) FAULT,");
        sb.append(" B.ALARMTIME_LEN/B.counts MTTR,A.AVA/B.counts GMTBF,((A.AVAILABLEMINS-B.ALARMTIME_LEN)/B.counts) HMTBF,D.abnormal,cast((A.AVAILABLEMINS*1.0-D.abnormal*1.0)/A.AVAILABLEMINS*1.0* 100.0 AS DECIMAL(10, 2)) AS HtimeEveryMove,cast((A.AVAILABLEMINS*1.0-D.abnormal*1.0)/A.AVAILABLEMINS*1.0* 100.0 AS DECIMAL(10, 2)) AS GtimeEveryMove,cast(E.MINUTE/(A.AVAILABLEMINS-D.abnormal)*100 AS DECIMAL(10, 2)) Hperformance,cast(E.MINUTE/(A.AVAILABLEMINS-D.abnormal)*100 AS DECIMAL(10, 2)) Gperformance, cast((E.QTY*1.0-G.QGSUM*1.0)/E.QTY*1.0 * 100.0 AS DECIMAL(10, 2))   yieldRate,E.QTY,B.MONTH  FROM  (");
        sb.append(" SELECT SUM(A.AVAILABLEMINS) AVAILABLEMINS,A.MONTH,SUM(A.AVAILABLEMINS)/COUNT(A.EQPID) AVA,COUNT(A.EQPID) EQPIDCount  FROM ( SELECT E.EQPID,SUM(E.AVAILABLEMINS) AVAILABLEMINS, month(E.PLANDATE)  MONTH");
        sb.append(" FROM EQP_AVAILABLETIME_SCHEDULE E LEFT JOIN MEQP M ON E.EQPID=M.EQPID WHERE E.PLANDATE LIKE '").append(year).append("%' AND E.AVAILABLEMINS!=0 AND M.PRODUCTTYPE='").append(type).append("' GROUP BY month(E.PLANDATE), E.EQPID) A");
        sb.append(" GROUP BY A.MONTH) A RIGHT JOIN ( SELECT SUM(A.counts) counts,SUM(A.ALARMTIME_LEN) ALARMTIME_LEN,A.MONTH FROM ( SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sb.append(" month(E.ALARMSTARTTIME)MONTH FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' AND M.ALARMTYPE = '").append(type).append("'");
        sb.append(" AND datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 GROUP BY month(E.ALARMSTARTTIME), E.EQPID ) A GROUP BY A.MONTH) B ON A.MONTH=B.MONTH LEFT JOIN");
        sb.append(" (SELECT  count(EQPID) count60,month(ALARMSTARTTIME) MONTH FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sb.append(" WHERE datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME) > 60 and  E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' AND M.ALARMTYPE = '").append(type).append("'");
        sb.append(" GROUP BY month(ALARMSTARTTIME)) C ON C.MONTH=B.MONTH LEFT JOIN (SELECT SUM(A.ALARMTIME_LEN) abnormal,A.MONTH FROM ( SELECT sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sb.append(" month(E.ALARMSTARTTIME)MONTH FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMTYPE = '").append(type).append("'");
        sb.append(" GROUP BY month(E.ALARMSTARTTIME), E.EQPID ) A GROUP BY A.MONTH) D ON D.MONTH=B.MONTH");
        sb.append(" LEFT JOIN (SELECT SUM(A.QTY) QTY,SUM(A.MINUTE) MINUTE ,A.MONTH FROM (SELECT COUNT(A.EQPID) QTY,sum(round(B.REAL_TIME / 60, 1)) MINUTE ,month(PROCESSCOMPLETETIME)  MONTH");
        sb.append(" FROM PROCESS_STEP  A   LEFT JOIN PROCESS_STEP_TIME B ON A.SYSID = B.SYSID AND A.EQPID = B.EQPID LEFT JOIN  MEQP C ON C.EQPID = B.EQPID");
        sb.append(" WHERE C.PRODUCTTYPE = '").append(type).append("' AND A.PROCESSCOMPLETETIME LIKE'").append(year).append("%' GROUP BY  month(PROCESSCOMPLETETIME)) A GROUP BY A.MONTH) E ON E.MONTH=B.MONTH");
        sb.append(" LEFT JOIN (SELECT SUM (DEFECTNUM) AS QGSUM,month(B.PROJECTCREATETIME) MONTH FROM ANALYSISRESULT_QCD  A LEFT JOIN  FLOW_FORM_UQF_S_NOW B ON  A.PROJECTID=B.PROJECTID");
        sb.append(" WHERE  B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND SOURCESTEPIP LIKE '").append(str).append("%' AND B.UQFTYPE ='UQFG0003' GROUP BY month(B.PROJECTCREATETIME))G ON G.MONTH=B.MONTH");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> resultsMES = query.getResultList();
        List<Object[]> list = new ArrayList();
        Object[] obj1 = new Object[13];
        Object[] obj2 = new Object[13];
        Object[] obj3 = new Object[13];
        Object[] obj4 = new Object[13];
        Object[] obj5 = new Object[13];
        Object[] obj6 = new Object[13];
        Object[] obj7 = new Object[13];
        Object[] obj8 = new Object[13];
        Object[] obj9 = new Object[13];
        Object[] obj10 = new Object[13];
        Object[] obj11 = new Object[13];
        Object[] obj12 = new Object[13];
        obj1[0] = "月度生产总工时(分)";
        obj2[0] = "故障件数合计(件)";
        obj3[0] = "故障停机工时合计(分)";
        obj4[0] = "设备可动率(%)";
        obj12[0] = "设备故障率";
        obj5[0] = "MTTR(分/件)";
        obj6[0] = "MTBF(分/件)";
        obj7[0] = "时间稼动率(%)";
        obj8[0] = "性能稼动率(%)";
        obj9[0] = "良率(%)";
        obj10[0] = "月平均OEE85％";
        obj11[0] = "故障60分以上件数";
        for (int i = 1; i <= 12; i++) {
            for (Object[] mes : resultsMES) {
                if (i == Integer.parseInt(mes[18].toString())) {
                    if (mes[1] != null) {
                        if (reportType.equals("H")) {
                            obj1[i] = mes[0];
                        } else {
                            obj1[i] = mes[1];
                        }
                    } else {
                        obj1[i] = 0;
                    }
                    if (mes[2] != null) {
                        obj2[i] = mes[2];
                    } else {
                        obj2[i] = 0;
                    }
                    obj3[i] = mes[4];
                    if (mes[5] != null) {
                        if (reportType.equals("H")) {
                            obj4[i] = mes[5];
                        } else {
                            obj4[i] = mes[6];
                        }
                    } else {
                        obj4[i] = 0;
                    }

                    if (mes[7] != null) {
                        obj12[i] = mes[7];
                    } else {
                        obj12[i] = 0;
                    }

                    obj5[i] = mes[8];
                    if (mes[10] != null) {
                        if (reportType.equals("H")) {
                            obj6[i] = mes[10];
                        } else {
                            obj6[i] = mes[9];
                        }
                    } else {
                        obj6[i] = 0;
                    }

                    if (mes[12] != null) {
                        if (reportType.equals("H")) {
                            obj7[i] = mes[12];
                        } else {
                            obj7[i] = mes[13];
                        }
                    } else {
                        obj7[i] = 100.00;
                    }

                    if (mes[14] != null) {
                        if (reportType.equals("H")) {
                            obj8[i] = mes[14];
                        } else {
                            obj8[i] = mes[15];
                        }
                    } else {
                        obj8[i] = 100.00;
                    }

                    if (mes[16] != null) {
                        obj9[i] = mes[16];
                    } else {
                        obj9[i] = 100.00;
                    }
                    double monthOEE = (Double.parseDouble(obj7[i].toString()) / 100 * Double.parseDouble(obj8[i].toString()) / 100 * Double.parseDouble(obj9[i].toString()) / 100) * 100;
                    if (String.valueOf(monthOEE).length() >= 5) {
                        obj10[i] = String.valueOf(monthOEE).substring(0, 5);
                    } else {
                        obj10[i] = monthOEE;
                    }

                    if (mes[3] == null) {
                        obj11[i] = 0;
                    } else {
                        obj11[i] = mes[3];
                    }
                    break;
                } else {
                    obj1[i] = 0;
                    obj2[i] = 0;
                    obj3[i] = 0;
                    obj4[i] = 0;
                    obj5[i] = 0;
                    obj6[i] = 0;
                    obj7[i] = 0;
                    obj8[i] = 0;
                    obj9[i] = 0;
                    obj10[i] = 0;
                    obj11[i] = 0;
                    obj12[i] = 0;
                }
            }
        }
        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        if (reportType.equals("H")) {
            list.add(obj12);
        }
        list.add(obj5);
        list.add(obj6);
        list.add(obj7);
        list.add(obj8);
        list.add(obj9);
        list.add(obj10);
        list.add(obj11);
        return list;
    }

    /**
     * @param time 需要查询的年月分 如:XXXX/XX
     * @param type 查询的数据是方型车间还是圆形车间
     * @return 返回故障件数统计表数据
     */
    public List getMonthFailuresNumberTable(String time, String type) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT B.EQPID,B.AVAILABLEMINS,A.counts,A.ALARMTIME_LEN,B.DAY FROM (SELECT A.EQPID,SUM(A.counts) counts,SUM(A.ALARMTIME_LEN) ALARMTIME_LEN,A.day FROM (");
        sbMES.append(" SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sbMES.append(" day(E.ALARMSTARTTIME) day FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMES.append(" WHERE E.ALARMSTARTTIME LIKE '").append(time).append("%' AND M.ALARMNAME = '设备故障' AND M.ALARMTYPE = '").append(type).append("' AND datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 GROUP BY day(E.ALARMSTARTTIME), E.EQPID");
        sbMES.append(" ) A GROUP BY  A.EQPID, A.day ) A RIGHT JOIN  (SELECT E.EQPID,AVAILABLEMINS,DAY(PLANDATE) DAY FROM EQP_AVAILABLETIME_SCHEDULE E");
        sbMES.append(" LEFT JOIN MEQP M ON E.EQPID=M.EQPID WHERE PLANDATE LIKE '").append(time).append("%' AND AVAILABLEMINS!=0 AND M.PRODUCTTYPE='").append(type).append("') B");
        sbMES.append(" ON A.day=B.DAY AND A.EQPID=B.EQPID ORDER BY B.EQPID,B.DAY");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        //更改在EAM里类别名称说明   
        if (type.equals("半成品方型件")) {
            type = "方型加工课";
        } else {
            type = "圆型加工课";
        }
        time = time.replace("/", "-");//在EAM中时间格式替换
        StringBuilder sbEAM = new StringBuilder();
        sbEAM.append(" SELECT A.formid,1440 ,COUNT(A.formid),TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime) TIME, DAY(R.hitchtime) day ,A.assetDesc");
        sbEAM.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno=A.formid WHERE R.hitchtime>'").append(time).append("' AND A.deptname='").append(type).append("' AND A.remark IS NULL  AND R.rstatus>='30' AND R.rstatus<'98'");
        sbEAM.append(" AND TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime)>10 GROUP BY  A.formid,DAY(R.hitchtime)");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query1.getResultList();
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将EQPID相同的数据存在一起 MES的
        resultsMES.forEach(objects -> {
            List<Object> EQPID = new ArrayList<>();
            String anKey = objects[0].toString();
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        EQPID = entry.getValue();
                        EQPID.add(objects);
                        moMap.put(anKey, EQPID);

                    }
                }
            } else {
                EQPID.add(objects);
                moMap.put(anKey, EQPID);
            }
        });
        //将编号相同的数据存在一起 MEA的
        resultsEAM.forEach(objects -> {
            List<Object> formid = new ArrayList<>();
            String anKey = objects[0].toString();
            objects[0] = objects[0].toString() + "      " + objects[5].toString();//将编号和设备名称拼接
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        formid = entry.getValue();
                        formid.add(objects);
                        moMap.put(anKey, formid);
                    }
                }
            } else {
                formid.add(objects);
                moMap.put(anKey, formid);
            }
        });
        List<Object> list = new ArrayList<>();
        List<Object[]> faultCount = new ArrayList<>();//故障次数集合
        List<Object[]> faultTime = new ArrayList<>();//故障时间集合
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            List<?> itemList = entry.getValue();
            List<Object[]> itemList1 = (List<Object[]>) itemList;
            Object[] obj1 = new Object[36];
            Object[] obj2 = new Object[36];
            Object[] obj3 = new Object[36];
            int single1 = 0;
            int single2 = 0;
            int single3 = 0;
            for (int i = 1; i <= 31; i++) {
                for (Object[] item : itemList1) {
                    obj1[0] = item[0];
                    obj2[0] = item[0];
                    obj3[0] = item[0];
                    obj1[1] = "稼动时间(分)";
                    obj2[1] = "故障次数";
                    obj3[1] = "故障时间(分)";
                    if (i == Integer.parseInt(item[4].toString())) {
                        if (item[1] != null) {
                            single1 += Double.parseDouble(item[1].toString());
                        }
                        if (item[2] != null) {
                            single2 += Double.parseDouble(item[2].toString());
                        }
                        if (item[3] != null) {
                            single3 += Double.parseDouble(item[3].toString());
                        }
                        obj1[i + 1] = item[1];
                        obj2[i + 1] = item[2];
                        obj3[i + 1] = item[3];
                    }
                    if (item.length == 6) {//将EAM的计划工时都赋值为1440
                        obj1[i + 1] = 1440;
                        single1 = 44640;
                    }
                }
            }
            obj1[33] = single1;
            obj2[33] = single2;
            obj3[33] = single3;
            obj1[34] = single1 / 31;
            obj2[34] = single2 == 0 ? 0 : (single1 - single3) / single2;
            obj3[34] = single2 == 0 ? 0 : single3 / single2;
            obj1[35] = "日均工时";
            obj2[35] = "MTBF";
            obj3[35] = "MTTR";
            list.add(obj1);
            list.add(obj2);
            list.add(obj3);
            faultCount.add(obj2);
            faultTime.add(obj3);
        }
        Object[] count = new Object[36];
        Object[] date = new Object[36];
        for (Object[] object : faultTime) {
            for (int i = 1; i <= 31; i++) {
                if (object[i + 1] != null) {
                    if (date[i + 1] != null) {
                        date[i + 1] = Double.parseDouble(date[i + 1].toString()) + Double.parseDouble(object[i + 1].toString());
                    } else {
                        date[i + 1] = Double.parseDouble(object[i + 1].toString());
                    }
                } else {
                    if (date[i + 1] == null) {
                        date[i + 1] = 0;
                    }
                }
            }
        }
        for (Object[] object : faultCount) {
            for (int i = 1; i <= 31; i++) {
                if (object[i + 1] != null) {
                    if (count[i + 1] != null) {
                        count[i + 1] = Double.parseDouble(count[i + 1].toString()) + Double.parseDouble(object[i + 1].toString());
                    } else {
                        count[i + 1] = Double.parseDouble(object[i + 1].toString());
                    }
                } else {
                    if (count[i + 1] == null) {
                        count[i + 1] = 0;
                    }
                }
            }
        }
        count[0] = "当日小计";
        count[1] = "故障次数";
        date[0] = "当日小计";
        date[1] = "故障时间";
        if (!resultsMES.isEmpty() || !resultsEAM.isEmpty()) {
            list.add(0, count);
            list.add(1, date);
        }
        return list;
    }

    /**
     * 获取年故障件数统计数据
     *
     * @param year 获取数据的年份
     * @param type 获取的数据的车间类别
     * @return 返回List为整年统计数据
     */
    public List getYearFailuresNumberTable(String year, String type) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT B.EQPID,B.AVAILABLEMINS,A.counts,A.ALARMTIME_LEN,B.month FROM (");
        sbMES.append(" SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sbMES.append(" month(E.ALARMSTARTTIME) month FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMES.append(" WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' AND M.ALARMTYPE = '").append(type).append("'");
        sbMES.append(" AND datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 GROUP BY month(E.ALARMSTARTTIME), E.EQPID) A RIGHT JOIN (");
        sbMES.append(" SELECT E.EQPID,sum(AVAILABLEMINS) AVAILABLEMINS ,month(PLANDATE) month FROM EQP_AVAILABLETIME_SCHEDULE E LEFT JOIN MEQP M ON E.EQPID=M.EQPID");
        sbMES.append(" WHERE PLANDATE LIKE '").append(year).append("%' AND AVAILABLEMINS!=0 AND M.PRODUCTTYPE='").append(type).append("' GROUP BY month(PLANDATE),E.EQPID) B ON A.month=B.month AND A.EQPID=B.EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        //更改在EAM里类别名称说明   
        if (type.equals("半成品方型件")) {
            type = "方型加工课";
        } else {
            type = "圆型加工课";
        }

        StringBuilder sbEAM = new StringBuilder();
        sbEAM.append(" SELECT A.formid,44640 ,COUNT(A.formid),TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime) TIME, MONTH(R.hitchtime) day ,A.assetDesc");
        sbEAM.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno=A.formid WHERE R.hitchtime LIKE '").append(year).append("%' AND A.deptname='").append(type).append("' AND A.remark IS NULL  AND R.rstatus>='30' AND R.rstatus<'98'");
        sbEAM.append(" AND TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime)>10 GROUP BY  A.formid,MONTH(R.hitchtime)");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query1.getResultList();
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将EQPID相同的数据存在一起 MES的
        resultsMES.forEach(objects -> {
            List<Object> EQPID = new ArrayList<>();
            String anKey = objects[0].toString();
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        EQPID = entry.getValue();
                        EQPID.add(objects);
                        moMap.put(anKey, EQPID);

                    }
                }
            } else {
                EQPID.add(objects);
                moMap.put(anKey, EQPID);
            }
        });
        //将编号相同的数据存在一起 MEA的
        resultsEAM.forEach(objects -> {
            List<Object> formid = new ArrayList<>();
            String anKey = objects[0].toString();
            objects[0] = objects[0].toString() + "      " + objects[5].toString();//将编号和设备名称拼接
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        formid = entry.getValue();
                        formid.add(objects);
                        moMap.put(anKey, formid);
                    }
                }
            } else {
                formid.add(objects);
                moMap.put(anKey, formid);
            }
        });
        List<Object> list = new ArrayList<>();
        List<Object[]> faultCount = new ArrayList<>();//故障次数集合
        List<Object[]> faultTime = new ArrayList<>();//故障时间集合
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            List<?> itemList = entry.getValue();
            List<Object[]> itemList1 = (List<Object[]>) itemList;
            Object[] obj1 = new Object[17];
            Object[] obj2 = new Object[17];
            Object[] obj3 = new Object[17];
            int single1 = 0;
            int single2 = 0;
            int single3 = 0;
            for (int i = 1; i <= 12; i++) {
                for (Object[] item : itemList1) {
                    obj1[0] = item[0];
                    obj2[0] = item[0];
                    obj3[0] = item[0];
                    obj1[1] = "稼动时间(分)";
                    obj2[1] = "故障次数";
                    obj3[1] = "故障时间(分)";
                    if (i == Integer.parseInt(item[4].toString())) {
                        if (item[1] != null) {
                            single1 += Double.parseDouble(item[1].toString());
                        }
                        if (item[2] != null) {
                            single2 += Double.parseDouble(item[2].toString());
                        }
                        if (item[3] != null) {
                            single3 += Double.parseDouble(item[3].toString());
                        }
                        obj1[i + 1] = item[1];
                        obj2[i + 1] = item[2];
                        obj3[i + 1] = item[3];
                    }
                    if (item.length == 6) {//将EAM的计划工时都赋值为44640
                        obj1[i + 1] = 44640;
                        single1 = 44640 * 12;
                    }
                }
            }
            obj1[14] = single1;
            obj2[14] = single2;
            obj3[14] = single3;
            obj1[15] = single1 / 12;
            obj2[15] = single2 == 0 ? 0 : (single1 - single3) / single2;
            obj3[15] = single2 == 0 ? 0 : single3 / single2;
            obj1[16] = "月均工时";
            obj2[16] = "MTBF";
            obj3[16] = "MTTR";
            list.add(obj1);
            list.add(obj2);
            list.add(obj3);
            faultCount.add(obj2);
            faultTime.add(obj3);
        }
        Object[] count = new Object[17];
        Object[] date = new Object[17];
        for (Object[] object : faultTime) {
            for (int i = 1; i <= 12; i++) {
                if (object[i + 1] != null) {
                    if (date[i + 1] != null) {
                        date[i + 1] = Double.parseDouble(date[i + 1].toString()) + Double.parseDouble(object[i + 1].toString());
                    } else {
                        date[i + 1] = Double.parseDouble(object[i + 1].toString());
                    }
                } else {
                    if (date[i + 1] == null) {
                        date[i + 1] = 0;
                    }
                }
            }
        }
        for (Object[] object : faultCount) {
            for (int i = 1; i <= 12; i++) {
                if (object[i + 1] != null) {
                    if (count[i + 1] != null) {
                        count[i + 1] = Double.parseDouble(count[i + 1].toString()) + Double.parseDouble(object[i + 1].toString());
                    } else {
                        count[i + 1] = Double.parseDouble(object[i + 1].toString());
                    }
                } else {
                    if (count[i + 1] == null) {
                        count[i + 1] = 0;
                    }
                }
            }
        }
        count[0] = "当月小计";
        count[1] = "故障次数";
        date[0] = "当月小计";
        date[1] = "故障时间";
        if (!resultsMES.isEmpty() || !resultsEAM.isEmpty()) {
            list.add(0, count);
            list.add(1, date);
        }
        return list;
    }

    /**
     * 获取总合效率表的数据
     *
     * @param year 获取的年月，格式为XXXX/XX
     * @param EPQID 获取的加工机编号
     * @return 返回总合效率表数据
     */
    public List getEquipmentTotalEfficiency(String year, String EPQID) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT A.day, 1440,CASE WHEN D.estimateNUM IS NULL THEN 0 ELSE D.estimateNUM END,C.QTY,C.MINUTE,CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END,1440-A.AVAILABLEMINS,B.停机待测,B.生技试模,B.欠料等待,B.刀具调试更换,B.停机换模,B.物料返修,B.上下料干涉等待,B.单模拆装,B.共用刀具模具等待,B.带教新人讲解,B.绿灯暖机,B.其他,B.未填,B.计划停机,B.设备故障,CASE WHEN A.counts IS NULL THEN 0 ELSE A.counts END counts,(A.AVAILABLEMINS-(1440-A.AVAILABLEMINS))/A.AVAILABLEMINS*100,1440-B.计划停机");
        sbMES.append(" ,A.AVAILABLEMINS-B.SUMLEN JiadongTime,C.QTY- (CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END) productQTY,convert(decimal(10,2),(C.QTY*1.0- (CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END))/C.QTY*100,2) productRate,convert(decimal(10,2),round((A.AVAILABLEMINS-B.设备故障*1.0)/A.AVAILABLEMINS*100,2))  equipmentRate,");
        sbMES.append(" convert(decimal(10,2),round(C.MINUTE/(A.AVAILABLEMINS-B.SUMLEN)*100,2)) xingNengRate,convert(decimal(10,2),round((A.AVAILABLEMINS-B.SUMLEN*1.0)/A.AVAILABLEMINS*100,2))  timeRate,convert(decimal(10,2),round(((C.QTY- (CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END))/C.QTY)*(C.MINUTE/(A.AVAILABLEMINS-B.SUMLEN))*((A.AVAILABLEMINS-B.SUMLEN*1.0)/A.AVAILABLEMINS*100),2)) OEE,B.设备故障/A.counts MTTR,CASE WHEN (A.AVAILABLEMINS-B.设备故障)/A.counts IS NULL THEN A.AVAILABLEMINS ELSE (A.AVAILABLEMINS-B.设备故障)/A.counts END MTBF");
        sbMES.append(" FROM ( SELECT B.EQPID,B.AVAILABLEMINS,A.counts,A.ALARMTIME_LEN,B.day  FROM ( SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN, day(E.ALARMSTARTTIME) day FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMES.append(" WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' GROUP BY day(E.ALARMSTARTTIME), E.EQPID) A RIGHT JOIN ( SELECT E.EQPID,sum(AVAILABLEMINS) AVAILABLEMINS ,day(PLANDATE) day FROM MEQP M  LEFT JOIN EQP_AVAILABLETIME_SCHEDULE E  ON E.EQPID=M.EQPID WHERE PLANDATE LIKE '").append(year).append("%' AND E.EQPID='").append(EPQID).append("' AND AVAILABLEMINS!=0");
        sbMES.append(" GROUP BY day(PLANDATE),E.EQPID) B ON A.day=B.day AND A.EQPID=B.EQPID) A LEFT JOIN (SELECT  A.DAY,MAX( case A.ALARMNAME when '设备故障' then A.ALARMTIME_LEN else 0 end) 设备故障,MAX( case A.ALARMNAME when '停机待测' then A.ALARMTIME_LEN else 0 end) 停机待测,MAX( case A.ALARMNAME when '生技试模' then A.ALARMTIME_LEN else 0 end) 生技试模,MAX( case A.ALARMNAME when '欠料等待' then A.ALARMTIME_LEN else 0 end) 欠料等待,MAX( case A.ALARMNAME when '刀具调试更换' then A.ALARMTIME_LEN else 0 end) 刀具调试更换,");
        sbMES.append(" MAX( case A.ALARMNAME when '停机换模' then A.ALARMTIME_LEN else 0 end) 停机换模,MAX( case A.ALARMNAME when '物料返修' then A.ALARMTIME_LEN else 0 end) 物料返修,MAX( case A.ALARMNAME when '计划停机' then A.ALARMTIME_LEN else 0 end) 计划停机,MAX( case A.ALARMNAME when '绿灯暖机' then A.ALARMTIME_LEN else 0 end) 绿灯暖机,MAX( case A.ALARMNAME when NULL then A.ALARMTIME_LEN else 0 end) 未填,MAX( case A.ALARMNAME when '上下料干涉等待' then A.ALARMTIME_LEN else 0 end) 上下料干涉等待,MAX( case A.ALARMNAME when '单模拆装' then A.ALARMTIME_LEN else 0 end) 单模拆装,");
        sbMES.append(" MAX( case A.ALARMNAME when '其他' then A.ALARMTIME_LEN else 0 end) 其他,MAX( case A.ALARMNAME when '共用刀具模具等待' then A.ALARMTIME_LEN else 0 end) 共用刀具模具等待,MAX( case A.ALARMNAME when '带教新人讲解' then A.ALARMTIME_LEN else 0 end) 带教新人讲解,SUM(A.ALARMTIME_LEN) SUMLEN FROM (");
        sbMES.append(" SELECT A.ALARMNAME,SUM(convert(INT, ALARMTIME_LEN))/60 ALARMTIME_LEN,DAY(A.DATE) DAY FROM (SELECT EQPID, B.ALARMNAME,convert(VARCHAR(10), ALARMSTARTTIME, 111) AS DATE, ALARMTIME_LEN FROM EQP_RESULT_ALARM A LEFT JOIN MALARM B ON A.SPECIALALARMID = B.ALARMID WHERE EQPID='").append(EPQID).append("' AND A.ALARMSTARTTIME LIKE '").append(year).append("%') A GROUP BY A.ALARMNAME,DAY(A.DATE)) A GROUP BY A.DAY) B ON A.day=B.DAY");
        sbMES.append(" LEFT JOIN (SELECT COUNT(A.EQPID) AS QTY, round(SUM(REAL_TIME) / 60, 1) AS MINUTE,day FROM(SELECT A.EQPID, B.REAL_TIME,day(PROCESSCOMPLETETIME) AS day FROM MEQP C LEFT JOIN PROCESS_STEP A ON A.EQPID = C.EQPID LEFT JOIN PROCESS_STEP_TIME B ON A.PRODUCTCOMPID = B.PRODUCTCOMPID AND A.EQPID = B.EQPID AND A.PRODUCTORDERID = B.PRODUCTORDERID AND A.SYSID = B.SYSID AND A.STEPID = B.STEPID WHERE");
        sbMES.append("  A.PROCESSCOMPLETETIME LIKE '").append(year).append("%' AND A.EQPID='").append(EPQID).append("' AND A.PROCESSCOMPLETETIME IS NOT NULL) A GROUP BY  day) C ON C.day=A.day LEFT JOIN (SELECT SUM(convert(INT, NUM)) estimateNUM,day(PLANDATE) day FROM PLAN_SEMI_SQUARE WHERE EQPID ='").append(EPQID).append("'  AND PLANDATE LIKE '").append(year).append("%'  GROUP BY DAY(PLANDATE)) D ON D.day=A.day");
        sbMES.append(" LEFT JOIN ( SELECT SUM ( convert(INT, DEFECTNUM)) AS DEFECTSUM,day(B.PROJECTCREATETIME) day FROM  FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD  A  ON  A.PROJECTID=B.PROJECTID left join PROCESS_STEP S ON A.PRODUCTORDERID =S.PRODUCTORDERID AND A.COMPIDSEQ =S.PRODUCTSERIALNUMBER AND B.SOURCESTEPIP=S.STEPID");
        sbMES.append(" WHERE  B.PROJECTID LIKE 'QC%' AND B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND B.UQFTYPE ='UQFG0003' AND S.EQPID ='").append(EPQID).append("' GROUP BY day(B.PROJECTCREATETIME)) E ON E.day=A.day");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        List<Object[]> list = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            Object[] obj = new Object[36];
            for (Object[] objects : resultsMES) {
                if (objects[0].equals(i)) {
                    obj = objects;
                }
            }
            if (obj[0] == null) {
                obj[0] = i;
            }
            list.add(obj);
        }

        return list;
    }

    /**
     * 获取加工机的EOQID
     *
     * @return
     */
    public List getEPQID() {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append("SELECT EQPID FROM MEQP WHERE EQPTYPEID ='实体设备' ORDER BY  EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        return resultsMES;
    }

    /**
     * 根据输入的str获取本月的第一天和最后一条 str=1 end=0为第一天 str=0 end=1为最好一天
     *
     * @param str
     * @return
     */
    public Date getMonthDay(int str) {
        Calendar calendar = Calendar.getInstance();
        if (str == 1) {
            str = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else if (str == 0) {
            str = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        calendar.set(Calendar.DAY_OF_MONTH, str);
        return calendar.getTime();
    }

}
