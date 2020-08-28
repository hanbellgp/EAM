/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentTrouble;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class EquipmentTroubleBean extends SuperEJBForEAM<EquipmentTrouble> {

    public EquipmentTroubleBean() {
        super(EquipmentTrouble.class);
    }

 public EquipmentTrouble findByTroubleid(String value) {
        Query query = getEntityManager().createNamedQuery("EquipmentTrouble.findByTroubleid");
        query.setParameter("troubleid", value);
        try {
            Object o = query.getSingleResult();
            return (EquipmentTrouble) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
