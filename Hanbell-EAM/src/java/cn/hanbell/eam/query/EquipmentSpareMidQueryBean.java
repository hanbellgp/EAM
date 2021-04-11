/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.EquipmentSpareMidBean;
import cn.hanbell.eam.entity.EquipmentSpareMid;
import cn.hanbell.eam.lazy.EquipmentSpareMidModel;
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
@ManagedBean(name = "equipmentSpareMidQueryBean")
@ViewScoped
public class EquipmentSpareMidQueryBean extends SuperQueryBean<EquipmentSpareMid> {

    @EJB
    private EquipmentSpareMidBean equipmentSpareMidBean;
    private String sCategory;

    public EquipmentSpareMidQueryBean() {
        super(EquipmentSpareMid.class);
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
        superEJB = equipmentSpareMidBean;
        model = new EquipmentSpareMidModel(equipmentSpareMidBean, userManagedBean);
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("sCategory")) {
                sCategory = String.valueOf(params.get("sCategory")[0]);
            }
        }
        if (sCategory != null) {
            model.getFilterFields().put("scategory.scategory", sCategory);
        }
        this.model.getFilterFields().put("status", "V");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("mcategory", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("mname", queryName);
            }
            if (sCategory != null) {
                model.getFilterFields().put("scategory.scategory", sCategory);
            }
            this.model.getFilterFields().put("status", "V");
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    public EquipmentSpareMidBean getEquipmentSpareMidBean() {
        return equipmentSpareMidBean;
    }

    public void setEquipmentSpareMidBean(EquipmentSpareMidBean equipmentSpareMidBean) {
        this.equipmentSpareMidBean = equipmentSpareMidBean;
    }

    public String getsCategory() {
        return sCategory;
    }

    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }

}
