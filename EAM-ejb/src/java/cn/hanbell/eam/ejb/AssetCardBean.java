/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetItem;
import com.lightshell.comm.BaseLib;
import java.util.Date;
import java.util.List;
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

    public List<AssetCard> findByItemnoAndNotUsed(String value) {
        Query query = getEntityManager().createNamedQuery("AssetCard.findByItemnoAndNotUsed");
        query.setParameter("itemno", value);
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

}
