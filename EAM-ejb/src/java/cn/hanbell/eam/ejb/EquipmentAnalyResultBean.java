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
import java.util.Date;
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
        sb.append("SELECT * FROM ");
        sb.append(this.className);
        sb.append(" equipmentanalyresult WHERE (1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();

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
                sb.append("  AND deptno LIKE '").append(deptnoTemp).append("%'");
            } else if ("MaintainType".equals(key)) {
                sb.append(MessageFormat.format(" AND formid LIKE ''{0}%''", value.toString()));
            } else if ("ExtraFilter".equals(key)) {
                sb.append(MessageFormat.format(" AND (formid LIKE ''%{0}%'' OR assetno LIKE ''%{0}%'' OR assetdesc LIKE ''%{0}%'' OR spareno LIKE ''%{0}%'')", value.toString()));
            } else if ("formdateBegin".equals(key)) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                String formdateBeginStr = fmt.format(new Date(value.toString()));
                sb.append(MessageFormat.format(" AND formdate >= ''{0}''", formdateBeginStr));
            } else if ("formdateEnd".equals(key)) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                String formdateEndStr = fmt.format(new Date(value.toString()));
                sb.append(MessageFormat.format(" AND formdate <= ''{0}''", formdateEndStr));
            } else {
                strMap.put(key, value);
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
        sb.append("SELECT E.assetno,E.assetdesc,A.deptname,count(E.assetno),month(nexttime) FROM equipmentstandard E");
        sb.append(" LEFT JOIN assetcard  A ON E.assetno=A.formid  WHERE");
        if (nexttime != null && !"".equals(nexttime)) {
            sb.append(" nexttime LIKE '%'").append(nexttime).append("'%'");
        }
        if (standardlevel != null && !"".equals(standardlevel)) {
            sb.append(" AND standardlevel LIKE '%'").append(standardlevel).append("'%'");
        }
        if (deptname != null && !"".equals(deptname)) {
            sb.append("  AND A.deptname LIKE '%'").append(deptname).append("'%'");
        }
        if (assetno != null && !"".equals(assetno)) {
            sb.append("  AND E.assetno LIKE '%'").append(assetno).append("'%'");
        }
        sb.append(" GROUP BY month(nexttime)");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }

}
