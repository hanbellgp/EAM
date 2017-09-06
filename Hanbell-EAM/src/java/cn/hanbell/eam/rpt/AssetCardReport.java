/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.entity.AssetCard;
import com.lightshell.comm.SuperSingleReportBean;

/**
 *
 * @author C0160
 */
public class AssetCodeReport extends SuperSingleReportBean<AssetCardBean, AssetCard> {

    public AssetCodeReport() {

    }

    @Override
    public AssetCard getEntity(int i) throws Exception {
        return superEJB.findById(i);
    }

    public AssetCard getAssetCard(String formid) throws Exception {
        return superEJB.findByAssetno(formid);
    }

}
