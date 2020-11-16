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
import java.util.Arrays;
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
    public List<EquipmentRepair> getRepairEmergencyStatisticsList(String staDate, String endDate, String assetno, String deptname) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,sum(if(R.hitchtype = '03', 1, 0)) AS emergency,sum(if(R.hitchtype = '02', 1, 0)) AS urgent,sum(if(R.hitchtype = '01', 1, 0)) AS general");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid");
        sb.append(" WHERE R.rstatus='95' AND  R.hitchtype IS NOT NULL");
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
        sb.append(" GROUP BY R.assetno,repairdeptname");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    
    //获取维修费用统计表的List
    public List<EquipmentRepair> getRepairCostStatisticsList(String staDate, String endDate, String deptname, String abrasehitch) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.formid,R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname, R.hitchtime,R.abrasehitch,R.repairmethod,R.repaircost,R.laborcosts,R.sparecost,R.repaircost + R.laborcosts + R.sparecost AS tot,R.serviceusername");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE  R.rstatus = '95'");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.abrasehitch = ").append("'").append(abrasehitch).append("'");
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
    public List<EquipmentRepair> getRepairCostSummaryList(String staDate, String endDate, String deptname, String abrasehitch) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,sum(R.repaircost) A ,sum(R.laborcosts) B,sum(R.sparecost) C,sum(R.repaircost+R.laborcosts+R.sparecost) D,COUNT(R.assetno) C");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE   R.rstatus='95'");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.abrasehitch = ").append("'").append(abrasehitch).append("'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,repairdeptname");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取故障责任统计表的List
    public List<EquipmentRepair> getFaultDutyStatisticalList(String staDate, String endDate, String deptname, String abrasehitch) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,sum(if(R.abrasehitch = '01', 1, 0)) A ,sum(if(R.abrasehitch = '02', 1, 0)) B,sum(if(R.abrasehitch = '03', 1, 0)) C,sum(if(R.abrasehitch = '04', 1, 0)) D,sum(if(R.abrasehitch = '05', 1, 0)) E");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE R.rstatus='95' AND  R.abrasehitch LIKE '%0%'");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno = ").append("'").append(abrasehitch).append("'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,R.repairusername");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

    //获取故障类型统计表的List
    public List<EquipmentRepair> getFaultTypeStatisticalList(String staDate, String endDate, String deptname, String abrasehitch) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,sum(if(R.hitchsort1 = '01', 1, 0)) A ,sum(if(R.hitchsort1 = '02', 1, 0)) B,sum(if(R.hitchsort1 = '03', 1, 0)) C,sum(if(R.hitchsort1 = '04', 1, 0)) D");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno = A.formid WHERE   R.abrasehitch LIKE '%0%' AND R.rstatus='95'");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno = ").append("'").append(abrasehitch).append("'");
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

    //获取现场维修统计表的List
    public List<EquipmentRepair> getRepairSceneStatisticslList(String staDate, String endDate, String deptname, String abrasehitch) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,R.hitchtime,R.hitchdesc,R.repairmethod,R.repairusername");
        sb.append(" FROM equipmentrepair R LEFT JOIN assetcard A ON R.assetno=A.formid WHERE R.rstatus='95' AND R.repairmethodtype='2'");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno = ").append("'").append(abrasehitch).append("'");
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

    public List<Object[]> getMTBFAndMTTR(String staDate, String endDate, String deptname, String abrasehitch, String sql) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT EQPID,sum(AVAILABLEMINS) AS AVAILABLEMINS FROM EQP_AVAILABLETIME_SCHEDULE");
        sbMES.append(" WHERE PLANDATE >= '").append(staDate).append("' AND PLANDATE < '").append(endDate).append("' AND (EQPID LIKE 'CG%' OR EQPID LIKE '%NSM%' OR EQPID LIKE '%KAPP%' OR EQPID LIKE '%NL%' OR EQPID LIKE '%FMS%') GROUP BY EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //生成SQL
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT A.remark, R.assetno,if(R.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.repairdeptname,if(sum(R.stopworktime) IS NULL ,0,sum(R.stopworktime)),count(R.itemno),SUM(TIMESTAMPDIFF(MINUTE,R.servicearrivetime,R.completetime)) time,if(SUM(excepttime) IS NULL ,0,SUM(excepttime))");
        sb.append(" FROM equipmentrepair R LEFT JOIN  assetcard A ON  R.assetno=A.formid WHERE R.rstatus='95'");
        staDate = staDate.replace("/", "-");
        endDate = endDate.replace("/", "-");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno = ").append("'").append(abrasehitch).append("'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        if (!"".equals(sql)) {
            sb.append(" AND R.itemno in ( ").append(sql).append(")");
        }
        sb.append(" GROUP BY R.assetno,R.repairdeptname");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sb.toString());

        List<Object[]> resultsEAM = query1.getResultList();
        List list = new ArrayList();
        for (Object[] eam : resultsEAM) {
            if (eam[0] == null) {
                continue;
            }
            for (Object[] mes : resultsMES) {
                if (eam[0].equals(mes[0])) {
                    Object[] obj = new Object[9];
                    obj[0] = eam[1];
                    obj[1] = eam[2];
                    obj[2] = eam[3];
                    obj[3] = mes[1];
                    obj[4] = eam[4];
                    obj[5] = eam[5];
                    BigDecimal eam4 = new BigDecimal(Double.toString(Double.parseDouble(eam[4].toString())));
                    BigDecimal eam5 = new BigDecimal(Double.toString(Double.parseDouble(eam[5].toString())));
                    BigDecimal eam6 = new BigDecimal(Double.toString(Double.parseDouble(eam[6].toString())));
                    BigDecimal eam7 = new BigDecimal(Double.toString(Double.parseDouble(eam[7].toString())));
                    BigDecimal mes1 = new BigDecimal(Double.toString(Double.parseDouble(mes[1].toString())));
                    Double sum = 60.00;
                    BigDecimal min = new BigDecimal(sum.toString());
                    obj[6] = eam6.subtract(eam7);
                    obj[7] = (mes1.subtract(eam4)).divide(eam5, 2, BigDecimal.ROUND_DOWN).divide(min, 2, BigDecimal.ROUND_DOWN);
                    obj[8] = ((eam6.subtract(eam7))).divide(eam5, 2, BigDecimal.ROUND_DOWN).divide(min, 2, BigDecimal.ROUND_DOWN);
                    list.add(obj);
                }
            }
        }
        return list;
    }

    //    获取设备故障率统计表数据
    public List<Object[]> getRepairFaultRateStatistics(String staDate, String endDate, String deptname, String abrasehitch) throws ParseException {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append(" SELECT EQPID,sum(AVAILABLEMINS) AS AVAILABLEMINS FROM EQP_AVAILABLETIME_SCHEDULE");
        sbMES.append(" WHERE PLANDATE >= '").append(staDate).append("' AND PLANDATE < '").append(endDate).append("' AND (EQPID LIKE 'CG%' OR EQPID LIKE '%NSM%' OR EQPID LIKE '%KAPP%' OR EQPID LIKE '%NL%' OR EQPID LIKE '%FMS%') GROUP BY EQPID");
        SuperEJBForMES superEJBForMES = lookupSuperEJBForMES();
        //生成SQL
        Query query = superEJBForMES.getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT A.remark,R.assetno,if(R.assetno IS NULL, '其他', A.assetDesc) assetDesc,R.repairdeptname,if(sum(R.stopworktime) IS NULL, 0, sum(R.stopworktime)) stopworktime");
        sb.append(" FROM equipmentrepair R LEFT JOIN  assetcard A ON  R.assetno=A.formid WHERE R.rstatus='95'");
        staDate = staDate.replace("/", "-");
        endDate = endDate.replace("/", "-");
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        if (abrasehitch != null && !"".equals(abrasehitch)) {
            sb.append(" AND R.assetno = ").append("'").append(abrasehitch).append("'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND R.repairdeptname Like ").append("'%").append(deptname).append("%'");
        }
        sb.append(" GROUP BY R.assetno,R.repairdeptname");
        //生成SQL
        Query query1 = getEntityManager().createNativeQuery(sb.toString());

        List<Object[]> resultsEAM = query1.getResultList();
        List list = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(staDate);
        Date date2 = format.parse(endDate);
        int day = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        for (Object[] eam : resultsEAM) {
            Object[] obj = new Object[6];
            obj[0] = eam[1];
            obj[1] = eam[2];
            obj[2] = eam[3];
            obj[4] = eam[4];
            BigDecimal eam4 = new BigDecimal(Double.toString(Double.parseDouble(eam[4].toString())));
            if (eam[0] == null) {
                BigDecimal AVAILABLEMINS = new BigDecimal(Double.parseDouble(String.valueOf(8 * day * 60)));
                obj[3] = AVAILABLEMINS;
                obj[5] = eam4.divide(AVAILABLEMINS, 4, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));;
                list.add(obj);
                continue;
            }
            resultsMES.forEach(mes -> {
                if (eam[0].equals(mes[0])) {
                    BigDecimal mes1 = new BigDecimal(Double.toString(Double.parseDouble(mes[1].toString())));
                    if (mes[1].equals(0)) {
                        obj[5] = 0;
                    } else {
                        obj[5] = eam4.divide(mes1, 4, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
                    }

                } else {
                    BigDecimal AVAILABLEMINS = new BigDecimal(Double.parseDouble(String.valueOf(8 * day * 60)));
                    obj[3] = AVAILABLEMINS;
                    obj[5] = eam4.divide(AVAILABLEMINS, 4, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));;
                }
            });
            list.add(obj);
        }
        return list;
    }
}
