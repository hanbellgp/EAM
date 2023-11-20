/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpare;
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
public class EquipmentSpareBean extends SuperEJBForEAM<EquipmentSpare> {

    public EquipmentSpareBean() {
        super(EquipmentSpare.class);
    }

    public List<EquipmentSpare> findByAllBasicInfo(String company, String basicInfoStr) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpare.findByAllBasicInfo").setMaxResults(100);
        query.setParameter("company", company);
        query.setParameter("spareno", "%" + basicInfoStr + "%");
        query.setParameter("sparenum", "%" + basicInfoStr + "%");
        query.setParameter("sparedesc", "%" + basicInfoStr + "%");
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentSpare> findBySparenum(String sparenum,String company) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpare.findBySparenum");
        query.setParameter("sparenum", sparenum + "%");
          query.setParameter("company", company);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
}
