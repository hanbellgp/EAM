/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.WarehouseRelation;
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
public class WarehouseRelationBean extends SuperEJBForEAM<WarehouseRelation> {

    public WarehouseRelationBean() {
        super(WarehouseRelation.class);
    }

    public WarehouseRelation findByCompanyAndPId(String company, int pid) {
        Query query = getEntityManager().createNamedQuery("WarehouseRelation.findByCompanyAndPId");
        query.setParameter("company", company);
        query.setParameter("pid", pid);
        try {
            Object o = query.getSingleResult();
            return (WarehouseRelation) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
