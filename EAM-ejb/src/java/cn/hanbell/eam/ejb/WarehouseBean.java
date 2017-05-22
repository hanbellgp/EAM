/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.Warehouse;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class WarehouseBean extends SuperEJBForEAM<Warehouse> {

    public WarehouseBean() {
        super(Warehouse.class);
    }

    public Warehouse findByWarehouseno(String value) {
        Query query = getEntityManager().createNamedQuery("Warehouse.findByWarehouseno");
        query.setParameter("warehosueno", value);
        try {
            Object o = query.getSingleResult();
            return (Warehouse) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
