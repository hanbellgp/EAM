/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.UnitBean;
import cn.hanbell.eam.entity.Unit;
import cn.hanbell.eam.lazy.UnitModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author kevindong
 */
@ManagedBean
@ViewScoped
public class UnitQueryBean extends SuperQueryBean<Unit> {

    @EJB
    private UnitBean unitBean;

    public UnitQueryBean() {
        super(Unit.class);
    }

    @Override
    public void init() {
        this.superEJB = unitBean;
        setModel(new UnitModel(unitBean));
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("unit", this.queryFormId);
            }
        }
    }

}
