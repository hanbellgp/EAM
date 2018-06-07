/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetCheckDetailForQueryBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCheckDetailForQuery;
import com.lightshell.comm.SuperSingleReportBean;
import java.util.List;

/**
 *
 * @author C0160
 */
public class AssetCheckExport extends SuperSingleReportBean<AssetCheckDetailForQueryBean, AssetCheckDetailForQuery> {

    public AssetCheckExport() {

    }

    @Override
    public AssetCheckDetailForQuery getEntity(int i) throws Exception {
        return superEJB.findById(i);
    }

}
