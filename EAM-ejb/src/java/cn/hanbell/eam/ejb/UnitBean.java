/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.Unit;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class UnitBean extends SuperEJBForEAM<Unit> {

    public UnitBean() {
        super(Unit.class);
    }

    public Unit findByUnit(String value) {
        Query query = getEntityManager().createNamedQuery("Unit.findByUnit");
        query.setParameter("unit", value);
        try {
            Object o = query.getSingleResult();
            return (Unit) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
