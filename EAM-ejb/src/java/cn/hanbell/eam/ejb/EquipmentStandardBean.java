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

    public List<EquipmentStandard> findByAssetnoAndStandardlevel(String assetno, String standardlevel, String nexttime) {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append(" SELECT * FROM equipmentstandard WHERE  status='V' AND assetno='").append(assetno).append("' AND standardlevel='").append(standardlevel).append("' AND nexttime>'").append(nexttime).append("' ");
        sbSql.append(" AND nexttime Like '%").append(nexttime.substring(0, 4)).append("%' ORDER BY  nexttime ASC");
        Query query = getEntityManager().createNativeQuery(sbSql.toString(), EquipmentStandard.class);
        List<EquipmentStandard> sList = query.getResultList();
        return sList;
    }

}
