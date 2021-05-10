/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareRecodeBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeModel;
import cn.hanbell.eam.web.FormMultiBean;
import java.math.BigDecimal;
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
@ManagedBean(name = "equipmentSpareDeliveryManagedBean")
@SessionScoped
public class EquipmentSpareDeliveryManagedBean extends FormMultiBean<EquipmentSpareRecode, EquipmentSpareRecodeDta> {

    @EJB
    private EquipmentSpareRecodeBean equipmentSpareRecodeBean;

    @EJB
    private EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    @EJB
    private EquipmentSpareStockBean equipmentSpareStockBean;
    protected List<String> paramPosition = null;
    private BigDecimal stockQty;//选择的库存数量
    private String queryUserno;

    public EquipmentSpareDeliveryManagedBean() {
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
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getRelano() != null) {
            newEntity.setAccepttype("25");
        } else {
            newEntity.setAccepttype("20");
        }
        if (this.newEntity != null) {
            String formid = this.superEJB.getFormId(newEntity.getFormdate(), "CK", "YYMM", 4);
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
            if (eSpareRecodeDta.getCqty().compareTo(eSpareStock.getQty()) == 1) {
                showErrorMsg("Error", "库存检验失败，请确认库存");
                return;
            }
            eSpareStock.setQty(eSpareStock.getQty().subtract(eSpareRecodeDta.getCqty()));
            eSpareStock.setOptdate(getDate());
            eSpareStock.setOptuser(userManagedBean.getUserid());
            stock.add(eSpareStock);
            eSpareRecodeDta.setStatus("V");
        }
        equipmentSpareStockBean.update(stock);
        equipmentSpareRecodeDtaBean.update(detailList);//更新对应字表状态
        super.verify(); //To change body of generated methods, choose Tools | Templates.
    }

    //作废更改单价转态为Z
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
        queryState = "N";
        this.model.getFilterFields().put("formid", "CK");
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
    public void openDialog(String view) {
        openParams.clear();
        if (paramPosition == null) {
            paramPosition = new ArrayList<>();
        } else {
            paramPosition.clear();
        }
        if (newEntity.getSarea() != null) {
            paramPosition.add(newEntity.getSarea());
        } else {
            paramPosition.add(currentEntity.getSarea());
        }

        openParams.put("sarea", paramPosition);
        super.openDialog(view, openParams);
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newDetail != null) {
            EquipmentSpareStock e = (EquipmentSpareStock) event.getObject();
            newDetail.setSparenum(e.getSparenum());
            stockQty = e.getQty();//将库存数量储存以便使用
        }
    }

    @Override
    public void doConfirmDetail() {
        List<EquipmentSpareStock> list = equipmentSpareStockBean.findBySparenumAndSarea(currentDetail.getSparenum().getSparenum(), currentEntity.getSarea());
        BigDecimal deliveryQty = BigDecimal.ZERO;//存放需要出库的数量
        for (EquipmentSpareRecodeDta eDta : detailList) {
            if (eDta.getSparenum().getSparenum().equals(currentDetail.getSparenum().getSparenum())) {
                showErrorMsg("Error", "该出库单已存在该备件。如需修改请删除后重新添加！！！");
                return;
            }
        }
        if (currentDetail != null) {
            deliveryQty = currentDetail.getCqty();
        }
        if (deliveryQty.compareTo(stockQty) == 1) {
            showErrorMsg("Error", "库存不足，请重新输入数量");
            return;
        }
        for (EquipmentSpareStock eStock : list) {
            EquipmentSpareRecodeDta eDta = new EquipmentSpareRecodeDta();
            eDta.setSparenum(eStock.getSparenum());
            eDta.setUprice(eStock.getUprice());
            eDta.setSlocation(eStock.getSlocation());
            eDta.setCreator(userManagedBean.getUserid());
            eDta.setCredate(getDate());
            eDta.setStatus("N");
            eDta.setSeq(addedDetailList.size() + 1);
            eDta.setRemark(eStock.getRemark());
            if (eStock.getQty().compareTo(deliveryQty) == -1 && eStock.getQty().signum() != 0) {
                eDta.setCqty(eStock.getQty());
                deliveryQty = deliveryQty.subtract(eStock.getQty());//该存储位置上的库存已用完，剩下的出库数量
                addedDetailList.add(eDta);
                detailList.add(eDta);
            } else {
                eDta.setCqty(deliveryQty);
                addedDetailList.add(eDta);
                detailList.add(eDta);
                setNewDetail(null);//将子对象清空
                setCurrentDetail(null);
                return;
            }
        }
        setNewDetail(null);//将子对象清空
        setCurrentDetail(null);
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
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("relano", queryName);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }

            this.model.getFilterFields().put("formid", "CK");

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

    public List<String> getParamPosition() {
        return paramPosition;
    }

    public void setParamPosition(List<String> paramPosition) {
        this.paramPosition = paramPosition;
    }

    public BigDecimal getStockQty() {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        this.stockQty = stockQty;
    }

    public String getQueryUserno() {
        return queryUserno;
    }

    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

}
