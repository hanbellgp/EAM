/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.entity.EquipmentSpareStockResponse;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentSpareStockBean extends SuperEJBForEAM<EquipmentSpareStock> {

    public EquipmentSpareStockBean() {
        super(EquipmentSpareStock.class);
    }

    //获取库存数量List
    public List<EquipmentSpareStock> getEquipmentSpareStockList(String sparenum, String sparedesc, String sparemodel, String company) {
        StringBuilder sb = new StringBuilder();
        sb.append(" Select T.sparenum,S.sparedesc,S.sparemodel, C.sname,M.mname,sum(T.qty) FROM  equipmentsparestock T  LEFT JOIN equipmentspare S on T.sparenum=S.sparenum AND S.company='" + company + "'");
        sb.append(" LEFT JOIN equipmentspareclass C ON S.scategory=C.scategory  AND C.company='" + company + "' LEFT JOIN  equipmentsparemid M ON S.scategory=M.scategory AND S.mcategory=M.mcategory Where 1=1  AND T.company='" + company + "'");
        if (!"".equals(sparenum) && sparenum != null) {
            sb.append(" AND S.sparedesc LIKE ").append("'%").append(sparenum).append("%'");
        }
        if (!"".equals(sparedesc) && sparedesc != null) {
            sb.append(" AND T.sparenum LIKE  ").append("'%").append(sparedesc).append("%'");
        }
        if (!"".equals(sparemodel) && sparemodel != null) {
            sb.append(" AND S.sparemodel LIKE  ").append("'%").append(sparemodel).append("%'");
        }
        sb.append(" GROUP BY sparenum");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

    //获取库存盘点数量List，按厂区及存放位置分类
    public List<EquipmentSpareStock> getEquipmentSpareStockCheckList(String sarea, String company) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT T.sparenum,S.sparedesc,S.sparemodel, C.sname,M.mname,sum(T.qty),T.sarea,T.slocation FROM  equipmentsparestock T");
        sb.append(" LEFT JOIN equipmentspare S ON T.sparenum=S.sparenum AND S.company = '" + company + "' LEFT JOIN equipmentspareclass C ON   S.scategory=C.scategory  AND C.company = '" + company + "' LEFT JOIN  equipmentsparemid M ON  S.scategory=M.scategory AND M.company = '" + company + "' AND S.mcategory=M.mcategory");
        sb.append(" Where  qty!=0");
        if (!"".equals(sarea) && sarea != null) {
            sb.append(" AND T.sarea='").append(sarea).append("'");
        }
        if (!"".equals(company) && company != null) {
            sb.append(" AND T.company='").append(company).append("'");
        }
        sb.append(" GROUP BY T.slocation ,sparenum ORDER BY sparenum");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

    public List<EquipmentSpareStock> findBySparenum(String sparenum, String company) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndCompany");
        query.setParameter("sparenum", sparenum);
        query.setParameter("company", company);
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }

    //获取库存数量List
    public List<EquipmentSpareStockResponse> getEquipmentSpareStockListByNativeQuery(String spareInfo, String company) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM equipmentsparestock T LEFT JOIN equipmentspare S ON T.sparenum = S.sparenum  AND S.company='" + company + "' WHERE 1 = 1 AND qty > 0  AND T.company='").append(company).append("' ");
        if (!"".equals(spareInfo) && spareInfo != null) {
            sb.append(MessageFormat.format(" AND (S.sparedesc LIKE ''%{0}%'' OR T.sparenum LIKE ''%{0}%'' OR S.sparemodel LIKE ''%{0}%'') ", spareInfo));
        }
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentSpareStock.class).setMaxResults(50);
        List<EquipmentSpareStock> results = query.getResultList();
           for (EquipmentSpareStock rs : results) {
            List<EquipmentSpare> eS = this.findBySpare(rs.getSparenum().getSparenum(), rs.getCompany());
            if (eS.size() > 0) {
                rs.setSparenum(eS.get(0));
            }
        }
        //List按照sparenum分组
        Map<EquipmentSpare, List<EquipmentSpareStock>> groupBySparenumMap = results.stream().collect(Collectors.groupingBy(EquipmentSpareStock::getSparenum));
        List<EquipmentSpareStockResponse> resList = new ArrayList<EquipmentSpareStockResponse>();
        groupBySparenumMap.forEach((key, value) -> {
            BigDecimal qtySum = BigDecimal.ZERO;
            for (int i = 0; i < value.size(); i++) {
                qtySum = qtySum.add(value.get(i).getQty());
            }
            EquipmentSpareStockResponse resTemp = new EquipmentSpareStockResponse(key, value, qtySum);
            resList.add(resTemp);
        });

        return resList;
    }

    public EquipmentSpareStock findBySparenumAndRemark(String sparenum, String remark, String slocation) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndRemark");
        query.setParameter("sparenum", sparenum);
        query.setParameter("remark", remark);
        query.setParameter("slocation", slocation);

        try {
            Object o = query.getSingleResult();
            return (EquipmentSpareStock) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public EquipmentSpareStock findBySparenumAndRemark(String sparenum, String remark) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndRemark");
        query.setParameter("sparenum", sparenum);
        query.setParameter("remark", remark);
        try {
            Object o = query.getSingleResult();
            return (EquipmentSpareStock) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentSpareStock> findBySparenumAndSarea(String sparenum, String sarea) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndSarea");
        query.setParameter("sparenum", sparenum);
        query.setParameter("sarea", sarea);
        query.setParameter("qty", 0);
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentSpareStock> findBySparenumAndLocation(String sparenum, String slocation) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndLocation");
        query.setParameter("sparenum", sparenum);
        query.setParameter("slocation", slocation);
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Object[]> getUseSpare(String formid, String strDate, String endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.assetno,A.remark,S.sparedesc,date_format(D.credate,'%y-%m-%d')  FROM equipmentsparerecodedta D LEFT JOIN equipmentspare S ON D.sparenum=S.sparenum LEFT JOIN equipmentsparerecode C");
        sb.append(" ON D.pid=C.formid RIGHT JOIN   equipmentrepair R ON C.relano=R.formid  LEFT JOIN assetcard A");
        sb.append(" ON A.formid=R.assetno WHERE C.relano IS NOT NULL AND C.status='V'AND R.rstatus='95' AND A.remark='").append(formid).append("'");
        if (strDate != null && !strDate.equals("")) {
            sb.append("  AND D.credate>'").append(strDate).append("'");
        }
        if (endDate != null && !endDate.equals("")) {
            sb.append("  AND D.credate<='").append(endDate).append("'");
        }
        sb.append(" ORDER BY  D.credate ASC");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();

        return results;
    }

    public List<Object[]> getEquipmentRecord(String formid, String strDate, String endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT R.hitchtime,R.hitchreason,R.measure,R.completetime,R.serviceusername FROM equipmentsparerecodedta D LEFT JOIN equipmentsparerecode C");
        sb.append(" ON D.pid=C.formid RIGHT JOIN   equipmentrepair R ON C.relano=R.formid  LEFT JOIN assetcard A ");
        sb.append(" ON A.formid=R.assetno WHERE C.relano IS NOT NULL AND C.status='V'AND R.rstatus='95' AND A.remark='").append(formid).append("'");
        if (strDate != null && !strDate.equals("")) {
            sb.append("  AND R.credate>'").append(strDate).append("'");
        }
        if (endDate != null && !endDate.equals("")) {
            sb.append("  AND R.credate<='").append(endDate).append("'");
        }
        sb.append(" ORDER BY  completetime ASC");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();

        return results;
    }

    @Override
    public List<EquipmentSpareStock> findByFilters(Map<String, Object> filters, int first, int pageSize, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
        }

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
        List<EquipmentSpareStock> results = query.getResultList();

        for (EquipmentSpareStock rs : results) {
            List<EquipmentSpare> eS = this.findBySpare(rs.getSparenum().getSparenum(), rs.getCompany());
            if (eS.size() > 0) {
                rs.setSparenum(eS.get(0));
            }
        }

        return results;
    }

    public List<EquipmentSpare> findBySpare(String sparenum, String company) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpare.findBySparenum");
        query.setParameter("sparenum", sparenum + "%");
        query.setParameter("company", company);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

}
