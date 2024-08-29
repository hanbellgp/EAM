/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentSpareBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.SystemUserBean;
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
    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    
      @EJB
    protected SystemUserBean systemUserBean;
    @EJB
    private EquipmentSpareBean equipmentSpareBean;

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
        newEntity.setDeptno(userManagedBean.getCurrentUser().getDeptno());
        newEntity.setDeptname(userManagedBean.getCurrentUser().getDept().getDept());
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getRelano() != null && !"".equals(newEntity.getRelano())) {
            if (equipmentRepairBean.findByFormid(newEntity.getRelano()).isEmpty()) {
                showErrorMsg("Error", "关联的维修单号不存在,请重新输入!");
                return false;
            }
            if (equipmentRepairBean.findByFormid(newEntity.getRelano()).get(0).getIsneedspare().equals("N")) {
                showErrorMsg("Error", "该维修单号对应的维修单不能领用备件!");
                return false;
            }
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
    public String view(String path) {
        for (EquipmentSpareRecodeDta edDta : detailList) {
            EquipmentSpare eSpare =new EquipmentSpare();
            eSpare=equipmentSpareBean.findBySparenum(edDta.getSparenum().getSparenum(), userManagedBean.getCompany()).get(0);
            edDta.setSparenum(eSpare);
        }
        return super.view(path); 
    }
    
    
    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public  String getUserName(String userId){
        if (systemUserBean.findByUserId(userId)!=null) {
            return systemUserBean.findByUserId(userId).getUsername();
        }
        return "";
    }

    @Override
    public void verify() {
        List<EquipmentSpareStock> stock = new ArrayList<>();
        for (EquipmentSpareRecodeDta eSpareRecodeDta : detailList) {
            EquipmentSpareStock eSpareStock = equipmentSpareStockBean.findBySparenumAndRemark(eSpareRecodeDta.getSparenum().getSparenum(), eSpareRecodeDta.getRemark(), eSpareRecodeDta.getSlocation());
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
        //当具有relano值时，查询是否存着对应维修单，否则不关联维修单
        if (currentEntity.getRelano() != null && !"".equals(currentEntity.getRelano())) {
            List<EquipmentRepair> equipmentRepair = equipmentRepairBean.findByFormid(currentEntity.getRelano()); //获取关联维修单号对应的维修单
            if (!equipmentRepair.isEmpty()) {
                EquipmentRepair eRepair = equipmentRepair.get(0);
                eRepair.setSparecost(getPartsCost());//给该维修单的备件价格赋值
                equipmentRepairBean.update(eRepair);//更新维修单备件价格信息
            }
        }
        super.verify(); //To change body of generated methods, choose Tools | Templates.
    }
//获取零件费用

    public BigDecimal getPartsCost() {
        BigDecimal maintenanceCosts = BigDecimal.ZERO;
        if (detailList != null) {
            for (EquipmentSpareRecodeDta eDta : detailList) {
                maintenanceCosts = maintenanceCosts.add(eDta.getUprice().multiply(eDta.getCqty()));
            }
        }
        return maintenanceCosts;
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
        queryState = "N";//初始查询未审核单据
        this.model.getFilterFields().put("formid", "CK");
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
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
//        paramPosition.add(userManagedBean.getCompany());
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
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            } else {
                this.model.getFilterFields().put("formid", "CK");
            }
            if (queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("creator", queryUserno);
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("relano", queryName);
            }
            if (queryState != null && !"ALL".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }

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
