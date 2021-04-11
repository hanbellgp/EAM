/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.EquipmentSpareRecodeBean;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeModel;
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
@ManagedBean(name = "equipmentSpareDeliveryQueryBean")
@ViewScoped
public class EquipmentSpareDeliveryQueryBean extends SuperQueryBean<EquipmentSpareRecode> {

    @EJB
    private EquipmentSpareRecodeBean equipmentSpareRecodeBean;
    private String relanos;

    public EquipmentSpareDeliveryQueryBean() {
        super(EquipmentSpareRecode.class);
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

        superEJB = equipmentSpareRecodeBean;
        model = new EquipmentSpareRecodeModel(equipmentSpareRecodeBean, userManagedBean);
        this.model.getFilterFields().put("status", "V");
        this.model.getFilterFields().put("accepttype", "20");
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        //当有关联单号传过来时则只有关联单号的数据才能选择
        if (params != null) {
            if (params.containsKey("relano")) {
                String str = String.valueOf(params.get("relano")[0]);
                this.model.getFilterFields().put("accepttype", "25");
                this.model.getFilterFields().put("relano", str);
            }
        }
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("creator", queryName);
            }
            if (relanos != null && !"".equals(relanos)) {
                model.getFilterFields().put("relano", relanos);
            }
            this.model.getFilterFields().put("status", "V");
            this.model.getFilterFields().put("accepttype", "20");
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    public String getRelanos() {
        return relanos;
    }

    public void setRelanos(String relanos) {
        this.relanos = relanos;
    }

}
