/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetDistributeDetailForQueryBean;
import cn.hanbell.eam.entity.AssetDistributeDetailForQuery;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetDistributeDetailForQueryModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "stationeryDistributeQueryBean")
@ViewScoped
public class StationeryDistributeQueryBean extends SuperQueryBean<AssetDistributeDetailForQuery> {

    @EJB
    private AssetDistributeDetailForQueryBean assetDistributeDetailForQueryBean;

    public StationeryDistributeQueryBean() {
        super(AssetDistributeDetailForQuery.class);
    }

    @Override
    public void init() {
        superEJB = assetDistributeDetailForQueryBean;
        model = new AssetDistributeDetailForQueryModel(assetDistributeDetailForQueryBean, userManagedBean);
         if (currentPrgGrant != null && currentPrgGrant.getSysprg().getNoauto()) {
            model.getFilterFields().put("assetDistribute.formid", currentPrgGrant.getSysprg().getNolead());
        }
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("assetDistribute.formid", queryFormId);
            } else {
                if (currentPrgGrant != null && currentPrgGrant.getSysprg().getNoauto()) {
                    model.getFilterFields().put("assetDistribute.formid", currentPrgGrant.getSysprg().getNolead());
                }
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("assetDistribute.formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("assetDistribute.formdateEnd", queryDateEnd);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("assetDistribute.status", queryState);
            }
        }
    }

}
