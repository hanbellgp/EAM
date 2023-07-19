/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentWorkTime;
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
public class EquipmentWorkTimeBean extends SuperEJBForEAM<EquipmentWorkTime> {

    public EquipmentWorkTimeBean() {
        super(EquipmentWorkTime.class);
    }
     public List getDept(String company) {
        StringBuilder sbMES = new StringBuilder();
        sbMES.append("SELECT DISTINCT dept,deptname FROM equipmentworktime WHERE  company='"+company+"'");
      
        Query query =getEntityManager().createNativeQuery(sbMES.toString());
        List<Object[]> resultsMES = query.getResultList();
        return resultsMES;
    }

}
