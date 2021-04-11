/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareClassBean;
import cn.hanbell.eam.entity.EquipmentSpareClass;
import cn.hanbell.eam.lazy.EquipmentSpareClassModel;
import cn.hanbell.eam.web.SuperSingleBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareClassManagedBean")
@SessionScoped
public class EquipmentSpareClassManagedBean extends SuperSingleBean<EquipmentSpareClass> {

    @EJB
    private EquipmentSpareClassBean equipmentSpareClassBean;

    public EquipmentSpareClassManagedBean() {
        super(EquipmentSpareClass.class);
    }

    @Override 
    public void create() {
        super.create();
        int size = equipmentSpareClassBean.findAll().size() + 1;
        if (size <= 9) {
            newEntity.setScategory("0" + size);
        } else {
            newEntity.setScategory(String.valueOf(size));
        }
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
        superEJB = equipmentSpareClassBean;
        model = new EquipmentSpareClassModel(equipmentSpareClassBean, userManagedBean);
        this.model.getFilterFields().put("status", "V");
        queryState = "V";
        this.model.getSortFields().put("scategory", "ASC");
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
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
            this.model.getSortFields().put("scategory", "ASC");
        }
    }

}
