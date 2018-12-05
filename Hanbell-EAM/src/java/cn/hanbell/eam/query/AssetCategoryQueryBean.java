/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetCategoryBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.lazy.AssetCategoryModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author C1368
 */
@ManagedBean(name = "assetCategoryQueryBean")
@ViewScoped
public class AssetCategoryQueryBean extends SuperQueryBean<AssetCategory> {

    @EJB
    private AssetCategoryBean assetCategoryBean;
    private Integer queryPId = -1;
    private String queryCategory = null;

    public AssetCategoryQueryBean() {
        super(AssetCategory.class);
    }

    @Override
    public void init() {
        superEJB = assetCategoryBean;
        model = new AssetCategoryModel(assetCategoryBean);
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("pid")) {
                queryPId = Integer.valueOf(params.get("pid")[0]);
            }
            if (params.containsKey("category")) {
                queryCategory = params.get("category")[0];
            }
        }
        if (queryPId == 0) {
            model.getFilterFields().put("parentCategory IS NULL", "");
        } else if (queryPId > 0) {
            model.getFilterFields().put("parentCategory.id", queryPId);
        }
        if (queryCategory != null && !"".equals(queryCategory)) {
            model.getFilterFields().put("category", queryCategory);
        }
        super.init();
    }

    @Override
    public void query() {
        if (model != null && model.getFilterFields() != null) {
            model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("category", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                model.getFilterFields().put("name", queryName);
            }
            if (queryPId == 0) {
                model.getFilterFields().put("parentCategory IS NULL", "");
            } else if (queryPId > 0) {
                model.getFilterFields().put("parentCategory.id", queryPId);
            }
        }
    }

}
