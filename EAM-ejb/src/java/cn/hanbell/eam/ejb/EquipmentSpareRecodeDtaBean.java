/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import java.text.MessageFormat;
import java.util.ArrayList;
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
public class EquipmentSpareRecodeDtaBean extends SuperEJBForEAM<EquipmentSpareRecodeDta> {

    public EquipmentSpareRecodeDtaBean() {
        super(EquipmentSpareRecodeDta.class);
    }

    public List<EquipmentSpareRecodeDta> getEquipmentSpareRecodeDtaList(String formid) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT CK.sparenum,CK.sparedesc,CK.sparemodel, CASE WHEN TK.cqty IS NULL  THEN CK.cqty ELSE CK.cqty-TK.cqty END , CK.uprice,convert( CASE WHEN TK.cqty IS NULL  THEN CK.uprice*CK.cqty ELSE (CK.uprice*CK.cqty) - (TK.uprice*TK.cqty) END,DECIMAL(10,2)),CK.slocation FROM (");
        sb.append(" SELECT A.pid,A.sparenum,C.sparedesc,C.sparemodel,sum(A.cqty) cqty,sum(A.uprice) uprice,A.slocation FROM equipmentsparerecodedta A LEFT JOIN equipmentspare C ON A.sparenum=C.sparenum");
        sb.append(" LEFT JOIN equipmentsparerecode B ON A .pid=B.formid  WHERE  (B.relano='").append(formid).append("' OR B.remark='").append(formid).append("') AND B.status='V'");
        sb.append(" AND A.pid LIKE 'CK%' GROUP BY sparenum,A.slocation) CK LEFT JOIN ( SELECT A.pid,A.sparenum,sum(A.cqty) cqty,SUM(A.uprice) uprice,A.slocation FROM equipmentsparerecodedta A LEFT JOIN equipmentsparerecode B ");
        sb.append(" ON A .pid=B.formid  WHERE  (B.relano='").append(formid).append("' OR B.remark='").append(formid).append("') AND B.status='V' AND A.pid LIKE 'TK%' GROUP BY sparenum,A.slocation) TK ON  CK.sparenum=TK.sparenum AND CK.slocation=TK.slocation WHERE  CASE WHEN TK.cqty IS NULL  THEN CK.cqty ELSE CK.cqty-TK.cqty END >0");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

    public List<EquipmentSpareRecodeDta> getEquipmentSpareRecodeDtaListByRepairFormId(String formid) {
        StringBuilder sb = new StringBuilder();
        //sb.append(" SELECT * FROM equipmentsparerecodedta WHERE pid IN(SELECT formid FROM equipmentsparerecode WHERE relano IN (SELECT formid FROM equipmentsparerecode WHERE relano = '").append(formid).append("') OR relano = '").append(formid).append("') ");
        sb.append(" SELECT id,pid,seq,sparenum, CASE WHEN pid LIKE 'TK%' THEN cqty * -1 ELSE cqty END cqty,uprice,slocation,remark,status,creator,credate,optuser,optdate,cfmuser,cfmdate FROM equipmentsparerecodedta WHERE pid IN(SELECT formid FROM equipmentsparerecode WHERE ( relano IN (SELECT formid FROM equipmentsparerecode WHERE relano = '").append(formid).append("') OR relano = '").append(formid).append("') AND status = 'V' ) ");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentSpareRecodeDta.class);
        List<EquipmentSpareRecodeDta> results = query.getResultList();
        return results;
    }

