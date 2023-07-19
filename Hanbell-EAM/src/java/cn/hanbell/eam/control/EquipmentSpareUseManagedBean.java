/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeDtaModel;
import cn.hanbell.eam.web.SuperQueryBean;
import java.text.SimpleDateFormat;
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
    private String queryFormName;

    public EquipmentSpareUseManagedBean() {
        super(EquipmentSpareRecodeDta.class);
    }

    @Override
    public void init() {
        superEJB = equipmentSpareRecodeDtaBean;
        model = new EquipmentSpareRecodeDtaModel(equipmentSpareRecodeDtaBean, userManagedBean);
        this.model.getFilterFields().put("status", "V");
        this.model.getSortFields().put("pid", "ASC");
        queryState = "LK";
        super.init();
    }

    @Override
    public void query() {
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (this.model != null) {
            this.model.getFilterFields().clear();
            this.model.getSortFields().clear();
            if (queryFormName != null && !"".equals(queryFormName)) {
                this.model.getFilterFields().put("sparenum.sparedesc", queryFormName);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("sparenum.sparenum", queryFormId);
            }
            if (queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("creator", queryUserno);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("pid", queryState);
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("credate", simpleDateFormat.format(queryDateBegin));
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("credateend",  simpleDateFormat.format(queryDateEnd));
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

    public String getQueryFormName() {
        return queryFormName;
    }

    public void setQueryFormName(String queryFormName) {
        this.queryFormName = queryFormName;
    }

}
