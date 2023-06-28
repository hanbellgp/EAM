/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareClass;
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
public class EquipmentSpareClassBean extends SuperEJBForEAM<EquipmentSpareClass> {

        public List<EquipmentSpareClass> findByCompany(String company) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareClass.findByCompany");
        query.setParameter("company", company);
        try {
             List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public EquipmentSpareClassBean() {
        super(EquipmentSpareClass.class);
    }

}
