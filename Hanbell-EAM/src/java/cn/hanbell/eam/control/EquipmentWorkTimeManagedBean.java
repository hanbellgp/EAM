/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentWorkTimeBean;
import cn.hanbell.eam.entity.EquipmentWorkTime;
import cn.hanbell.eam.lazy.EquipmentWorkTimeModel;
import cn.hanbell.eam.web.SuperSingleBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentWorkTimeManagedBean")
@SessionScoped
public class EquipmentWorkTimeManagedBean extends SuperSingleBean<EquipmentWorkTime> {

    @EJB
    private EquipmentWorkTimeBean equipmentWorkTimeBean;

    public EquipmentWorkTimeManagedBean() {
        super(EquipmentWorkTime.class);
    }

    @Override
    public void init() {
        superEJB = equipmentWorkTimeBean;
        model = new EquipmentWorkTimeModel(equipmentWorkTimeBean, userManagedBean);
        queryDateBegin = getDate();
        queryDateEnd = getDate();
        model.getFilterFields().put("formdateBegin", queryDateBegin);
        model.getFilterFields().put("formdateEnd", queryDateEnd);
        String dept =userManagedBean.getCurrentUser().getDeptno().substring(0,2);
//        model.getFilterFields().put("deptno", queryDateEnd);
        super.init();
    }

    @Override
    public void query() {

        if (model != null) {
            this.model.getFilterFields().clear();
            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
        }
    }

}
