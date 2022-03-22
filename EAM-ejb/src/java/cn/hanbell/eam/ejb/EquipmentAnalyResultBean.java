/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentAnalyResult;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class EquipmentAnalyResultBean extends SuperEJBForEAM<EquipmentAnalyResult> {

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
            }
             else if ("MaintainType".equals(key)) {
                if("BQ".equals(value.toString())){
                    dateFilterStr = " AND A.formdate = CURDATE()";
                    sb.append(" AND A.standardlevel = '一级'");
                }
                else
                {
                    dateFilterStr = " AND date_format(A.formdate,'%Y-%m') = date_format(CURDATE(),'%Y-%m')";
                    sb.append(" AND A.standardlevel <> '一级'");
                }
            }else if("AnalysisUser".equals(key)){
                sb.append(MessageFormat.format(" AND A.formid IN (SELECT DISTINCT pid FROM equipmentanalyresultdta WHERE analysisuser = ''{0}'') ", value.toString()));
            }else if ("ExtraFilter".equals(key)) {
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
        
        if(dateFilterFlag){
            sb.append(")").append(exFilterStr);
        }
        else
            sb.append(dateFilterStr).append(")").append(exFilterStr);
        
        

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
        return results;
    }

    private void setNativeQueryFilter(StringBuilder queryStrBuilder, Map<String, Object> filters) {
        filters.forEach((key, value) -> {
            queryStrBuilder.append(MessageFormat.format(" AND {0} LIKE ''%{1}%''", key, value.toString()));
        });
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

    
}
