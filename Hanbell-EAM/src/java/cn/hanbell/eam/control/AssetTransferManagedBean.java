/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetTransferBean;
import cn.hanbell.eam.ejb.AssetTransferDetailBean;
import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetTransfer;
import cn.hanbell.eam.entity.AssetTransferDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetTransferModel;
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
@ManagedBean(name = "assetTransferManagedBean")
@SessionScoped
public class AssetTransferManagedBean extends FormMultiBean<AssetTransfer, AssetTransferDetail> {

    @EJB
    protected AssetTransferBean assetDistributeBean;
    @EJB
    protected AssetTransferDetailBean assetDistributeDetailBean;
    @EJB
    protected AssetInventoryBean assetInventoryBean;
    @EJB
    protected AssetCardBean assetCardBean;
    @EJB
    protected WarehouseBean warehouseBean;

    protected List<String> paramItemno = null;
    protected List<String> paramPosition = null;
    protected List<String> paramUsed = null;
    protected List<String> paramHascost = null;

    public AssetTransferManagedBean() {
        super(AssetTransfer.class, AssetTransferDetail.class);
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
            for (AssetTransferDetail d : detailList) {
                if (d.getAssetItem().getCategory().getNoauto() && d.getAssetCard() == null) {
                    showErrorMsg("Error", d.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (d.getUsed() == d.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
                if (d.getCompany().equals(d.getAssetCard().getCompany())) {
                    showErrorMsg("Error", "转入转出公司不能相同");
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
            for (AssetTransferDetail d : detailList) {
                if (d.getAssetItem().getCategory().getNoauto() && d.getAssetCard() == null) {
                    showErrorMsg("Error", d.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (d.getUsed() == d.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
                if (d.getCompany().equals(d.getAssetCard().getCompany())) {
                    showErrorMsg("Error", "转入转出公司不能相同");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (super.doBeforeUnverify()) {
            AssetInventory ai;
            AssetCard ac = null;
            for (AssetTransferDetail add : detailList) {
                ai = assetInventoryBean.findAssetInventory(add.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可还原量不足");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (super.doBeforeVerify()) {
            int i;
            boolean flag;
            String wareh;
            AssetInventory ai;
            AssetCard ac;
            List<AssetTransferDetail> details = new ArrayList<>();
            for (AssetTransferDetail atd : detailList) {
                if (atd.getAssetCard() == null) {
                    showErrorMsg("Error", atd.getAssetItem().getItemno() + "需要指定资产编号");
                    return false;
                }
                if (atd.getAssetCard() != null) {
                    ac = assetCardBean.findByAssetno(atd.getAssetno());
                    if ((ac == null) || (ac.getQty().compareTo(BigDecimal.ZERO) < 1)) {
                        showErrorMsg("Error", atd.getAssetno() + "不存在或没有可转移数量");
                        return false;
                    }
                }//刀工量仪在领用时才产生卡片信息，此处不用判断是否存在卡片
                //数量累加后再判断库存可利用量
                flag = true;
                for (AssetTransferDetail d : details) {
                    if (d.getAssetItem().getItemno().equals(atd.getAssetItem().getItemno())) {
                        d.setQty(d.getQty().add(atd.getQty()));
                        flag = false;
                    }
                }
                if (flag) {
                    details.add(atd);
                }
            }
            //用累加数量进行库存可用量判断
            for (AssetTransferDetail add : details) {
                ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可利用量不足");
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail == null) {
            return;
        }
        if (currentDetail.getDeptno() == null || "".equals(currentDetail.getDeptno())) {
            showErrorMsg("Error", "请输入使用部门");
            return;
        }
        if (currentDetail.getUserno() == null || "".equals(currentDetail.getUserno())) {
            showErrorMsg("Error", "请输入使用人");
            return;
        }
        if (currentDetail.getAssetItem() == null) {
            showErrorMsg("Error", "请输入件号");
            return;
        }
        if (currentDetail.getAssetCard() == null) {
            showErrorMsg("Error", "请输入编号");
            return;
        }
        if (currentDetail.getWarehouse() == null) {
            showErrorMsg("Error", "请输入仓库");
            return;
        }
        if (currentDetail.getQty().compareTo(BigDecimal.ZERO) != 1) {
            showErrorMsg("Error", "请输入数量");
            return;
        }
        if (currentDetail.getCompany().equals(currentDetail.getAssetCard().getCompany())) {
            showErrorMsg("Error", "转入转出公司不能相同");
            return;
        }
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
        }
    }

    public void handleDialogReturnAssetCardWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetCard e = (AssetCard) event.getObject();
            currentDetail.setAssetCard(e);
            currentDetail.setAssetno(e.getFormid());
            currentDetail.setQty(e.getQty());
            currentDetail.setDeptno(e.getDeptno());
            currentDetail.setDeptname(e.getDeptname());
            currentDetail.setUserno(e.getUserno());
            currentDetail.setUsername(e.getUsername());
            currentDetail.setSurplusValue(e.getAmts());
            currentDetail.setUsed(e.getUsed());
            currentDetail.setWarehouse(e.getWarehouse());
        }
    }

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Department d = (Department) event.getObject();
            currentDetail.setDeptno(d.getDeptno());
            currentDetail.setDeptname(d.getDept());
        }
    }

    public void handleDialogReturnPosition1WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition1(e);
        }
    }

    public void handleDialogReturnPosition2WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition2(e);
        }
    }

    public void handleDialogReturnPosition3WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition3(e);
        }
    }

    public void handleDialogReturnPosition4WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition4(e);
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
        superEJB = assetDistributeBean;
        detailEJB = assetDistributeDetailBean;
        model = new AssetTransferModel(assetDistributeBean, userManagedBean);
        model.getFilterFields().put("status", "N");
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
        openParams = new HashMap<>();
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "assetcardSelect":
                openParams.clear();
                if (paramItemno == null) {
                    paramItemno = new ArrayList<>();
                } else {
                    paramItemno.clear();
                }
                paramItemno.add(currentDetail.getAssetItem().getItemno());
                openParams.put("itemno", paramItemno);
                if (paramUsed == null) {
                    paramUsed = new ArrayList<>();
                } else {
                    paramUsed.clear();
                }
                paramUsed.add("1");
                openParams.put("used", paramUsed);
                if (openOptions == null) {
                    openOptions = new HashMap();
                    openOptions.put("modal", true);
                    openOptions.put("contentWidth", "900");
                }
                super.openDialog("assetcardSelect", openOptions, openParams);
                break;
            case "warehouseSelect":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("0");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            case "assetposition1Select":
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add("0");//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition2Select":
                if (currentDetail == null || currentDetail.getPosition1() == null) {
                    showWarnMsg("Warn", "请先选择公司位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition1().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition3Select":
                if (currentDetail == null || currentDetail.getPosition2() == null) {
                    showWarnMsg("Warn", "请先选择厂区位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition2().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition4Select":
                if (currentDetail == null || currentDetail.getPosition3() == null) {
                    showWarnMsg("Warn", "请先选择厂房位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition3().getId().toString());//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
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

}
