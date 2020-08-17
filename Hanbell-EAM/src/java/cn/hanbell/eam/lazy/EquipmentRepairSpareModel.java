/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.entity.EquipmentRepairSpare;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;

/**
 *
 * @author C2079
 */
public class EquipmentRepairSpareModel extends BaseLazyModel<EquipmentRepairSpare> {



    public EquipmentRepairSpareModel(SuperEJB superEJB) {
        this.superEJB = superEJB;
    }
}
