/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.lazy.EquipmentSpareStockModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareStockQueryBean")
@ViewScoped
public class EquipmentSpareStockQueryBean extends SuperQueryBean<EquipmentSpareStock> {

    @EJB
    private EquipmentSpareStockBean equipmentSpareStockBean;
    private String sArea;
    private String warehouseno;
    private String querySparemodel;

    public EquipmentSpareStockQueryBean() {
        super(EquipmentSpareStock.class);
    }

    public void closeMultiSelect() {
        if (entityList != null && !entityList.isEmpty()) {
            RequestContext.getCurrentInstance().closeDialog(entityList);
        } else {
            showErrorMsg("Error", "没有选择数据源");
        }
    }

    @Override
    public void init() {
        superEJB = equipmentSpareStockBean;
        model = new EquipmentSpareStockModel(equipmentSpareStockBean, userManagedBean);
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("sarea")) {
                sArea = String.valueOf(params.get("sarea")[0]);
            }
        }

        if (sArea != null) {
            model.getFilterFields().put("sarea", sArea);
        }

        this.model.getFilterFields().put("status", "V");
        //默认不含零数量
        this.model.getFilterFields().put("qty <>", 0);

        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("sparenum.sparenum", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("sparenum.sparedesc", queryName);
            }
            if (querySparemodel != null && !"".equals(this.querySparemodel)) {
                this.model.getFilterFields().put("sparenum.sparemodel", queryName);
            }
            
            if (sArea != null) {
                model.getFilterFields().put("sarea", sArea);
            }
            this.model.getFilterFields().put("status", "V");
            this.model.getFilterFields().put("qty <>", 0);
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    public String getsArea() {
        return sArea;
    }

    public void setsArea(String sArea) {
        this.sArea = sArea;
    }

    public String getWarehouseno() {
        return warehouseno;
    }

    public void setWarehouseno(String warehouseno) {
        this.warehouseno = warehouseno;
    }

    public String getQuerySparemodel() {
        return querySparemodel;
    }

    public void setQuerySparemodel(String querySparemodel) {
        this.querySparemodel = querySparemodel;
    }

}
