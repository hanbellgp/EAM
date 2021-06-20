/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
public class EquipmentSpareRecodeBean extends SuperEJBForEAM<EquipmentSpareRecode> {

    public EquipmentSpareRecodeBean() {
        super(EquipmentSpareRecode.class);
    }

    public List<EquipmentSpareRecode> findByScategory(String srcformid) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareMid.findByScategory");
        query.setParameter("scategory", srcformid);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentSpareRecode> findByRelano(String relano) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareRecode.findByRelano");
        query.setParameter("relano", relano);
        query.setParameter("status", "V");
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentSpareRecode> getEquipmentRepairListByNativeQuery(Map<String, Object> filters, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        StringBuilder exFilterStr = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(this.className);
        sb.append(" equipmentsparerecode WHERE (1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("BackLog".equals(value)) {
                sb.append("  AND status = 'N'");
            } else if ("deptno".equals(key)) {
                String deptnoTemp = "";
                if (value.toString().contains("000")) {
                    deptnoTemp = value.toString().substring(0, 2);
                } else {
                    deptnoTemp = value.toString().substring(0, 3);
                }
                sb.append("  AND deptno LIKE '").append(deptnoTemp).append("%'");
            } else if ("ExtraFilter".equals(key)) {
                sb.append(MessageFormat.format(" AND (formid LIKE ''%{0}%'' OR remark LIKE ''%{0}%'' OR creator LIKE ''%{0}%'' OR optuser LIKE ''%{0}%'')", value.toString()));
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
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentSpareRecode.class).setMaxResults(50);

        List<EquipmentSpareRecode> results = query.getResultList();
        return results;
    }

    private void setNativeQueryFilter(StringBuilder queryStrBuilder, Map<String, Object> filters) {
        filters.forEach((key, value) -> {
            queryStrBuilder.append(MessageFormat.format(" AND {0} LIKE ''%{1}%''", key, value.toString()));
        });
    }

    public String getAcceptTypeName(String acceotType) {
        switch (acceotType) {
            case "10":
                return "手工入库";
            case "20":
                return "手工出库";
            case "25":
                return "维修领料";
            case "30":
                return "手工退库";
            case "35":
                return "维修退料";
            default:
                return "";
        }
    }
}
