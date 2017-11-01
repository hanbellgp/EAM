/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.entity.WarehouseRelation;
import javax.ejb.EJB;
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

    @EJB
    private WarehouseRelationBean warehouseRelationBean;

    public WarehouseBean() {
        super(Warehouse.class);
    }

    public boolean allowDelete(String value) {
        Integer count;
        Query query;
        //验收
        query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM assetacceptancedetail WHERE warehouseno = ?1");
        query.setParameter(1, value);
        try {
            count = Integer.valueOf(query.getSingleResult().toString());
            if (count > 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        //交易
        query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM assettransaction WHERE warehouseno = ?1");
        query.setParameter(1, value);
        try {
            count = Integer.valueOf(query.getSingleResult().toString());
            if (count > 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public Warehouse findByWarehouseno(String value) {
        Query query = getEntityManager().createNamedQuery("Warehouse.findByWarehouseno");
        query.setParameter("warehouseno", value);
        try {
            Object o = query.getSingleResult();
            return (Warehouse) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public String findERPWarehouse(String company, int pid) {
        WarehouseRelation wr = warehouseRelationBean.findByCompanyAndPId(company, pid);
        if (wr != null) {
            return wr.getWarehouseno();
        }
        Warehouse w = findById(pid);
        return w.getRemark();
    }

}
