/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentStandard;
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
public class EquipmentStandardBean extends SuperEJBForEAM<EquipmentStandard> {

    public EquipmentStandardBean() {
        super(EquipmentStandard.class);
    }

    public List<EquipmentStandard> findByAssetno(String assetno,String standardlevel) {
        Query query = getEntityManager().createNamedQuery("EquipmentStandard.findByAssetnoAndStandardlevel");
        query.setParameter("assetno", assetno);
        query.setParameter("standardlevel", standardlevel);
        query.setParameter("status", "V");
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }



}
