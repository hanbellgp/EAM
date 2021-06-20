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

    public List<EquipmentStandard> findByAssetno(String assetno) {
        Query query = getEntityManager().createNamedQuery("EquipmentStandard.findByAssetno");
        query.setParameter("assetno", assetno);
        query.setParameter("status", "V");
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentStandard> getEquipmentStandardList(String respondept, String standardlevel, String standardtype, String assetno) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" WHERE e.status='V'");
        if (respondept != null && !"".equals(respondept)) {
            sb.append(" AND e.respondept='").append(respondept).append("'");
        }
        if (standardlevel != null && !"".equals(standardlevel)) {
            sb.append(" AND e.standardlevel='").append(standardlevel).append("'");
        }
        if (standardtype != null && !"".equals(standardtype)) {
            sb.append(" AND e.standardtype='").append(standardtype).append("'");
        }
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND e.assetno='").append(assetno).append("'");
        }
        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

}
