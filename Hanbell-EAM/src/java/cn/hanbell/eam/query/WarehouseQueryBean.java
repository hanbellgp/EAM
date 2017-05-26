/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.WarehouseModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "warehouseQueryBean")
@ViewScoped
public class WarehouseQueryBean extends SuperQueryBean<Warehouse> {

    @EJB
    private WarehouseBean warehouseBean;
    private Integer queryHascost = -1;

    public WarehouseQueryBean() {
        super(Warehouse.class);
    }

    @Override
    public void init() {
        superEJB = warehouseBean;
        model = new WarehouseModel(warehouseBean, userManagedBean);
        model.getSortFields().put("warehouseno", "ASC");
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("hascost")) {
                queryHascost = Integer.valueOf(params.get("hascost")[0]);
            }
        }
        if (queryHascost == 0) {
            model.getFilterFields().put("hascost", false);
        } else if (queryHascost > 0) {
            model.getFilterFields().put("hascost", true);
        }
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("warehouseno", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("name", this.queryName);
            }
            if (queryHascost == 0) {
                model.getFilterFields().put("hascost", false);
            } else if (queryHascost > 0) {
                model.getFilterFields().put("hascost", true);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (queryHascost == 0) {
            model.getFilterFields().put("hascost", false);
        } else if (queryHascost > 0) {
            model.getFilterFields().put("hascost", true);
        }
    }

}
