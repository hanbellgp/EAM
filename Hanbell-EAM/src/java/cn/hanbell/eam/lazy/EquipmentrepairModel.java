/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.control.UserManagedBean;
import cn.hanbell.eam.entity.EquipmentRepair2;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;

/**
 *
 * @author C2079
 */
public class EquipmentRepairModel extends BaseLazyModel<EquipmentRepair2> {

    private final UserManagedBean userManagedBean;
  
    public EquipmentRepairModel(SuperEJB superEJB, UserManagedBean userManagedBean) {
        this.superEJB = superEJB;
        this.userManagedBean = userManagedBean;
    }

  
}
