/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareRecodeBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeModel;
import cn.hanbell.eam.web.FormMultiBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareRecodeManagedBean")
@SessionScoped
public class EquipmentSpareRecodeManagedBean extends FormMultiBean<EquipmentSpareRecode, EquipmentSpareRecodeDta> {

    @EJB
    private EquipmentSpareRecodeBean equipmentSpareRecodeBean;

    @EJB
    private EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    @EJB
    private EquipmentSpareStockBean equipmentSpareStockBean;
    private String queryUserno;

    public EquipmentSpareRecodeManagedBean() {
        super(EquipmentSpareRecode.class, EquipmentSpareRecodeDta.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCredate(getDate());
        newEntity.setFormdate(getDate());
        newEntity.setStatus("N");
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCreator(userManagedBean.getUserid());
        newEntity.setDeptno(userManagedBean.getCurrentUser().getDeptno());
        newEntity.setDeptname(userManagedBean.getCurrentUser().getDept().getDept());
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        newEntity.setAccepttype("10");
        detailList.forEach(equipmentSpareRecode -> {
            equipmentSpareRecode.setSlocation(newEntity.getSlocation());//将主表位置更新至子表(小程序需求)
        });
        return super.doBeforePersist(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void verify() {
        List<EquipmentSpareStock> eStickList = new ArrayList<>();
        for (EquipmentSpareRecodeDta eSpareRecodeDta : detailList) {
            EquipmentSpareStock eSpareStock = new EquipmentSpareStock();
            eSpareStock.setIntime(getDate());
            eSpareStock.setRemark(currentEntity.getFormid());
            eSpareStock.setCreator(userManagedBean.getUserid());
            eSpareStock.setCredate(getDate());
            eSpareStock.setQty(eSpareRecodeDta.getCqty());
            eSpareStock.setUprice(eSpareRecodeDta.getUprice());
            eSpareStock.setSparenum(eSpareRecodeDta.getSparenum());
            eSpareStock.setSarea(currentEntity.getSarea());
            eSpareStock.setSlocation(eSpareRecodeDta.getSlocation());
            eSpareStock.setWarehouseno(currentEntity.getWarehouseno());
            eSpareStock.setCompany(userManagedBean.getCompany());
            eSpareStock.setStatus("V");
            eSpareRecodeDta.setStatus("V");//更改明细状态
            eStickList.add(eSpareStock);
        }
        equipmentSpareStockBean.update(eStickList);//更新库存数据
        equipmentSpareRecodeDtaBean.update(detailList);//更新明细状态
        super.verify(); //To change body of generated methods, choose Tools | Templates.
    }

    //作废更改单价转态为N
    public void invalid() {
        currentEntity.setStatus("Z");
        super.update();
    }

    @Override
    public void init() {
        superEJB = equipmentSpareRecodeBean;
        model = new EquipmentSpareRecodeModel(equipmentSpareRecodeBean, userManagedBean);
        detailEJB = equipmentSpareRecodeDtaBean;
        this.model.getFilterFields().put("status", "N");
        this.model.getFilterFields().put("formid", "LK");
        queryState = "N";
        this.model.getSortFields().put("formid", "ASC");
        super.init();
    }

    @Override
    public void createDetail() {
        super.createDetail(); //To change body of generated methods, choose Tools | Templates.
        currentDetail.setStatus("N");
        currentDetail.setCredate(getDate());
        currentDetail.setCreator(userManagedBean.getUserid());
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            EquipmentSpare e = (EquipmentSpare) event.getObject();
            for (EquipmentSpareRecodeDta eDta : detailList) {
                if (eDta.getSparenum().getSparenum().equals(e.getSparenum())) {
                    showWarnMsg("Warn", "该入库单中已存在该备件,请直接单击修改相关数据!!!");
                    currentDetail = null;
                    return;
                }
            }
            currentDetail.setSparenum(e);
            currentDetail.setSlocation(currentEntity.getSlocation());
        }
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            this.model.getSortFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            }
            if (queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("creator", queryUserno);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
            this.model.getFilterFields().put("formid", "LK");
        }
    }

    /**
     * 获取入库类型名称
     *
     * @param type
     * @return
     */
    public String getAcceptTypeName(String type) {
        return equipmentSpareRecodeBean.getAcceptTypeName(type);
    }

    public String getQueryUserno() {
        return queryUserno;
    }

    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

}
