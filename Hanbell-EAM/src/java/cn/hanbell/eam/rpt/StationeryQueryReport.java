/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetDistributeDetailForQueryBean;
import cn.hanbell.eam.entity.AssetDistributeDetailForQuery;
import com.lightshell.comm.SuperSingleReportBean;
import java.util.List;

/**
 *
 * @author C0160
 */
public class StationeryQueryReport extends SuperSingleReportBean<AssetDistributeDetailForQueryBean, AssetDistributeDetailForQuery> {

    public StationeryQueryReport() {

    }

    @Override
    public AssetDistributeDetailForQuery getEntity(int i) throws Exception {
        return superEJB.findById(i);
    }
    
}
