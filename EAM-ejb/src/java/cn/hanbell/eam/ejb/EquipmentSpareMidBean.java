/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareMid;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentSpareMidBean extends SuperEJBForEAM<EquipmentSpareMid> {

    public EquipmentSpareMidBean() {
        super(EquipmentSpareMid.class);
    }
   public List<EquipmentSpareMid> findByScategory(String srcformid,String company) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareMid.findByScategory");
        query.setParameter("scategory", srcformid);
        query.setParameter("company", company);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
}
