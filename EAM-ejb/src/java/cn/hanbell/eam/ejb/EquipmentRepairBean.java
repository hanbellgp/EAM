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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import javax.persistence.Query;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ELState;

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

    @Override
    public EquipmentRepair findById(int value) {
        EquipmentRepair e = super.findById(value);
        return e; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
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
        sb.append(" WHERE R.rstatus>'30' AND  R.hitchtype IS NOT NULL AND R.assetno IS NOT NULL");
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

    public String getAssetno(String formid) {
        //流水码不按年份重新编号
        String maxid;
        Query query = getEntityManager().createNativeQuery("Select assetno from equipmentrepair where formid='" + formid + "'");

        maxid = query.getSingleResult().toString();

        return maxid;

    }

    //获取维修费用统计表的List
    public List<EquipmentRepair> getRepairCostStatisticsList(String staDate, String endDate, String deptname, String abrasehitch, String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.formid,R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname, R.hitchtime,R.abrasehitch,R.repairmethod,R.repaircost,R.laborcosts,R.sparecost,R.repaircost + R.laborcosts + R.sparecost AS tot,R.serviceusername");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE  R.rstatus >= '60' AND R.RepairMethodType='1'  and  R.rstatus!='98'");
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
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE   R.rstatus>='60'  and R.rstatus!='98'  AND R.RepairMethodType='1' ");
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
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE R.rstatus>='60' AND  R.rstatus!='98'  AND R.RepairMethodType='1' AND  R.abrasehitch LIKE '%0%'");
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
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE   R.abrasehitch LIKE '%0%' AND R.rstatus>='60' AND  R.rstatus!='98'  AND R.RepairMethodType='1' ");
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
        sbMES.append(" SELECT A.EQPID,A.counts,A.ALARMTIME_LEN,1440,cast((1440*1.0-A.ALARMTIME_LEN*1.0)/A.counts*1.0 AS DECIMAL(10, 2))  MTBF,cast(A.ALARMTIME_LEN*1.0/A.counts*1.0 AS DECIMAL(10, 2)) MTTB  FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN FROM EQP_RESULT_ALARM_D");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        Date d1 = sdf.parse(staDate);

        Date d2 = sdf.parse(endDate);
        long daysBetween = (d2.getTime() - d1.getTime() + 1000000) / (60 * 60 * 24 * 1000) + 1;
        sbMES.append(" SELECT A.EQPID,A.ALARMTIME_LEN,  (CASE  B.estimateNUM WHEN NULL THEN 1440*30  ELSE  B.estimateNUM END),cast(A.ALARMTIME_LEN * 1.0 / CASE WHEN   (CASE  B.estimateNUM WHEN NULL THEN 1440*30  ELSE  B.estimateNUM END)=0 then null ELSE  (  (CASE  B.estimateNUM WHEN NULL THEN 1440*30  ELSE  B.estimateNUM END)) END * 1.0 * 100.0 AS DECIMAL(10, 2)) AS Failure FROM (");
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN FROM EQP_RESULT_ALARM_D");
        sbMES.append(" WHERE ALARMSTARTTIME >= '").append(staDate).append("'AND ALARMSTARTTIME <= '").append(endDate).append("'");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10");
        sbMES.append(" GROUP BY EQPID)A LEFT JOIN (");
        sbMES.append(" SELECT EQPID, 1440*" + daysBetween + "- SUM(convert(DECIMAL, WORKHOUR)) estimateNUM FROM PLAN_SEMI_CIRCLE WHERE   PLANDATE >= '" + staDate + "' AND PLANDATE <= '" + endDate + "' AND PRODUCTID='计划停机' GROUP BY EQPID");

        sbMES.append(" UNION  ALL SELECT EQPID, 1440*" + daysBetween + "- SUM(convert(DECIMAL, WORKHOUREXTRA)) estimateNUM FROM PLAN_SEMI_SQUARE WHERE   PLANDATE >= '" + staDate + "' AND PLANDATE <= '" + endDate + "' AND PRODUCTID='计划停机' GROUP BY EQPID) B ON A.EQPID=B.EQPID");
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
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,month(ALARMSTARTTIME) MONTH FROM EQP_RESULT_ALARM_D WHERE ALARMSTARTTIME LIKE '").append(year).append("%' ");
        sbMES.append(" AND (SPECIALALARMID='B0001' OR SPECIALALARMID='A0001') AND datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)>10 GROUP BY month(ALARMSTARTTIME),EQPID)A RIGHT JOIN (");

        sbMES.append("  SELECT DISTINCT EQPID,day( DATEADD(DD, -DAY(DATEADD(MM, 1,PLANDATE )), DATEADD(MM, 1,PLANDATE)))*1440  -  SUM((CASE PRODUCTID WHEN '计划停机' THEN  convert(DECIMAL, WORKHOUREXTRA)  ELSE  0 END)) AVAILABLEMINS,month(PLANDATE) MONTH");
        sbMES.append(" FROM PLAN_SEMI_SQUARE WHERE PLANDATE LIKE '%" + year + "%' GROUP BY EQPID, MONTH(PLANDATE) UNION  ALL ");
        sbMES.append(" SELECT DISTINCT EQPID, day( DATEADD(DD, -DAY(DATEADD(MM, 1,PLANDATE )), DATEADD(MM, 1,PLANDATE)))*1440 - SUM((CASE PRODUCTID WHEN '计划停机' THEN  convert(DECIMAL, WORKHOUR)  ELSE  0 END)) AVAILABLEMINS, month(PLANDATE) MONTH");
        sbMES.append(" FROM PLAN_SEMI_CIRCLE WHERE PLANDATE LIKE '%" + year + "%' GROUP BY EQPID, month(PLANDATE)) B ON A.EQPID = B.EQPID AND A.MONTH = B.MONTH");

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
        sbMES.append(" SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,year(ALARMSTARTTIME) MONTH FROM EQP_RESULT_ALARM_D WHERE ALARMSTARTTIME >= '").append(staYear).append("' AND ALARMSTARTTIME<'").append(endYear).append("'");
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
     * @param deptno 部门
     * @param companyType 公司别
     * @return
     */
    public List<Object[]> getMonthlyReport(String year, String deptno, String companyType, String type, String isOutsourcing) {
//        String str = type.substring(3, type.length());//获取不良数的类型
        //月度总计划工时及平均计划工时,当月设备数量
        StringBuilder sbAVA = new StringBuilder();
//        sbAVA.append(" SELECT A.MONTH,SUM(A.AVAILABLEMINS) AVAILABLEMINS,SUM(A.AVAILABLEMINS)/COUNT(A.EQPID) AVA,COUNT(A.EQPID) EQPIDCount   FROM ( SELECT E.EQPID,SUM(E.AVAILABLEMINS) AVAILABLEMINS, month(E.PLANDATE)  MONTH FROM EQP_AVAILABLETIME_SCHEDULE E");
//        sbAVA.append(" LEFT JOIN MEQP M ON E.EQPID=M.EQPID WHERE E.PLANDATE LIKE '").append(year).append("%' AND E.PLANDATE< getdate()  AND E.AVAILABLEMINS!=0 AND M.PRODUCTTYPE='").append(type).append("'");
//        sbAVA.append(" GROUP BY month(E.PLANDATE), E.EQPID) A GROUP BY A.MONTH");
        //获取该部门的上班时间
        sbAVA.append(" SELECT month(formdate),sum(worktime+overtime*60) FROM equipmentworktime where dept = '" + deptno + "' AND formdate < now() AND company='" + companyType + "'  AND formdate LIKE '%" + year + "%'  GROUP BY month(formdate)     ");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = getEntityManager().createNativeQuery(sbAVA.toString());
        List<Object[]> avaList = query.getResultList();
        //获取MTBF总合
//        StringBuilder sbMTBF = new StringBuilder();
////        sbMTBF.append(" SELECT A.MONTH,SUM(A.MTBF) FROM ( SELECT CASE WHEN   (cast((B.AVAILABLEMINS * 1.0 - A.ALARMTIME_LEN * 1.0) / A.counts * 1.0 AS DECIMAL(10, 2))) IS NULL THEN B.AVAILABLEMINS ELSE cast((B.AVAILABLEMINS * 1.0 - A.ALARMTIME_LEN * 1.0) / A.counts * 1.0 AS DECIMAL(10, 2)) END   MTBF,");
////        sbMTBF.append(" B.MONTH  FROM (SELECT EQPID,COUNT(EQPID) counts,sum(datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME)) AS ALARMTIME_LEN,month(ALARMSTARTTIME) MONTH");
////        sbMTBF.append(" FROM EQP_RESULT_ALARM WHERE ALARMSTARTTIME LIKE '").append(year).append("%' AND (SPECIALALARMID = 'B0001' OR SPECIALALARMID = 'A0001') AND");
////        sbMTBF.append(" datediff(MINUTE, ALARMSTARTTIME, ALARMENDTIME) > 10 GROUP BY month(ALARMSTARTTIME), EQPID) A RIGHT JOIN (SELECT A.EQPID,SUM(AVAILABLEMINS) AVAILABLEMINS,month(PLANDATE) MONTH");
////        sbMTBF.append(" FROM EQP_AVAILABLETIME_SCHEDULE A LEFT JOIN MEQP M ON A.EQPID = M.EQPID");
////        sbMTBF.append(" WHERE PLANDATE LIKE '").append(year).append("%' AND PLANDATE< getdate()  and AVAILABLEMINS!=0  AND  M.PRODUCTTYPE = '").append(type).append("'");
////        sbMTBF.append(" GROUP BY month(PLANDATE), A.EQPID) B ON A.EQPID = B.EQPID AND A.MONTH = B.MONTH )A  GROUP BY  A.MONTH");
//        query = superEJBForMES.getEntityManager().createNativeQuery(sbMTBF.toString());
//        List<Object[]> mtbfList = query.getResultList();

        //-- 大于10分钟的故障次数和60分钟以上的故障次数及故障停机时间和其他总的异常时间
//        StringBuilder sbCount = new StringBuilder();
////        sbCount.append(" SELECT  A.MONTH, SUM(A.counts) counts10,sum(A.counts60) counts60,(CASE WHEN SUM(A.ALARMTIME_LEN) IS NULL THEN 0 ELSE SUM(A.ALARMTIME_LEN) END) ALARMTIME_LEN,SUM(A.abnormal) abnormal");
////        sbCount.append(" FROM ( SELECT E.EQPID, COUNT(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 60 AND M.ALARMNAME = '设备故障' THEN E.EQPID END ) counts60, COUNT(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 AND M.ALARMNAME = '设备故障'  THEN E.EQPID END ) counts,");
////        sbCount.append(" sum(CASE WHEN datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 AND M.ALARMNAME = '设备故障'  THEN  datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME )END) AS ALARMTIME_LEN,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS abnormal,");
////        sbCount.append(" month(E.ALARMSTARTTIME)MONTH FROM EQP_RESULT_ALARM E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%'  AND M.ALARMTYPE = '").append(type).append("' GROUP BY month(E.ALARMSTARTTIME), E.EQPID ) A GROUP BY A.MONTH");
////        query = superEJBForMES.getEntityManager().createNativeQuery(sbCount.toString());
//        List<Object[]> countList = query.getResultList();
//
//        //每月报工数及标准工时
//        StringBuilder sbQty = new StringBuilder();
////        sbQty.append(" SELECT month(PROCESSCOMPLETETIME) MONTH ,count(CASE WHEN A.STEPID LIKE '%").append(str).append("清洗%' THEN A.EQPID END )  QTY,sum(round(B.STD_TIME / 60, 1)) MINUTE ");
////        sbQty.append(" FROM PROCESS_STEP  A   LEFT JOIN PROCESS_STEP_TIME B ON A.SYSID = B.SYSID  AND A.EQPID = B.EQPID");
////        sbQty.append(" LEFT JOIN  MEQP C ON C.EQPID = A.EQPID WHERE C.PRODUCTTYPE = '").append(type).append("'  AND A.PROCESSCOMPLETETIME LIKE'%").append(year).append("%' GROUP BY  month(PROCESSCOMPLETETIME)");
////        query = superEJBForMES.getEntityManager().createNativeQuery(sbQty.toString());
//        List<Object[]> qtyList = query.getResultList();
//        //不良数
//        StringBuilder sbQG = new StringBuilder();
////        sbQG.append(" SELECT month(B.PROJECTCREATETIME) MONTH, SUM(DEFECTNUM) AS QGSUM FROM ANALYSISRESULT_QCD A LEFT JOIN  FLOW_FORM_UQF_S_NOW B ON  A.PROJECTID=B.PROJECTID");
////        sbQG.append(" WHERE  B.ISPROCESSED='Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND SOURCESTEPIP LIKE '").append(str).append("%' AND B.UQFTYPE ='UQFG0003' GROUP BY month(B.PROJECTCREATETIME)");
////        query = superEJBForMES.getEntityManager().createNativeQuery(sbQG.toString());
//        List<Object[]> qgList = query.getResultList();
        //在报修系统中获取故障时间为60分钟以上的次数
        StringBuilder sb60Count = new StringBuilder();
        String strTypeString = "";
//        if (type.equals("半成品方型件")) {
//            strTypeString = "方型加工课";
//        } else {
//            strTypeString = "圆型加工课";
//        }
        deptno = deptno.substring(0, 3);
        sb60Count.append(" SELECT month(hitchtime),count(CASE WHEN TIMESTAMPDIFF(MINUTE,hitchtime, completetime) > 60 THEN E.assetno END) as count60,count(*) count10,SUM(TIMESTAMPDIFF(MINUTE, hitchtime, completetime)) FROM equipmentrepair E LEFT JOIN assetcard A ON E.assetno=A.formid ");
        sb60Count.append(" WHERE A.deptno like'%").append(deptno).append("%' AND hitchtime LIKE '%").append(year).append("%'");
        sb60Count.append(" AND hitchtime<now() AND rstatus<98 AND A.company='" + companyType + "' ");
        if (type.equals("加工机")) {
            sb60Count.append(" AND A.remark IS NOT NULL ");
        } else if (type.equals("非加工机")) {
            sb60Count.append(" AND A.remark IS  NULL ");
        }
        if (isOutsourcing.equals("N")) {//不包含委外维修
            sb60Count.append(" and repairmethodtype!=3 ");
        }
        sb60Count.append(" GROUP BY month(hitchtime)");
        query = getEntityManager().createNativeQuery(sb60Count.toString());
        List<Object[]> sb60CountList = query.getResultList();
        //获取跨月维修报修数据
        StringBuilder sbDiffDate = new StringBuilder();
        sbDiffDate.append("SELECT (YEAR(completetime) - YEAR(hitchtime)) * 12 +(MONTH(completetime) - MONTH(hitchtime)) AS months_difference,MONTH(hitchtime) Month,TIMESTAMPDIFF(MINUTE, hitchtime, DATE_FORMAT(hitchtime + INTERVAL 1 MONTH, '%Y-%m-01')) endDate,TIMESTAMPDIFF(MINUTE, hitchtime, completetime) sumDate");
        sbDiffDate.append(" FROM equipmentrepair B  LEFT JOIN  assetcard A  ON B.assetno=A.formid where hitchtime like '" + year + "%' and B.company='C'  and repairdeptno like '%" + deptno + "%' and rstatus<98  and (YEAR(completetime) - YEAR(hitchtime)) * 12 +(MONTH(completetime) - MONTH(hitchtime))  >0  ");
        if (type.equals("加工机")) {
            sbDiffDate.append(" AND A.remark IS NOT NULL ");
        } else if (type.equals("非加工机")) {
            sbDiffDate.append(" AND A.remark IS  NULL ");
        }
        if (isOutsourcing.equals("N")) {//不包含委外维修
            sbDiffDate.append(" and B.repairmethodtype!=3 ");
        }
        query = getEntityManager().createNativeQuery(sbDiffDate.toString());
        List<Object[]> sbDiffDateList = query.getResultList();

        StringBuilder sbNoDiffDate = new StringBuilder();
        sbNoDiffDate.append("SELECT (YEAR(NOW()) - YEAR(hitchtime)) * 12 +(MONTH(NOW()) - MONTH(hitchtime)) AS months_difference,MONTH(hitchtime) Month,TIMESTAMPDIFF(MINUTE, hitchtime, DATE_FORMAT(hitchtime + INTERVAL 1 MONTH, '%Y-%m-01')) endDate,TIMESTAMPDIFF(MINUTE, hitchtime, NOW()) sumDate");
        sbNoDiffDate.append(" FROM equipmentrepair B  LEFT JOIN  assetcard A  ON B.assetno=A.formid where hitchtime like '" + year + "%' and B.company='C'  and repairdeptno like '%" + deptno + "%' and rstatus<98  and completetime is null   and (YEAR(NOW()) - YEAR(hitchtime)) * 12 + (MONTH(NOW()) - MONTH(hitchtime))>0");
        if (type.equals("加工机")) {
            sbNoDiffDate.append(" AND A.remark IS NOT NULL ");
        } else if (type.equals("非加工机")) {
            sbNoDiffDate.append(" AND A.remark IS  NULL ");
        }
        if (isOutsourcing.equals("N")) {//不包含委外维修
            sbNoDiffDate.append(" and B.repairmethodtype!=3 ");
        }
        query = getEntityManager().createNativeQuery(sbNoDiffDate.toString());
        List<Object[]> sbNoDiffDateList = query.getResultList();
//将所有的数据按所需要的模板整合到一个List中
        List<Object[]> listMES = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Object[] obj = new Object[12];
            for (Object[] ava : avaList) {
                if (i == Integer.parseInt(ava[0].toString())) {
                    obj[1] = ava[1];
                }
            }
//            for (Object[] count : countList) {
//                if (i == Integer.parseInt(count[0].toString())) {
//
//                    obj[4] = count[3];
//                    obj[5] = count[4];
//                }
//            }
//            for (Object[] qty : qtyList) {
//                if (i == Integer.parseInt(qty[0].toString())) {
//                    obj[6] = qty[1];
//                    obj[7] = qty[2];
//                }
//            }
//            for (Object[] qg : qgList) {
//                if (i == Integer.parseInt(qg[0].toString())) {
//                    obj[8] = qg[1];
//                }
//            }
//            for (Object[] mtbf : mtbfList) {
//                if (i == Integer.parseInt(mtbf[0].toString())) {
//                    obj[10] = mtbf[1];
//                }
//            }
            for (Object[] count60 : sb60CountList) {
                if (i == Integer.parseInt(count60[0].toString())) {
                    obj[2] = count60[2];
                    obj[3] = count60[3];
                }
            }
            obj[9] = i;
            listMES.add(obj);
        }
        //将List中的数据经过模板中的公式进行计算处理在存入新的List中
        List<Object[]> resultsMES = new ArrayList<>();
        for (Object[] oMes : listMES) {
            if (oMes[1] != null) {
                Object[] obj = new Object[19];
                obj[0] = oMes[1];
                obj[1] = oMes[2];
                obj[2] = oMes[3];
                obj[18] = oMes[9];//月份
                resultsMES.add(obj);
//                obj[3] = oMes[3];
//                obj[4] = oMes[4];
//                BigDecimal HAVA = BigDecimal.valueOf(Double.valueOf(oMes[0].toString()));//月度总生产工时
//                BigDecimal GAVA = BigDecimal.valueOf(Double.valueOf(oMes[1].toString()));//月度平均生产工时
//                BigDecimal ALA = BigDecimal.valueOf(Double.valueOf(oMes[4].toString()));//故障停机时间
//                BigDecimal count10;
//                if (oMes[2] == null) {
//                    count10 = BigDecimal.ONE;
//                } else {
//                    count10 = BigDecimal.valueOf(Double.valueOf(oMes[2].toString()));//10分钟以上的故障次数
//                }
//                BigDecimal abnormal = BigDecimal.valueOf(Double.valueOf(oMes[5].toString()));//总异常时间
//                BigDecimal MINUTE = BigDecimal.valueOf(Double.valueOf(oMes[7].toString()));//产出标准工时
//                BigDecimal QTY = BigDecimal.valueOf(Double.valueOf(oMes[6].toString()));//报工数
//                BigDecimal QGQTY = BigDecimal.valueOf(Double.valueOf(oMes[8].toString()));//不良数
//                BigDecimal MTBF = BigDecimal.valueOf(Double.valueOf(oMes[10].toString()));//各加工机MTBF总合
//                BigDecimal avaCount = BigDecimal.valueOf(Double.valueOf(oMes[11].toString()));//所有加工机数量
//                //汉钟版设备可动率
//                obj[5] = ((HAVA.subtract(ALA)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//月度生产总工时-故障停机时间/月度生产总工时
//                //顾问版设备可动率
//                obj[6] = ((GAVA.subtract(ALA)).divide(GAVA, 4, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//月度平均生产总工时-故障停机时间/月度平均生产总工时
//                //设备故障率(%)   故障停机工时合计/月度生产总工时
//                obj[7] = ALA.divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//                //MTTR   故障停机工时合计/故障件数合计
//                obj[8] = ALA.divide(count10, 4, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
//                //顾问版MTBF   月度平均生产总工时/故障件数合计
//                obj[9] = GAVA.divide(count10, 4, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(60), 0, BigDecimal.ROUND_HALF_UP);
//                //汉钟版MTBF   月度生产总工时-故障停机时间/故障件数合计
//                obj[10] = (HAVA.subtract(ALA)).divide(count10, 0, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(60), 0, BigDecimal.ROUND_HALF_UP);
//
//                obj[11] = oMes[5];
//                //时间稼动率   （月度生产总工时-总异常时间）/月度生产总工时
//                obj[12] = (HAVA.subtract(abnormal)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//                obj[13] = (HAVA.subtract(abnormal)).divide(HAVA, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//                //性能稼动率   产出标准工时/(月度生产总工时-总异常时间)
//                obj[14] = MINUTE.divide((HAVA.subtract(abnormal)), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//                obj[15] = MINUTE.divide((HAVA.subtract(abnormal)), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//                //良率  报工数-不良单据)/报工数(不良不论厂内外责任，只要经过加工就计入)
//                obj[16] = (QTY.subtract(QGQTY)).divide(QTY, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
//                obj[17] = QTY;

            }
        }

        List<Object[]> list = new ArrayList();
        Object[] obj1 = new Object[18];
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
        int Q1Qty = 0;
        int Q2Qty = 0;
        int Q3Qty = 0;
        int Q4Qty = 0;
        obj1[0] = "计划生产总工时(分)A";
        obj2[0] = "Σ故障件数(件)B";
        obj3[0] = "Σ故障停机工时(分)C";
//        obj2[13] = "=全设备故障件数总和(停线10分钟以上,报修开始到维修结束)来源MES异常报表";
//        obj3[0] = "故障停机工时合计(分)";
//        obj3[13] = "=全设备故障时间总和(停线10分钟以上,报修开始到维修结束) 来源MES异常报表";
//        obj4[0] = "设备可动率(%)";
//        obj4[13] = "=(月度生产总工时-故障停机工时合计)/月度生产总工时";
//        obj5[0] = "MTTR(分/件)";
//        obj5[13] = "=故障停机工时合计/故障件数合计";
//        obj6[0] = "MTBF(小时/件)";
//        obj6[13] = "=全车间单台设备MTBF总和均值(单台的MTBF=(计划工作时间-故障停机时间)/维修次数)\n"
//                + "仅记录停线10分钟以上数据  ";
//        obj7[0] = "时间稼动率(%)";
//        obj7[13] = "=（厂内生管计划工时-异常报表工时合计）/厂内生管计划工时";
//        obj8[0] = "性能稼动率(%)";
//        obj8[13] = "=产出标准工时/(厂内生管计划工时-异常报表工时合计)";
//        obj9[0] = "良率(%)";
//        obj9[13] = "=(报工数-不良单据)/报工数(不良不论厂内外责任，只要经过加工就计入)";
//        obj10[0] = "月平均OEE(%)";
//        obj10[13] = "=时间稼动率*性能稼动率*良率";
//        obj11[0] = "故障60分以上件数";
//        obj11[13] = "=全车间单台设备计划工时总和";
//        obj12[0] = "设备故障率(%)";
//        obj12[13] = "=全设备故障件数总和(停线60分钟以上,报修开始到维修结束)来源MES异常报表";

        for (int i = 1; i <= 12; i++) {
            for (Object[] mes : resultsMES) {
                if (i == Integer.parseInt(mes[18].toString())) {
//                    if (mes[1] != null) {
//                        if (reportType.equals("H")) {
//                            obj1[i] = mes[0];
//                        } else {
//                            obj1[i] = mes[1];
//                        }
//                    }

                    obj1[i] = mes[0];

                    obj2[i] = mes[1];
                    if (i <= 3) {
                        if (mes[1] != null) {
                            Q1Qty += Integer.parseInt(mes[1].toString());
                        }

                    } else if (i > 3 && i <= 6) {
                        if (mes[1] != null) {
                            Q2Qty += Integer.parseInt(mes[1].toString());
                        }
                    } else if (i > 6 && i <= 9) {
                        if (mes[1] != null) {
                            Q3Qty += Integer.parseInt(mes[1].toString());
                        }
                    } else if (i > 9 && i <= 12) {
                        if (mes[1] != null) {
                            Q4Qty += Integer.parseInt(mes[1].toString());
                        }
                    }

                    obj3[i] = mes[2];

//                    obj3[i] = mes[4];
//                    if (mes[5] != null) {
//                        if (reportType.equals("H")) {
//                            obj4[i] = mes[5];
//                        } else {
//                            obj4[i] = mes[6];
//                        }
//                    }
//
//                    if (mes[7] != null) {
//                        obj12[i] = mes[7];
//                    }
//
//                    obj5[i] = mes[8];
//                    if (mes[10] != null) {
//                        if (reportType.equals("H")) {
//                            obj6[i] = mes[10];
//                        } else {
//                            obj6[i] = mes[9];
//                        }
//                    }
//
//                    if (mes[12] != null) {
//                        if (reportType.equals("H")) {
//                            obj7[i] = mes[12];
//                        } else {
//                            obj7[i] = mes[13];
//                        }
//                    } else {
//                        obj7[i] = 100.00;
//                    }
//
//                    if (mes[14] != null) {
//                        if (reportType.equals("H")) {
//                            obj8[i] = mes[14];
//                        } else {
//                            obj8[i] = mes[15];
//                        }
//                    } else {
//                        obj8[i] = 100.00;
//                    }
//
//                    if (mes[16] != null) {
//                        obj9[i] = mes[16];
//                    } else {
//                        obj9[i] = 100.00;
//                    }
//                    double monthOEE = (Double.parseDouble(obj7[i].toString()) / 100 * Double.parseDouble(obj8[i].toString()) / 100 * Double.parseDouble(obj9[i].toString()) / 100) * 100;
//                    if (String.valueOf(monthOEE).length() >= 5) {
//                        obj10[i] = String.valueOf(monthOEE).substring(0, 5);
//                    } else {
//                        obj10[i] = monthOEE;
//                    }
//
//                    if (mes[3] == null) {
//                        obj11[i] = 0;
//                    } else {
//                        obj11[i] = mes[3];
//                    }
                }
            }
        }
        list.add(obj1);
        list.add(obj2);
        list.add(obj3);

        for (Object[] diff : sbDiffDateList) {
            int monthSum = Integer.parseInt(diff[0].toString());//总共跨多少个月 
            int month = Integer.parseInt(diff[1].toString());//当前月份
            int endDate = Integer.parseInt(diff[2].toString());//当前月份时间
            int sumDate = Integer.parseInt(diff[3].toString());//总差异时间
            int daySumDate = 0;//总共跨满了多少个月的时间
            for (int i = month; i <= month + monthSum; i++) {
                if (i == month) {//第一个月减去跨度的时间
                    list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) - (sumDate - endDate);
                } else {
                    if (monthSum > 1) {//跨度大于一个月的时间
                        if (i == (month + monthSum)) {//最后一个月
                            list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + (sumDate - endDate - daySumDate);//跨满一个月加上全部时间

                            list.get(1)[i] = Integer.parseInt(list.get(1)[i].toString()) + 1;//维修数量加1

                        } else {
                            // 获取当前月份的最后一天
                            LocalDate lastDayOfMonth = LocalDate.of(Integer.parseInt(year), i, 1);
                            // 获取当前月份的天数
                            int daysInMonth = (int) lastDayOfMonth.lengthOfMonth();
                            list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + 1440 * daysInMonth;//跨满一个月加上全部时间
                            list.get(1)[i] = Integer.parseInt(list.get(1)[i].toString()) + 1;//维修数量加1
                            daySumDate += 1440 * daysInMonth;

                        }
                    } else {
                        if (month != 12) {
                            list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + (sumDate - endDate);//只夸度一个月就加上差异部分
                            list.get(1)[i] = Integer.parseInt(list.get(1)[i].toString()) + 1;//维修数量加1
                        }
                    }

                }

            }

        }

        //未维修完成的时间也加上去
        for (Object[] diff : sbNoDiffDateList) {
            int monthSum = Integer.parseInt(diff[0].toString());//总共跨多少个月 
            int month = Integer.parseInt(diff[1].toString());//当前月份
            int endDate = Integer.parseInt(diff[2].toString());//当前月份时间
            int sumDate = Integer.parseInt(diff[3].toString());//总差异时间
            int daySumDate = 0;//总共跨满了多少个月的时间
            for (int i = month; i <= month + monthSum; i++) {
                if (i == month) {//第一个月减去跨度的时间
                    list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + endDate;
                } else {
                    if (monthSum > 1) {//跨度大于一个月的时间
                        if (i == (month + monthSum)) {//最后一个月
                            list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + (sumDate - endDate - daySumDate);//跨满一个月加上全部时间
                            list.get(1)[i] = Integer.parseInt(list.get(1)[i].toString()) + 1;//维修数量加1

                        } else {
                            // 获取当前月份的最后一天
                            LocalDate lastDayOfMonth = LocalDate.of(Integer.parseInt(year), i, 1);
                            // 获取当前月份的天数
                            int daysInMonth = (int) lastDayOfMonth.lengthOfMonth();
                            list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + 1440 * daysInMonth;//跨满一个月加上全部时间
                            list.get(1)[i] = Integer.parseInt(list.get(1)[i].toString()) + 1;//维修数量加1
                            daySumDate += 1440 * daysInMonth;
                        }
                    } else {
                        if (month != 12) {
                            list.get(2)[i] = Integer.parseInt(list.get(2)[i].toString()) + (sumDate - endDate);//只夸度一个月就加上差异部分
                            list.get(1)[i] = Integer.parseInt(list.get(1)[i].toString()) + 1;//维修数量加1
                        }

                    }

                }

            }

        }
        list.get(0)[14] = Q1Qty;
        list.get(0)[15] = Q2Qty;
        list.get(0)[16] = Q3Qty;
        list.get(0)[17] = Q4Qty;

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
        sbMTBF.append(" FROM EQP_RESULT_ALARM_D WHERE ALARMSTARTTIME LIKE '").append(year).append("%' AND (SPECIALALARMID = 'B0001' OR SPECIALALARMID = 'A0001') AND");
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
        sbCount.append(" DAY(E.ALARMSTARTTIME) DAY FROM EQP_RESULT_ALARM_D E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%'  AND M.ALARMTYPE = '").append(type).append("' GROUP BY DAY(E.ALARMSTARTTIME), E.EQPID ) A GROUP BY A.DAY");
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
        String deptType = "";
        List<Object[]> resultsMES = new ArrayList<>();
        StringBuilder sbMES = new StringBuilder();//mes对应年月故障件数统计表SQL
        sbMES.append(" SELECT B.EQPID,B.AVAILABLEMINS,A.counts,A.ALARMTIME_LEN,B.DAY FROM (SELECT A.EQPID,SUM(A.counts) counts,SUM(A.ALARMTIME_LEN) ALARMTIME_LEN,A.day FROM (");
        sbMES.append(" SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sbMES.append(" day(E.ALARMSTARTTIME) day FROM EQP_RESULT_ALARM_D E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMES.append(" WHERE E.ALARMSTARTTIME LIKE '").append(time).append("%' AND M.ALARMNAME = '设备故障' AND M.ALARMTYPE = '").append(type).append("' AND datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 GROUP BY day(E.ALARMSTARTTIME), E.EQPID");
        sbMES.append(" ) A GROUP BY  A.EQPID, A.day ) A RIGHT JOIN  (");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        if (type.equals("半成品方型件")) {
            sbMES.append("  SELECT DISTINCT EQPID,1440  - SUM((CASE PRODUCTID WHEN '计划停机' THEN  convert(DECIMAL, WORKHOUREXTRA)  ELSE  0 END)) AVAILABLEMINS,day(PLANDATE) DAY");
            sbMES.append(" FROM PLAN_SEMI_SQUARE WHERE PLANDATE LIKE '%" + time + "%' GROUP BY EQPID, day(PLANDATE)) B ON A.day=B.DAY AND A.EQPID=B.EQPID ORDER BY B.EQPID,B.DAY");
            deptType = "方型加工课";
            Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
            resultsMES = query.getResultList();
        } else if (type.equals("半成品圆型件")) {
            sbMES.append(" SELECT DISTINCT EQPID, 1440  - SUM((CASE PRODUCTID WHEN '计划停机' THEN  convert(DECIMAL, WORKHOUR)  ELSE  0 END)) AVAILABLEMINS, day(PLANDATE) DAY");
            sbMES.append(" FROM PLAN_SEMI_CIRCLE WHERE PLANDATE LIKE '%" + time + "%'GROUP BY EQPID, day(PLANDATE)) B ON A.day=B.DAY AND A.EQPID=B.EQPID ORDER BY B.EQPID,B.DAY");
            deptType = "圆型加工课";
            Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
            resultsMES = query.getResultList();
        }
        //更改在EAM里类别名称说明   
        if (type.equals("半成品方型件")) {
            type = "方型加工课";
        } else if (type.equals("半成品圆型件")) {
            type = "圆型加工课";
        }
        time = time.replace("/", "-");//在EAM中时间格式替换
        StringBuilder sbEAM = new StringBuilder();
        sbEAM.append(" SELECT A.formid, W.overtime+W.worktime,COUNT(A.formid),TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime) TIME, DAY(R.hitchtime) day ,A.assetDesc");
        sbEAM.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno=A.formid LEFT JOIN  equipmentworktime W ON A.deptno=W.dept and R.formdate=W.formdate WHERE R.hitchtime like'%").append(time).append("%' AND A.deptname='").append(type).append("'  AND R.rstatus>='30' AND R.rstatus<'98'");
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
                        obj1[i + 1] = item[1];
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
        String deptType = "";
        List<Object[]> resultsMES = new ArrayList<>();
        StringBuilder sbMES = new StringBuilder();//mes对应年月故障件数统计表SQL
        sbMES.append(" SELECT B.EQPID,B.AVAILABLEMINS,A.counts,A.ALARMTIME_LEN,B.DAY FROM (SELECT A.EQPID,SUM(A.counts) counts,SUM(A.ALARMTIME_LEN) ALARMTIME_LEN,A.day FROM (");
        sbMES.append(" SELECT E.EQPID, COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN,");
        sbMES.append(" month(E.ALARMSTARTTIME) day FROM EQP_RESULT_ALARM_D E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMES.append(" WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' AND M.ALARMTYPE = '").append(type).append("' AND datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME) > 10 GROUP BY month(E.ALARMSTARTTIME), E.EQPID");
        sbMES.append(" ) A GROUP BY  A.EQPID, A.day ) A RIGHT JOIN  (");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //更改在EAM里类别名称说明   
        if (type.equals("半成品方型件")) {
            sbMES.append("  SELECT DISTINCT EQPID,day( DATEADD(DD, -DAY(DATEADD(MM, 1,PLANDATE )), DATEADD(MM, 1,PLANDATE)))*1440  -  SUM((CASE PRODUCTID WHEN '计划停机' THEN  convert(DECIMAL, WORKHOUREXTRA)  ELSE  0 END)) AVAILABLEMINS,month(PLANDATE) DAY");
            sbMES.append(" FROM PLAN_SEMI_SQUARE WHERE PLANDATE LIKE '%" + year + "%' GROUP BY EQPID, month(PLANDATE)) B ON A.day=B.DAY AND A.EQPID=B.EQPID ORDER BY B.EQPID,B.DAY");
            deptType = "方型加工课";
            Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
            resultsMES = query.getResultList();
        } else if (type.equals("半成品圆型件")) {
            sbMES.append(" SELECT DISTINCT EQPID, day( DATEADD(DD, -DAY(DATEADD(MM, 1,PLANDATE )), DATEADD(MM, 1,PLANDATE)))*1440 - SUM((CASE PRODUCTID WHEN '计划停机' THEN  convert(DECIMAL, WORKHOUR)  ELSE  0 END)) AVAILABLEMINS, month(PLANDATE) DAY");
            sbMES.append(" FROM PLAN_SEMI_CIRCLE WHERE PLANDATE LIKE '%" + year + "%' GROUP BY EQPID, month(PLANDATE)) B ON A.day=B.DAY AND A.EQPID=B.EQPID ORDER BY B.EQPID,B.DAY");
            deptType = "圆型加工课";
            Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
            resultsMES = query.getResultList();
        } else {
            deptType = type;
        }

        StringBuilder sbEAM = new StringBuilder();
        sbEAM.append(" SELECT A.formid, W.worktime* day(LAST_DAY(R.formdate)) ,COUNT(A.formid),TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime) TIME, MONTH(R.hitchtime) day ,A.assetDesc");
        sbEAM.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno=A.formid LEFT JOIN  equipmentworktime W ON A.deptno=W.dept and R.formdate=W.formdate WHERE R.hitchtime like'%").append(year).append("%' AND A.deptname='").append(deptType).append("'  AND R.rstatus>='30' AND R.rstatus<'98'");
        sbEAM.append(" AND TIMESTAMPDIFF(MINUTE, R.hitchtime, R.completetime)>10 GROUP BY  A.formid,MONTH(R.hitchtime)");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query1.getResultList();
        Map<String, List<Object>> moMap = new LinkedHashMap<>();

        StringBuilder sbWtiem = new StringBuilder();

        sbWtiem.append(" SELECT MONTH(formdate),sum(worktime)FROM equipmentworktime WHERE formdate LIKE '%" + year + "%' AND deptname = '" + type + "'GROUP BY MONTH(formdate)");

        query1 = getEntityManager().createNativeQuery(sbWtiem.toString());
        List<Object[]> resultsTime = query1.getResultList();
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
                for (Object[] objects : resultsTime) {
                    if (i == Integer.parseInt(objects[0].toString())) {
                        obj1[i + 1] = objects[1];
                    }
                }

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
    public List getEquipmentTotalEfficiency(String year, String EPQID) throws ParseException {

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
        sbMESLEN.append(" SELECT A.DAY,MAX(CASE A.ALARMNAME WHEN '设备故障' THEN A.ALARMTIME_LEN ELSE 0 END) 设备故障,MAX(CASE A.ALARMNAME WHEN '计划停机' THEN A.ALARMTIME_LEN ELSE 0 END) 计划停机,MAX(CASE A.ALARMNAME WHEN '绿灯暖机' THEN A.ALARMTIME_LEN ELSE 0 END)  绿灯暖机,MAX(CASE A.ALARMNAME WHEN '设备保养' THEN A.ALARMTIME_LEN ELSE 0 END) 设备保养,MAX(CASE A.ALARMNAME WHEN '欠料等待' THEN A.ALARMTIME_LEN ELSE 0 END) 欠料等待, MAX(CASE A.ALARMNAME WHEN '物料返修' THEN A.ALARMTIME_LEN  ELSE 0 END) 物料返修,MAX(CASE A.ALARMNAME WHEN '生技试模' THEN A.ALARMTIME_LEN ELSE 0 END) 生技试模,   MAX(CASE A.ALARMNAME WHEN '共用刀具模具等待' THEN A.ALARMTIME_LEN ELSE 0 END)   共用刀具模具等待, MAX(CASE A.ALARMNAME WHEN '刀具调试更换' THEN A.ALARMTIME_LEN  ELSE 0 END) 刀具调试更换, MAX(CASE A.ALARMNAME WHEN '停机换模' THEN A.ALARMTIME_LEN ELSE 0 END) 停机换模, MAX(CASE A.ALARMNAME WHEN '单模拆装' THEN A.ALARMTIME_LEN ELSE 0 END) 单模拆装, MAX(CASE A.ALARMNAME WHEN '换砂轮/夹头' THEN A.ALARMTIME_LEN ELSE 0 END) 换砂轮夹头,MAX(CASE A.ALARMNAME WHEN '停机待测' THEN A.ALARMTIME_LEN ELSE 0 END) 停机待测,MAX(CASE A.ALARMNAME WHEN '首件调整' THEN A.ALARMTIME_LEN ELSE 0 END) 首件调整,  MAX(CASE A.ALARMNAME WHEN '上下料干涉等待' THEN A.ALARMTIME_LEN  ELSE 0 END)    上下料干涉等待, MAX(CASE A.ALARMNAME WHEN '带教新人讲解' THEN A.ALARMTIME_LEN  ELSE 0 END)  带教新人不足,  MAX(CASE A.ALARMNAME WHEN '其他' THEN A.ALARMTIME_LEN ELSE 0 END)      其他, MAX(CASE A.ALARMNAME WHEN NULL THEN A.ALARMTIME_LEN ELSE 0 END)      未填, ");
        sbMESLEN.append(" SUM(A.ALARMTIME_LEN) SUMLEN FROM (SELECT A.ALARMNAME,SUM(convert(INT, ALARMTIME_LEN)) /60 ALARMTIME_LEN, DAY(A.DATE) DAY FROM (SELECT EQPID,B.ALARMNAME,convert(VARCHAR(10), cast(dateadd(hour,-8,A.ALARMSTARTTIME) as date), 111) AS DATE,");
        sbMESLEN.append(" ALARMTIME_LEN FROM EQP_RESULT_ALARM_D A LEFT JOIN MALARM B ON A.SPECIALALARMID = B.ALARMID WHERE EQPID = '").append(EPQID).append("' AND convert(VARCHAR(10), cast(dateadd(hour, -8, A.ALARMSTARTTIME) as date), 111) LIKE '").append(year).append("%') A  WHERE A.DATE LIKE '" + year + "%' GROUP BY A.ALARMNAME, DAY(A.DATE)) A  GROUP BY A.DAY");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESLEN.toString());
        List<Object[]> resultsLEN = query.getResultList();

        //      报工数量及产出标准工时
//        StringBuilder sbMESQTY = new StringBuilder();
////        sbMESQTY.append(" SELECT day,COUNT(A.EQPID) AS QTY, round(SUM(STD_TIME) / 60, 1) AS MINUTE FROM(SELECT A.EQPID, B.STD_TIME,day(PROCESSCOMPLETETIME) AS day FROM MEQP C");
////        sbMESQTY.append(" LEFT JOIN PROCESS_STEP A ON A.EQPID = C.EQPID LEFT JOIN PROCESS_STEP_TIME B ON A.PRODUCTCOMPID = B.PRODUCTCOMPID AND A.EQPID = B.EQPID AND A.PRODUCTORDERID = B.PRODUCTORDERID AND A.SYSID = B.SYSID AND A.STEPID = B.STEPID");
////        sbMESQTY.append(" WHERE  A.PROCESSCOMPLETETIME LIKE '").append(year).append("%' AND A.EQPID='").append(EPQID).append("' AND A.PROCESSCOMPLETETIME IS NOT NULL) A GROUP BY  day");
//        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQTY.toString());
//        List<Object[]> resultsQTY = query.getResultList();
        String tName = "";//获取查询计划件数的表名
        String avaTime = "";//周计划工时
        //      计划产出件数
        StringBuilder sbMESPlan = new StringBuilder();
        if (dept.equals("半成品方型件")) {
            tName = "PLAN_SEMI_SQUARE";
            avaTime = ",sum(convert(DECIMAL, WORKHOUR)*convert(DECIMAL, NUM))";
        } else {
            tName = "PLAN_SEMI_CIRCLE";
        }
        sbMESPlan.append(" SELECT  day(PLANDATE) day,  SUM(convert(DECIMAL, NUM)) estimateNUM ").append(avaTime).append("FROM ").append(tName).append(" WHERE EQPID ='").append(EPQID).append("'  AND PLANDATE LIKE '").append(year).append("%'  GROUP BY DAY(PLANDATE)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESPlan.toString());
        List<Object[]> resultsPlan = query.getResultList();

        //        生管停机计划时间获取
        StringBuilder sbMESDowntime = new StringBuilder();
        sbMESDowntime.append(" SELECT day(PLANDATE) day,SUM(convert(DECIMAL, WORKHOUR)) FROM  ").append(tName).append(" WHERE  EQPID ='").append(EPQID).append("' AND PLANDATE LIKE '").append(year).append("%' AND PRODUCTID='计划停机' GROUP BY DAY(PLANDATE)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDowntime.toString());
        List<Object[]> resultsDowntime = query.getResultList();
        //      不合格单数量
        StringBuilder sbMESQCQTY = new StringBuilder();
        sbMESQCQTY.append(" SELECT SUM ( convert(DECIMAL, DEFECTNUM)) AS DEFECTSUM,day(C.NODESTARTTIME) day FROM  FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD  A  ON  A.PROJECTID=B.PROJECTID          left join (select  * from FLOW_PROJECT_HISTORY where PROCESSNODEID='UQFN0002' AND EFFECTIVEFLAG='Y' AND ADDFLAG='N'  ) C on B.PROJECTID =C.PROJECTID  WHERE  B.PROJECTID LIKE 'QC%' and B.ANALYSISJUDGEMENTRESULT != '良品' and B.ANALYSISJUDGEMENTRESULT != '告知' AND B.ISPROCESSED='Y' AND C.NODESTARTTIME LIKE '").append(year).append("%' AND B.UQFTYPE ='UQFG0003' AND A.EQPID ='").append(EPQID).append("' GROUP BY day(C.NODESTARTTIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTY.toString());
        List<Object[]> resultsQCQTY = query.getResultList();
        //      特采数量
        StringBuilder sbMESQCQTYSpecial = new StringBuilder();
        sbMESQCQTYSpecial.append(" SELECT DAY(B.PROJECTCREATETIME) DAY,SUM(convert(DECIMAL, A.DEFECTNUM)) AS DEFECTSUM FROM FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD A ON A.PROJECTID = B.PROJECTID AND B.ANALYSISJUDGEMENTRESULT ='特采'");
        sbMESQCQTYSpecial.append(" LEFT JOIN PROCESS_STEP S ON A.PRODUCTORDERID = S.PRODUCTORDERID AND A.COMPIDSEQ = S.PRODUCTSERIALNUMBER AND B.SOURCESTEPIP = S.STEPID ");
        sbMESQCQTYSpecial.append(" WHERE B.PROJECTID LIKE 'QC%' AND B.ISPROCESSED = 'Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND B.UQFTYPE = 'UQFG0003'  AND S.EQPID = '").append(EPQID).append("' GROUP BY DAY(B.PROJECTCREATETIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTYSpecial.toString());
        List<Object[]> resultsQCQTYSpecial = query.getResultList();
        //        EAM
        StringBuilder sbEAM = new StringBuilder();
        String yearEAM = year.replace("/", "-").substring(0, 7);
        sbEAM.append(" SELECT DAY(E.hitchtime),count(E.formid),SUM(TIMESTAMPDIFF(MINUTE, E.hitchtime, E.completetime)),SUM(TIMESTAMPDIFF(MINUTE,E.servicearrivetime,E.completetime)),  SUM(TIMESTAMPDIFF(MINUTE, E.hitchtime, DATE_SUB(DATE_FORMAT(E.hitchtime,'%Y-%m-%d'),INTERVAL -1 DAY)))  FROM equipmentrepair E LEFT JOIN assetcard A  ON E.assetno=A.formid");
        sbEAM.append(" WHERE E.hitchtime LIKE '%").append(yearEAM).append("%' AND  A.remark='").append(EPQID).append("' AND E.rstatus>=30 AND E.rstatus!=98  GROUP BY DAY(E.hitchtime)");
        query = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query.getResultList();
        List<Object[]> rList = new ArrayList<>();
        List<Object[]> remarkList = new ArrayList<>();
        //数据选取为产能加工报表
        String str = "";
        String remarkSql = "";
        if (dept.equals("半成品方型件")) {
            str = " SELECT   DAY(PRODUCTTIME) DAY,sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A)) + SUM(convert(DECIMAL(13,2), SHIFT_B)) + SUM(convert(DECIMAL(13,2), SHIFT_C)),SUM(convert(DECIMAL(13,2), SHIFT_A)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_B)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_C)*convert(DECIMAL(13,2), WORKHOUR)) FROM dbo.PLAN_CAPACITY_FX_DETAIL WHERE PRODUCTTIME LIKE '%" + year + "%' AND EQPID='" + EPQID + "'  GROUP BY  DAY(PRODUCTTIME)";
            remarkSql = " SELECT DAY(PRODUCTTIME),REMARK FROM PLAN_CAPACITY_FX_REMARK WHERE   PRODUCTTIME LIKE '" + year + "%' And EQPID='" + EPQID + "'";
        } else {
            str = " SELECT  DAY(PRODUCTTIME),  sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)+convert(DECIMAL(13,2), SHIFT_A_B)+convert(DECIMAL(13,2), SHIFT_B_A)+convert(DECIMAL(13,2), SHIFT_B_B)+convert(DECIMAL(13,2), SHIFT_C_A)+convert(DECIMAL(13,2), SHIFT_C_B)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_B_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_C_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_A_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_B_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_C_B)*convert(DECIMAL(13,2), WORKHOUR_B)) FROM PLAN_CAPACITY_YX_DETAIL WHERE PRODUCTTIME  Like '" + year + "%' And EQPID='" + EPQID + "' GROUP BY  DAY(PRODUCTTIME)";
            remarkSql = " SELECT DAY(PRODUCTTIME),REMARK FROM PLAN_CAPACITY_YX_REMARK WHERE   PRODUCTTIME LIKE '" + year + "%' And EQPID='" + EPQID + "'";
        }
        query = superEJBForMES.getEntityManager().createNativeQuery(str);
        rList = query.getResultList();
        //获取产能加工报表备注
        query = superEJBForMES.getEntityManager().createNativeQuery(remarkSql);
        remarkList = query.getResultList();
        List<Object[]> list = new ArrayList<>();
        int strMin = 0;
        int endMin = 0;
        int downTime = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(year));
        // 获取当前月份的天数
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= days; i++) {
            Object[] obj = new Object[45];
            for (int j = 0; j < 35; j++) {
                obj[j] = 0;
            }

            for (Object[] objects : resultsAVA) {
                if (objects[0].equals(i)) {
                    //      将有生管计划的所有值赋0
                    for (int j = 0; j < 42; j++) {
                        obj[j] = 0;
                    }
                    obj[7] = 1440 - Integer.parseInt(objects[2].toString());
//                    故障次数为0时将数值赋0
                    if (objects[3] != null) {
                        obj[26] = objects[4];
                        obj[28] = objects[3];
                    }
                }
            }
            obj[1] = 1440;
//          实际产出件数及标准工时
//            for (Object[] object : resultsQTY) {
//                if (object[0].equals(i)) {
//                    obj[3] = object[1];
//                    obj[4] = object[2];
//                }
//            }
            String[] y = year.split("/");//获取对应年月份
//            计划产出件数
            for (Object[] object : resultsPlan) {
                if (object[0].equals(i)) {
                    obj[2] = object[1];
                    if ((Integer.parseInt(y[0].toString()) == 2022 && Integer.parseInt(y[1].toString()) <= 7) || Integer.parseInt(y[0].toString()) < 2022) {//从22年8月前开始维护的数据
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
            }
            //            生管计划停机时间
            for (Object[] object : resultsDowntime) {
                if (object[0].equals(i)) {
                    if ((Integer.parseInt(y[0].toString()) == 2022 && Integer.parseInt(y[1].toString()) >= 8) || Integer.parseInt(y[0].toString()) > 2022) {//从22年8月开始维护的数据
                        obj[7] = object[1];
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
                if (strMin >= 1440) {
                    obj[27] = 1440;
                    obj[30] = 1440;
                } else {
                    obj[27] = strMin;
                    obj[30] = endMin;
                }
            } else {
                obj[27] = 0;
                obj[30] = 0;
            }
//            EAM故障时间及故障次数
            for (Object[] objects : resultsEAM) {
                if (objects[0].equals(i)) {
                    obj[27] = Integer.parseInt(objects[2].toString()) + Integer.parseInt(obj[25].toString());
                    obj[29] = objects[1];
                    obj[30] = Integer.parseInt(objects[3].toString()) + Integer.parseInt(obj[28].toString());
                    obj[31] = objects[1];
                    int day = Integer.parseInt(objects[0].toString());//获取是哪天

                    if (Integer.parseInt(objects[2].toString()) > 1440) {//当维修时间超出当天时间，将超出部分归类到第二天
                        obj[27] = Integer.parseInt(objects[4].toString());
                        strMin = Integer.parseInt(objects[2].toString()) - Integer.parseInt(objects[4].toString());
                        endMin = Integer.parseInt(objects[3].toString()) - Integer.parseInt(objects[4].toString());
                        obj[30] = Integer.parseInt(objects[4].toString()) - (Integer.parseInt(objects[2].toString()) - Integer.parseInt(objects[3].toString()));
                    }
                }
            }
            for (Object[] object : remarkList) {
                if (object[0].equals(i)) {
                    if (obj[40] == null) {
                        obj[40] = object[1].toString();
                    } else {
                        obj[40] += " ," + object[1].toString();
                    }

                }
            }

            for (Object[] objects : resultsLEN) {
                if (objects[0].equals(i)) {
                    for (int j = 2; j <= 18; j++) {
                        obj[7 + j] = objects[j];
                    }
                    if (dept.equals("半成品方型件")) {
                        obj[8] = Integer.parseInt(objects[2].toString()) + Integer.parseInt(objects[3].toString());
                    } else {
                        obj[8] = objects[2].toString();
                    }
                    obj[26] = objects[1];
                    if (!obj[26].equals(0)) {
                        obj[28] = 1;
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
            for (Object[] objects : rList) {
                if (objects[0].equals(i)) {
                    obj[2] = objects[1];
                    obj[3] = objects[2];
                    obj[4] = objects[3];
                }
            }
            obj[0] = i;
            list.add(obj);
        }

        return list;
    }

    /**
     * 检查日期是否在范围内
     */
    public static boolean isDateBetween(String targetDateStr,
            String startDateStr,
            String endDateStr
    ) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date targetDate = formatter.parse(targetDateStr.trim());
        Date startDate = formatter.parse(startDateStr.trim());
        Date endDate = formatter.parse(endDateStr.trim());
        return !targetDate.before(startDate) && !targetDate.after(endDate);
    }
//根据年份获取故障停线率

    public List<Object[]> getYearFailure(String Year) throws ParseException {

        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        StringBuilder sbEAM = new StringBuilder();
        //获取停线的加工设备和停线时间
        sbEAM.append("SELECT B.remark,B.deptno,MONTH(hitchtime),DATE_FORMAT(A.hitchtime, '%Y/%m/%d') hitchtime ,DATE_FORMAT(A.completetime, '%Y/%m/%d') completetime ,TIMESTAMPDIFF(MINUTE, A.hitchtime,CASE WHEN A.completetime IS NULL THEN NOW() ELSE A.completetime END) diffTime ,downinitiatetime,completetime"
                + " FROM equipmentrepair A LEFT JOIN assetcard B  ON A.assetno=B.formid   where  A.hitchtime like '" + Year + "%'  and A.company='C'"
                + " and B.remark is not null  and B.remark!=''  and (A.hitchurgency = '03'   or downinitiatetime is not null)  and A.rstatus<=95");
        sbEAM.append(" AND (B.remark LIKE 'M/C%'  OR  B.remark LIKE 'KAPP%' OR  B.remark LIKE 'FMS%' OR  B.remark LIKE '%立式加工机%' OR  B.remark LIKE 'NSM%' OR  B.remark LIKE 'CG%' )  and B.remark!='KAPP-09' AND qty>0");
        Query query = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query.getResultList();

        // 按件号分组
        Map<String, List<Object[]>> groupMap = new LinkedHashMap<>();
        for (Object[] row : resultsEAM) {
            String assetno = (String) row[0];
            if (assetno == null) {
                assetno = ""; // 处理空件号
            }
            groupMap.computeIfAbsent(assetno, k -> new ArrayList<>()).add(row);
        }
        List<Object[]> resultEAMList = new ArrayList<>();

        for (List<Object[]> list : groupMap.values()) {
            // 按开始时间升序排序
            list.sort(Comparator.comparing(row -> parseDateTime(row[6])));

            LocalDateTime lastEnd = null; // 已覆盖的最大结束时间

            for (Object[] row : list) {
                LocalDateTime start = parseDateTime(row[6]);
                LocalDateTime end = parseDateTime(row[7]);

                // 未完成的记录，结束时间取当前时间
                if (end == null) {
                    end = LocalDateTime.now();
                }

                long effectiveMinutes = 0;

                if (lastEnd == null || start.isAfter(lastEnd)) {
                    // 无重叠，全部有效
                    effectiveMinutes = ChronoUnit.MINUTES.between(start, end);
                } else {
                    // 有重叠，只计算超出 lastEnd 的部分
                    if (end.isAfter(lastEnd)) {
                        effectiveMinutes = ChronoUnit.MINUTES.between(lastEnd, end);
                    }
                    // 完全重叠时 effectiveMinutes 保持 0
                }

             // 直接更新 diffTime 列的值
                row[5] = effectiveMinutes;
                // 更新已覆盖的最大结束时间
                if (lastEnd == null || end.isAfter(lastEnd)) {
                    lastEnd = end;
                }
                resultEAMList.add(row);
            }
        }
        resultsEAM = resultEAMList;
        List list = new ArrayList();
        //圆型负荷时间
        StringBuilder sbMESYX = new StringBuilder();
        sbMESYX.append(" SELECT  month(PRODUCTTIME),sum(DOWN) FROM (");
        sbMESYX.append(" SELECT A.EQPID,A.PRODUCTTIME ,1440- sum(convert(DECIMAL(13, 2), DOWNTIME)) DOWN FROM  PLAN_CAPACITY_YX_DETAIL A left join MEQP  ON "
                + "A.EQPID=MEQP.EQPID");
        sbMESYX.append(" where  PRODUCTTIME LIKE '" + Year + "%'  and  EQPTYPEID ='实体设备'    AND A.EQPID!='KAPP-09' group by  A.EQPID,PRODUCTTIME)  loadTime group by month(PRODUCTTIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESYX.toString());
        //方型负荷时间
        List<Object[]> MESYXList = query.getResultList();
        StringBuilder sbMESFX = new StringBuilder();
        sbMESFX.append(" SELECT  month(PRODUCTTIME),sum(DOWN) FROM (");
        sbMESFX.append(" SELECT A.EQPID,A.PRODUCTTIME ,1440- sum(convert(DECIMAL(13, 2), DOWNTIME)) DOWN FROM  PLAN_CAPACITY_FX_DETAIL A left join MEQP "
                + " ON A.EQPID=MEQP.EQPID");
        sbMESFX.append("   where  PRODUCTTIME LIKE '" + Year + "%'  and  EQPTYPEID ='实体设备'  and A.EQPID !='M/C-09' group by  A.EQPID,PRODUCTTIME)  loadTime group by month(PRODUCTTIME)");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESFX.toString());
        List<Object[]> MESFXList = query.getResultList();
        //获取圆型生管停机时间
        StringBuilder sbMESDownYX = new StringBuilder();
        sbMESDownYX.append(" SELECT EQPID,PLANDATE,sum( convert(DECIMAL, WORKHOUR))  FROM PLAN_SEMI_CIRCLE    WHERE   PRODUCTID = '计划停机'  and PRODUCTNAME!='故障停机'");
        sbMESDownYX.append(" AND PLANDATE LIKE '" + Year + "%' GROUP BY EQPID,PLANDATE");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDownYX.toString());
        List<Object[]> MESDownYXList = query.getResultList();
        //圆型每个月合计停机时间
        StringBuilder MonthDownYXSj = new StringBuilder();
        MonthDownYXSj.append(" SELECT month(WORKDATE),sum(convert(int, ALARMTIME_LEN))/60  FROM EQP_RESULT_ALARM_D  where (EQPID like 'CG%'  OR EQPID LIKE 'NSM%')  AND  SPECIALALARMID='B0001'  AND ALARMSTARTTIME LIKE '%" + Year + "%'");
        MonthDownYXSj.append(" group by  month(WORKDATE)  ");
        query = superEJBForMES.getEntityManager().createNativeQuery(MonthDownYXSj.toString());
        List<Object[]> MonthDownYXSjList = query.getResultList();
        //获取方型生管停机时间
        StringBuilder sbMESDownFX = new StringBuilder();
        sbMESDownFX.append(" SELECT EQPID,PLANDATE,sum( convert(DECIMAL, WORKHOUR))  FROM PLAN_SEMI_SQUARE    WHERE   PRODUCTID = '计划停机'  and PRODUCTNAME!='故障停机'");
        sbMESDownFX.append(" AND PLANDATE LIKE '" + Year + "%' GROUP BY EQPID,PLANDATE");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDownFX.toString());
        List<Object[]> MESDownFXList = query.getResultList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Object[] FxLoad = new Object[13];
        Object[] YxLoad = new Object[13];
        int[] FxTime = new int[13];
        int[] YxTime = new int[13];
        Object[] FxTimeObject = new Object[13];
        Object[] YxTimeObject = new Object[13];
        for (Object[] eam : resultsEAM) {
            for (int i = 1; i <= 12; i++) {
                //如果发生时间等于结束时间，则直接将故障时间算入总故障
                if (eam[3].equals(eam[4])) {
                    if (Integer.parseInt(eam[2].toString()) == i) {
                        if (eam[1].equals("1P100")) {
                            FxTime[i] += Integer.parseInt(eam[5].toString());
                        } else if (eam[1].equals("1P500")) {
                            YxTime[i] += Integer.parseInt(eam[5].toString());
                        }
                    }
                } else {//代表有跨天的故障在判断故障内是否有生管计划停机的，故障开始时间不等于生管时间
                    if (Integer.parseInt(eam[2].toString()) == i) {
                        int downTime = Integer.parseInt(eam[5].toString());
                        if (eam[1].equals("1P100")) {
                            for (Object[] downFx : MESDownFXList) {
                                if (eam[0].equals(downFx[0]) && !eam[3].equals(downFx[1])) {
                                    if (eam[4] == null) {
                                        eam[4] = LocalDateTime.now().format(formatter);
                                    }
                                    boolean isTime = isDateBetween(downFx[1].toString(), eam[3].toString(), eam[4].toString());
                                    if (isTime) {
                                        downTime -= Integer.parseInt(downFx[2].toString());
                                    }
                                }
                            }
                            FxTime[i] += downTime;
                        } else if (eam[1].equals("1P500")) {
                            for (Object[] downYx : MESDownYXList) {
                                if (eam[0].equals(downYx[0]) && !eam[3].equals(downYx[1])) {
                                    if (eam[4] == null) {
                                        eam[4] = LocalDateTime.now().format(formatter);
                                    }
                                    boolean isTime = isDateBetween(downYx[1].toString(), eam[3].toString(), eam[4].toString());
                                    if (isTime) {
                                        downTime -= Integer.parseInt(downYx[2].toString());
                                    }
                                }
                            }
                            YxTime[i] += downTime;
                        }
                    }

                }
            }
        }
        for (Object[] yxLoad : MESYXList) {
            for (int i = 1; i <= 12; i++) {
                if (Integer.parseInt(yxLoad[0].toString()) == i) {
                    int loadC = 19 * 1440 * getDays(Integer.parseInt(Year), i);
                    for (Object[] monthYx : MonthDownYXSjList) {
                        if (Integer.parseInt(monthYx[0].toString()) == i) {
                            loadC = loadC - Integer.parseInt(monthYx[1].toString());
                        }
                    }
                    if (yxLoad[1] != null) {
                        YxLoad[i] = Double.parseDouble(yxLoad[1].toString()) + loadC;
                    }

                }
            }
        }
        for (Object[] fxLoad : MESFXList) {
            for (int i = 1; i <= 12; i++) {
                if (Integer.parseInt(fxLoad[0].toString()) == i) {
                    FxLoad[i] = fxLoad[1];
                }
            }
        }
        list.add(FxLoad);
        list.add(YxLoad);
        for (int i = 1; i < FxTime.length; i++) {
            FxTimeObject[i] = FxTime[i];
        }
        list.add(FxTimeObject);
        for (int i = 1; i < YxTime.length; i++) {
            YxTimeObject[i] = YxTime[i];
        }
        list.add(YxTimeObject);
        return list;
    }

    private static LocalDateTime parseDateTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toLocalDateTime();
        }
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        if (obj instanceof java.util.Date) {
            return ((java.util.Date) obj).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        String str = obj.toString().trim();
        // 去除末尾的毫秒部分（如 .0）
        if (str.contains(".")) {
            str = str.substring(0, str.lastIndexOf('.'));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(str, formatter);
        } catch (Exception e) {
            // 尝试其他格式
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            return LocalDateTime.parse(str, formatter);
        }
    }

    //获取根据佐证年份获取故障停线率
    public List<List<Object[]>> getEvidenceYearFailure(String Year) throws ParseException {
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        StringBuilder sbEAM = new StringBuilder();
        //获取停线的加工设备和停线时间
        sbEAM.append("SELECT B.remark,DATE_FORMAT(A.hitchtime, '%Y/%m/%d')   hitchtime,DATE_FORMAT(A.completetime, '%Y/%m/%d')   completetime,A.formid,A.assetno,A.itemno,B.assetDesc,A.repairusername,B.deptno,A.rstatus,A.serviceusername,hitchtime,servicearrivetime,autotransfertime ,    repairtransfertime,downinitiatetime, completetime,TIMESTAMPDIFF(MINUTE, A.downinitiatetime,CASE WHEN A.completetime IS NULL THEN NOW() ELSE A.completetime END) diffTime,B.remark"
                + " FROM equipmentrepair A LEFT JOIN assetcard B  ON A.assetno=B.formid   where  A.hitchtime like '" + Year + "%'  and A.company='C'"
                + " and B.remark is not null  and B.remark!=''  and (A.hitchurgency = '03'   or downinitiatetime is not null)  and A.rstatus<=95");
        sbEAM.append(" AND (B.remark LIKE 'M/C%'  OR  B.remark LIKE 'KAPP%' OR  B.remark LIKE 'FMS%' OR  B.remark LIKE '%立式加工机%' OR  B.remark LIKE 'NSM%' OR  B.remark LIKE 'CG%' )  and B.remark!='KAPP-09' AND qty>0");
        Query query = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query.getResultList();
        // 按件号分组
        Map<String, List<Object[]>> groupMap = new LinkedHashMap<>();
        for (Object[] row : resultsEAM) {
            String assetno = (String) row[0];
            if (assetno == null) {
                assetno = ""; // 处理空件号
            }
            groupMap.computeIfAbsent(assetno, k -> new ArrayList<>()).add(row);
        }
        List<Object[]> resultEAMList = new ArrayList<>();

        for (List<Object[]> list : groupMap.values()) {
            // 按开始时间升序排序
            list.sort(Comparator.comparing(row -> parseDateTime(row[15])));

            LocalDateTime lastEnd = null; // 已覆盖的最大结束时间

            for (Object[] row : list) {
                LocalDateTime start = parseDateTime(row[15]);
                LocalDateTime end = parseDateTime(row[16]);

                // 未完成的记录，结束时间取当前时间
                if (end == null) {
                    end = LocalDateTime.now();
                }

                long effectiveMinutes = 0;

                if (lastEnd == null || start.isAfter(lastEnd)) {
                    // 无重叠，全部有效
                    effectiveMinutes = ChronoUnit.MINUTES.between(start, end);
                } else {
                    // 有重叠，只计算超出 lastEnd 的部分
                    if (end.isAfter(lastEnd)) {
                        effectiveMinutes = ChronoUnit.MINUTES.between(lastEnd, end);
                    }
                    // 完全重叠时 effectiveMinutes 保持 0
                }

                // 追加有效分钟数列
                Object[] newRow = Arrays.copyOf(row, row.length + 1);
                newRow[row.length] = effectiveMinutes;  // Long 类型
                resultEAMList.add(newRow);
                // 更新已覆盖的最大结束时间
                if (lastEnd == null || end.isAfter(lastEnd)) {
                    lastEnd = end;
                }
            }
        }
        resultsEAM = resultEAMList;

        List<Object[]> result = new ArrayList<>();
        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        for (Object[] row : resultsEAM) {
            // 提取开始和结束时间
            String startStr = row[15].toString();
            String endStr = row[16].toString();
            LocalDateTime start = LocalDateTime.parse(startStr, formatterData);
            LocalDateTime end = LocalDateTime.parse(endStr, formatterData);

            // 同月处理
            if (YearMonth.from(start).equals(YearMonth.from(end))) {
                Object[] newRow = new Object[row.length + 1];
                System.arraycopy(row, 0, newRow, 0, row.length);
                newRow[15] = startStr; // 保持不变
                newRow[16] = endStr;
                newRow[17] = (int) ChronoUnit.MINUTES.between(start, end);
                result.add(newRow);
                continue;
            }
            // 切割处理
            LocalDateTime currentStart = start;
            while (true) {
                LocalDateTime monthEnd = YearMonth.from(currentStart).atEndOfMonth().atTime(23, 59, 59);
                boolean isLast = !end.isAfter(monthEnd); // 如果结束时间不晚于本月最后一天，则最后一段
                LocalDateTime currentEnd = isLast ? end : monthEnd;

                Object[] newRow = new Object[row.length + 1];
                System.arraycopy(row, 0, newRow, 0, row.length);
                newRow[15] = currentStart.format(formatterData);
                newRow[16] = currentEnd.format(formatterData);
                newRow[17] = (int) ChronoUnit.MINUTES.between(currentStart, isLast ? end : currentEnd);
                result.add(newRow);

                if (isLast) {
                    break;
                }
                // 下一段开始为下月第一天 00:00:00
                currentStart = monthEnd.plusSeconds(1).withHour(0).withMinute(0).withSecond(0);
            }
        }
        resultsEAM = result;
        //圆型负荷时间
        StringBuilder sbMESYX = new StringBuilder();
        sbMESYX.append(" SELECT EQPID,SUM(CASE WHEN MonthNum = 1 THEN DOWN ELSE 0 END) AS [1月],SUM(CASE WHEN MonthNum = 2 THEN DOWN ELSE 0 END) AS [2月],SUM(CASE WHEN MonthNum = 3 THEN DOWN ELSE 0 END) AS [3月],"
                + "SUM(CASE WHEN MonthNum = 4 THEN DOWN ELSE 0 END) AS [4月],SUM(CASE WHEN MonthNum = 5 THEN DOWN ELSE 0 END) AS [5月],");
        sbMESYX.append(" SUM(CASE WHEN MonthNum = 6 THEN DOWN ELSE 0 END) AS [6月],SUM(CASE WHEN MonthNum = 7 THEN DOWN ELSE 0 END) AS [7月],");
        sbMESYX.append(" SUM(CASE WHEN MonthNum = 8 THEN DOWN ELSE 0 END) AS [8月],SUM(CASE WHEN MonthNum = 9 THEN DOWN ELSE 0 END) AS [9月],");
        sbMESYX.append(" SUM(CASE WHEN MonthNum = 10 THEN DOWN ELSE 0 END) AS [10月],SUM(CASE WHEN MonthNum = 11 THEN DOWN ELSE 0 END) AS [11月],");
        sbMESYX.append(" SUM(CASE WHEN MonthNum = 12 THEN DOWN ELSE 0 END) AS [12月] FROM (SELECT A.EQPID,MONTH(A.PRODUCTTIME) AS MonthNum,SUM(DOWN) AS DOWN");
        sbMESYX.append(" FROM (SELECT A.EQPID,PRODUCTTIME,1440 - SUM(CONVERT(DECIMAL(13, 2), DOWNTIME)) AS DOWN FROM PLAN_CAPACITY_YX_DETAIL A LEFT JOIN MEQP ON A.EQPID = MEQP.EQPID WHERE PRODUCTTIME LIKE '" + Year + "%'AND EQPTYPEID = '实体设备'");
        sbMESYX.append(" AND A.EQPID != 'KAPP-09' GROUP BY A.EQPID, PRODUCTTIME) A GROUP BY A.EQPID, MONTH(A.PRODUCTTIME)) AS MonthlyData GROUP BY EQPID ORDER BY EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESYX.toString());
        List<Object[]> MESYXList = query.getResultList();
        //方型负荷时间
        StringBuilder sbMESFX = new StringBuilder();
        sbMESFX.append(" SELECT EQPID,SUM(CASE WHEN MonthNum = 1 THEN DOWN ELSE 0 END) AS [1月],SUM(CASE WHEN MonthNum = 2 THEN DOWN ELSE 0 END) AS [2月],SUM(CASE WHEN MonthNum = 3 THEN DOWN ELSE 0 END) AS [3月],"
                + "SUM(CASE WHEN MonthNum = 4 THEN DOWN ELSE 0 END) AS [4月],SUM(CASE WHEN MonthNum = 5 THEN DOWN ELSE 0 END) AS [5月],");
        sbMESFX.append(" SUM(CASE WHEN MonthNum = 6 THEN DOWN ELSE 0 END) AS [6月],SUM(CASE WHEN MonthNum = 7 THEN DOWN ELSE 0 END) AS [7月],");
        sbMESFX.append(" SUM(CASE WHEN MonthNum = 8 THEN DOWN ELSE 0 END) AS [8月],SUM(CASE WHEN MonthNum = 9 THEN DOWN ELSE 0 END) AS [9月],");
        sbMESFX.append(" SUM(CASE WHEN MonthNum = 10 THEN DOWN ELSE 0 END) AS [10月],SUM(CASE WHEN MonthNum = 11 THEN DOWN ELSE 0 END) AS [11月],");
        sbMESFX.append(" SUM(CASE WHEN MonthNum = 12 THEN DOWN ELSE 0 END) AS [12月] FROM (SELECT A.EQPID,MONTH(A.PRODUCTTIME) AS MonthNum,SUM(DOWN) AS DOWN");
        sbMESFX.append(" FROM (SELECT A.EQPID,PRODUCTTIME,1440 - SUM(CONVERT(DECIMAL(13, 2), DOWNTIME)) AS DOWN FROM PLAN_CAPACITY_FX_DETAIL A LEFT JOIN MEQP ON A.EQPID = MEQP.EQPID WHERE PRODUCTTIME LIKE '" + Year + "%'AND EQPTYPEID = '实体设备'");
        sbMESFX.append(" AND A.EQPID !='M/C-09' GROUP BY A.EQPID, PRODUCTTIME) A GROUP BY A.EQPID, MONTH(A.PRODUCTTIME)) AS MonthlyData GROUP BY EQPID ORDER BY EQPID");

        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESFX.toString());
        List<Object[]> MESFXList = query.getResultList();
        //获取圆型生管停机时间
        StringBuilder sbMESDownYX = new StringBuilder();
        sbMESDownYX.append(" SELECT EQPID,PLANDATE,sum( convert(DECIMAL, WORKHOUR))  FROM PLAN_SEMI_CIRCLE    WHERE   PRODUCTID = '计划停机'  and PRODUCTNAME!='故障停机'");
        sbMESDownYX.append(" AND PLANDATE LIKE '" + Year + "%' GROUP BY EQPID,PLANDATE order by  PLANDATE");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDownYX.toString());
        List<Object[]> MESDownYXList = query.getResultList();
        //圆型连线停机数据
        StringBuilder sbMESDownYXSj = new StringBuilder();
        sbMESDownYXSj.append(" SELECT EQPID,WORKDATE,sum(convert(int, ALARMTIME_LEN))/60  FROM EQP_RESULT_ALARM_D  where (EQPID like 'CG%'  OR EQPID LIKE 'NSM%')  AND  SPECIALALARMID='B0001'  AND ALARMSTARTTIME LIKE '%" + Year + "%'");
        sbMESDownYXSj.append(" group by  EQPID,WORKDATE  order by  WORKDATE");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDownYXSj.toString());
        List<Object[]> MESDownYXSjList = query.getResultList();
        MESDownYXList.addAll(MESDownYXSjList);//NSM  CG实际停机当计划停机计算
        //圆型每个月合计停机时间
        StringBuilder MonthDownYXSj = new StringBuilder();
        MonthDownYXSj.append(" SELECT EQPID,month(WORKDATE),sum(convert(int, ALARMTIME_LEN))/60  FROM EQP_RESULT_ALARM_D  where (EQPID like 'CG%'  OR EQPID LIKE 'NSM%')  AND  SPECIALALARMID='B0001'  AND ALARMSTARTTIME LIKE '%" + Year + "%'");
        MonthDownYXSj.append(" group by  EQPID,month(WORKDATE)  order by  EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(MonthDownYXSj.toString());
        List<Object[]> MonthDownYXSjList = query.getResultList();

        //获取方型生管停机时间
        StringBuilder sbMESDownFX = new StringBuilder();
        sbMESDownFX.append(" SELECT EQPID,PLANDATE,sum( convert(DECIMAL, WORKHOUR))  FROM PLAN_SEMI_SQUARE    WHERE   PRODUCTID = '计划停机'  and PRODUCTNAME!='故障停机'");
        sbMESDownFX.append(" AND PLANDATE LIKE '" + Year + "%' GROUP BY EQPID,PLANDATE");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDownFX.toString());
        List<Object[]> MESDownFXList = query.getResultList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String[] MESYXAdd = new String[21];
        MESYXAdd[0] = "NSM-01";
        MESYXAdd[1] = "NSM-02";
        MESYXAdd[2] = "NSM-03";
        MESYXAdd[3] = "NSM-04";
        MESYXAdd[4] = "NSM-06";
        MESYXAdd[5] = "NSM-07";
        MESYXAdd[6] = "NSM-08";
        MESYXAdd[7] = "NSM-09";
        MESYXAdd[8] = "CG-05";
        MESYXAdd[9] = "CG-06";
        MESYXAdd[10] = "CG-07";
        MESYXAdd[11] = "CG-08";
        MESYXAdd[12] = "CG-09";
        MESYXAdd[13] = "CG-10";
        MESYXAdd[14] = "CG-11";
        MESYXAdd[15] = "CG-12";
        MESYXAdd[16] = "CG-13";
        MESYXAdd[17] = "CG-14";
        MESYXAdd[18] = "CG-15";
        MESYXAdd[19] = "CG-18";
        MESYXAdd[20] = "CG-19";
        for (String eqpid : MESYXAdd) {
            Object[] obj = new Object[13];
            obj[0] = eqpid;
            for (int i = 1; i <= 12; i++) {
                int downTime = 0;
                // 获取当前月份的最后一天
                LocalDate lastDayOfMonth = LocalDate.of(Integer.parseInt(Year), i, 1);
                // 获取当前月份的天数
                int daysInMonth = (int) lastDayOfMonth.lengthOfMonth();
                for (Object[] objects : MonthDownYXSjList) {
                    if (eqpid.equals(objects[0]) && i == Integer.parseInt(objects[1].toString())) {
                        downTime = Integer.parseInt(objects[2].toString());
                    }
                }
                obj[i] = daysInMonth * 1440 - downTime;
            }
            MESYXList.add(obj);//将

        }
        for (Object[] eam : resultsEAM) {
            eam[17] = eam[19];
            int downTime = Integer.parseInt(eam[17].toString());
            DateTimeFormatter formatterEa = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime dt1 = LocalDateTime.parse(eam[15].toString(), formatterEa);
            LocalDateTime dt2 = LocalDateTime.parse(eam[16].toString(), formatterEa);
            //如果发生时间等于结束时间，则直接将故障时间算入总故障
            if (dt1.toLocalDate().equals(dt2.toLocalDate())) {
                continue;//跳出本次循环
            } else {//代表有跨天的故障在判断故障内是否有生管计划停机的，故障开始时间不等于生管时间

                if (eam[8].equals("1P100")) {
                    for (Object[] downFx : MESDownFXList) {
                        if (eam[0].equals(downFx[0]) && !eam[1].equals(downFx[1])) {
                            if (eam[2] == null) {
                                eam[2] = LocalDateTime.now().format(formatter);
                            }
                            boolean isTime = isDateBetween(downFx[1].toString(), dt1.toLocalDate().toString().split(" ")[0].replace('-', '/'), dt2.toLocalDate().toString().split(" ")[0].replace('-', '/'));
                            if (isTime) {
                                downTime -= Integer.parseInt(downFx[2].toString());
                            }
                        }
                    }

                } else if (eam[8].equals("1P500")) {
                    for (Object[] downYx : MESDownYXList) {
                        if (eam[0].equals(downYx[0]) && !eam[1].equals(downYx[1])) {
                            if (eam[2] == null) {
                                eam[2] = LocalDateTime.now().format(formatter);
                            }
                            boolean isTime = isDateBetween(downYx[1].toString(), dt1.toLocalDate().toString().split(" ")[0].replace('-', '/'), dt2.toLocalDate().toString().split(" ")[0].replace('-', '/'));
                            if (isTime) {
                                downTime -= Integer.parseInt(downYx[2].toString());
                            }
                        }
                    }

                }

            }
            if (downTime < 0) {
                eam[17] = 0;
            } else {
                eam[17] = downTime;//将减完后的停机时间为此数据
            }

        }
        List<List<Object[]>> list2 = new ArrayList<>();
        list2.add(resultsEAM);
        list2.add(MESYXList);
        list2.add(MESFXList);
        list2.add(MESDownYXList);
        list2.add(MESDownFXList);

        return list2;
    }

    public int getDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                // 判断是否为闰年
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return 0;
        }
    }

    public List<Object[]> getLEN(String type, String strDate, String endDate) {
        List<String> deptName = this.getEPQIDDeptname(type);
        if (!deptName.isEmpty()) {
            type = deptName.get(0);//获取加工机所在部门信息
        }
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT A.* FROM ( SELECT  EQPID, SUM(convert(INT, ALARMTIME_LEN))/60 LEN FROM EQP_RESULT_ALARM_D LEFT JOIN MALARM B ON SPECIALALARMID = B.ALARMID");
        sbMES.append(" WHERE ALARMSTARTTIME>= '").append(strDate).append("' AND ALARMSTARTTIME<='").append(endDate).append("'  AND B.ALARMTYPE='").append(type).append("'");
        sbMES.append(" GROUP BY EQPID) A  WHERE  A.LEN>120 ");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> lenList = query.getResultList();
        return lenList;

    }

    //获取当前班次明细数据
    public List<Object[]> getLENDta(String EPQID, String strDate, String endDate) {

        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT EQPID,ALARMSTARTTIME,ALARMENDTIME,convert(INT, ALARMTIME_LEN)/60,B.ALARMNAME ,REMARK FROM EQP_RESULT_ALARM_D LEFT JOIN MALARM B ON SPECIALALARMID = B.ALARMID WHERE");
        sbMES.append("  ALARMSTARTTIME>= '").append(strDate).append("' AND ALARMSTARTTIME<='").append(endDate).append("' AND convert(INT, ALARMTIME_LEN)/60>5   AND EQPID IN (" + EPQID + ") ORDER BY EQPID,ALARMSTARTTIME");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> lenList = query.getResultList();
        return lenList;

    }
//获取OEE年报

    public List getEquipmentTotalEfficiencyYearOEE(String year, String EPQID, String type, String cancellation) throws ParseException {

//       生管计划工时，连线故障次数及故障工时
        List<String> deptName = this.getEPQIDDeptname(EPQID);
        String dept = "";
        if (!deptName.isEmpty()) {
            dept = deptName.get(0);//获取加工机所在部门信息
        }

        List<Object[]> rList = new ArrayList<>();

        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        String remarkSql = "";
        String str = "";
        String loadStr = "";      //负荷时间计算SQL
        String loadStrNoQty = "";      //负荷时间计算SQL
        if (dept.equals("半成品方型件")) {
            str = " SELECT  EQPID,  month(PRODUCTTIME),sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A)) + SUM(convert(DECIMAL(13,2), SHIFT_B)) + SUM(convert(DECIMAL(13,2), SHIFT_C)),SUM(convert(DECIMAL(13,2), SHIFT_A)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_B)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_C)*convert(DECIMAL(13,2), WORKHOUR)) FROM dbo.PLAN_CAPACITY_FX_DETAIL WHERE PRODUCTTIME like '%" + year + "%' and PRODUCTTIME<=(SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE))) GROUP BY EQPID, month(PRODUCTTIME)";
            remarkSql = " SELECT EQPID,REMARK FROM PLAN_CAPACITY_FX_REMARK   WHERE  PRODUCTTIME like'%" + year + "%'  and PRODUCTTYPE='半成品方型件' GROUP BY EQPID, month(PRODUCTTIME)";
            loadStr = " SELECT  EQPID,  PRODUCTTIME,sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A)) + SUM(convert(DECIMAL(13,2), SHIFT_B)) + SUM(convert(DECIMAL(13,2), SHIFT_C)) QTY,SUM(convert(DECIMAL(13,2), SHIFT_A)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_B)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_C)*convert(DECIMAL(13,2), WORKHOUR)) FROM dbo.PLAN_CAPACITY_FX_DETAIL WHERE PRODUCTTIME like '%" + year + "%' and PRODUCTTIME<=(SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE))) GROUP BY EQPID, PRODUCTTIME";
            loadStrNoQty = "SELECT DISTINCT EQPID, PLANDATE, SUM(convert(DECIMAL, WORKHOUR)) halt FROM PLAN_SEMI_SQUARE WHERE PLANDATE LIKE '" + year + "%' AND PRODUCTID = '计划停机' GROUP BY EQPID, PLANDATE";
        } else {
            str = " SELECT  EQPID,  month(PRODUCTTIME),  sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)+convert(DECIMAL(13,2), SHIFT_A_B)+convert(DECIMAL(13,2), SHIFT_B_A)+convert(DECIMAL(13,2), SHIFT_B_B)+convert(DECIMAL(13,2), SHIFT_C_A)+convert(DECIMAL(13,2), SHIFT_C_B)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_B_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_C_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_A_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_B_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_C_B)*convert(DECIMAL(13,2), WORKHOUR_B)) FROM PLAN_CAPACITY_YX_DETAIL WHERE PRODUCTTIME like '%" + year + "%' and PRODUCTTIME<=(SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE))) GROUP BY EQPID, month(PRODUCTTIME)";
            remarkSql = " SELECT EQPID,REMARK FROM PLAN_CAPACITY_YX_REMARK   WHERE  PRODUCTTIME like'%" + year + "%'  and PRODUCTTYPE='半成品圆型件' GROUP BY EQPID, month(PRODUCTTIME)";
            loadStr = " SELECT  EQPID,  PRODUCTTIME,  sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)+convert(DECIMAL(13,2), SHIFT_A_B)+convert(DECIMAL(13,2), SHIFT_B_A)+convert(DECIMAL(13,2), SHIFT_B_B)+convert(DECIMAL(13,2), SHIFT_C_A)+convert(DECIMAL(13,2), SHIFT_C_B)) QTY,SUM(convert(DECIMAL(13,2), SHIFT_A_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_B_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_C_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_A_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_B_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_C_B)*convert(DECIMAL(13,2), WORKHOUR_B)) FROM PLAN_CAPACITY_YX_DETAIL WHERE PRODUCTTIME like '%" + year + "%'  and PRODUCTTIME<=(SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE))) GROUP BY EQPID, PRODUCTTIME";
            loadStrNoQty = "SELECT DISTINCT EQPID, PLANDATE, SUM(convert(DECIMAL, WORKHOUR)) halt FROM PLAN_SEMI_CIRCLE WHERE PLANDATE LIKE '" + year + "%' AND PRODUCTID = '计划停机' GROUP BY EQPID, PLANDATE";
        }
        String loadSql = " select A.EQPID, month(A.PRODUCTTIME) month, count(A.EQPID)*1440 halt from (";
        loadSql += loadStr + ") A   where A.QTY = 0   group by A.EQPID, month(A.PRODUCTTIME) ";
        Query query = superEJBForMES.getEntityManager().createNativeQuery(str);
        rList = query.getResultList();
        //计划停机时间
        query = superEJBForMES.getEntityManager().createNativeQuery(loadSql);
        List<Object[]> resultsLoad = query.getResultList();

        String loadActualSql = " select A.EQPID, month(A.PRODUCTTIME) month, sum(halt) halt from ( " + loadStr + ") A  left join (SELECT EQPID,  cast(dateadd(hour, -8, ALARMSTARTTIME) as date) as   WORKDATE ,ROUND(SUM(convert(DECIMAL(13, 1),ALARMTIME_LEN))/60,0) halt FROM EQP_RESULT_ALARM_D  where (SPECIALALARMID='B0001'  OR SPECIALALARMID='A0001'  OR SPECIALALARMID='A0014')  and ALARMSTARTTIME LIKE '" + year + "%' and ALARMSTARTTIME <= (SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE)))  GROUP BY  EQPID, cast(dateadd(hour, -8, ALARMSTARTTIME) as date)) B ON A.EQPID = B.EQPID AND A.PRODUCTTIME = B.WORKDATE where A.QTY != 0 and B.halt is not null group by A.EQPID, month(A.PRODUCTTIME) ";
        //实际停机时间
        query = superEJBForMES.getEntityManager().createNativeQuery(loadActualSql);
        List<Object[]> resultsActualLoad = query.getResultList();
        //       获取除外工时合计
        String speId = "B0001";
        String LDSql = "";
        if (dept.equals("半成品方型件")) {
            speId = "A0001";
            LDSql = "and   A.SPECIALALARMID!='A0014'";
        }
        StringBuilder sbMESLEN = new StringBuilder();
        sbMESLEN.append(" SELECT A.EQPID,month(convert(VARCHAR(10), cast(dateadd(HOUR, -8, A.ALARMSTARTTIME) AS DATE), 111)) AS DATE,sum(convert(INT, ALARMTIME_LEN)) / 60,");
        sbMESLEN.append("   sum(CASE A.SPECIALALARMID  WHEN '" + speId + "' THEN convert(INT, ALARMTIME_LEN) / 60  ELSE 0 END) FROM EQP_RESULT_ALARM_D  A   LEFT JOIN  (" + loadStr + ") B  ON A.EQPID=B.EQPID AND A.WORKDATE=B.PRODUCTTIME WHERE A.ALARMSTARTTIME LIKE '" + year + "%' " + LDSql + " and B.QTY>0  and ALARMSTARTTIME<=(SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE)))  and A.SPECIALALARMID != '" + speId + "'");
        sbMESLEN.append(" GROUP BY A.EQPID, month(convert(VARCHAR(10), cast(dateadd(HOUR, -8, A.ALARMSTARTTIME) AS DATE), 111))");

        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESLEN.toString());
        List<Object[]> resultsLEN = query.getResultList();

        String tName = "";//获取查询计划件数的表名
        String avaTime = "";//周计划工时
        //      计划产出件数
        StringBuilder sbMESPlan = new StringBuilder();
        String eamDept = "";
        if (dept.equals("半成品方型件")) {
            eamDept = "方型加工课";
            tName = "PLAN_SEMI_SQUARE";
            avaTime = ",sum(convert(DECIMAL, WORKHOUR)*convert(DECIMAL, NUM))";
        } else {
            tName = "PLAN_SEMI_CIRCLE";
            eamDept = "圆型加工课";
        }
//        生管停机计划时间获取
        StringBuilder sbMESDowntime = new StringBuilder();
        sbMESDowntime.append("SELECT A.EQPID,A.month,A.planda    FROM ");
        sbMESDowntime.append("( SELECT DISTINCT EQPID,month(PLANDATE) month,MAX(CASE WHEN YEAR(PLANDATE) = YEAR(GETDATE()) AND MONTH(PLANDATE) = MONTH(GETDATE())  THEN DAY(DATEADD(DD, -1, GETDATE())) ELSE DAY(DATEADD(DD, -DAY(DATEADD(MM, 1, PLANDATE)), DATEADD(MM, 1, PLANDATE))) END)*1440 AS planda  FROM  ").append(tName).append(" WHERE  PLANDATE LIKE '").append(year).append("%' GROUP BY EQPID,month(PLANDATE)) A  LEFT JOIN");
        sbMESDowntime.append(" (SELECT DISTINCT EQPID,month(PLANDATE) month, SUM(convert(DECIMAL, WORKHOUR)) halt    FROM  ").append(tName).append(" WHERE  PLANDATE LIKE '").append(year).append("%' AND PRODUCTID='计划停机' and PLANDATE<=(SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE)))  GROUP BY EQPID,month(PLANDATE)) B ON A.EQPID=B.EQPID AND A.month=B.month");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDowntime.toString());
        List<Object[]> resultsDowntime = query.getResultList();
        //      不合格单数量
        StringBuilder sbMESQCQTY = new StringBuilder();
        sbMESQCQTY.append(" SELECT A.EQPID,month(C.NODESTARTTIME) month,SUM(convert(DECIMAL, DEFECTNUM)) AS DEFECTSUM FROM  FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD  A  ON  A.PROJECTID=B.PROJECTID          left join (select  * from FLOW_PROJECT_HISTORY where PROCESSNODEID='UQFN0002' AND EFFECTIVEFLAG='Y' AND ADDFLAG='N'  ) C on B.PROJECTID =C.PROJECTID WHERE  B.PROJECTID LIKE 'QC%' and B.ANALYSISJUDGEMENTRESULT != '良品' and B.ANALYSISJUDGEMENTRESULT != '告知' AND B.ISPROCESSED='Y' AND C.NODESTARTTIME LIKE '").append(year).append("%' AND B.UQFTYPE ='UQFG0003'  and A.EQPID IS NOT NULL  GROUP BY A.EQPID,month(C.NODESTARTTIME)");

        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTY.toString());
        List<Object[]> resultsQCQTY = query.getResultList();
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将EQPID相同的数据存在一起 MES的
        resultsDowntime.forEach(objects -> {
            List<Object> EQPID = new ArrayList<>();
            String anKey = objects[0].toString();

            if (!cancellation.contains(anKey)) {
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
            }

        });
        List<Object[]> list = new ArrayList<>();
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            List<?> itemList = entry.getValue();
            List<Object[]> itemList1 = (List<Object[]>) itemList;
            Object[] entity = new Object[160];
            entity[0] = entry.getKey();
            if (entity[0].toString().equals("KAPP-02")) {
                entity[0] = entry.getKey();
            }
            for (int i = 1; i <= 12; i++) {
                int downTime = 0;
                for (Object[] obj : itemList1) {
                    if (obj[0].toString().equals(entry.getKey()) && Integer.parseInt(obj[1].toString()) == i) {
                        entity[2 + (i - 1) * 9] = obj[2];
                        if (obj[2] != null) {
                            downTime = Integer.parseInt(obj[2].toString());
                        }

                    }
                }
                for (Object[] obj : resultsLoad) {
                    if (obj[0].toString().equals(entry.getKey()) && Integer.parseInt(obj[1].toString()) == i) {

                        if (obj[2] != null) {
                            downTime = downTime - ((int) (Integer.parseInt(obj[2].toString())));
                            entity[2 + (i - 1) * 9] = downTime;
                        }

                    }
                }
                for (Object[] obj : resultsActualLoad) {
                    if (obj[0].toString().equals(entry.getKey()) && Integer.parseInt(obj[1].toString()) == i) {
                        if (obj[2] != null) {
                            downTime = downTime - ((int) (Double.parseDouble(obj[2].toString())));
                            entity[2 + (i - 1) * 9] = downTime;
                        }

                    }
                }
                for (Object[] obj : rList) {
                    if (obj[0].toString().equals(entry.getKey()) && Integer.parseInt(obj[1].toString()) == i) {
                        entity[1 + (i - 1) * 9] = obj[4];
                        entity[4 + (i - 1) * 9] = obj[3];
                    }
                }

                for (Object[] obj : resultsLEN) {
                    if (obj[0].toString().equals(entry.getKey()) && Integer.parseInt(obj[1].toString()) == i) {
                        if (obj[2] != null && obj[3] != null) {
                            entity[3 + (i - 1) * 9] = Integer.parseInt(obj[2].toString()) - Integer.parseInt(obj[3].toString());
//                            entity[2 + (i - 1) * 9] = downTime - Integer.parseInt(obj[3].toString());
                        }

                    }
                }

                for (Object[] obj : resultsQCQTY) {
                    if (obj[0].toString().equals(entry.getKey()) && Integer.parseInt(obj[1].toString()) == i) {
                        entity[5 + (i - 1) * 9] = obj[2];

                    }
                }
            }
            list.add(entity);
        }

        return list;
    }

    public List getEquipmentTotalEfficiencyDayOEE(String year, String EPQID, String type, String cancellation) throws ParseException {

//       生管计划工时，连线故障次数及故障工时
        List<String> deptName = this.getEPQIDDeptname(EPQID);
        String dept = "";
        if (!deptName.isEmpty()) {
            dept = deptName.get(0);//获取加工机所在部门信息
        }

        List<Object[]> rList = new ArrayList<>();

        StringBuilder sbMESAVA = new StringBuilder();
        sbMESAVA.append(" SELECT  B.EQPID,B.AVAILABLEMINS,A.counts, A.ALARMTIME_LEN");
        sbMESAVA.append(" FROM (SELECT E.EQPID,COUNT(E.EQPID) counts,sum(datediff(MINUTE, E.ALARMSTARTTIME, E.ALARMENDTIME)) AS ALARMTIME_LEN");
        sbMESAVA.append("  FROM EQP_RESULT_ALARM_D E LEFT JOIN MALARM M ON E.SPECIALALARMID = M.ALARMID");
        sbMESAVA.append(" WHERE E.ALARMSTARTTIME LIKE '").append(year).append("%' AND M.ALARMNAME = '设备故障' and M.ALARMTYPE='" + dept + "'");
        sbMESAVA.append(" GROUP BY  E.EQPID) A RIGHT JOIN (SELECT E.EQPID,sum(AVAILABLEMINS) AVAILABLEMINS");
        sbMESAVA.append(" FROM MEQP M LEFT JOIN EQP_AVAILABLETIME_SCHEDULE E ON E.EQPID = M.EQPID  WHERE PLANDATE LIKE '").append(year).append("%' AND M.PRODUCTTYPE = '").append(dept).append("'");
        sbMESAVA.append("  AND M.EQPTYPEID='实体设备' GROUP BY E.EQPID) B ON  A.EQPID = B.EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMESAVA.toString());
        List<Object[]> resultsAVA = query.getResultList();
        String remarkSql = "";
        List<Object[]> remarkList = query.getResultList();
        if (type.equals("G")) {//当选择顾问版时数据选取为产能加工报表
            String str = "";

            if (dept.equals("半成品方型件")) {
                str = " SELECT  EQPID,sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A)) + SUM(convert(DECIMAL(13,2), SHIFT_B)) + SUM(convert(DECIMAL(13,2), SHIFT_C)),SUM(convert(DECIMAL(13,2), SHIFT_A)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_B)*convert(DECIMAL(13,2), WORKHOUR)+convert(DECIMAL(13,2), SHIFT_C)*convert(DECIMAL(13,2), WORKHOUR)) FROM dbo.PLAN_CAPACITY_FX_DETAIL WHERE PRODUCTTIME = '" + year + "' GROUP BY EQPID";
                remarkSql = " SELECT EQPID,REMARK FROM PLAN_CAPACITY_FX_REMARK   WHERE  PRODUCTTIME='" + year + "'  and PRODUCTTYPE='半成品方型件'";
            } else {
                str = " SELECT  EQPID,  sum(convert(DECIMAL(13,2), PRODUCTQTY)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)+convert(DECIMAL(13,2), SHIFT_A_B)+convert(DECIMAL(13,2), SHIFT_B_A)+convert(DECIMAL(13,2), SHIFT_B_B)+convert(DECIMAL(13,2), SHIFT_C_A)+convert(DECIMAL(13,2), SHIFT_C_B)),SUM(convert(DECIMAL(13,2), SHIFT_A_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_B_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_C_A)*convert(DECIMAL(13,2), WORKHOUR_A)+convert(DECIMAL(13,2), SHIFT_A_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_B_B)*convert(DECIMAL(13,2), WORKHOUR_B)+convert(DECIMAL(13,2), SHIFT_C_B)*convert(DECIMAL(13,2), WORKHOUR_B)) FROM PLAN_CAPACITY_YX_DETAIL WHERE PRODUCTTIME = '" + year + "' GROUP BY EQPID";
                remarkSql = " SELECT EQPID,REMARK FROM PLAN_CAPACITY_YX_REMARK   WHERE  PRODUCTTIME='" + year + "'  and PRODUCTTYPE='半成品圆型件'";
            }
            query = superEJBForMES.getEntityManager().createNativeQuery(str);
            rList = query.getResultList();
            query = superEJBForMES.getEntityManager().createNativeQuery(remarkSql);
            remarkList = query.getResultList();
        }
//       连线故障时间及总和
        StringBuilder sbMESLEN = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date thisDate = new SimpleDateFormat("yyyy/MM/dd").parse(year);
        //获得日历类
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisDate);
        // 把日期往后增加一天.整数往后推,负数往前移动
        calendar.add(Calendar.DATE, 1);
        //返回增加一天后的时间。
        String nextDate = formatter.format(calendar.getTime());
        sbMESLEN.append(" SELECT A.EQPID,MAX(CASE A.ALARMNAME WHEN '设备故障' THEN A.ALARMTIME_LEN ELSE 0 END) 设备故障, MAX(CASE A.ALARMNAME WHEN '计划停机' THEN A.ALARMTIME_LEN ELSE 0 END) 计划停机,MAX(CASE A.ALARMNAME WHEN '绿灯暖机' THEN A.ALARMTIME_LEN ELSE 0 END)  绿灯暖机,MAX(CASE A.ALARMNAME WHEN '设备保养' THEN A.ALARMTIME_LEN ELSE 0 END) 设备保养,MAX(CASE A.ALARMNAME WHEN '欠料等待' THEN A.ALARMTIME_LEN ELSE 0 END) 欠料等待,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '物料返修' THEN A.ALARMTIME_LEN  ELSE 0 END) 物料返修,MAX(CASE A.ALARMNAME WHEN '生技试模' THEN A.ALARMTIME_LEN ELSE 0 END) 生技试模,  ");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '共用刀具模具等待' THEN A.ALARMTIME_LEN ELSE 0 END)   共用刀具模具等待, MAX(CASE A.ALARMNAME WHEN '刀具调试更换' THEN A.ALARMTIME_LEN  ELSE 0 END) 刀具调试更换,");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '停机换模' THEN A.ALARMTIME_LEN ELSE 0 END) 停机换模, MAX(CASE A.ALARMNAME WHEN '单模拆装' THEN A.ALARMTIME_LEN ELSE 0 END) 单模拆装, MAX(CASE A.ALARMNAME WHEN '换砂轮/夹头' THEN A.ALARMTIME_LEN ELSE 0 END) 换砂轮夹头,MAX(CASE A.ALARMNAME WHEN '停机待测' THEN A.ALARMTIME_LEN ELSE 0 END) 停机待测,MAX(CASE A.ALARMNAME WHEN '首件调整' THEN A.ALARMTIME_LEN ELSE 0 END) 首件调整, ");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '上下料干涉等待' THEN A.ALARMTIME_LEN  ELSE 0 END)    上下料干涉等待, MAX(CASE A.ALARMNAME WHEN '带教新人讲解' THEN A.ALARMTIME_LEN  ELSE 0 END)  带教新人不足, ");
        sbMESLEN.append(" MAX(CASE A.ALARMNAME WHEN '其他' THEN A.ALARMTIME_LEN ELSE 0 END)      其他, MAX(CASE A.ALARMNAME WHEN NULL THEN A.ALARMTIME_LEN ELSE 0 END)      未填,");
        sbMESLEN.append(" SUM(A.ALARMTIME_LEN) SUMLEN FROM (SELECT A.ALARMNAME,SUM(convert(INT, ALARMTIME_LEN)) /60 ALARMTIME_LEN, EQPID FROM (SELECT EQPID,B.ALARMNAME,convert(VARCHAR(10), ALARMSTARTTIME,111) AS DATE,");
        sbMESLEN.append(" ALARMTIME_LEN FROM EQP_RESULT_ALARM_D A LEFT JOIN MALARM B ON A.SPECIALALARMID = B.ALARMID WHERE A.ALARMSTARTTIME>='" + year + " 08:00:00' and A.ALARMSTARTTIME<='" + nextDate + " 08:00:00'").append("and A .EQPID !='') A GROUP BY A.EQPID, A.ALARMNAME) A GROUP BY EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESLEN.toString());
        List<Object[]> resultsLEN = query.getResultList();

        //      报工数量及产出标准工时
        StringBuilder sbMESQTY = new StringBuilder();
        sbMESQTY.append(" SELECT A.EQPID,COUNT(A.EQPID) AS QTY, round(SUM(STD_TIME) / 60, 1) AS MINUTE FROM(SELECT A.EQPID, B.STD_TIME,day(PROCESSCOMPLETETIME) AS day FROM MEQP C");
        sbMESQTY.append(" LEFT JOIN PROCESS_STEP A ON A.EQPID = C.EQPID LEFT JOIN PROCESS_STEP_TIME B ON A.PRODUCTCOMPID = B.PRODUCTCOMPID AND A.EQPID = B.EQPID AND A.PRODUCTORDERID = B.PRODUCTORDERID AND A.SYSID = B.SYSID AND A.STEPID = B.STEPID");
        sbMESQTY.append(" WHERE  A.PROCESSCOMPLETETIME LIKE '").append(year).append("%' and C.EQPTYPEID='实体设备' AND C.PRODUCTTYPE='").append(dept).append("' AND A.PROCESSCOMPLETETIME IS NOT NULL) A GROUP BY  EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQTY.toString());
        List<Object[]> resultsQTY = query.getResultList();
        String tName = "";//获取查询计划件数的表名
        String avaTime = "";//周计划工时
        //      计划产出件数
        StringBuilder sbMESPlan = new StringBuilder();
        String eamDept = "";

        if (dept.equals("半成品方型件")) {
            eamDept = "方型加工课";
            tName = "PLAN_SEMI_SQUARE";
            avaTime = ",sum(convert(DECIMAL, WORKHOUR)*convert(DECIMAL, NUM))";
        } else {
            tName = "PLAN_SEMI_CIRCLE";
            eamDept = "圆型加工课";
        }
        sbMESPlan.append(" SELECT  EQPID,  SUM(convert(DECIMAL, NUM)) estimateNUM ").append(avaTime).append("FROM ").append(tName).append(" WHERE PLANDATE LIKE '").append(year).append("%'  GROUP BY EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESPlan.toString());
        List<Object[]> resultsPlan = query.getResultList();

//        生管停机计划时间获取
        StringBuilder sbMESDowntime = new StringBuilder();
        sbMESDowntime.append(" SELECT EQPID,SUM(convert(DECIMAL, WORKHOUR)) FROM  ").append(tName).append(" WHERE  PLANDATE LIKE '").append(year).append("%' AND PRODUCTID='计划停机' GROUP BY EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESDowntime.toString());
        List<Object[]> resultsDowntime = query.getResultList();
        //      不合格单数量
        StringBuilder sbMESQCQTY = new StringBuilder();
        sbMESQCQTY.append(" SELECT SUM ( convert(DECIMAL, DEFECTNUM)) AS DEFECTSUM,A.EQPID FROM  FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD  A  ON  A.PROJECTID=B.PROJECTID          left join (select  * from FLOW_PROJECT_HISTORY where PROCESSNODEID='UQFN0002' AND EFFECTIVEFLAG='Y' AND ADDFLAG='N'  ) C on B.PROJECTID =C.PROJECTID WHERE  B.PROJECTID LIKE 'QC%' and B.ANALYSISJUDGEMENTRESULT != '良品' and B.ANALYSISJUDGEMENTRESULT != '告知' AND B.ISPROCESSED='Y' AND C.NODESTARTTIME LIKE '").append(year).append("%' AND B.UQFTYPE ='UQFG0003'  GROUP BY A.EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTY.toString());
        List<Object[]> resultsQCQTY = query.getResultList();
        //      特采数量
        StringBuilder sbMESQCQTYSpecial = new StringBuilder();
        sbMESQCQTYSpecial.append(" SELECT S.EQPID,SUM(convert(DECIMAL, A.DEFECTNUM)) AS DEFECTSUM FROM FLOW_FORM_UQF_S_NOW B LEFT JOIN ANALYSISRESULT_QCD A ON A.PROJECTID = B.PROJECTID AND B.ANALYSISJUDGEMENTRESULT ='特采'");
        sbMESQCQTYSpecial.append(" LEFT JOIN PROCESS_STEP S ON A.PRODUCTORDERID = S.PRODUCTORDERID AND A.COMPIDSEQ = S.PRODUCTSERIALNUMBER AND B.SOURCESTEPIP = S.STEPID ");
        sbMESQCQTYSpecial.append(" WHERE B.PROJECTID LIKE 'QC%' AND B.ISPROCESSED = 'Y' AND B.PROJECTCREATETIME LIKE '").append(year).append("%' AND B.UQFTYPE = 'UQFG0003'   GROUP BY S.EQPID");
        query = superEJBForMES.getEntityManager().createNativeQuery(sbMESQCQTYSpecial.toString());
        List<Object[]> resultsQCQTYSpecial = query.getResultList();
        //        EAM
        StringBuilder sbEAM = new StringBuilder();
        String yearEAM = year.replace("/", "-");
        sbEAM.append(" SELECT A.remark,count(E.formid),SUM(TIMESTAMPDIFF(MINUTE, E.hitchtime, E.completetime))-E.excepttime,SUM(TIMESTAMPDIFF(MINUTE,E.servicearrivetime,E.completetime))-E.excepttime,  SUM(TIMESTAMPDIFF(MINUTE, E.hitchtime, DATE_SUB(DATE_FORMAT(E.hitchtime,'%Y-%m-%d'),INTERVAL -1 DAY)))  FROM equipmentrepair E LEFT JOIN assetcard A  ON E.assetno=A.formid");
        sbEAM.append(" WHERE E.hitchtime LIKE '%").append(yearEAM).append("%' AND E.rstatus>=30 AND E.rstatus!=98 AND A.remark IS NOT NULL   GROUP BY A.remark");
        query = getEntityManager().createNativeQuery(sbEAM.toString());
        List<Object[]> resultsEAM = query.getResultList();
        StringBuilder sbEAMEPQID = new StringBuilder();
        sbEAMEPQID.append(" SELECT A.remark,deptname FROM assetcard A LEFT JOIN  assetitem I ON A.itemno=I.itemno");
        sbEAMEPQID.append(" WHERE  A.remark IS NOT  NULL  and I.categoryid=3  AND A.remark !=''and I.categoryid=3 AND A. company='C'  AND qty!=0 ORDER BY remark");
        query = getEntityManager().createNativeQuery(sbEAMEPQID.toString());
        List<Object[]> resultsEAMEPQID = query.getResultList();
        List<Object[]> list = new ArrayList<>();
        int strMin = 0;
        int endMin = 0;
        for (Object[] i : resultsEAMEPQID) {
            if (!cancellation.contains(i[0].toString())) {

                Object[] obj = new Object[43];
                for (Object[] objects : resultsAVA) {
                    if (objects[0].equals(i[0].toString())) {

                        //      将有生管计划的所有值赋0
                        for (int j = 0; j < 40; j++) {
                            obj[j] = 0;
                        }
                        obj[1] = 1440;
                        obj[7] = 1440 - Integer.parseInt(objects[1].toString());
//                    故障次数为0时将数值赋0
                        if (objects[3] != null) {
                            obj[25] = objects[3];
                            obj[27] = objects[2];
                        }
                    }
                }

//          实际产出件数及标准工时
                for (Object[] object : resultsQTY) {
                    if (object[0].equals(i[0].toString())) {
                        obj[3] = object[1];
                        obj[4] = object[2];
                    }
                }
                String[] y = year.split("/");//获取对应年月份
//            计划产出件数
                for (Object[] object : resultsPlan) {
                    if (object[0].equals(i[0].toString())) {
                        obj[2] = object[1];
                        obj[1] = 1440;
                        if ((Integer.parseInt(y[0].toString()) == 2022 && Integer.parseInt(y[1].toString()) <= 7) || Integer.parseInt(y[0].toString()) < 2022) {//从22年8月前开始维护的数据
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
                }

                //            生管计划停机时间
                for (Object[] object : resultsDowntime) {
                    if (object[0].equals(i[0].toString())) {
                        if ((Integer.parseInt(y[0].toString()) == 2022 && Integer.parseInt(y[1].toString()) >= 8) || Integer.parseInt(y[0].toString()) > 2022) {//从22年8月开始维护的数据
                            obj[7] = object[1];
                        }
                    }
                }
//            不合格单数量
                for (Object[] object : resultsQCQTY) {
                    if (object[1] != null) {
                        if (object[1].equals(i[0].toString())) {
                            obj[5] = object[0];
                        }
                    }

                }
//            特采数量
                for (Object[] object : resultsQCQTYSpecial) {
                    if (object[0] != null) {
                        if (object[0].equals(i[0].toString())) {
                            obj[6] = object[1];
                        }
                    }

                }
                if (strMin != 0) {
                    obj[27] = strMin;
                    obj[30] = endMin;
                    strMin = 0;
                    endMin = 0;
                } else {
                    obj[27] = 0;
                    obj[30] = 0;
                }
//            EAM故障时间及故障次数
                for (Object[] objects : resultsEAM) {
                    if (objects[0].equals(i[0].toString()) && obj[26] != null) {
                        obj[27] = Integer.parseInt(objects[2].toString()) + Integer.parseInt(obj[26].toString());
                        obj[29] = objects[1];
                        obj[30] = Integer.parseInt(objects[3].toString()) + Integer.parseInt(obj[29].toString());
                        obj[31] = objects[1];
                        if (Integer.parseInt(objects[2].toString()) > 1440) {//当维修时间超出当天时间，将超出部分归类到第二天
                            strMin = Integer.parseInt(objects[2].toString()) - Integer.parseInt(objects[4].toString());
                            endMin = Integer.parseInt(objects[3].toString()) - Integer.parseInt(objects[4].toString());
                        } else {
                            strMin = 0;
                            endMin = 0;
                        }
                    }
                }
                for (Object[] objects : resultsLEN) {
                    if (objects[0].equals(i[0].toString())) {
                        for (int j = 2; j <= 18; j++) {
                            obj[7 + j] = objects[j];
                        }
                        if (dept.equals("半成品方型件")) {
                            obj[8] = Integer.parseInt(objects[3].toString()) + Integer.parseInt(objects[18].toString());
                        } else {
                            obj[8] = objects[2].toString();
                        }
                        obj[26] = objects[1];
                        if (!obj[26].equals(0)) {
                            obj[28] = 1;
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
                for (Object[] object : remarkList) {
                    if (object[0].equals(i[0].toString())) {
                        if (obj[40] == null) {
                            obj[40] = object[1].toString();
                        } else {
                            obj[40] += " ," + object[1].toString();
                        }

                    }
                }
//            if (type.equals("G")) {
                for (Object[] objects : rList) {
                    if (objects[0].equals(i[0].toString())) {
                        obj[2] = objects[1];
                        obj[3] = objects[2];
                        obj[4] = objects[3];
                    }
                }
//            }

                obj[0] = i[0].toString();
                if (obj[1] != null) {
                    list.add(obj);
                }
            }
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
     * 获取加工机所在的部门
     *
     * @return
     */
    public List getEPQID(String Dept) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append("SELECT EQPID FROM MEQP WHERE EQPTYPEID ='实体设备' ");
        if (!Dept.equals("Null")) {
            sbMES.append(" AND PRODUCTTYPE='").append(Dept).append("'");
        }
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

    public List getAssetZeroList(String strDate, String endDate, String type, String dept, String Company) throws ParseException {
        List EQPIDList = getEPQID(dept);
        String itemString = "";
        String deptno = "1P1";

        if (dept.equals("半成品圆型件")) {
            deptno = "1P5";
        } else if (dept.equals("半成品方型件")) {
            deptno = "1P1";
        } else if (dept.equals("生产涡旋加工")) {
            EQPIDList = getEPQID("Null");
            deptno = "1P9";
        }

        String deptText = "";
        if (!dept.equals("Null")) {
            deptText += " and   A.deptno  like '" + deptno + "%'   And A.Company='" + Company + "'";
        }
        for (Object object : EQPIDList) {
            if (!object.equals("M/C-09") && !object.toString().contains("NL") && !object.toString().contains("NM")) {
                itemString += "'" + object + "',";
            }
        }
        itemString = itemString.substring(0, itemString.length() - 1);
        StringBuilder sbZero = new StringBuilder();
        sbZero.append(" select A.formid,A.assetDesc,A.remark,A.assetSpec,A.deptname,A.paramvalue,A.itemno,A.count, DATE_FORMAT(A.hitchtime, '%Y-%m-%d %h:%m'),DATE_FORMAT(A.completetime, '%Y-%m-%d %h:%m'),DATEDIFF('" + endDate + "',A.completetime)   from  (");
        sbZero.append(" SELECT A.formid,A.assetDesc,A.remark,A.assetSpec,A.deptname,B.paramvalue,A.itemno,count(C.formid) count ,max(hitchtime) hitchtime,max(completetime) completetime FROM assetcard  A  left join  assetitem D ON A.itemno=D.itemno  LEFT JOIN  assetcategory F ON D.categoryid=F.id left join  assetparameterdta B ON A.formid=B.pid and   paramname='设备等级' ");
        sbZero.append(" left  JOIN equipmentrepair C on C.assetno=A.formid AND hitchtime>'" + strDate + "' AND hitchtime <='" + endDate + "'");
        sbZero.append(" WHERE     qty>0   and C.rstatus<=95");
        if (type.equals("加工机")) {
            sbZero.append("  and      A.remark in (" + itemString + ")");
        } else if (type.equals("非加工机")) {
            sbZero.append("  and     (  A.remark IS NULL)");
        }
        sbZero.append(deptText);
        sbZero.append("   group by  A.formid) A");
        Query query = getEntityManager().createNativeQuery(sbZero.toString());
        List<Object[]> resZeroList = query.getResultList();
        List<Object[]> list = new ArrayList<>();

        for (Object[] obj : resZeroList) {
            Object[] zeroObj = new Object[15];
            for (int i = 0; i <= 10; i++) {

                if (i == 10) {
                    if (obj[10] != null) {
                        if (Integer.parseInt(obj[10].toString()) <= 90) {
                            if (Integer.parseInt(obj[10].toString()) <= 0) {
                                zeroObj[10] = 0;
                            } else {
                                zeroObj[10] = obj[10];
                            }

                        } else if (Integer.parseInt(obj[10].toString()) <= 180) {
                            zeroObj[11] = obj[10];
                        } else if (Integer.parseInt(obj[10].toString()) <= 365) {
                            zeroObj[12] = obj[10];
                        } else if (Integer.parseInt(obj[10].toString()) > 365) {
                            zeroObj[13] = obj[10];
                        }
                    } else {
                        // 将字符串转换为LocalDate对象
                        LocalDate date1 = LocalDate.parse(strDate);
                        LocalDate date2 = LocalDate.parse(endDate);
                        // 计算两个日期之间的天数差异
                        long daysBetween = ChronoUnit.DAYS.between(date1, date2);

                        zeroObj[13] = daysBetween;
                    }

                } else {
                    zeroObj[i] = obj[i];
                }
            }

            list.add(zeroObj);

        }
        return list;
    }

}
