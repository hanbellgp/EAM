/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetTransferBean;
import cn.hanbell.eam.entity.AssetTransfer;
import cn.hanbell.eam.entity.AssetTransferDetail;
import com.lightshell.comm.SuperMultiReportBean;
import java.util.List;

/**
 *
 * @author C2079
 */
public class AssetTransferReport extends SuperMultiReportBean<AssetTransferBean, AssetTransfer, AssetTransferDetail> {

    public AssetTransferReport() {

    }

    @Override
    public List<AssetTransferDetail> getDetail(Object o) throws Exception {
        this.superEJB.setDetail(o);
        return this.superEJB.getDetailList();
    }

    @Override
    public AssetTransfer getEntity(int i) throws Exception {
        return this.superEJB.findById(i);
    }

    public String getCompany(String company) {
        return this.superEJB.getCompany(company);
    }

}
