/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetDistributeBean;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import com.lightshell.comm.SuperMultiReportBean;
import java.util.List;

/**
 *
 * @author C0160
 */
public class AssetDistributeReport extends SuperMultiReportBean<AssetDistributeBean, AssetDistribute, AssetDistributeDetail> {

    public AssetDistributeReport() {

    }

    @Override
    public List<AssetDistributeDetail> getDetail(Object o) throws Exception {
        this.superEJB.setDetail(o);
        return this.superEJB.getDetailList();
    }

    @Override
    public AssetDistribute getEntity(int i) throws Exception {
        return this.superEJB.findById(i);
    }

}
