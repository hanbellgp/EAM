/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.WarehouseBean;
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
@ManagedBean(name = "stationeryInventoryQueryBean")
@ViewScoped
public class StationeryInventoryQueryBean extends SuperQueryBean<AssetInventory> {

    @EJB
    private WarehouseBean warehouseBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    private Warehouse queryWarehouse;

    public StationeryInventoryQueryBean() {
        super(AssetInventory.class);
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null) {
            queryWarehouse = (Warehouse) event.getObject();
        }
    }

    @Override
    public void init() {
        if (queryWarehouse == null) {
            queryWarehouse = warehouseBean.findByWarehouseno("WJ01");
        }
        superEJB = assetInventoryBean;
        model = new AssetInventoryModel(assetInventoryBean, userManagedBean);
        model.getFilterFields().put("qty <>", 0);
        model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouse.getWarehouseno());
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
            if (this.queryWarehouse != null && (this.queryWarehouse.getId() != -1)) {
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
        model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouse.getWarehouseno());
        queryFormId = null;
        queryName = null;
    }

    /**
     * @return the queryWarehouse
     */
    public Warehouse getQueryWarehouse() {
        return queryWarehouse;
    }

}
