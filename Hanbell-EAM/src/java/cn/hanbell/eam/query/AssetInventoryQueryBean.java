/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetInventoryModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetInventoryQueryBean")
@ViewScoped
public class AssetInventoryQueryBean extends SuperQueryBean<AssetInventory> {

    @EJB
    private AssetInventoryBean assetInventoryBean;

    private Warehouse queryWarehouse;

    public AssetInventoryQueryBean() {
        super(AssetInventory.class);
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null) {
            setQueryWarehouse((Warehouse) event.getObject());
        }
    }

    @Override
    public void init() {
        superEJB = assetInventoryBean;
        model = new AssetInventoryModel(assetInventoryBean, userManagedBean);
        model.getFilterFields().put("qty <>", 0);
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
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("assetItem.itemno", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("assetItem.itemdesc", this.queryName);
            }
            if (this.getQueryWarehouse() != null && (this.getQueryWarehouse().getId() != -1)) {
                this.model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouse.getWarehouseno());
            }
            if (queryState != null && !"V".equals(queryState)) {
                this.model.getFilterFields().put("qty <>", 0);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        model.getFilterFields().put("qty <>", 0);
        queryFormId = null;
        queryName = null;
        queryWarehouse = new Warehouse();
        queryWarehouse.setId(-1);
        queryWarehouse.setName("请选择仓库");
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

}
