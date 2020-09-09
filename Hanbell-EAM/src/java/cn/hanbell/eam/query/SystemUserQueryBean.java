/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.lazy.SystemUserModel;
import cn.hanbell.eam.web.SuperQueryBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "systemUserQueryBean")
@ViewScoped
public class SystemUserQueryBean extends SuperQueryBean<SystemUser> {

    @EJB
    private SystemUserBean systemUserBean;
    private String queryDeptno;

    public SystemUserQueryBean() {
        super(SystemUser.class);
    }

    @Override
    public void init() {
        this.superEJB = systemUserBean;
        setModel(new SystemUserModel(systemUserBean));
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("deptno")) {
                queryDeptno = String.valueOf(params.get("deptno")[0]);
            }
        }
        if (queryDeptno != null) {
            model.getFilterFields().put("deptno", queryDeptno);
        }
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
        }
    }

}
