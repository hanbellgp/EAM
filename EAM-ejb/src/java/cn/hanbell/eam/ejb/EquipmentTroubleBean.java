/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentTrouble2;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class EquipmentTroubleBean extends SuperEJBForEAM<EquipmentTrouble2> {

    public EquipmentTroubleBean() {
        super(EquipmentTrouble2.class);
    }



}
