/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.Unit;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C1587
 */
@Stateless
@LocalBean
public class UnitBean extends SuperEJBForEAM<Unit> {

    public UnitBean() {
        super(Unit.class);
    }

    public List<Unit> findByPUnit(Object value) {
         Query query = getEntityManager().createNamedQuery("Unit.findRootByUnit");
        query.setParameter("Unit", value);
        return query.getResultList();
    }
    
}
