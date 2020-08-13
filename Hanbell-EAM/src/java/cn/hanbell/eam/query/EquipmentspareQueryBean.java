/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.EquipmentSpareBean;
import cn.hanbell.eam.entity.Equipmentspare;
import cn.hanbell.eam.lazy.EquipmentspareModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "euipmentspareQueryBean")
@ViewScoped
public class EquipmentspareQueryBean extends SuperQueryBean<Equipmentspare> {

    @EJB
    private EquipmentSpareBean equipmentspareBean;
    private String querySparenum;
    private String querySparedesc;

    public EquipmentspareQueryBean() {
        super(Equipmentspare.class);
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
        superEJB = equipmentspareBean;
        model = new EquipmentspareModel(equipmentspareBean, userManagedBean);
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.querySparenum != null && !"".equals(this.querySparenum)) {
                this.model.getFilterFields().put("sparenum", this.querySparenum);
            }
            if (this.querySparedesc != null && !"".equals(this.querySparedesc)) {
                this.model.getFilterFields().put("sparedesc", this.querySparedesc);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    public String getQuerySparenum() {
        return querySparenum;
    }

    public void setQuerySparenum(String querySparenum) {
        this.querySparenum = querySparenum;
    }

    public String getQuerySparedesc() {
        return querySparedesc;
    }

    public void setQuerySparedesc(String querySparedesc) {
        this.querySparedesc = querySparedesc;
    }

 

}
