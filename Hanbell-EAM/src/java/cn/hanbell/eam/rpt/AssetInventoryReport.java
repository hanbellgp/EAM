/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.entity.AssetInventory;
import com.lightshell.comm.SuperSingleReportBean;

/**
 *
 * @author C0160
 */
public class AssetInventoryReport extends SuperSingleReportBean<AssetInventoryBean, AssetInventory> {

    public AssetInventoryReport() {

    }

    @Override
    public AssetInventory getEntity(int i) throws Exception {
        return superEJB.findById(i);
    }

}
