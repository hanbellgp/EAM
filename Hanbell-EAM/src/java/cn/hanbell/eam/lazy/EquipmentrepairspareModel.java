/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.entity.Equipmentrepairspare;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;

/**
 *
 * @author C2079
 */
public class EquipmentrepairspareModel extends BaseLazyModel<Equipmentrepairspare> {



    public EquipmentrepairspareModel(SuperEJB superEJB) {
        this.superEJB = superEJB;
    }
}
