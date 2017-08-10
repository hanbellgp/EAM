/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetAdjustBean;
import cn.hanbell.eam.entity.AssetAdjust;
import cn.hanbell.eam.entity.AssetAdjustDetail;
import com.lightshell.comm.SuperMultiReportBean;
import java.util.List;

/**
 *
 * @author C0160
 */
public class AssetAdjustReport extends SuperMultiReportBean<AssetAdjustBean, AssetAdjust, AssetAdjustDetail> {

    public AssetAdjustReport() {

    }

    @Override
    public List<AssetAdjustDetail> getDetail(Object o) throws Exception {
        this.superEJB.setDetail(o);
        return this.superEJB.getDetailList();
    }

    @Override
    public AssetAdjust getEntity(int i) throws Exception {
        return this.superEJB.findById(i);
    }

}
