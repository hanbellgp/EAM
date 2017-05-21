/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetTransaction;
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
public class AssetTransactionBean extends SuperEJBForEAM<AssetTransaction> {

    public AssetTransactionBean() {
        super(AssetTransaction.class);
    }

    @Override
    public List<AssetTransaction> findByPId(Object value) {
        return findByFormid(value.toString());
    }

    public AssetTransaction findByPIdAndSeq(Object value, int seq) {
        return findByFormidAndSeq(value.toString(), seq);
    }

    @Override
    public List<AssetTransaction> findByFormid(String formid) {
        Query query = this.getEntityManager().createNamedQuery("AssetTransaction.findByFormid");
        query.setParameter("formid", formid);
        return query.getResultList();
    }

    public AssetTransaction findByFormidAndSeq(String formid, int seq) {
        Query query = this.getEntityManager().createNamedQuery("AssetTransaction.findByFormidAndSeq");
        query.setParameter("formid", formid);
        query.setParameter("seq", seq);
        try {
            return (AssetTransaction) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public void setDefaultValue(AssetTransaction entity) {
        if (entity.getBrand() == null) {
            entity.setBrand("");
        }
        if (entity.getBatch() == null) {
            entity.setBatch("");
        }
        if (entity.getSn() == null) {
            entity.setSn("");
        }
    }

}
