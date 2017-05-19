/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetItemBean;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.lazy.AssetItemModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetItemQueryBean")
@ViewScoped
public class AssetItemQueryBean extends SuperQueryBean<AssetItem> {

    @EJB
    private AssetItemBean assetItemBean;

    public AssetItemQueryBean() {
        super(AssetItem.class);
    }

    @Override
    public void init() {
        superEJB = assetItemBean;
        model = new AssetItemModel(assetItemBean);
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("itemno", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("itemdesc", this.queryName);
            }
        }
    }

}
