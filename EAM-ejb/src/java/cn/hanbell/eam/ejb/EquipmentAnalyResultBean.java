/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.comm.SuperEJBForMES;
import cn.hanbell.eam.entity.EquipmentAnalyResult;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class EquipmentAnalyResultBean extends SuperEJBForEAM<EquipmentAnalyResult> {

    @EJB
    private SuperEJBForMES mesEJB;

    public EquipmentAnalyResultBean() {
        super(EquipmentAnalyResult.class);
    }

    public List<EquipmentAnalyResult> getEquipmentAnalyResultListByNativeQuery(Map<String, Object> filters, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        StringBuilder exFilterStr = new StringBuilder();
        sb.append("SELECT A.id,A.formid,A.company,A.assetno,A.assetdesc,A.spareno,A.deptno,A.deptname,A.standardlevel,A.startdate,A.enddate,A.analysisresult,B.remark,A.status,A.creator,A.credate,A.optuser,A.optdate,A.cfmuser,A.cfmdate FROM ");
        sb.append(this.className);
        sb.append(" A LEFT JOIN assetcard B ON A.assetno=B.formid  WHERE (1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        boolean dateFilterFlag = false;
        String dateFilterStr = " AND A.formdate = A.formdate = CURDATE()";

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("deptno".equals(key)) {
                String deptnoTemp = "";
                if (value.toString().contains("000")) {
                    deptnoTemp = value.toString().substring(0, 2);
                } else {
                    deptnoTemp = value.toString().substring(0, 3);
                }
                sb.append("  AND A.deptno LIKE '").append(deptnoTemp).append("%'");
            } else if ("MaintainType".equals(key)) {
                if ("BQ".equals(value.toString())) {
                    dateFilterStr = " AND A.formdate = CURDATE()";
                    sb.append(" AND A.standardlevel = '一级'");
                } else {
                    dateFilterStr = " AND date_format(A.formdate,'%Y-%m') = date_format(CURDATE(),'%Y-%m')";
                    sb.append(" AND A.standardlevel <> '一级'");
                }
            } else if ("AnalysisUser".equals(key)) {
                sb.append(MessageFormat.format(" AND A.formid IN (SELECT DISTINCT pid FROM equipmentanalyresultdta WHERE analysisuser = ''{0}'') ", value.toString()));
            } else if ("ExtraFilter".equals(key)) {
                sb.append(MessageFormat.format(" AND (A.formid LIKE ''%{0}%'' OR A.assetno LIKE ''%{0}%'' OR A.assetdesc LIKE ''%{0}%'' OR A.spareno LIKE ''%{0}%'')", value.toString()));
            } else if ("formdateBegin".equals(key)) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                String formdateBeginStr = fmt.format(new Date(value.toString()));
                sb.append(MessageFormat.format(" AND A.formdate >= ''{0}''", formdateBeginStr));
                dateFilterFlag = true;
            } else if ("formdateEnd".equals(key)) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                String formdateEndStr = fmt.format(new Date(value.toString()));
                sb.append(MessageFormat.format(" AND A.formdate <= ''{0}''", formdateEndStr));
                dateFilterFlag = true;
            } else {
                strMap.put(key, value);
            }
        }

        if (dateFilterFlag) {
            sb.append(")").append(exFilterStr);
        } else {
            sb.append(dateFilterStr).append(")").append(exFilterStr);
        }

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
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentAnalyResult.class).setMaxResults(50);
        List<EquipmentAnalyResult> results = query.getResultList();
        List<String> fMESList = new ArrayList<>();
        List<String> yMESList = new ArrayList<>();
        String stopSql = "";
        stopSql = " SELECT EQPID FROM  PLAN_SEMI_SQUARE WHERE  PLANDATE = convert(char,getdate(),111) AND PRODUCTID='计划停机' AND WORKHOUR LIKE '%1440%'";

        query = mesEJB.getEntityManager().createNativeQuery(stopSql.toString());
        fMESList = query.getResultList();
        stopSql = "   SELECT EQPID FROM  PLAN_SEMI_CIRCLE WHERE  PLANDATE = convert(char,getdate(),111) AND PRODUCTID='计划停机' AND WORKHOUR LIKE '%1440%'";
        query = mesEJB.getEntityManager().createNativeQuery(stopSql.toString());
        yMESList = query.getResultList();
        fMESList.addAll(yMESList);
        for (String epqId : fMESList) {
            for (EquipmentAnalyResult rString : results) {
                if (epqId.equals(rString.getRemark())) {
                    rString.setCreator("停机");
                }
            }
        }
        return results;
    }

    public List<String> getEPQIDDowntime() {
        List<String> fMESList = new ArrayList<>();
        List<String> yMESList = new ArrayList<>();
        String stopSql = "";
        stopSql = " SELECT EQPID FROM  PLAN_SEMI_SQUARE WHERE  PLANDATE = convert(char,getdate(),111)  AND PRODUCTID='计划停机' AND WORKHOUR LIKE '%1440%'";

        Query query = mesEJB.getEntityManager().createNativeQuery(stopSql.toString());
        fMESList = query.getResultList();
        stopSql = "   SELECT EQPID FROM  PLAN_SEMI_CIRCLE WHERE  PLANDATE = convert(char,getdate(),111) AND PRODUCTID='计划停机' AND WORKHOUR LIKE '%1440%'";
        query = mesEJB.getEntityManager().createNativeQuery(stopSql.toString());
        yMESList = query.getResultList();
        fMESList.addAll(yMESList);
        return fMESList;
    }

    private void setNativeQueryFilter(StringBuilder queryStrBuilder, Map<String, Object> filters) {
        filters.forEach((key, value) -> {
            queryStrBuilder.append(MessageFormat.format(" AND {0} LIKE ''%{1}%''", key, value.toString()));
        });
    }

    @Override
    public List<EquipmentAnalyResult> findByFilters(Map<String, Object> filters, int first, int pageSize, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            strMap.put(key, value);
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
        List<EquipmentAnalyResult> results = query.getResultList();
        String assetno = "";
        for (EquipmentAnalyResult result : results) {
            assetno += "'" + result.getAssetno() + "',";
        }
        if (assetno != "") {
            assetno = assetno.substring(0, assetno.length() - 1);
            String stopSql = "";
            stopSql = " SELECT formid, remark FROM assetcard WHERE formid IN(" + assetno + ")";
            query = this.getEntityManager().createNativeQuery(stopSql.toString());
            List<Object[]> mesid = query.getResultList();
            for (Object[] objects : mesid) {
                for (EquipmentAnalyResult result : results) {
                    if (objects[0].equals(result.getAssetno())) {
                        if (objects[1] != null) {
                            result.setMesid(objects[1].toString());
                        }
                    }
                }
            }
        }
        List<String> sEPQID = getEPQIDDowntime();
        for (String epqId : sEPQID) {
            for (EquipmentAnalyResult rString : results) {
                if (epqId.equals(rString.getMesid())) {
                    rString.setDowntime("停机");
                }
            }
        }

        return results;
    }

    @Override
    public List<EquipmentAnalyResult> findByFilters(Map<String, Object> filters, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            strMap.put(key, value);
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
        List<EquipmentAnalyResult> results = query.getResultList();
        String assetno = "";
        for (EquipmentAnalyResult result : results) {
            assetno += "'" + result.getAssetno() + "',";
        }
        if (assetno != "") {
            assetno = assetno.substring(0, assetno.length() - 1);
            String stopSql = "";
            stopSql = " SELECT formid, remark FROM assetcard WHERE formid IN(" + assetno + ")";
            query = this.getEntityManager().createNativeQuery(stopSql.toString());
            List<Object[]> mesid = query.getResultList();
            for (Object[] objects : mesid) {
                for (EquipmentAnalyResult result : results) {
                    if (objects[0].equals(result.getAssetno())) {
                        if (objects[1] != null) {
                            result.setMesid(objects[1].toString());
                        }
                    }
                }
            }
        }
        List<String> sEPQID = getEPQIDDowntime();
        for (String epqId : sEPQID) {
            for (EquipmentAnalyResult rString : results) {
                if (epqId.equals(rString.getMesid())) {
                    rString.setDowntime("停机");
                }
            }
        }

        return results;
    }

    /**
     * 获取本年份基准次数
     *
     * @param nexttime 查询的基准年份
     * @param standardlevel 查询的基准等级
     * @param deptname 查询的资产所属部门
     * @param assetno 资产编号
     * @return
     */
    public List getEquipmentStandardList(String nexttime, String standardlevel, String deptname, String assetno) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT E.assetno,E.assetdesc,A.deptname,E.standardlevel,count(E.assetno),month(nexttime) FROM equipmentstandard E");
        sb.append(" LEFT JOIN assetcard  A ON E.assetno=A.formid  WHERE");
        if (nexttime != null && !"".equals(nexttime)) {
            sb.append(" nexttime LIKE '%").append(nexttime).append("%'");
        }
        if (standardlevel != null && !"".equals(standardlevel)) {
            sb.append(" AND standardlevel LIKE '%").append(standardlevel).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append(" AND A.deptname LIKE '%").append(deptname).append("%'");
        }
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND E.assetno LIKE '%").append(assetno).append("%'");
        }
        sb.append(" GROUP BY  E.assetno,month(nexttime) ORDER BY E.assetno");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();//未生成的计划保全项目
        StringBuilder sbDta = new StringBuilder();
        sbDta.append(" SELECT E.assetno,E.assetdesc,E.deptname,E.standardlevel,COUNT(EDTA.pid),month(E.credate)");
        sbDta.append(" FROM equipmentanalyresult E LEFT JOIN equipmentanalyresultdta EDTA ON E.formid=EDTA.pid WHERE");
        if (nexttime != null && !"".equals(nexttime)) {
            sbDta.append(" E.credate LIKE '%").append(nexttime).append("%'");
        }
        if (standardlevel != null && !"".equals(standardlevel)) {
            sbDta.append(" AND E.standardlevel LIKE '%").append(standardlevel).append("%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sbDta.append(" AND E.deptname LIKE '%").append(deptname).append("%'");
        }
        if (assetno != null && !"".equals(assetno)) {
            sbDta.append(" AND E.assetno LIKE '%").append(assetno).append("%'");
        }
        query = getEntityManager().createNativeQuery(sbDta.toString());
        List<Object[]> results1 = query.getResultList();//已生成的计划保全单
        results.addAll(results1);//将已生成的计划保全单数据合并到一起处理
        //数据处理,将相同编号的次数整合在一条数据中
        Map<String, List<Object[]>> map = new HashMap<>();
        results.forEach(result -> {
            if (map.containsKey(result[0].toString())) {//判断是否已存在对应键号
                map.get(result[0].toString()).add(result);//直接在对应的map中添加数据
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(result);
                map.put(result[0].toString(), tmpList);//新增一个键号
            }
        });
        List moList = new ArrayList();
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> list = entry.getValue();//取出对应的值
            Object[] obj1 = new Object[16];
            for (Object[] obj : list) {
                obj1[0] = obj[0];
                obj1[1] = obj[1];
                obj1[2] = obj[2];
                obj1[3] = obj[3];
                obj1[4] = obj[4];
                switch (Integer.parseInt(obj[5].toString())) {
                    case 1:
                        obj1[4] = obj[4];
                        break;
                    case 2:
                        obj1[5] = obj[4];
                        break;
                    case 3:
                        obj1[6] = obj[4];
                        break;
                    case 4:
                        obj1[7] = obj[4];
                        break;
                    case 5:
                        obj1[8] = obj[4];
                        break;
                    case 6:
                        obj1[9] = obj[4];
                        break;
                    case 7:
                        obj1[10] = obj[4];
                        break;
                    case 8:
                        obj1[11] = obj[4];
                        break;
                    case 9:
                        obj1[12] = obj[4];
                        break;
                    case 10:
                        obj1[13] = obj[4];
                        break;
                    case 11:
                        obj1[14] = obj[4];
                        break;
                    case 12:
                        obj1[15] = obj[4];
                        break;
                    default:
                        break;
                }
            }
            moList.add(obj1);
        }
        return moList;
    }

    /**
     * 自主保全实施表
     *
     * @param formdate
     * @return
     */
    public List getImplementation(String formdate) {
        String resultSql = "SELECT A.deptname,A.assetno,a.assetdesc, B.totCount,b.sCount,DAY FROM (SELECT formid,assetno,assetdesc,deptname,DAY(formdate) DAY  FROM equipmentanalyresult WHERE formdate LIKE '%" + formdate + "%' AND company='C'AND standardlevel='一级' ) A LEFT JOIN (SELECT pid,COUNT(PID) totCount,CASE pid WHEN edate IS     NULL THEN count(pid) ELSE 0 END sCount FROM equipmentanalyresultdta GROUP BY pid) B ON A.formid=B.pid ORDER BY A.deptname";
        Query query = getEntityManager().createNativeQuery(resultSql);
        List<Object[]> results = query.getResultList();//已生成的计划保全单
        String assetCardSql = "SELECT A.formid, A.remark,deptname FROM assetcard A LEFT JOIN  assetitem I ON A.itemno=I.itemno WHERE  A.remark IS NOT NULL  and I.categoryid=3 AND A. company='C'  AND qty!=0 ORDER BY remark";
        query = getEntityManager().createNativeQuery(assetCardSql);
        List<Object[]> cList = query.getResultList();//已生成的计划保全单
        Map<String, List<Object[]>> map = new HashMap<>();
        results.forEach(result -> {
            if (map.containsKey(result[1].toString())) {//判断是否已存在对应键号
                map.get(result[1].toString()).add(result);//直接在对应的map中添加数据
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(result);
                map.put(result[1].toString(), tmpList);//新增一个键号
            }
        });
        List moList1 = new ArrayList();
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> list = entry.getValue();//取出对应的值
            Object[] obj1 = new Object[69];
            for (Object[] obj : list) {
                obj1[0] = obj[0];
                obj1[1] = obj[1];
                obj1[2] = obj[2];
                for (Object[] c : cList) {
                    if (c[0].equals(obj[1])) {
                         obj1[3] = c[1];
                    }
                }
                for (int i = 1; i <= 31; i++) {
                    if (Integer.parseInt(obj[5].toString()) == i) {
                        obj1[i * 2 + 2] = obj[3];
                        obj1[i * 2 + 3] = obj[4];
                    }
                }
            }
            moList1.add(obj1);
        }
        return moList1;
    }

    /**
     * 计划保全实施表
     *
     * @param formdate
     * @return
     */
    public List getImplementationYear(String formdate) {
        String resultSql = "SELECT A.deptname,A.assetno,a.assetdesc, B.totCount,b.sCount,DAY FROM (SELECT formid,assetno,assetdesc,deptname,MONTH(formdate) DAY  FROM equipmentanalyresult WHERE formdate LIKE '%" + formdate + "%' AND company='C' and standardlevel!='一级' ) A LEFT JOIN (SELECT pid,COUNT(PID) totCount,CASE pid WHEN edate IS     NULL THEN count(pid) ELSE 0 END sCount FROM equipmentanalyresultdta GROUP BY pid) B ON A.formid=B.pid ORDER BY A.deptname";
        Query query = getEntityManager().createNativeQuery(resultSql);
        List<Object[]> results = query.getResultList();//已生成的计划保全单
          String assetCardSql = "SELECT A.formid, A.remark,deptname FROM assetcard A LEFT JOIN  assetitem I ON A.itemno=I.itemno WHERE  A.remark IS NOT NULL  and I.categoryid=3 AND A. company='C'  AND qty!=0 ORDER BY remark";
        query = getEntityManager().createNativeQuery(assetCardSql);
        List<Object[]> cList = query.getResultList();//已生成的计划保全单
        Map<String, List<Object[]>> map = new HashMap<>();
        results.forEach(result -> {
            if (map.containsKey(result[1].toString())) {//判断是否已存在对应键号
                map.get(result[1].toString()).add(result);//直接在对应的map中添加数据
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(result);
                map.put(result[1].toString(), tmpList);//新增一个键号
            }
        });
        List moList1 = new ArrayList();
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> list = entry.getValue();//取出对应的值
            Object[] obj1 = new Object[29];
            for (Object[] obj : list) {
                obj1[0] = obj[0];
                obj1[1] = obj[1];
                obj1[2] = obj[2];
                   for (Object[] c : cList) {
                    if (c[0].equals(obj[1])) {
                         obj1[3] = c[1];
                    }
                }
                for (int i = 1; i <= 12; i++) {
                    if (Integer.parseInt(obj[5].toString()) == i) {
                        obj1[i * 2 + 2] = obj[3];
                        obj1[i * 2 + 3] = obj[4];
                    }
                }
            }
            moList1.add(obj1);
        }
        return moList1;
    }

}
