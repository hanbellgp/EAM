/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentRepairHis2;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentRepairHisBean extends SuperEJBForEAM<EquipmentRepairHis2> {

    public EquipmentRepairHisBean() {
        super(EquipmentRepairHis2.class);
    }
    
   
}
