/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetCheckDetailForQueryBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetCheckDetailForQuery;
import cn.hanbell.eam.lazy.AssetCheckDetailForQueryModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCheckDetailQueryBean")
@ViewScoped
public class AssetCheckDetailQueryBean extends SuperQueryBean<AssetCheckDetailForQuery> {

    @EJB
    private AssetCheckDetailForQueryBean assetCheckDetailForQueryBean;

    private AssetCategory queryCategory;

    public AssetCheckDetailQueryBean() {
        super(AssetCheckDetailForQuery.class);
    }

    public void handleDialogReturnCategoryWhenNew(SelectEvent event) {
        if (event.getObject() != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            setQueryCategory(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetCheckDetailForQueryBean;
        model = new AssetCheckDetailForQueryModel(assetCheckDetailForQueryBean, userManagedBean);
        super.init();
    }

    @Override
    public void print() throws Exception {
        if (queryCategory == null) {
            showWarnMsg("Warn", "请先选择资产类别");
            return;
        }
        super.print();
    }

    @Override
    public void query() {
        if (queryCategory == null) {
            showWarnMsg("Warn", "请先选择资产类别");
            return;
        }
        if (model != null) {
            model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("assetCheck.formid", queryFormId);
            }
            if (queryDateBegin != null) {
                model.getFilterFields().put("assetCheck.formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                model.getFilterFields().put("assetCheck.formdateEnd", queryDateEnd);
            }
            if (getQueryCategory() != null) {
                model.getFilterFields().put("assetCheck.category.id", getQueryCategory().getId());
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                model.getFilterFields().put("status", queryState);
            }
            model.getSortFields().clear();
            model.getSortFields().put("assetCheck.formid", "ASC");
            model.getSortFields().put("seq", "ASC");
        }
    }

    /**
     * @return the queryCategory
     */
    public AssetCategory getQueryCategory() {
        return queryCategory;
    }

    /**
     * @param queryCategory the queryCategory to set
     */
    public void setQueryCategory(AssetCategory queryCategory) {
        this.queryCategory = queryCategory;
    }

}
