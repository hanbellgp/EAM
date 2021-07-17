/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.web.SuperSingleBean;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareStockManagedBean")
@SessionScoped
public class EquipmentSpareStockManagedBean extends SuperSingleBean<EquipmentSpareStock> {

    @EJB
    private EquipmentSpareStockBean equipmentSpareStockBean;
    private List<EquipmentSpareStock> listStock;
    private String querySparemodel;
    private Object obj;

    public EquipmentSpareStockManagedBean() {
        super(EquipmentSpareStock.class);
    }

    @Override
    public void init() {
        superEJB = equipmentSpareStockBean;
        listStock = equipmentSpareStockBean.getEquipmentSpareStockList(queryName, queryFormId,querySparemodel);
    }

    @Override
    public void query() {
        listStock = equipmentSpareStockBean.getEquipmentSpareStockList(queryName, queryFormId,querySparemodel);
    }

    @Override
    public String view(String path) {
        Object[] item = (Object[]) obj;
        entityList = equipmentSpareStockBean.findBySparenum(item[0].toString());
        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }

    public List<EquipmentSpareStock> getListStock() {
        return listStock;
    }

    public void setListStock(List<EquipmentSpareStock> listStock) {
        this.listStock = listStock;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getQuerySparemodel() {
        return querySparemodel;
    }

    public void setQuerySparemodel(String querySparemodel) {
        this.querySparemodel = querySparemodel;
    }

}
