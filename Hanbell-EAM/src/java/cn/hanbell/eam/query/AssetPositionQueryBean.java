/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetPositionBean;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.lazy.AssetPositionModel;
import cn.hanbell.eam.web.SuperQueryBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetPositionQueryBean")
@ViewScoped
public class AssetPositionQueryBean extends SuperQueryBean<AssetPosition> {

    @EJB
    private AssetPositionBean assetPositionBean;
    private Integer queryPId = -1;

    public AssetPositionQueryBean() {
        super(AssetPosition.class);
    }

    @Override
    public void init() {
        superEJB = assetPositionBean;
        model = new AssetPositionModel(assetPositionBean, userManagedBean);
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("pid")) {
                queryPId = Integer.valueOf(params.get("pid")[0]);
            }
        }
        if (queryPId == 0) {
            model.getFilterFields().put("parentPosition IS NULL", "");
        } else if (queryPId > 0) {
            model.getFilterFields().put("parentPosition.id", queryPId);
        }
        super.init();
    }

    @Override
    public void query() {
        if (model != null && model.getFilterFields() != null) {
            model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("position", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                model.getFilterFields().put("name", queryName);
            }
            if (queryPId == 0) {
                model.getFilterFields().put("parentPosition IS NULL", "");
            } else if (queryPId > 0) {
                model.getFilterFields().put("parentPosition.id", queryPId);
            }
        }
    }

}
