/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.Equipmentrepairfile;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentRepairFileBean extends SuperEJBForEAM<Equipmentrepairfile> {

    public EquipmentRepairFileBean() {
        super(Equipmentrepairfile.class);
    }
    
   
}
