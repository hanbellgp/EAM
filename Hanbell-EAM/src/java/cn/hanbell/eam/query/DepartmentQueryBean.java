/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.lazy.DepartmentModel;
import cn.hanbell.eam.web.SuperQueryBean;
import cn.hanbell.eap.ejb.DepartmentBean;
import cn.hanbell.eap.entity.Department;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "departmentQueryBean")
@ViewScoped
public class DepartmentQueryBean extends SuperQueryBean<Department> {

    @EJB
    private DepartmentBean departmentBean;

    public DepartmentQueryBean() {
        super(Department.class);
    }

    @Override
    public void init() {
        superEJB = departmentBean;
        model = new DepartmentModel(departmentBean);
        model.getSortFields().put("deptno", "ASC");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("deptno", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("dept", this.queryName);
            }
        }
    }

}
