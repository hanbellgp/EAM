/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetTransactionBean;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetTransaction;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetTransactionModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetTransactionQueryBean")
@ViewScoped
public class AssetTransactionQueryBean extends SuperQueryBean<AssetTransaction> {

    @EJB
    private AssetTransactionBean assetTransactionBean;

    private Warehouse queryWarehouse;
    private String queryAssetno;

    public AssetTransactionQueryBean() {
        super(AssetTransaction.class);
    }

    public void handleDialogReturnAssetItem(SelectEvent event) {
        if (event.getObject() != null) {
            AssetItem item = (AssetItem) event.getObject();
            setQueryFormId(item.getItemno());
            setQueryName(item.getItemdesc());
        }
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null) {
            setQueryWarehouse((Warehouse) event.getObject());
        }
    }

    @Override
    public void init() {
        superEJB = assetTransactionBean;
        model = new AssetTransactionModel(assetTransactionBean, userManagedBean);
        model.getSortFields().put("formdate", "ASC");
        model.getSortFields().put("id", "ASC");
        model.getFilterFields().put("assetItem.itemno", "-1");
        if (queryWarehouse == null) {
            queryWarehouse = new Warehouse();
            queryWarehouse.setId(-1);
            queryWarehouse.setName("请选择仓库");
        }
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("assetItem.itemno", this.queryFormId);
            }
            if (this.queryAssetno != null && !"".equals(this.queryAssetno)) {
                this.model.getFilterFields().put("assetno", this.queryAssetno);
            }
            if (this.getQueryWarehouse() != null && (this.getQueryWarehouse().getId() != -1)) {
                this.model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouse.getWarehouseno());
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.model.getFilterFields().put("assetItem.itemno", "-1");
        queryFormId = null;
        queryName = null;
        queryWarehouse = new Warehouse();
        queryWarehouse.setId(-1);
        queryWarehouse.setName("请选择仓库");
        queryDateBegin = null;
        queryDateEnd = null;
    }

    /**
     * @return the queryWarehouse
     */
    public Warehouse getQueryWarehouse() {
        return queryWarehouse;
    }

    /**
     * @param queryWarehouse the queryWarehouse to set
     */
    public void setQueryWarehouse(Warehouse queryWarehouse) {
        this.queryWarehouse = queryWarehouse;
    }

    /**
     * @return the queryAssetno
     */
    public String getQueryAssetno() {
        return queryAssetno;
    }

    /**
     * @param queryAssetno the queryAssetno to set
     */
    public void setQueryAssetno(String queryAssetno) {
        this.queryAssetno = queryAssetno;
    }

}
