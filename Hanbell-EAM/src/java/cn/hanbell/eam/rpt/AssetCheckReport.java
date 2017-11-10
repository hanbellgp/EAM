/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetCheckBean;
import cn.hanbell.eam.entity.AssetCheck;
import cn.hanbell.eam.entity.AssetCheckDetail;
import com.lightshell.comm.SuperMultiReportBean;
import java.util.List;

/**
 *
 * @author C0160
 */
public class AssetCheckReport extends SuperMultiReportBean<AssetCheckBean, AssetCheck, AssetCheckDetail> {

    public AssetCheckReport() {

    }

    @Override
    public List<AssetCheckDetail> getDetail(Object o) throws Exception {
        superEJB.setDetail(o);
        return superEJB.getDetailList();
    }

    @Override
    public AssetCheck getEntity(int i) throws Exception {
        return superEJB.findById(i);
    }

}
