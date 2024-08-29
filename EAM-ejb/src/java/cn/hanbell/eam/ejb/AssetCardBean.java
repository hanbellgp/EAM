/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCardSpecial;
import cn.hanbell.eam.entity.AssetItem;
import com.lightshell.comm.BaseLib;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetCardBean extends SuperEJBForEAM<AssetCard> {

    public AssetCardBean() {
        super(AssetCard.class);
    }

    public AssetCard findByAssetno(String value) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByFormid");
        query.setParameter("formid", value);
        try {
            Object o = query.getSingleResult();
            return (AssetCard) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public AssetCard findByAssetno(String company, String value) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByCompanyAndFormid");
        query.setParameter("company", company);
        query.setParameter("formid", value);
        try {
            Object o = query.getSingleResult();
            return (AssetCard) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public AssetCard findByFilters(String company, String assetno, String itemno, String deptno, String userno) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByFilters");
        query.setParameter("company", company);
        query.setParameter("formid", assetno);
        query.setParameter("itemno", itemno);
        query.setParameter("deptno", deptno);
        query.setParameter("userno", userno);
        try {
            Object o = query.getSingleResult();
            return (AssetCard) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<AssetCard> findByFiltersAndNotUsed(String srcformid, int srcseq) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByFiltersAndNotUsed");
        query.setParameter("srcformid", srcformid);
        query.setParameter("srcseq", srcseq);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public AssetCard findByFiltersAndUsed(String relformid, int relseq) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByFiltersAndUsed");
        query.setParameter("relformid", relformid);
        query.setParameter("relseq", relseq);
        try {
            Object o = query.getSingleResult();
            return (AssetCard) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public AssetCard findByFiltersAndScrapped(String relformid, int relseq) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByFiltersAndScrapped");
        query.setParameter("relformid", relformid);
        query.setParameter("relseq", relseq);
        try {
            Object o = query.getSingleResult();
            return (AssetCard) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<AssetCard> findByItemnoAndNotUsed(String value) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByItemnoAndNotUsed");
        query.setParameter("itemno", value);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<AssetCard> findByRelformidAndNeedDelete(String relformid) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByRelformidAndNeedDelete");
        query.setParameter("relformid", relformid);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<AssetCard> findBySrcformid(String srcformid) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findBySrcformid");
        query.setParameter("srcformid", srcformid);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public String getFormId(String company, Date day, AssetItem item) {
        String lead = company + item.getItemno() + "-";
        return getFormId(day, lead, "yy", 4);
    }

    @Override
    public String getFormId(Date day, String code, String format, int len) {
        //流水码不按年份重新编号
        String maxid, newid;
        int id, m;
        if (day != null && code != null && len > 0) {
            String d = "";
            if (format != null && !format.equals("")) {
                d = BaseLib.formatDate(format, day);
            }
            d = d + "-";
            int c = code.length();
            Query query = getEntityManager().createNativeQuery("select max(formid) from assetcard where substring(formid,1," + c + ")='" + (code) + "'");
            if (query.getSingleResult() != null) {
                maxid = query.getSingleResult().toString();
                m = maxid.length();
                id = Integer.parseInt(maxid.substring(m - len, m)) + 1;
                newid = code + d + String.format("%0" + len + "d", id);
            } else {
                newid = code + d + String.format("%0" + len + "d", 1);
            }
            return newid;
        } else {
            return "";
        }
    }

    public BigDecimal getQtyByCategory(String company, String category, int hascost, int idle, int scrap) {
        StringBuilder sb = new StringBuilder();
        sb.append("select assetcategory.name,assetinventory.warehouseno,sum(assetinventory.qty) from assetinventory,assetitem,assetcategory,warehouse ");
        sb.append(" where assetinventory.itemno = assetitem.itemno and assetitem.categoryid = assetcategory.id and assetinventory.warehouseno = warehouse.warehouseno ");
        sb.append(" and assetinventory.company='").append(company).append("' and assetcategory.category='").append(category).append("' ");
        sb.append(" and warehouse.hascost = ").append(hascost).append(" and warehouse.idle = ").append(idle).append(" and warehouse.scrap = ").append(scrap);
        sb.append(" group by assetcategory.category,assetinventory.warehouseno");
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List result = query.getResultList();
        if (result != null && !result.isEmpty()) {
            Object[] row = (Object[]) result.get(0);
            return BigDecimal.valueOf(Double.valueOf(row[2].toString()));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public List<AssetCard> getAssetCardList(String company, String queryParam) {
        String sqlStr = "SELECT a FROM AssetCard a WHERE a.company = :company AND a.assetItem.category.id = :categoryid AND (a.formid LIKE :formid OR a.assetItem.itemdesc LIKE :itemdesc OR a.assetDesc LIKE :assetDesc OR a.userno LIKE :userno OR a.username LIKE :username)";

        //生成SQL
        Query query = getEntityManager().createQuery(sqlStr).setMaxResults(100);

        //参数赋值
        query.setParameter("company", company);
        query.setParameter("formid", "%" + queryParam + "%");
        query.setParameter("categoryid", 3);
        query.setParameter("itemdesc", "%" + queryParam + "%");
        query.setParameter("assetDesc", "%" + queryParam + "%");
        query.setParameter("userno", "%" + queryParam + "%");
        query.setParameter("username", "%" + queryParam + "%");

        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<AssetCard> getCardList(final Map<String, Object> filters, final Map<String, String> orderBy) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
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
        final Query query = this.getEntityManager().createQuery(sb.toString());
        if (filters != null) {
            this.setQueryParam(query, filters);
        }
        return (List<AssetCard>) query.getResultList();
    }

    public List<AssetCard> getAssetCardList(String company, String queryParam, String deptNo, Map<String, Object> filters) {
        String sqlStr = "SELECT e FROM AssetCard e WHERE e.company = :company AND e.assetItem.category.id = :categoryid AND e.deptno LIKE :deptno AND (e.formid LIKE :formidTemp OR e.assetItem.itemdesc LIKE :itemdesc OR e.assetDesc LIKE :assetDesc OR e.userno LIKE :userno OR e.username LIKE :username)";
        StringBuilder sb = new StringBuilder();
        sb.append(sqlStr);
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }

        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString()).setMaxResults(100);

        //参数赋值
        if (filters != null) {
            this.setQueryParam(query, filters);
        }
        
        String deptnoTemp = "";
        if (deptNo.contains("000")) {
            deptnoTemp = deptNo.substring(0, 2);
        } else if(deptNo.length() > 2) {
            deptnoTemp = deptNo.substring(0, 3);
        }
        query.setParameter("company", company);
        query.setParameter("formidTemp", "%" + queryParam + "%");
        query.setParameter("categoryid", 3);
        query.setParameter("deptno", deptnoTemp + "%");
        query.setParameter("itemdesc", "%" + queryParam + "%");
        query.setParameter("assetDesc", "%" + queryParam + "%");
        query.setParameter("userno", "%" + queryParam + "%");
        query.setParameter("username", "%" + queryParam + "%");

        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    
    public List<AssetCardSpecial> getAssetCardSpecialList(String company, String queryParam, String deptNo, Map<String, Object> filters) {
        String sqlStr = "SELECT e FROM assetcardspecial e WHERE e.company = :company AND e.assetItem.category.id = :categoryid AND e.deptno LIKE :deptno AND (e.formid LIKE :formidTemp OR e.assetItem.itemdesc LIKE :itemdesc OR e.assetDesc LIKE :assetDesc OR e.userno LIKE :userno OR e.username LIKE :username)";
        StringBuilder sb = new StringBuilder();
        sb.append(sqlStr);
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }
        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString()).setMaxResults(100);
        //参数赋值
        if (filters != null) {
            this.setQueryParam(query, filters);
        }
        
        String deptnoTemp = "";
        if (deptNo.contains("000")) {
            deptnoTemp = deptNo.substring(0, 2);
        } else if(deptNo.length() > 2) {
            deptnoTemp = deptNo.substring(0, 3);
        }
        query.setParameter("company", company);
        query.setParameter("formidTemp", "%" + queryParam + "%");
        query.setParameter("categoryid", 3);
        query.setParameter("deptno", deptnoTemp + "%");
        query.setParameter("itemdesc", "%" + queryParam + "%");
        query.setParameter("assetDesc", "%" + queryParam + "%");
        query.setParameter("userno", "%" + queryParam + "%");
        query.setParameter("username", "%" + queryParam + "%");
        List <AssetCardSpecial> list =query.getResultList();
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
    
    
}
