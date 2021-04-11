/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareRecodeBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeDtaModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareUseManagedBean")
@SessionScoped
public class EquipmentSpareUseManagedBean extends SuperQueryBean<EquipmentSpareRecodeDta> {

    @EJB
    private EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    private String queryUserno;

    public EquipmentSpareUseManagedBean() {
        super(EquipmentSpareRecodeDta.class);
    }

    @Override
    public void init() {
        superEJB = equipmentSpareRecodeDtaBean;
        model = new EquipmentSpareRecodeDtaModel(equipmentSpareRecodeDtaBean, userManagedBean);
        this.model.getFilterFields().put("status", "V");
        this.model.getSortFields().put("pid", "ASC");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            this.model.getSortFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("sparenum.sparedesc", queryFormId);
            }
            if (queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("creator", queryUserno);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("pid", queryState);
            }
            this.model.getFilterFields().put("status", "V");
        }
    }

    public String getQueryUserno() {
        return queryUserno;
    }

    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

}
