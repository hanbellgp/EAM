/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.EquipmentSpareClassBean;
import cn.hanbell.eam.entity.EquipmentSpareClass;
import cn.hanbell.eam.lazy.EquipmentSpareClassModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "equipmentSpareClassQueryBean")
@ViewScoped
public class EquipmentSpareClassQueryBean extends SuperQueryBean<EquipmentSpareClass> {

    @EJB
    private EquipmentSpareClassBean equipmentSpareClassBean;

    public EquipmentSpareClassQueryBean() {
        super(EquipmentSpareClass.class);
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
        superEJB = equipmentSpareClassBean;
        model = new EquipmentSpareClassModel(equipmentSpareClassBean, userManagedBean);
        this.model.getFilterFields().put("status", "V");
        super.init(); 
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("scategory", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("sname", queryName);
            }
            this.model.getFilterFields().put("status", "V");
        }
    }

    @Override
    public void reset() {
        super.reset();
    }



}
