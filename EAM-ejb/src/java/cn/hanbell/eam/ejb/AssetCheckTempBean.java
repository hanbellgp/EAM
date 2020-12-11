/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCheckTemp;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author Administrator
 */
@Stateless
@LocalBean
public class AssetCheckTempBean extends SuperEJBForEAM<AssetCheckTemp>{

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    public AssetCheckTempBean() {
        super(AssetCheckTemp.class);
    }
}
