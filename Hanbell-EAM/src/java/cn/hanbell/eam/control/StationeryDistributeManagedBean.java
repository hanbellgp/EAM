/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.StationeryDistributeBean;
import cn.hanbell.eam.ejb.StationeryDistributeDetailBean;
import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetDistributeModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "stationeryDistributeManagedBean")
@SessionScoped
public class StationeryDistributeManagedBean extends FormMultiBean<AssetDistribute, AssetDistributeDetail> {

    @EJB
    protected StationeryDistributeBean stationeryDistributeBean;
    @EJB
    protected StationeryDistributeDetailBean stationeryDistributeDetailBean;
    @EJB
    protected AssetInventoryBean assetInventoryBean;
    @EJB
    protected WarehouseBean warehouseBean;

    protected List<String> paramCategory = null;
    protected List<String> paramHascost = null;

    public StationeryDistributeManagedBean() {
        super(AssetDistribute.class, AssetDistributeDetail.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (super.doBeforePersist()) {
            for (AssetDistributeDetail add : detailList) {
                if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (!add.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            for (AssetDistributeDetail add : detailList) {
                if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (!add.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (currentEntity == null) {
            showWarnMsg("Warn", "没有可更新数据");
            return false;
        }
        AssetDistribute e = stationeryDistributeBean.findById(currentEntity.getId());
        if (!"T".equals(e.getStatus())) {
            showWarnMsg("Warn", "状态已变更");
            return false;
        }
        if (detailList != null && !detailList.isEmpty()) {
            detailList.clear();
        }
        detailList = detailEJB.findByPId(currentEntity.getFormid());
        return true;
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (currentEntity == null) {
            showWarnMsg("Warn", "没有可更新数据");
            return false;
        }
        AssetDistribute e = stationeryDistributeBean.findById(currentEntity.getId());
        if ("V".equals(e.getStatus()) || "T".equals(e.getStatus())) {
            showWarnMsg("Warn", "状态已变更");
            return false;
        }
        int i;
        boolean flag;
        String wareh;
        AssetInventory ai;
        List<AssetDistributeDetail> details = new ArrayList<>();
        for (AssetDistributeDetail add : detailList) {
            //数量累加后再判断库存可利用量
            flag = true;
            for (AssetDistributeDetail d : details) {
                if (d.getAssetItem().getItemno().equals(add.getAssetItem().getItemno())) {
                    d.setQty(d.getQty().add(add.getQty()));
                    flag = false;
                }
            }
            if (flag) {
                details.add(add);
            }
        }
        for (AssetDistributeDetail add : details) {
            ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse().getWarehouseno());
            if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可利用量不足");
                return false;
            }
        }
        return true;
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail == null) {
            return;
        }
        if (currentDetail.getDeptno() == null || "".equals(currentDetail.getDeptno())) {
            showErrorMsg("Error", "请输入领用部门");
            return;
        }
        if (currentDetail.getUserno() == null || "".equals(currentDetail.getUserno())) {
            showErrorMsg("Error", "请输入领用人");
            return;
        }
        if (currentDetail.getAssetItem() == null) {
            showErrorMsg("Error", "请输入件号");
            return;
        }
        if (currentDetail.getAssetItem().getCategory().getNoauto() && currentDetail.getAssetCard() == null) {
            showErrorMsg("Error", "请输入编号");
            return;
        }
        if (currentDetail.getWarehouse() == null) {
            showErrorMsg("Error", "请输入来源仓");
            return;
        }
        if (currentDetail.getQty().compareTo(BigDecimal.ZERO) != 1) {
            showErrorMsg("Error", "请输入数量");
            return;
        }
        currentDetail.setAmts(currentDetail.getQty().multiply(currentDetail.getPrice()));
        super.doConfirmDetail();
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno(d.getDeptno());
            currentEntity.setDeptname(d.getDept());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setUnit(e.getUnit());
            if (e.getCategory().getWarehouse() != null) {
                currentDetail.setWarehouse(e.getCategory().getWarehouse());
                currentDetail.setWarehouse2(e.getCategory().getWarehouse());
            }
        }
    }

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Department d = (Department) event.getObject();
            currentDetail.setDeptno(d.getDeptno());
            currentDetail.setDeptname(d.getDept());
        }
    }

    public void handleDialogReturnUserWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail.setUserno(u.getUserid());
            currentDetail.setUsername(u.getUsername());
        }
    }

    public void handleDialogReturnWarehouseWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse(e);
        }
    }

    @Override
    public void init() {
        if (openParams == null) {
            openParams = new HashMap<>();
        }
        if (paramCategory == null) {
            paramCategory = new ArrayList<>();
        } else {
            paramCategory.clear();
        }
        paramCategory.add("C1");
        superEJB = stationeryDistributeBean;
        detailEJB = stationeryDistributeDetailBean;
        model = new AssetDistributeModel(stationeryDistributeBean, userManagedBean);
        model.getFilterFields().put("status", "N");
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
        }
        super.init();
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "assetitemSelect":
                openParams.clear();
                openParams.put("category", paramCategory);
                super.openDialog("assetitemSelect", openParams);
                break;
            case "warehouseSelect":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("1");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
    }

    @Override
    public void query() {
        if (this.model != null && this.model.getFilterFields() != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            } else {
                if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
                    model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
                }
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
        }
    }

    @Override
    protected void setToolBar() {
        if (currentEntity != null && getCurrentPrgGrant() != null && currentEntity.getStatus() != null) {
            switch (currentEntity.getStatus()) {
                case "T":
                    this.doEdit = false;
                    this.doDel = false;
                    this.doCfm = false;
                    this.doUnCfm = true;
                    break;
                default:
                    this.doEdit = getCurrentPrgGrant().getDoedit() && true;
                    this.doDel = getCurrentPrgGrant().getDodel() && true;
                    this.doCfm = getCurrentPrgGrant().getDocfm() && true;
                    this.doUnCfm = false;
            }
        } else {
            this.doEdit = false;
            this.doDel = false;
            this.doCfm = false;
            this.doUnCfm = false;
        }
    }

    @Override
    public void verify() {
        if (null != getCurrentEntity()) {
            try {
                if (doBeforeVerify()) {
                    currentEntity.setStatus("T");
                    currentEntity.setCfmuser(getUserManagedBean().getCurrentUser().getUsername());
                    currentEntity.setCfmdateToNow();
                    superEJB.verify(currentEntity);
                    doAfterVerify();
                    showInfoMsg("Info", "更新成功");
                } else {
                    showErrorMsg("Error", "审核前检查失败");
                }
            } catch (Exception ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        } else {
            showWarnMsg("Warn", "没有可更新数据");
        }
    }

}