    //    获取每月备件消耗数量
    public List<Object[]> getSpareConsumeQty(String queryDateBegin, String queryDateEnd, String sarea, String type, String deptSql) {
        StringBuilder sbCK = new StringBuilder();
        sbCK.append("  SELECT ").append(type).append("(A.credate), SUM(A.cqty),sum(A.cqty*A.uprice) FROM equipmentsparerecodedta A LEFT JOIN  equipmentsparerecode B ON A.pid=B.formid");
        sbCK.append("  WHERE A.pid LIKE '%CK%' AND B.sarea='").append(sarea).append("' AND   A.status='V' AND  A.credate>='").append(queryDateBegin).append("' and A.credate<='").append(queryDateEnd).append("'");
        if (!"".equals(deptSql)) {
            sbCK.append("AND deptname IN(").append(deptSql).append(")");
        }
        sbCK.append(" GROUP BY ").append(type).append("(A.credate)");;
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sbCK.toString());
        List<Object[]> ckList = query.getResultList();//出库对应的日期及数量
        StringBuilder sbTK = new StringBuilder();
        sbTK.append("  SELECT ").append(type).append("(A.credate), SUM(A.cqty),sum(A.cqty*A.uprice) FROM equipmentsparerecodedta A LEFT JOIN  equipmentsparerecode B ON A.pid=B.formid");
        sbTK.append("  WHERE A.pid LIKE '%TK%' AND B.sarea='").append(sarea).append("' AND   A.status='V' AND  A.credate>='").append(queryDateBegin).append("' and A.credate<='").append(queryDateEnd).append("'");
        if (!"".equals(deptSql)) {
            sbTK.append("AND deptname IN(").append(deptSql).append(")");
        }
        sbTK.append(" GROUP BY ").append(type).append("(A.credate)");
        query = getEntityManager().createNativeQuery(sbTK.toString());
        List<Object[]> tkList = query.getResultList();//退库对应的日期及数量
        for (Object[] ck : ckList) {
            for (Object[] tk : tkList) {
                if (ck[0] == tk[0]) {
                    ck[1] = Double.parseDouble(ck[1].toString()) - Double.parseDouble(tk[1].toString());
                    ck[2] = Double.parseDouble(ck[2].toString()) - Double.parseDouble(tk[2].toString());
                }
            }
        }
        return ckList;
    }

    //根据报修单查询可退料的备件列表
    public List<EquipmentSpareRecodeDta> getRetreatSpareListByNativeQuery(String relano, String spareInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM equipmentsparerecodedta D LEFT JOIN equipmentspare S ON D.sparenum = S.sparenum WHERE pid IN (SELECT formid FROM equipmentsparerecode WHERE relano = '").append(relano).append("') AND D.status = 'V' ");
        if (!"".equals(spareInfo) && spareInfo != null) {
            sb.append(MessageFormat.format(" AND (S.sparedesc LIKE ''%{0}%'' OR D.sparenum LIKE ''%{0}%'') ", spareInfo));
        }
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentSpareRecodeDta.class);
        List<EquipmentSpareRecodeDta> results = query.getResultList();
        for (EquipmentSpareRecodeDta item : results) {
            if (item.getPid().contains("TK")) {
                return new ArrayList<EquipmentSpareRecodeDta>();
            }
        }
        return results;
    }
    //根据报修单查询可退料的备件列表

    public List<EquipmentSpareRecodeDta> getRetreatSpareListByRepairFormId(String relano, String spareInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM equipmentsparerecodedta D LEFT JOIN equipmentspare S ON D.sparenum = S.sparenum WHERE pid IN(SELECT formid FROM equipmentsparerecode WHERE (relano IN (SELECT formid FROM equipmentsparerecode WHERE relano = '").append(relano).append("') OR relano = '").append(relano).append("') AND status <>'Z' ) AND (D.status = 'V' OR D.pid LIKE 'TK%') ");
        if (!"".equals(spareInfo) && spareInfo != null) {
            sb.append(MessageFormat.format(" AND (S.sparedesc LIKE ''%{0}%'' OR D.sparenum LIKE ''%{0}%'') ", spareInfo));
        }
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentSpareRecodeDta.class);
        List<EquipmentSpareRecodeDta> results = query.getResultList();
        for (EquipmentSpareRecodeDta item : results) {
            if (item.getPid().contains("TK")) {
                return new ArrayList<EquipmentSpareRecodeDta>();
            }
        }
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
            if ("credate".equals(key)) {
                sb.append("  AND e.credate>'" + value + "'");
            }
            if ("credateend".equals(key)) {
                sb.append("  AND e.credate<'" + value + "'");
            }
            if (!"credate".equals(key) && !"credateend".equals(key)) {
                strMap.put(key, value);
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
    public List<EquipmentSpareRecodeDta> findByFilters(Map<String, Object> filters, int first, int pageSize, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("credate".equals(key)) {
                sb.append("  AND e.credate>'" + value + "'");
            }
            if ("credateend".equals(key)) {
                sb.append("  AND e.credate<'" + value + "'");
            }
            if (!"credate".equals(key) && !"credateend".equals(key)) {
                strMap.put(key, value);
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

    public List<EquipmentSpareRecodeDta> findByRemark(String remark) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareRecodeDta.findByRemark");
        query.setParameter("remark", remark);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
}
