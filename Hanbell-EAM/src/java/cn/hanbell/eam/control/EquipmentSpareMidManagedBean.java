/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareMidBean;
import cn.hanbell.eam.entity.EquipmentSpareClass;
import cn.hanbell.eam.entity.EquipmentSpareMid;
import cn.hanbell.eam.lazy.EquipmentSpareMidModel;
import cn.hanbell.eam.web.SuperSingleBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareMidManagedBean")
@SessionScoped
public class EquipmentSpareMidManagedBean extends SuperSingleBean<EquipmentSpareMid> {

    @EJB
    private EquipmentSpareMidBean equipmentSpareMidBean;
    private String mcategory;
    private String mname;

    public EquipmentSpareMidManagedBean() {
        super(EquipmentSpareMid.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCredate(getDate());
        newEntity.setStatus("V");
        newEntity.setCreator(userManagedBean.getUserid());
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    //作废更改单价转态为N
    public void invalid() {
        currentEntity.setStatus("N");
        super.update();
    }

    @Override
    public void init() {
        superEJB = equipmentSpareMidBean;
        model = new EquipmentSpareMidModel(equipmentSpareMidBean, userManagedBean);
        this.model.getFilterFields().put("status", "V");
        this.model.getSortFields().put("scategory", "ASC");
        this.model.getSortFields().put("mcategory", "ASC");
        queryState = "V";
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("scategory.scategory", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("scategory.sname", queryName);
            }
            if (mcategory != null && !"".equals(mcategory)) {
                this.model.getFilterFields().put("mcategory", mcategory);
            }
            if (mname != null && !"".equals(this.mname)) {
                this.model.getFilterFields().put("mname", mname);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
            this.model.getSortFields().put("scategory", "ASC");
            this.model.getSortFields().put("mcategory", "ASC");
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            EquipmentSpareClass e = (EquipmentSpareClass) event.getObject();
            newEntity.setScategory(e);
            int size = equipmentSpareMidBean.findByScategory(e.getScategory()).size() + 1;
            if (size <= 9) {
                newEntity.setMcategory("0" + size);
            } else {
                newEntity.setMcategory(String.valueOf(size));
            }
        }
    }

    public String getMcategory() {
        return mcategory;
    }

    public void setMcategory(String mcategory) {
        this.mcategory = mcategory;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

}
