/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.comm.SuperEJBForMES;
import cn.hanbell.eam.entity.EquipmentRepair;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
                } else if ("repairdeptno".equals(key)) {
                    String deptnoTemp = "";
                    if (value.toString().contains("000")) {
                        deptnoTemp = value.toString().substring(0, 2);
                    } else {
                        deptnoTemp = value.toString().substring(0, 3);
                    }
                    sb.append("  AND e.repairdeptno LIKE '").append(deptnoTemp).append("%'");
                } else if ("ManagerCheck".equals(key)) {
                    exFilterStr.append(" OR e.rstatus = '60'");
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
        sbMES.append(" SELECT A.EQPID,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN*1.0/B.AVAILABLEMINS*1.0*100.0 AS DECIMAL(10,2)) AS Failure FROM (");
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
        sbMES.append(" SELECT A.EQPID,A.counts,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN*1.0/B.AVAILABLEMINS*1.0*100.0 AS DECIMAL(10,2)) AS Failure,cast((B.AVAILABLEMINS*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB,A.MONTH FROM (");
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
        sbMES.append(" SELECT A.EQPID,A.counts,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN*1.0/B.AVAILABLEMINS*1.0*100.0 AS DECIMAL(10,2)) AS Failure,cast((B.AVAILABLEMINS*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB,A.MONTH FROM (");
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
