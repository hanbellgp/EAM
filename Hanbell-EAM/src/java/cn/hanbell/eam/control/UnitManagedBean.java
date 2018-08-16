/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.UnitBean;
import cn.hanbell.eam.entity.Unit;
import cn.hanbell.eam.lazy.UnitModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "unitManagedBean")
@SessionScoped
public class UnitManagedBean extends SuperSingleBean<Unit> {

    @EJB
    private UnitBean unitBean;

    public UnitManagedBean() {
        super(Unit.class);
    }

    @Override
    public void init() {
        this.superEJB = unitBean;
        model = new UnitModel(unitBean);
        super.init();
    }

}
