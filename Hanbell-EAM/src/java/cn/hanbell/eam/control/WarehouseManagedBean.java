/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.WarehouseModel;
import cn.hanbell.eam.web.SuperSingleBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "warehouseManagedBean")
@SessionScoped
public class WarehouseManagedBean extends SuperSingleBean<Warehouse> {

    @EJB
    private WarehouseBean warehouseBean;

    public WarehouseManagedBean() {
        super(Warehouse.class);
    }

    public void init() {
        this.superEJB = warehouseBean;
        this.model = new WarehouseModel(warehouseBean, userManagedBean);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("warehouseno", "ASC");
        super.init();
    }

}
