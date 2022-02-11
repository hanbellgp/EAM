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
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        String exFilterStr = "";
        String companyFilter = "";
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
                    exFilterStr = " OR e.rstatus = '60'";
                } else if ("ExtraFilter".equals(key)) {
                    sb.append(MessageFormat.format(" AND (e.formid LIKE ''%{0}%'' OR e.hitchalarm LIKE ''%{0}%'' OR e.repairusername LIKE ''%{0}%'' OR e.serviceusername LIKE ''%{0}%'')", value.toString()));
                } else if ("RepairCompanyFilter".equals(key)) {

                    if ("HANBELL".equals(value)) {
                        companyFilter = "  AND e.company IN ('C','K')";
                    } else if ("HANSON".equals(value)) {
                        companyFilter = "  AND e.company IN ('H','E')";
                    } else if ("HANYOUNG".equals(value)) {
                        companyFilter = "  AND e.company = 'Y'";
                    }

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

        if (companyFilter != null && !companyFilter.equals("")) {
            sb.append(companyFilter);
            if (exFilterStr != null && !exFilterStr.equals("")) {
                exFilterStr = " OR (e.rstatus = '60'" + companyFilter + ") ";
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
            if (!("repairuser".equals(key) || "repairmanager".equals(key))) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                } else if ("RepairCompanyFilter".equals(key)) {
                    if ("HANBELL".equals(value)) {
                        sb.append("  AND e.company IN ('C','K')");
                    } else if ("HANSON".equals(value)) {
                        sb.append("  AND e.company IN ('H','E')");
                    } else if ("HANYOUNG".equals(value)) {
                        sb.append("  AND e.company = 'Y'");
                    }
                } else {
                    strMap.put(key, value);
                }

            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get(key)).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get(key)).append("'");
                if ("repairmanager".equals(key)) {
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

    public List<EquipmentRepair> getEquipmentRepairListByNativeQuery(Map<String, Object> filters, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        StringBuilder exFilterStr = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(this.className);
        sb.append(" equipmentrepair WHERE (1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"repairuser".equals(key)) {
                if ("ALL".equals(value)) {
                    sb.append("  AND rstatus<'95'");
                } else if ("repairdeptno".equals(key)) {
                    String deptnoTemp = "";
                    if (value.toString().contains("000")) {
                        deptnoTemp = value.toString().substring(0, 2);
                    } else {
                        deptnoTemp = value.toString().substring(0, 3);
                    }
                    sb.append("  AND repairdeptno LIKE '").append(deptnoTemp).append("%'");
                } else if ("ManagerCheck".equals(key)) {
                    exFilterStr.append(" OR rstatus = '60'");
                } else if ("ExtraFilter".equals(key)) {
                    sb.append(MessageFormat.format(" AND (formid LIKE ''%{0}%'' OR hitchalarm LIKE ''%{0}%'' OR repairusername LIKE ''%{0}%'' OR serviceusername LIKE ''%{0}%'')", value.toString()));
                } else if ("formdateBegin".equals(key)) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    String formdateBeginStr = fmt.format(new Date(value.toString()));
                    sb.append(MessageFormat.format(" AND formdate >= ''{0}''", formdateBeginStr));
                } else if ("formdateEnd".equals(key)) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    String formdateEndStr = fmt.format(new Date(value.toString()));
                    sb.append(MessageFormat.format(" AND formdate <= ''{0}''", formdateEndStr));
                } else if ("RepairmentDelay".equals(key)) {
                    sb.append(" AND ((rstatus >= '20' AND rstatus < '28' AND TIMESTAMPDIFF(HOUR,servicearrivetime,now()) > 0) OR (rstatus >= '10' AND rstatus < '20' AND TIMESTAMPDIFF(HOUR,credate,now()) > 0)) ");
                } else {
                    strMap.put(key, value);
                }
            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (repairuser = '");
                sb.append(filters.get("repairuser")).append("'");
                sb.append("  OR hitchdutyuser = '");
                sb.append(filters.get("repairuser")).append("')");
            }
        }
        sb.append(")").append(exFilterStr);

        filters = strMap;
        setNativeQueryFilter(sb, filters);

        if (orderBy != null && orderBy.size() > 0) {
            sb.append(" ORDER BY ");
            for (final Map.Entry<String, String> o : orderBy.entrySet()) {
                sb.append(o.getKey()).append(" ").append(o.getValue()).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }

        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentRepair.class).setMaxResults(50);

        List<EquipmentRepair> results = query.getResultList();
        return results;
    }

    private void setNativeQueryFilter(StringBuilder queryStrBuilder, Map<String, Object> filters) {
        filters.forEach((key, value) -> {
            queryStrBuilder.append(MessageFormat.format(" AND {0} LIKE ''%{1}%''", key, value.toString()));
        });
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
        sb.append(" from equipmentrepair R WHERE R.hitchtime >= '").append(staDate).append("' AND R.hitchtime <= '").append(endDate).append("' AND R.rstatus !='98' GROUP BY R.assetno) R ON  A.formid=R.assetno ");
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
        sbMES.append(" SELECT B.EQPID,A.counts,A.ALARMTIME_LEN,B.AVAILABLEMINS,cast(A.ALARMTIME_LEN * 1.0 / CASE WHEN B.AVAILABLEMINS=0 then null ELSE  (B.AVAILABLEMINS ) END * 1.0 * 100.0 AS DECIMAL(10, 2)) AS Failure,cast((B.AVAILABLEMINS*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB,B.MONTH FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,month(ALARMSTARTTIME) MONTH FROM EQP_RESULT_ALARM WHERE ALARMSTARTTIME LIKE '").append(year).append("%' ");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10 GROUP BY month(ALARMSTARTTIME),EQPID)A RIGHT JOIN (");
        sbMES.append(" SELECT EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS,month(PLANDATE) MONTH FROM EQP_AVAILABLETIME_SCHEDULE A");
        sbMES.append(" WHERE PLANDATE LIKE '").append(year).append("%' AND PLANDATE< getdate() and AVAILABLEMINS!=0 GROUP BY month(PLANDATE),EQPID) B ON A.EQPID=B.EQPID AND A.MONTH=B.MONTH ORDER BY EQPID");
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
                    if (mes[5] == null) {
                        obj[5] = mes[3];
                    } else {
                        obj[5] = mes[5];
                    }
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
        String str = type.substring(3, type.length());//获取不良数的类型
        //月度总计划工时及平均计划工时,当月设备数量
        StringBuilder sbAVA = new StringBuilder();
        sbAVA.append(" SELECT A.MONTH,SUM(A.AVAILABLEMINS) AVAILABLEMINS,SUM(A.AVAILABLEMINS)/COUNT(A.EQPID) AVA,COUNT(A.EQPID) EQPIDCount   FROM ( SELECT E.EQPID,SUM(E.AVAILABLEMINS) AVAILABLEMINS, month(E.PLANDATE)  MONTH FROM EQP_AVAILABLETIME_SCHEDULE E");
        sbAVA.append(" LEFT JOIN MEQP M ON E.EQPID=M.EQPID WHERE E.PLANDATE LIKE '").append(year).append("%' AND E.PLANDATE< getdate()  AND E.AVAILABLEMINS!=0 AND M.PRODUCTTYPE='").append(type).append("'");
        sbAVA.append(" GROUP BY month(E.PLANDATE), E.EQPID) A GROUP BY A.MONTH");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbAVA.toString());
        List<Object[]> avaList = query.getResultList();
        //获取MTBF总合
        StringBuilder sbMTBF = new StringBuilder();
        sbMTBF.append(" SELECT A.MONTH,SUM(A.MTBF) FROM ( SELECT CASE WHEN   (cast((B.AVAILABLEMINS * 1.0 - A.ALARMTIME_LEN * 1.0) / A.counts * 1.0 AS DECIMAL(10, 2))) IS NULL THEN B.AVAILABLEMINS ELSE cast((B.AVAILABLEMINS * 1.0 - A.ALARMTIME_LEN * 1.0) / A.counts * 1.0 AS DECIMAL(10, 2)) END   MTBF,");
        sbMTBF.append(" B.MONTH  FROM (SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,month(ALARMSTARTTIME) MONTH");
        sbMTBF.append(" FROM EQP_RESULT_ALARM WHERE ALARMSTARTTIME LIKE '").append(year).append("%' AND (SPECIALALARMID = 'B0001' OR SPECIALALARMID = 'A0001') AND");
        sbMTBF.append(" datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME) > 10 GROUP BY month(ALARMSTARTTIME), EQPID) A RIGHT JOIN (SELECT A.EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS,month(PLANDATE) MONTH");
        sbMTBF.append(" FROM EQP_AVAILABLETIME_SCHEDULE A LEFT JOIN MEQP M ON A.EQPID = M.EQPID");
        sbMTBF.append(" WHERE PLANDATE LIKE '").append(year).append("%' AND PLANDATE< getdate()  and AVAILABLEMINS!=0  AND  M.PRODUCTTYPE = '").append(type).append("'");
        sbMTBF.append(" GROUP BY month(PLANDATE), A.EQPID) B ON A.EQPID = B.EQPID AND A.MONTH = B.MONTH )A  GROUP BY  A.MONTH");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMTBF.toString());
        List<Object[]> mtbfList = query.getResultList();

        //-- 大于10分钟的故障次数和60分钟以上的故障次数及故障停机时间和其他总的异常时间
        StringBuilder sbCount = new StringBuilder();
        sbCount.append(" SELECT  A.MONTH, SUM(A.counts) counts10,sum(A.counts60) counts60,(CASE WHEN SUM(A.ALARMTIME_LEN) IS NULL THEN 0 ELSE SUM(A.ALARMTIME_LEN) END) ALARMTIME_LEN,SUM(A.abnormal) abnormal");
        sbCount.append(" FROM ( SELECT E.EQPID, COUNT(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 60 AND M.ALARMNAME = '设备故障' THEN E.EQPID END ) counts60, COUNT(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 AND M.ALARMNAME = '设备故障'  THEN E.EQPID END ) counts,");
        sbCount.append(" sum(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 AND M.ALARMNAME = '设备故障'  THEN  datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME )END) AS ALARMTIME_LEN,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS abnormal,");
        sbCount.append(" month(E.ALARMSTARTTIME)MONTH FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%'  AND M.ALARMTYPE = '").append(type).append("' GROUP BY month(E.ALARMSTARTTIME), E.EQPID ) A GROUP BY A.MONTH");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbCount.toString());
        List<Object[]> countList = query.getResultList();

        //每月报工数及标准工时
        StringBuilder sbQty = new StringBuilder();
        sbQty.append(" SELECT month(PROCESSCOMPLETETIME) MONTH ,count(CASE WHEN A.STEPID LIKE '%").append(str).append("清洗%' THEN A.EQPID END )  QTY,sum(round(B.STD_TIME / 60, 1)) MINUTE ");
        sbQty.append(" FROM PROCESS_STEP  A   LEFT JOIN PROCESS_STEP_TIME B ON A.SYSID = B.SYSID  AND A.EQPID = B.EQPID");
        sbQty.append(" LEFT JOIN  MEQP C ON C.EQPID = A.EQPID WHERE C.PRODUCTTYPE = '").append(type).append("'  AND A.PROCESSCOMPLETETIME LIKE'%").append(year).append("%' GROUP BY  month(PROCESSCOMPLETETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbQty.toString());
        List<Object[]> qtyList = query.getResultList();

        //不良数
        StringBuilder sbQG = new StringBuilder();
        sbQG.append(" SELECT month(B.PROJECTCREATETIME) MONTH, SUM(DEFECTNUM) AS QGSUM FROM ANALYSISRESULT_QCD A LEFT JOIN  FLOW_FORM_UQF_S_NOW B ON  A.PROJECTID=B.PROJECTID");
        sbQG.append(" WHERE  B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND SOURCESTEPIP LIKE '").append(str).append("%' AND B.UQFTYPE ='UQFG0003' GROUP BY month(B.PROJECTCREATETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbQG.toString());
        List<Object[]> qgList = query.getResultList();
        //在报修系统中获取故障时间为60分钟以上的次数
        StringBuilder sb60Count = new StringBuilder();
        String strTypeString = "";
        if (type.equals("半成品方型件")) {
            strTypeString = "方型加工课";
        } else {
            strTypeString = "圆型加工课";
        }
        sb60Count.append(" SELECT month(hitchtime),count(CASE WHEN TIMESTAMPDIFF(MINUTE,hitchtime, completetime) > 60 THEN E.assetno END) as count60,count(*) count10 FROM equipmentrepair E LEFT JOIN assetcard A ON E.assetno=A.formid ");
        sb60Count.append(" WHERE A.remark IS NOT NULL AND TIMESTAMPDIFF(MINUTE, hitchtime,completetime)>10 AND A.deptname='").append(strTypeString).append("' AND hitchtime LIKE '%").append(year).append("%'");
        sb60Count.append(" AND hitchtime<now() AND hitchurgency='03' GROUP BY month(hitchtime)");
        query = getEntityManager().createNativeQuery(sb60Count.toString());
        List<Object[]> sb60CountList = query.getResultList();
        //将所有的数据按所需要的模板整合到一个List中
        List<Object[]> listMES = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Object[] obj = new Object[12];
            for (Object[] ava : avaList) {
                if (i == Integer.parseInt(ava[0].toString())) {
                    obj[0] = ava[1];
                    obj[1] = ava[2];
                    obj[11] = ava[3];
                }
            }
            for (Object[] count : countList) {
                if (i == Integer.parseInt(count[0].toString())) {

                    obj[4] = count[3];
                    obj[5] = count[4];
                }
            }
            for (Object[] qty : qtyList) {
                if (i == Integer.parseInt(qty[0].toString())) {
                    obj[6] = qty[1];
                    obj[7] = qty[2];
                }
            }
            for (Object[] qg : qgList) {
                if (i == Integer.parseInt(qg[0].toString())) {
                    obj[8] = qg[1];
                }
            }
            for (Object[] mtbf : mtbfList) {
                if (i == Integer.parseInt(mtbf[0].toString())) {
                    obj[10] = mtbf[1];
                }
            }
            for (Object[] count60 : sb60CountList) {
                if (i == Integer.parseInt(count60[0].toString())) {
                    obj[3] = count60[1];
                    obj[2] = count60[2];
                }
            }
            obj[9] = i;
            listMES.add(obj);
        }
        //将List中的数据经过模板中的公式进行计算处理在存入新的List中
        List<Object[]> resultsMES = new ArrayList<>();
        for (Object[] oMes : listMES) {
            if (oMes[0] != null) {
                Object[] obj = new Object[19];
                obj[0] = oMes[0];
                obj[1] = oMes[1];
                obj[2] = oMes[2];
                obj[3] = oMes[3];
                obj[4] = oMes[4];
                BigDecimal HAVA = BigDecimal.valueOf(Double.valueOf(oMes[0].toString()));//月度总生产工时
                BigDecimal GAVA = BigDecimal.valueOf(Double.valueOf(oMes[1].toString()));//月度平均生产工时
                BigDecimal ALA = BigDecimal.valueOf(Double.valueOf(oMes[4].toString()));//故障停机时间
                BigDecimal count10;
                if (oMes[2] == null) {
                    count10 = BigDecimal.ONE;
                } else {
                    count10 = BigDecimal.valueOf(Double.valueOf(oMes[2].toString()));//10分钟以上的故障次数
                }
                BigDecimal abnormal = BigDecimal.valueOf(Double.valueOf(oMes[5].toString()));//总异常时间
                BigDecimal MINUTE = BigDecimal.valueOf(Double.valueOf(oMes[7].toString()));//产出标准工时
                BigDecimal QTY = BigDecimal.valueOf(Double.valueOf(oMes[6].toString()));//报工数
                BigDecimal QGQTY = BigDecimal.valueOf(Double.valueOf(oMes[8].toString()));//不良数
                BigDecimal MTBF = BigDecimal.valueOf(Double.valueOf(oMes[10].toString()));//各加工机MTBF总合
                BigDecimal avaCount = BigDecimal.valueOf(Double.valueOf(oMes[11].toString()));//所有加工机数量
                //汉钟版设备可动率
                obj[5] = ((HAVA.subtract(ALA)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//月度生产总工时-故障停机时间/月度生产总工时
                //顾问版设备可动率
                obj[6] = ((GAVA.subtract(ALA)).divide(GAVA, 4, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//月度平均生产总工时-故障停机时间/月度平均生产总工时
                //设备故障率(%)   故障停机工时合计/月度生产总工时
                obj[7] = ALA.divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //MTTR   故障停机工时合计/故障件数合计
                obj[8] = ALA.divide(count10, 4, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                //顾问版MTBF   月度平均生产总工时/故障件数合计
                obj[9] = GAVA.divide(count10, 4, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(60), 0, BigDecimal.ROUND_HALF_UP);
                //汉钟版MTBF   月度生产总工时-故障停机时间/故障件数合计
                obj[10] = (HAVA.subtract(ALA)).divide(count10, 0, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(60), 0, BigDecimal.ROUND_HALF_UP);

                obj[11] = oMes[5];
                //时间稼动率   （月度生产总工时-总异常时间）/月度生产总工时
                obj[12] = (HAVA.subtract(abnormal)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                obj[13] = (HAVA.subtract(abnormal)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //性能稼动率   产出标准工时/(月度生产总工时-总异常时间)
                obj[14] = MINUTE.divide((HAVA.subtract(abnormal)), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                obj[15] = MINUTE.divide((HAVA.subtract(abnormal)), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //良率  报工数-不良单据)/报工数(不良不论厂内外责任，只要经过加工就计入)
                obj[16] = (QTY.subtract(QGQTY)).divide(QTY, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                obj[17] = QTY;
                obj[18] = oMes[9];//月份
                resultsMES.add(obj);
            }
        }

        List<Object[]> list = new ArrayList();
        Object[] obj1 = new Object[14];
        Object[] obj2 = new Object[14];
        Object[] obj3 = new Object[14];
        Object[] obj4 = new Object[14];
        Object[] obj5 = new Object[14];
        Object[] obj6 = new Object[14];
        Object[] obj7 = new Object[14];
        Object[] obj8 = new Object[14];
        Object[] obj9 = new Object[14];
        Object[] obj10 = new Object[14];
        Object[] obj11 = new Object[14];
        Object[] obj12 = new Object[14];
        obj1[0] = "月度生产总工时(分)";
        obj1[13] = "=全车间单台设备计划工时总和";
        obj2[0] = "故障件数合计(件)";
        obj2[13] = "=全设备故障件数总和(停线10分钟以上,报修开始到维修结束)来源MES异常报表";
        obj3[0] = "故障停机工时合计(分)";
        obj3[13] = "=全设备故障时间总和(停线10分钟以上,报修开始到维修结束) 来源MES异常报表";
        obj4[0] = "设备可动率(%)";
        obj4[13] = "=(月度生产总工时-故障停机工时合计)/月度生产总工时";
        obj5[0] = "MTTR(分/件)";
        obj5[13] = "=故障停机工时合计/故障件数合计";
        obj6[0] = "MTBF(小时/件)";
        obj6[13] = "=全车间单台设备MTBF总和均值(单台的MTBF=(计划工作时间-故障停机时间)/维修次数)\n"
                + "仅记录停线10分钟以上数据  ";
        obj7[0] = "时间稼动率(%)";
        obj7[13] = "=（厂内生管计划工时-异常报表工时合计）/厂内生管计划工时";
        obj8[0] = "性能稼动率(%)";
        obj8[13] = "=产出标准工时/(厂内生管计划工时-异常报表工时合计)";
        obj9[0] = "良率(%)";
        obj9[13] = "=(报工数-不良单据)/报工数(不良不论厂内外责任，只要经过加工就计入)";
        obj10[0] = "月平均OEE(%)";
        obj10[13] = "=时间稼动率*性能稼动率*良率";
        obj11[0] = "故障60分以上件数";
        obj11[13] = "=全车间单台设备计划工时总和";
        obj12[0] = "设备故障率(%)";
        obj12[13] = "=全设备故障件数总和(停线60分钟以上,报修开始到维修结束)来源MES异常报表";

        for (int i = 1; i <= 12; i++) {
            for (Object[] mes : resultsMES) {
                if (i == Integer.parseInt(mes[18].toString())) {
                    if (mes[1] != null) {
                        if (reportType.equals("H")) {
                            obj1[i] = mes[0];
                        } else {
                            obj1[i] = mes[1];
                        }
                    }
                    if (mes[2] != null) {
                        obj2[i] = mes[2];
                    }
                    obj3[i] = mes[4];
                    if (mes[5] != null) {
                        if (reportType.equals("H")) {
                            obj4[i] = mes[5];
                        } else {
                            obj4[i] = mes[6];
                        }
                    }

                    if (mes[7] != null) {
                        obj12[i] = mes[7];
                    }

                    obj5[i] = mes[8];
                    if (mes[10] != null) {
                        if (reportType.equals("H")) {
                            obj6[i] = mes[10];
                        } else {
                            obj6[i] = mes[9];
                        }
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
     * 获取车间日报数据
     *
     * @param year 获取数据的年份
     * @param type 获取的车间
     * @param reportType 根据选择的版本调换取值数据及计算方法
     * @return
     */
    public List<Object[]> getDayReport(String year, String type, String reportType) {
        String str = type.substring(3, type.length());//获取不良数的类型
        //月度总计划工时及平均计划工时,当月设备数量
        StringBuilder sbAVA = new StringBuilder();
        sbAVA.append(" SELECT A.DAY,SUM(A.AVAILABLEMINS) AVAILABLEMINS,SUM(A.AVAILABLEMINS)/COUNT(A.EQPID) AVA,COUNT(A.EQPID) EQPIDCount   FROM ( SELECT E.EQPID,SUM(E.AVAILABLEMINS) AVAILABLEMINS, DAY(E.PLANDATE)  DAY FROM EQP_AVAILABLETIME_SCHEDULE E");
        sbAVA.append(" LEFT JOIN MEQP M ON E.EQPID=M.EQPID WHERE E.PLANDATE LIKE '").append(year).append("%' AND E.PLANDATE< getdate()  AND E.AVAILABLEMINS!=0 AND M.PRODUCTTYPE='").append(type).append("'");
        sbAVA.append(" GROUP BY DAY(E.PLANDATE), E.EQPID) A GROUP BY A.DAY");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbAVA.toString());
        List<Object[]> avaList = query.getResultList();
        //获取MTBF总合
        StringBuilder sbMTBF = new StringBuilder();
        sbMTBF.append(" SELECT A.DAY,SUM(A.MTBF) FROM ( SELECT CASE WHEN   (cast((B.AVAILABLEMINS * 1.0 - A.ALARMTIME_LEN * 1.0) / A.counts * 1.0 AS DECIMAL(10, 2))) IS NULL THEN B.AVAILABLEMINS ELSE cast((B.AVAILABLEMINS * 1.0 - A.ALARMTIME_LEN * 1.0) / A.counts * 1.0 AS DECIMAL(10, 2)) END   MTBF,");
        sbMTBF.append(" B.DAY  FROM (SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,DAY(ALARMSTARTTIME) DAY");
        sbMTBF.append(" FROM EQP_RESULT_ALARM WHERE ALARMSTARTTIME LIKE '").append(year).append("%' AND (SPECIALALARMID = 'B0001' OR SPECIALALARMID = 'A0001') AND");
        sbMTBF.append(" datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME) > 10 GROUP BY DAY(ALARMSTARTTIME), EQPID) A RIGHT JOIN (SELECT A.EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS,DAY(PLANDATE) DAY");
        sbMTBF.append(" FROM EQP_AVAILABLETIME_SCHEDULE A LEFT JOIN MEQP M ON A.EQPID = M.EQPID");
        sbMTBF.append(" WHERE PLANDATE LIKE '").append(year).append("%' AND PLANDATE< getdate()  and AVAILABLEMINS!=0  AND  M.PRODUCTTYPE = '").append(type).append("'");
        sbMTBF.append(" GROUP BY DAY(PLANDATE), A.EQPID) B ON A.EQPID = B.EQPID AND A.DAY = B.DAY )A  GROUP BY  A.DAY");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMTBF.toString());
        List<Object[]> mtbfList = query.getResultList();

        //-- 大于10分钟的故障次数和60分钟以上的故障次数及故障停机时间和其他总的异常时间
        StringBuilder sbCount = new StringBuilder();
        sbCount.append(" SELECT  A.DAY, SUM(A.counts) counts10,sum(A.counts60) counts60,(CASE WHEN SUM(A.ALARMTIME_LEN) IS NULL THEN 0 ELSE SUM(A.ALARMTIME_LEN) END) ALARMTIME_LEN,SUM(A.abnormal) abnormal");
        sbCount.append(" FROM ( SELECT E.EQPID, COUNT(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 60 AND M.ALARMNAME = '设备故障' THEN E.EQPID END ) counts60, COUNT(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 AND M.ALARMNAME = '设备故障'  THEN E.EQPID END ) counts,");
        sbCount.append(" sum(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 AND M.ALARMNAME = '设备故障'  THEN  datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME )END) AS ALARMTIME_LEN,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS abnormal,");
        sbCount.append(" DAY(E.ALARMSTARTTIME) DAY FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%'  AND M.ALARMTYPE = '").append(type).append("' GROUP BY DAY(E.ALARMSTARTTIME), E.EQPID ) A GROUP BY A.DAY");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbCount.toString());
        List<Object[]> countList = query.getResultList();

        //每月报工数及标准工时
        StringBuilder sbQty = new StringBuilder();
        sbQty.append(" SELECT DAY(PROCESSCOMPLETETIME) DAY ,count(CASE WHEN A.STEPID LIKE '%").append(str).append("清洗%' THEN A.EQPID END )  QTY,sum(round(B.STD_TIME / 60, 1)) MINUTE ");
        sbQty.append(" FROM PROCESS_STEP  A   LEFT JOIN PROCESS_STEP_TIME B ON A.SYSID = B.SYSID  AND A.EQPID = B.EQPID");
        sbQty.append(" LEFT JOIN  MEQP C ON C.EQPID = A.EQPID WHERE C.PRODUCTTYPE = '").append(type).append("'  AND A.PROCESSCOMPLETETIME LIKE'%").append(year).append("%' GROUP BY  DAY(PROCESSCOMPLETETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbQty.toString());
        List<Object[]> qtyList = query.getResultList();

        //不良数
        StringBuilder sbQG = new StringBuilder();
        sbQG.append(" SELECT DAY(B.PROJECTCREATETIME) DAY, SUM(DEFECTNUM) AS QGSUM FROM ANALYSISRESULT_QCD A LEFT JOIN  FLOW_FORM_UQF_S_NOW B ON  A.PROJECTID=B.PROJECTID");
        sbQG.append(" WHERE  B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND SOURCESTEPIP LIKE '").append(str).append("%' AND B.UQFTYPE ='UQFG0003' GROUP BY DAY(B.PROJECTCREATETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbQG.toString());
        List<Object[]> qgList = query.getResultList();
        //在报修系统中获取故障时间为60分钟以上的次数
        StringBuilder sb60Count = new StringBuilder();
        String strTypeString = "";
        if (type.equals("半成品方型件")) {
            strTypeString = "方型加工课";
        } else {
            strTypeString = "圆型加工课";
        }
        year = year.replace("/", "-");//在EAM中时间格式替换

        sb60Count.append(" SELECT DAY(hitchtime),count(CASE WHEN TIMESTAMPDIFF(MINUTE,hitchtime, completetime) > 60 THEN E.assetno END) as count60,count(*) count10 FROM equipmentrepair E LEFT JOIN assetcard A ON E.assetno=A.formid ");
        sb60Count.append(" WHERE A.remark IS NOT NULL AND TIMESTAMPDIFF(MINUTE, hitchtime,completetime)>10 AND A.deptname='").append(strTypeString).append("' AND hitchtime LIKE '%").append(year).append("%'");
        sb60Count.append(" AND hitchtime<now() AND hitchurgency='03' GROUP BY DAY(hitchtime)");
        query = getEntityManager().createNativeQuery(sb60Count.toString());
        List<Object[]> sb60CountList = query.getResultList();
        //将所有的数据按所需要的模板整合到一个List中
        List<Object[]> listMES = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            Object[] obj = new Object[12];
            for (Object[] ava : avaList) {
                if (i == Integer.parseInt(ava[0].toString())) {
                    obj[0] = ava[1];
                    obj[1] = ava[2];
                    obj[11] = ava[3];
                }
            }
            for (Object[] count : countList) {
                if (i == Integer.parseInt(count[0].toString())) {

                    obj[4] = count[3];
                    obj[5] = count[4];
                }
            }
            for (Object[] qty : qtyList) {
                if (i == Integer.parseInt(qty[0].toString())) {
                    obj[6] = qty[1];
                    obj[7] = qty[2];
                }
            }
            for (Object[] qg : qgList) {
                if (i == Integer.parseInt(qg[0].toString())) {
                    obj[8] = qg[1];
                }
            }
            for (Object[] mtbf : mtbfList) {
                if (i == Integer.parseInt(mtbf[0].toString())) {
                    obj[10] = mtbf[1];
                }
            }
            for (Object[] count60 : sb60CountList) {
                if (i == Integer.parseInt(count60[0].toString())) {
                    obj[3] = count60[1];
                    obj[2] = count60[2];
                }
            }
            obj[9] = i;
            listMES.add(obj);
        }
        //将List中的数据经过模板中的公式进行计算处理在存入新的List中
        List<Object[]> resultsMES = new ArrayList<>();
        for (Object[] oMes : listMES) {
            if (oMes[7] != null) {
                Object[] obj = new Object[19];
                obj[0] = oMes[0];
                obj[1] = oMes[1];
                obj[2] = oMes[2];
                obj[3] = oMes[3];
                obj[4] = oMes[4];
                BigDecimal HAVA = BigDecimal.valueOf(Double.valueOf(oMes[0].toString()));//月度总生产工时
                BigDecimal GAVA = BigDecimal.valueOf(Double.valueOf(oMes[1].toString()));//月度平均生产工时
                BigDecimal ALA = BigDecimal.valueOf(Double.valueOf(oMes[4].toString()));//故障停机时间
                BigDecimal count10;
                if (oMes[2] == null) {
                    count10 = BigDecimal.ONE;
                } else {
                    count10 = BigDecimal.valueOf(Double.valueOf(oMes[2].toString()));//10分钟以上的故障次数
                }
                BigDecimal abnormal = BigDecimal.valueOf(Double.valueOf(oMes[5].toString()));//总异常时间
                BigDecimal MINUTE = BigDecimal.valueOf(Double.valueOf(oMes[7].toString()));//产出标准工时
                BigDecimal QTY = BigDecimal.valueOf(Double.valueOf(oMes[6].toString()));//报工数
                BigDecimal QGQTY = BigDecimal.valueOf(Double.valueOf(oMes[8].toString()));//不良数
                BigDecimal MTBF = BigDecimal.valueOf(Double.valueOf(oMes[10].toString()));//各加工机MTBF总合
                BigDecimal avaCount = BigDecimal.valueOf(Double.valueOf(oMes[11].toString()));//所有加工机数量
                //汉钟版设备可动率
                obj[5] = ((HAVA.subtract(ALA)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//月度生产总工时-故障停机时间/月度生产总工时
                //顾问版设备可动率
                obj[6] = ((GAVA.subtract(ALA)).divide(GAVA, 4, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//月度平均生产总工时-故障停机时间/月度平均生产总工时
                //设备故障率(%)   故障停机工时合计/月度生产总工时
                obj[7] = ALA.divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //MTTR   故障停机工时合计/故障件数合计
                obj[8] = ALA.divide(count10, 4, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                //顾问版MTBF   月度平均生产总工时/故障件数合计
                obj[9] = GAVA.divide(count10, 4, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(60), 0, BigDecimal.ROUND_HALF_UP);
                //汉钟版MTBF   月度生产总工时-故障停机时间/故障件数合计
                obj[10] = (HAVA.subtract(ALA)).divide(count10, 0, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(60), 0, BigDecimal.ROUND_HALF_UP);

                obj[11] = oMes[5];
                //时间稼动率   （月度生产总工时-总异常时间）/月度生产总工时
                obj[12] = (HAVA.subtract(abnormal)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                obj[13] = (HAVA.subtract(abnormal)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //性能稼动率   产出标准工时/(月度生产总工时-总异常时间)
                obj[14] = MINUTE.divide((HAVA.subtract(abnormal)), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                obj[15] = MINUTE.divide((HAVA.subtract(abnormal)), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //良率  报工数-不良单据)/报工数(不良不论厂内外责任，只要经过加工就计入)
                if (QTY.equals(BigDecimal.ZERO)) {
                    obj[16] = 0;
                } else {
                    obj[16] = (QTY.subtract(QGQTY)).divide(QTY, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                }

                obj[17] = QTY;
                obj[18] = oMes[9];//月份
                resultsMES.add(obj);
            }
        }

        List<Object[]> list = new ArrayList();
        Object[] obj1 = new Object[32];
        Object[] obj2 = new Object[32];
        Object[] obj3 = new Object[32];
        Object[] obj4 = new Object[32];
        Object[] obj5 = new Object[32];
        Object[] obj6 = new Object[32];
        Object[] obj7 = new Object[32];
        Object[] obj8 = new Object[32];
        Object[] obj9 = new Object[32];
        Object[] obj10 = new Object[32];
        Object[] obj11 = new Object[32];
        Object[] obj12 = new Object[32];
        obj1[0] = "月度生产总工时(分)";
        obj2[0] = "故障件数合计(件)";
        obj3[0] = "故障停机工时合计(分)";
        obj4[0] = "设备可动率(%)";
        obj5[0] = "MTTR(分/件)";
        obj6[0] = "MTBF(小时/件)";
        obj7[0] = "时间稼动率(%)";
        obj8[0] = "性能稼动率(%)";
        obj9[0] = "良率(%)";
        obj10[0] = "月平均OEE(%)";
        obj11[0] = "故障60分以上件数";
        obj12[0] = "设备故障率(%)";

        for (int i = 1; i <= 31; i++) {
            for (Object[] mes : resultsMES) {
                if (i == Integer.parseInt(mes[18].toString())) {
                    if (mes[1] != null) {
                        if (reportType.equals("H")) {
                            obj1[i] = mes[0];
                        } else {
                            obj1[i] = mes[1];
                        }
                    }
                    if (mes[2] != null) {
                        obj2[i] = mes[2];
                    }
                    obj3[i] = mes[4];
                    if (mes[5] != null) {
                        if (reportType.equals("H")) {
                            obj4[i] = mes[5];
                        } else {
                            obj4[i] = mes[6];
                        }
                    }

                    if (mes[7] != null) {
                        obj12[i] = mes[7];
                    }

                    obj5[i] = mes[8];
                    if (mes[10] != null) {
                        if (reportType.equals("H")) {
                            obj6[i] = mes[10];
                        } else {
                            obj6[i] = mes[9];
                        }
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
        StringBuilder sbMES = new StringBuilder();//mes对应年月故障件数统计表SQL
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
        } else if (type.equals("半成品圆型件")) {
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
        //将编号相同的数据存在一起 EAM的
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
     * 获取故障明细
     *
     * @param type
     * @return
     */
    public List getFaultDetail(String type) {
        StringBuilder sbEAM = new StringBuilder();
        if (type.equals("半成品方型件")) {
            type = "方型加工课";
        } else {
            type = "圆型加工课";
        }
        sbEAM.append(" SELECT E.hitchtime,A.deptname,A.remark,E.serviceusername,TIMESTAMPDIFF(MINUTE,E.hitchtime,E.completetime) STOP,E.hitchreason,E.repairmethod,E.repairprocess");
        sbEAM.append(" FROM equipmentrepair E LEFT JOIN assetcard A ON E.assetno=A.formid ");
        sbEAM.append(" WHERE hitchtime>SUBSTR(CURDATE(),1,7) AND A.remark IS NOT NULL AND A.deptname = '").append(type).append("'");
        sbEAM.append(" AND hitchurgency='03' AND TIMESTAMPDIFF(MINUTE, hitchtime, completetime) > 60");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query.getResultList();
        return resultsEAM;
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
        } else if (type.equals("半成品圆型件")) {
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
//        StringBuilder sbMES = new StringBuilder();
//        sbMES.append(" SELECT A.day, 1440,CASE WHEN D.estimateNUM IS NULL THEN 0 ELSE D.estimateNUM END,C.QTY,C.MINUTE,CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END,1440-A.AVAILABLEMINS,B.停机待测,B.生技试模,B.欠料等待,B.刀具调试更换,B.停机换模,B.物料返修,B.上下料干涉等待,B.单模拆装,B.共用刀具模具等待,B.带教新人讲解,B.绿灯暖机,B.其他,B.未填,B.计划停机,B.设备故障,CASE WHEN A.counts IS NULL THEN 0 ELSE A.counts END counts,(A.AVAILABLEMINS-(1440-A.AVAILABLEMINS))/A.AVAILABLEMINS*100,1440-B.计划停机");
//        sbMES.append(" ,A.AVAILABLEMINS-B.SUMLEN JiadongTime,C.QTY- (CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END) productQTY,convert(decimal(10,2),(C.QTY*1.0- (CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END))/C.QTY*100,2) productRate,convert(decimal(10,2),round((A.AVAILABLEMINS-B.设备故障*1.0)/A.AVAILABLEMINS*100,2))  equipmentRate,");
//        sbMES.append(" convert(decimal(10,2),round(C.MINUTE/(A.AVAILABLEMINS-B.SUMLEN)*100,2)) xingNengRate,convert(decimal(10,2),round((A.AVAILABLEMINS-B.SUMLEN*1.0)/A.AVAILABLEMINS*100,2))  timeRate,convert(decimal(10,2),round(((C.QTY*1.0- (CASE WHEN E.DEFECTSUM IS NULL THEN 0 ELSE E.DEFECTSUM END))/C.QTY)*(C.MINUTE/(A.AVAILABLEMINS-B.SUMLEN))*((A.AVAILABLEMINS-B.SUMLEN*1.0)/A.AVAILABLEMINS*100),2)) OEE,B.设备故障/A.counts MTTR,CASE WHEN (A.AVAILABLEMINS-B.设备故障)/A.counts IS NULL THEN A.AVAILABLEMINS ELSE (A.AVAILABLEMINS-B.设备故障)/A.counts END MTBF");
//        sbMES.append(" ,0,0,0,0  FROM ( SELECT B.EQPID,B.AVAILABLEMINS,A.counts,A.ALARMTIME_LEN,B.day  FROM ( SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN, day(E.ALARMSTARTTIME) day FROM EQP_RESULT_ALARM_D E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
//        sbMES.append(" WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' GROUP BY day(E.ALARMSTARTTIME), E.EQPID) A RIGHT JOIN ( SELECT E.EQPID,sum(AVAILABLEMINS) AVAILABLEMINS ,day(PLANDATE) day FROM MEQP M  LEFT JOIN EQP_AVAILABLETIME_SCHEDULE E  ON E.EQPID=M.EQPID WHERE PLANDATE LIKE '").append(year).append("%' AND E.EQPID='").append(EPQID).append("' AND AVAILABLEMINS!=0");
//        sbMES.append(" GROUP BY day(PLANDATE),E.EQPID) B ON A.day=B.day AND A.EQPID=B.EQPID) A LEFT JOIN (SELECT  A.DAY,MAX( case A.ALARMNAME when '设备故障' then A.ALARMTIME_LEN else 0 end) 设备故障,MAX( case A.ALARMNAME when '停机待测' then A.ALARMTIME_LEN else 0 end) 停机待测,MAX( case A.ALARMNAME when '生技试模' then A.ALARMTIME_LEN else 0 end) 生技试模,MAX( case A.ALARMNAME when '欠料等待' then A.ALARMTIME_LEN else 0 end) 欠料等待,MAX( case A.ALARMNAME when '刀具调试更换' then A.ALARMTIME_LEN else 0 end) 刀具调试更换,");
//        sbMES.append(" MAX( case A.ALARMNAME when '停机换模' then A.ALARMTIME_LEN else 0 end) 停机换模,MAX( case A.ALARMNAME when '物料返修' then A.ALARMTIME_LEN else 0 end) 物料返修,MAX( case A.ALARMNAME when '计划停机' then A.ALARMTIME_LEN else 0 end) 计划停机,MAX( case A.ALARMNAME when '绿灯暖机' then A.ALARMTIME_LEN else 0 end) 绿灯暖机,MAX( case A.ALARMNAME when NULL then A.ALARMTIME_LEN else 0 end) 未填,MAX( case A.ALARMNAME when '上下料干涉等待' then A.ALARMTIME_LEN else 0 end) 上下料干涉等待,MAX( case A.ALARMNAME when '单模拆装' then A.ALARMTIME_LEN else 0 end) 单模拆装,");
//        sbMES.append(" MAX( case A.ALARMNAME when '其他' then A.ALARMTIME_LEN else 0 end) 其他,MAX( case A.ALARMNAME when '共用刀具模具等待' then A.ALARMTIME_LEN else 0 end) 共用刀具模具等待,MAX( case A.ALARMNAME when '带教新人讲解' then A.ALARMTIME_LEN else 0 end) 带教新人讲解,SUM(A.ALARMTIME_LEN) SUMLEN FROM (");
//        sbMES.append(" SELECT A.ALARMNAME,SUM(convert(INT, ALARMTIME_LEN))/60 ALARMTIME_LEN,DAY(A.DATE) DAY FROM (SELECT EQPID, B.ALARMNAME,convert(VARCHAR(10), ALARMSTARTTIME, 111) AS DATE, ALARMTIME_LEN FROM EQP_RESULT_ALARM_D A LEFT JOIN MALARM B ON A.SPECIALALARMID = B.ALARMID WHERE EQPID='").append(EPQID).append("' AND A.ALARMSTARTTIME LIKE '").append(year).append("%') A GROUP BY A.ALARMNAME,DAY(A.DATE)) A GROUP BY A.DAY) B ON A.day=B.DAY");
//        sbMES.append(" LEFT JOIN (SELECT COUNT(A.EQPID) AS QTY, round(SUM(STD_TIME) / 60, 1) AS MINUTE,day FROM(SELECT A.EQPID, B.STD_TIME,day(PROCESSCOMPLETETIME) AS day FROM MEQP C LEFT JOIN PROCESS_STEP A ON A.EQPID = C.EQPID LEFT JOIN PROCESS_STEP_TIME B ON A.PRODUCTCOMPID = B.PRODUCTCOMPID AND A.EQPID = B.EQPID AND A.PRODUCTORDERID = B.PRODUCTORDERID AND A.SYSID = B.SYSID AND A.STEPID = B.STEPID WHERE");
//        sbMES.append("  A.PROCESSCOMPLETETIME LIKE '").append(year).append("%' AND A.EQPID='").append(EPQID).append("' AND A.PROCESSCOMPLETETIME IS NOT NULL) A GROUP BY  day) C ON C.day=A.day LEFT JOIN (SELECT SUM(convert(INT, NUM)) estimateNUM,day(PLANDATE) day FROM PLAN_SEMI_SQUARE WHERE EQPID ='").append(EPQID).append("'  AND PLANDATE LIKE '").append(year).append("%'  GROUP BY DAY(PLANDATE)) D ON D.day=A.day");
//        sbMES.append(" LEFT JOIN ( SELECT SUM ( convert(INT, DEFECTNUM)) AS DEFECTSUM,day(B.PROJECTCREATETIME) day FROM  FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD  A  ON  A.PROJECTID=B.PROJECTID left join PROCESS_STEP S ON A.PRODUCTORDERID =S.PRODUCTORDERID AND A.COMPIDSEQ =S.PRODUCTSERIALNUMBER AND B.SOURCESTEPIP=S.STEPID");
//        sbMES.append(" WHERE  B.PROJECTID LIKE 'QC%' AND B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND B.UQFTYPE ='UQFG0003' AND S.EQPID ='").append(EPQID).append("' GROUP BY day(B.PROJECTCREATETIME)) E ON E.day=A.day");
//        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
//        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
//        List<Object[]> resultsMES = query.getResultList();

//       生管计划工时，连线故障次数及故障工时
        List<String> deptName = this.getEPQIDDeptname(EPQID);
        String dept = "";
        if (!deptName.isEmpty()) {
            dept = deptName.get(0);//获取加工机所在部门信息
        }
        StringBuilder sbMESAVA = new StringBuilder();
        sbMESAVA.append(" SELECT B.day, B.EQPID,B.AVAILABLEMINS,A.counts, A.ALARMTIME_LEN");
        sbMESAVA.append(" FROM (SELECT E.EQPID,COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sbMESAVA.append(" day(E.ALARMSTARTTIME)  day FROM EQP_RESULT_ALARM_D E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMESAVA.append(" WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障'");
        sbMESAVA.append(" GROUP BY day(E.ALARMSTARTTIME), E.EQPID) A RIGHT JOIN (SELECT E.EQPID,sum(AVAILABLEMINS) AVAILABLEMINS,day(PLANDATE) day ");
        sbMESAVA.append(" FROM MEQP M LEFT JOIN EQP_AVAILABLETIME_SCHEDULE E ON E.EQPID = M.EQPID  WHERE PLANDATE LIKE '").append(year).append("%' AND E.EQPID = '").append(EPQID).append("'");
        sbMESAVA.append("  GROUP BY day(PLANDATE), E.EQPID) B ON A.day = B.day AND A.EQPID = B.EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMESAVA.toString());
        List<Object[]> resultsAVA = query.getResultList();
//       连线故障时间及总和
        StringBuilder sbMESLEN = new StringBuilder();
        sbMESLEN.append(" SELECT A.DAY,MAX(CASE A.ALARMNAME WHEN '设备故障' THEN A.ALARMTIME_LEN ELSE 0 END) 设备故障,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '停机待测' THEN A.ALARMTIME_LEN ELSE 0 END) 停机待测, MAX(CASE A.ALARMNAME WHEN '生技试模' THEN A.ALARMTIME_LEN ELSE 0 END) 生技试模,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '欠料等待' THEN A.ALARMTIME_LEN ELSE 0 END) 欠料等待, MAX(CASE A.ALARMNAME WHEN '刀具调试更换' THEN A.ALARMTIME_LEN  ELSE 0 END) 刀具调试更换,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '停机换模' THEN A.ALARMTIME_LEN ELSE 0 END) 停机换模, MAX(CASE A.ALARMNAME WHEN '物料返修' THEN A.ALARMTIME_LEN  ELSE 0 END) 物料返修, MAX(CASE A.ALARMNAME WHEN '上下料干涉等待' THEN A.ALARMTIME_LEN  ELSE 0 END)    上下料干涉等待,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '单模拆装' THEN A.ALARMTIME_LEN ELSE 0 END) 单模拆装,MAX(CASE A.ALARMNAME WHEN '共用刀具模具等待' THEN A.ALARMTIME_LEN ELSE 0 END)   共用刀具模具等待,MAX(CASE A.ALARMNAME WHEN '带教新人讲解' THEN A.ALARMTIME_LEN  ELSE 0 END)  带教新人讲解, MAX(CASE A.ALARMNAME WHEN '绿灯暖机' THEN A.ALARMTIME_LEN ELSE 0 END)  绿灯暖机,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '其他' THEN A.ALARMTIME_LEN ELSE 0 END)      其他, MAX(CASE A.ALARMNAME WHEN NULL THEN A.ALARMTIME_LEN ELSE 0 END)      未填,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '计划停机' THEN A.ALARMTIME_LEN ELSE 0 END) 计划停机,");
        sbMESLEN.append(" SUM(A.ALARMTIME_LEN) SUMLEN FROM (SELECT A.ALARMNAME,SUM(convert(INT, ALARMTIME_LEN)) /60 ALARMTIME_LEN, DAY(A.DATE) DAY FROM (SELECT EQPID,B.ALARMNAME,convert(VARCHAR(10), ALARMSTARTTIME,111) AS DATE,");
        sbMESLEN.append(" ALARMTIME_LEN FROM EQP_RESULT_ALARM_D A LEFT JOIN MALARM B ON A.SPECIALALARMID = B.ALARMID WHERE EQPID = '").append(EPQID).append("' AND A.ALARMSTARTTIME LIKE '").append(year).append("%') A GROUP BY A.ALARMNAME, DAY(A.DATE)) A GROUP BY A.DAY");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESLEN.toString());
        List<Object[]> resultsLEN = query.getResultList();

        //      报工数量及产出标准工时
        StringBuilder sbMESQTY = new StringBuilder();
        sbMESQTY.append(" SELECT day,COUNT(A.EQPID) AS QTY, round(SUM(STD_TIME) / 60, 1) AS MINUTE FROM(SELECT A.EQPID, B.STD_TIME,day(PROCESSCOMPLETETIME) AS day FROM MEQP C");
        sbMESQTY.append(" LEFT JOIN PROCESS_STEP A ON A.EQPID = C.EQPID LEFT JOIN PROCESS_STEP_TIME B ON A.PRODUCTCOMPID = B.PRODUCTCOMPID AND A.EQPID = B.EQPID AND A.PRODUCTORDERID = B.PRODUCTORDERID AND A.SYSID = B.SYSID AND A.STEPID = B.STEPID");
        sbMESQTY.append(" WHERE  A.PROCESSCOMPLETETIME LIKE '").append(year).append("%' AND A.EQPID='").append(EPQID).append("' AND A.PROCESSCOMPLETETIME IS NOT NULL) A GROUP BY  day");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQTY.toString());
        List<Object[]> resultsQTY = query.getResultList();
        String tName = "";//获取查询计划件数的表名
        String avaTime = "";//周计划工时
        //      计划产出件数
        StringBuilder sbMESPlan = new StringBuilder();
        if (dept.equals("半成品方型件")) {
            tName = "PLAN_SEMI_SQUARE";
            avaTime = ",sum(convert(DECIMAL, WORKHOUR)*convert(INT, NUM))";
        } else {
            tName = "PLAN_SEMI_CIRCLE";
        }
        sbMESPlan.append(" SELECT  day(PLANDATE) day,  SUM(convert(INT, NUM)) estimateNUM ").append(avaTime).append("FROM ").append(tName).append(" WHERE EQPID ='").append(EPQID).append("'  AND PLANDATE LIKE '").append(year).append("%'  GROUP BY DAY(PLANDATE)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESPlan.toString());
        List<Object[]> resultsPlan = query.getResultList();
        //      不合格单数量
        StringBuilder sbMESQCQTY = new StringBuilder();
        sbMESQCQTY.append(" SELECT SUM ( convert(INT, DEFECTNUM)) AS DEFECTSUM,day(B.PROJECTCREATETIME) day FROM  FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD  A  ON  A.PROJECTID=B.PROJECTID left join PROCESS_STEP S ON A.PRODUCTORDERID =S.PRODUCTORDERID AND A.COMPIDSEQ =S.PRODUCTSERIALNUMBER AND B.SOURCESTEPIP=S.STEPID WHERE  B.PROJECTID LIKE 'QC%' AND B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND B.UQFTYPE ='UQFG0003' AND S.EQPID ='").append(EPQID).append("' GROUP BY day(B.PROJECTCREATETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTY.toString());
        List<Object[]> resultsQCQTY = query.getResultList();
        //      特采数量
        StringBuilder sbMESQCQTYSpecial = new StringBuilder();
        sbMESQCQTYSpecial.append(" SELECT DAY(B.PROJECTCREATETIME) DAY,SUM(convert(INT, A.DEFECTNUM)) AS DEFECTSUM FROM FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD A ON A.PROJECTID = B.PROJECTID AND B.ANALYSISJUDGEMENTRESULT ='特采'");
        sbMESQCQTYSpecial.append(" LEFT JOIN PROCESS_STEP S ON A.PRODUCTORDERID = S.PRODUCTORDERID AND A.COMPIDSEQ = S.PRODUCTSERIALNUMBER AND B.SOURCESTEPIP = S.STEPID ");
        sbMESQCQTYSpecial.append(" WHERE B.PROJECTID LIKE 'QC%' AND B.ISPROCESSED = 'Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND B.UQFTYPE = 'UQFG0003'  AND S.EQPID = '").append(EPQID).append("' GROUP BY DAY(B.PROJECTCREATETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTYSpecial.toString());
        List<Object[]> resultsQCQTYSpecial = query.getResultList();
        //        EAM
        StringBuilder sbEAM = new StringBuilder();
        String yearEAM = year.replace("/", "-").substring(0, 7);
        sbEAM.append(" SELECT DAY(E.hitchtime),count(E.formid),SUM(TIMESTAMPDIFF(MINUTE, E.hitchtime, E.completetime)),SUM(TIMESTAMPDIFF(MINUTE,E.servicearrivetime,E.completetime)),  SUM(TIMESTAMPDIFF(MINUTE, E.hitchtime, DATE_SUB(DATE_FORMAT(E.hitchtime,'%Y-%m-%d'),INTERVAL -1 DAY)))  FROM equipmentrepair E LEFT JOIN assetcard A  ON E.assetno=A.formid");
        sbEAM.append(" WHERE E.hitchtime LIKE '%").append(yearEAM).append("%' AND  A.remark='").append(EPQID).append("' AND E.rstatus>=30  GROUP BY DAY(E.hitchtime)");
        query = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query.getResultList();
        List<Object[]> list = new ArrayList<>();
        int strMin = 0;
        int endMin = 0;
        for (int i = 1; i <= 31; i++) {
            Object[] obj = new Object[40];

            for (Object[] objects : resultsAVA) {
                if (objects[0].equals(i)) {
                    //      将有生管计划的所有值赋0
                    for (int j = 0; j < 39; j++) {
                        obj[j] = 0;
                    }
                    obj[1] = 1440;
                    obj[7] = 1440 - Integer.parseInt(objects[2].toString());
//                    故障次数为0时将数值赋0
                    if (objects[3] != null) {
                        obj[22] = objects[4];
                        obj[24] = objects[3];
                    }
                }
            }

//          实际产出件数及标准工时
            for (Object[] object : resultsQTY) {
                if (object[0].equals(i)) {
                    obj[3] = object[1];
                    obj[4] = object[2];
                }
            }
//            计划产出件数
            for (Object[] object : resultsPlan) {
                if (object[0].equals(i)) {
                    obj[2] = object[1];
                    if (dept.equals("半成品方型件")) {
                        if (Integer.parseInt(object[2].toString()) == 0) {
                            obj[7] = 1440;
                        } else if (Integer.parseInt(object[2].toString()) > 720) {
                            obj[7] = 0;
                        } else {
                            obj[7] = 720;
                        }
                    }
                }
            }
//            不合格单数量
            for (Object[] object : resultsQCQTY) {
                if (object[1].equals(i)) {
                    obj[5] = object[0];
                }
            }
//            特采数量
            for (Object[] object : resultsQCQTYSpecial) {
                if (object[0].equals(i)) {
                    obj[6] = object[1];
                }
            }
            if (strMin != 0) {
                obj[23] = strMin;
                obj[26] = endMin;
                strMin = 0;
                endMin = 0;
            } else {
                obj[23] = 0;
                obj[26] = 0;
            }
//            EAM故障时间及故障次数
            for (Object[] objects : resultsEAM) {
                if (objects[0].equals(i)) {
                    obj[23] = Integer.parseInt(objects[2].toString()) + Integer.parseInt(obj[23].toString());
                    obj[25] = objects[1];
                    obj[26] = Integer.parseInt(objects[3].toString()) + Integer.parseInt(obj[26].toString());
                    obj[27] = objects[1];
                    if (Integer.parseInt(objects[2].toString()) > 1440) {//当维修时间超出当天时间，将超出部分归类到第二天
                        obj[23] = Integer.parseInt(objects[4].toString());
                        strMin = Integer.parseInt(objects[2].toString()) - Integer.parseInt(objects[4].toString());
                        endMin = Integer.parseInt(objects[3].toString()) - Integer.parseInt(objects[4].toString());
                        obj[26] = Integer.parseInt(objects[4].toString()) - (Integer.parseInt(objects[2].toString()) - Integer.parseInt(objects[3].toString()));
                    } else {
                        strMin = 0;
                        endMin = 0;
                    }
                }
            }
            for (Object[] objects : resultsLEN) {
                if (objects[0].equals(i)) {
                    for (int j = 2; j <= 15; j++) {
                        obj[6 + j] = objects[j];
                    }
//                    obj[21]=objects[15];
////                  获取计划停机时间-实际停机时间的差异
//                    if (obj[7] != null && Integer.parseInt(obj[7].toString()) != 0) {
//                        int seq = (int) ((1440 - Integer.parseInt(objects[16].toString())) - Double.parseDouble(obj[4].toString()));
//                        obj[21] = seq;
//                    } else {
//                        obj[21] = 0;
//                    }
                }
            }
            obj[0] = i;
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
     * 获取加工机所在的部门
     *
     * @return
     */
    public List getEPQIDDeptname(String EPQID) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append("SELECT PRODUCTTYPE FROM MEQP WHERE EQPTYPEID ='实体设备' AND EQPID='").append(EPQID).append("'");
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
