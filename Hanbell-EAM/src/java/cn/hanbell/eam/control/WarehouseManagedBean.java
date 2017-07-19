/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.ejb.WarehouseRelationBean;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.entity.WarehouseRelation;
import cn.hanbell.eam.lazy.WarehouseModel;
import cn.hanbell.eam.web.SuperMultiBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "warehouseManagedBean")
@SessionScoped
public class WarehouseManagedBean extends SuperMultiBean<Warehouse, WarehouseRelation> {

    @EJB
    private WarehouseBean warehouseBean;
    @EJB
    private WarehouseRelationBean warehouseRelationBean;

    public WarehouseManagedBean() {
        super(Warehouse.class, WarehouseRelation.class);
    }

    @Override
    public void createDetail() {
        super.createDetail();
        newDetail.setCompany(this.userManagedBean.getCompany());
    }

    @Override
    protected boolean doBeforeDelete(Warehouse entity) throws Exception {
        if (super.doBeforeDelete(entity)) {
            if (!warehouseBean.allowDelete(entity.getWarehouseno())) {
                showErrorMsg("Error", "已有交易记录不可删除");
                return false;
            }
            return true;
        }
        return false;
    }

    public void init() {
        this.superEJB = warehouseBean;
        this.model = new WarehouseModel(warehouseBean, userManagedBean);
        this.detailEJB = warehouseRelationBean;
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("warehouseno", "ASC");
        super.init();
    }

}
