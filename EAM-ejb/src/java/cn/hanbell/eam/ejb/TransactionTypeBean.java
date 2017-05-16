/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.TransactionType;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class TransactionTypeBean extends SuperEJBForEAM<TransactionType> {

    public TransactionTypeBean() {
        super(TransactionType.class);
    }

    public TransactionType findByTrtype(String trtype) {
        Query query = this.getEntityManager().createNamedQuery("TransactionType.findByTrtype");
        query.setParameter("trtype", trtype);
        try {
            Object o = query.getSingleResult();
            return (TransactionType) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
