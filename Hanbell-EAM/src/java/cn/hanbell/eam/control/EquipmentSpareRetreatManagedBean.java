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
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareRetreatManagedBean")
@SessionScoped
public class EquipmentSpareRetreatManagedBean extends FormMultiBean<EquipmentSpareRecode, EquipmentSpareRecodeDta> {

    @EJB
    private EquipmentSpareRecodeBean equipmentSpareRecodeBean;
    @EJB
    private EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    @EJB
    private EquipmentSpareStockBean equipmentSpareStockBean;
    private String queryUserno;
    protected List<String> paramPosition = null;

    public EquipmentSpareRetreatManagedBean() {
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
        newEntity.setRelano(null);
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        newEntity.setAccepttype("30");
        //将不同pid的转化为相同的并添加
        for (EquipmentSpareRecodeDta eDta : detailList) {
            if (!eDta.getPid().equals(newEntity.getFormid())) {
                eDta.setPid(newEntity.getFormid());
                eDta.setStatus("N");
                addedDetailList.add(eDta);
            }
        }
        editedDetailList.clear();
        if (this.newEntity != null) {
            String formid = this.superEJB.getFormId(newEntity.getFormdate(), "TK", "YYMM", 4);
            this.newEntity.setFormid(formid);
            if (this.addedDetailList != null && !this.addedDetailList.isEmpty()) {
                this.addedDetailList.stream().forEach((detail) -> {
                    detail.setPid(newEntity.getFormid());
                });
            }
            if (this.editedDetailList != null && !this.editedDetailList.isEmpty()) {
                this.editedDetailList.stream().forEach((detail) -> {
                    detail.setPid(newEntity.getFormid());
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void verify() {
        List<EquipmentSpareStock> stock = new ArrayList<>();
        for (EquipmentSpareRecodeDta eSpareRecodeDta : detailList) {
            EquipmentSpareStock eSpareStock = equipmentSpareStockBean.findBySparenumAndRemark(eSpareRecodeDta.getSparenum().getSparenum(), eSpareRecodeDta.getRemark());
            if (eSpareStock == null) {
                showErrorMsg("Error", "该备件已被删除！！！");
                return;
            }
            eSpareStock.setQty(eSpareStock.getQty().add(eSpareRecodeDta.getCqty()));
            eSpareStock.setOptdate(getDate());
            eSpareStock.setOptuser(userManagedBean.getUserid());
            stock.add(eSpareStock);
            eSpareRecodeDta.setStatus("V");
        }
        equipmentSpareStockBean.update(stock);
        equipmentSpareRecodeDtaBean.update(detailList);
        super.verify(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openDialog(String view) {
        openParams.clear();
        if (paramPosition == null) {
            paramPosition = new ArrayList<>();
        } else {
            paramPosition.clear();
        }
        if (newEntity.getRemark() != null) {
            paramPosition.add(newEntity.getRemark());
        }
        openParams.put("relano", paramPosition);
        super.openDialog(view, openParams);
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
        queryState = "N";
        this.model.getFilterFields().put("formid", "TK");
         this.model.getFilterFields().put("status", queryState);
        this.model.getSortFields().put("formid", "ASC");
        openParams = new HashMap<>();
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
        if (event.getObject() != null && newEntity != null) {
            EquipmentSpareRecode e = (EquipmentSpareRecode) event.getObject();
            newEntity.setSarea(e.getSarea());
            newEntity.setWarehouseno(e.getWarehouseno());
            newEntity.setRelano(e.getFormid());
            detailList = equipmentSpareRecodeDtaBean.findByPId(e.getFormid());
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
            this.model.getFilterFields().put("formid", "TK");
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

    public List<String> getParamPosition() {
        return paramPosition;
    }

    public void setParamPosition(List<String> paramPosition) {
        this.paramPosition = paramPosition;
    }

}
