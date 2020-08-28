/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentRepairFile;
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
public class EquipmentRepairFileBean extends SuperEJBForEAM<EquipmentRepairFile> {

    public EquipmentRepairFileBean() {
        super(EquipmentRepairFile.class);
    }
  
    public List<EquipmentRepairFile> findByFilefrom(String filefrom) {
        Query query = getEntityManager().createNamedQuery("EquipmentRepairFile.findByFilefrom");
        query.setParameter("filefrom", filefrom);
        try {
             List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }
   
}
