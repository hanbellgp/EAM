/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareBean;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.Unit;
import cn.hanbell.eam.lazy.EquipmentSpareModel;
import cn.hanbell.eam.web.SuperSingleBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C0160
 */
@ManagedBean(name = "equipmentSpareManagedBean")
@SessionScoped
public class EquipmentSpareManagedBean extends SuperSingleBean<EquipmentSpare> {

    @EJB
    private EquipmentSpareBean equipmentSpareBean;

    public EquipmentSpareManagedBean() {
        super(EquipmentSpare.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCredate(getDate());
        newEntity.setStatus("N");
        newEntity.setCreator(userManagedBean.getUserid());

    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getUnit() == null) {
            showErrorMsg("Error", "请选择单位！");
            return false;
        }
        return super.doBeforePersist(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {
        superEJB = equipmentSpareBean;
        model = new EquipmentSpareModel(equipmentSpareBean, userManagedBean);
        super.init();
    }

    public void handleDialogReturnUnitWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Unit e = (Unit) event.getObject();
            currentEntity.setUnit(e);
        }
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("sparenum", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("sparedesc", queryName);
            }
        }
    }

}
