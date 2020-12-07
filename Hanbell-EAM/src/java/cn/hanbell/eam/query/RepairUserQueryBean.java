/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.lazy.SystemUserModel;
import cn.hanbell.eam.web.SuperQueryBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "repairUserQueryBean")
@ViewScoped
public class RepairUserQueryBean extends SuperQueryBean<SystemUser> {

    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private SysCodeBean sysCodeBean;

    public RepairUserQueryBean() {
        super(SystemUser.class);
    }

    @Override
    public void init() {
        this.superEJB = systemUserBean;
        setModel(new SystemUserModel(systemUserBean));
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairDeptno").getCvalue();
        this.model.getFilterFields().put("status", "N");
        this.model.getFilterFields().put("deptno", deptno);

        model.getSortFields().put("userid", "ASC");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("userid", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("username", this.queryName);
            }
            this.model.getFilterFields().put("status", "N");
            String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairDeptno").getCvalue();
            this.model.getFilterFields().put("deptno", deptno);
        }
    }

}
