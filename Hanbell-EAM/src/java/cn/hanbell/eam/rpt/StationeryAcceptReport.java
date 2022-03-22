/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.rpt;

import cn.hanbell.eam.ejb.AssetAcceptanceBean;
import cn.hanbell.eam.entity.AssetAcceptance;
import cn.hanbell.eam.entity.AssetAcceptanceDetail;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.SuperMultiReportBean;
import java.util.List;

/**
 *
 * @author 2079
 */
public class StationeryAcceptReport extends SuperMultiReportBean<AssetAcceptanceBean, AssetAcceptance, AssetAcceptanceDetail> {

    public StationeryAcceptReport() {

    }

    @Override
    public List<AssetAcceptanceDetail> getDetail(Object o) throws Exception {
        this.superEJB.setDetail(o);
        List<AssetAcceptanceDetail> list = this.superEJB.getDetailList();
        return this.superEJB.getDetailList();
    }

    @Override
    public AssetAcceptance getEntity(int i) throws Exception {
        return this.superEJB.findById(i);
    }
    
    public String  getUserName(String userId) {
        SystemUser s =this.superEJB.getUserName(userId);
        String userName="";
        if (s!=null) {
            userName=s.getUsername();
        }
        return userName;
    }
    
}
