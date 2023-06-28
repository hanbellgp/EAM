/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetAdjustBean;
import cn.hanbell.eam.ejb.AssetAdjustDetailBean;
import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.TransactionTypeBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetAdjust;
import cn.hanbell.eam.entity.AssetAdjustDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetAdjustModel;
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
@ManagedBean(name = "assetAdjustManagedBean")
@SessionScoped
public class AssetAdjustManagedBean extends FormMultiBean<AssetAdjust, AssetAdjustDetail> {

    @EJB
    private AssetAdjustBean assetAdjustBean;
    @EJB
    private AssetAdjustDetailBean assetAdjustDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;
    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private TransactionTypeBean transactoinTypeBean;

    private TransactionType trtype;

    private List<String> paramPosition = null;
    private List<String> paramUsed = null;
    private List<String> paramPause = null;
    private List<String> paramHascost = null;
    private String queryAssetno;

    public AssetAdjustManagedBean() {
        super(AssetAdjust.class, AssetAdjustDetail.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    public void createDetail() {
        super.createDetail();
        currentDetail.setTrtype(trtype);
    }

    public void createDetailNew() {
        createDetail();
        currentDetail.setDeptno2(newEntity.getDeptno2());
        currentDetail.setDeptname2(newEntity.getDeptname2());
        currentDetail.setUserno2(newEntity.getUserno2());
        currentDetail.setUsername2(newEntity.getUsername2());

    }

    public void createDetailEdit() {
        createDetail();
        currentDetail.setDeptno2(currentEntity.getDeptno2());
        currentDetail.setDeptname2(currentEntity.getDeptname2());
        currentDetail.setUserno2(currentEntity.getUserno2());
        currentDetail.setUsername2(currentEntity.getUsername2());
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (super.doBeforePersist()) {
            for (AssetAdjustDetail add : detailList) {
                if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "需要输入编号");
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
            for (AssetAdjustDetail add : detailList) {
                if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "需要输入编号");
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
            AssetCard ac;
            for (AssetAdjustDetail aad : detailList) {
                ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), aad.getAssetItem().getItemno(), "", "", "", aad.getWarehouse2().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(aad.getQty()) == -1) {
                    showErrorMsg("Error", aad.getAssetItem().getItemno() + "库存可还原量不足");
                    return false;
                }
                if (aad.getAssetCard() != null) {
                    if (aad.getAssetItem().getCategory().getNoauto()) {
                        ac = assetCardBean.findByFilters(currentEntity.getCompany(), aad.getAssetno(), aad.getAssetItem().getItemno(), aad.getDeptno2(), aad.getUserno2());
                        if ((ac == null) || ac.getQty().compareTo(aad.getQty()) == -1) {
                            showErrorMsg("Error", aad.getAssetno() + "不存在或可还原量不足");
                            return false;
                        }
                    } else {
                        //需要处理刀工量仪的逻辑
                        String assetno = aad.getPid() + "-" + assetCardBean.formatString(String.valueOf(aad.getSeq()), "0000");
                        ac = assetCardBean.findByFilters(currentEntity.getCompany(), assetno, aad.getAssetItem().getItemno(), aad.getDeptno2(), aad.getUserno2());
                        if ((ac == null) || ac.getQty().compareTo(aad.getQty()) == -1) {
                            showErrorMsg("Error", assetno + "不存在或可还原量不足");
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (super.doBeforeVerify()) {
            AssetInventory ai;
            AssetCard ac;
            for (AssetAdjustDetail aad : detailList) {
                ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), aad.getAssetItem().getItemno(), "", "", "", aad.getWarehouse().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(aad.getQty()) == -1) {
                    showErrorMsg("Error", aad.getAssetItem().getItemno() + "库存可利用量不足");
                    return false;
                }
                if (aad.getAssetCard() != null) {
                    ac = assetCardBean.findByAssetno(aad.getAssetno());
                    if ((ac == null) || ac.getQty().compareTo(aad.getQty()) == -1) {
                        showErrorMsg("Error", aad.getAssetno() + "不存在或可利用量不足");
                        return false;
                    }
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
        if (currentDetail.getDeptno2() == null || "".equals(currentDetail.getDeptno2())) {
            showErrorMsg("Error", "请输入领用部门");
            return;
        }
        if (currentDetail.getUserno2() == null || "".equals(currentDetail.getUserno2())) {
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
        if (currentDetail.getWarehouse2() == null) {
            showErrorMsg("Error", "请输入目的仓");
            return;
        }
        if (currentDetail.getQty().compareTo(BigDecimal.ZERO) != 1) {
            showErrorMsg("Error", "请输入数量");
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

    public void handleDialogReturnDeptWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno2(d.getDeptno());
            currentEntity.setDeptname2(d.getDept());
        }
    }

    public void handleDialogReturnDeptWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Department d = (Department) event.getObject();
            newEntity.setDeptno2(d.getDeptno());
            newEntity.setDeptname2(d.getDept());
        }
    }

    public void handleDialogReturnUserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setUserno2(u.getUserid());
            currentEntity.setUsername2(u.getUsername());
            currentEntity.setDeptno2(u.getDeptno());
            currentEntity.setDeptname2(u.getDept().getDept());

        }
    }

    public void handleDialogReturnUserWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            newEntity.setUserno2(u.getUserid());
            newEntity.setUsername2(u.getUsername());
            newEntity.setDeptno2(u.getDeptno());
            newEntity.setDeptname2(u.getDept().getDept());
        }
    }

    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetCard e = (AssetCard) event.getObject();
            if (!isExist(e)) {
                currentDetail.setAssetCard(e);
                currentDetail.setAssetno(e.getFormid());
                currentDetail.setAssetItem(e.getAssetItem());
                currentDetail.setDeptno(e.getDeptno());
                currentDetail.setDeptname(e.getDeptname());
                currentDetail.setUserno(e.getUserno());
                currentDetail.setUsername(e.getUsername());
                currentDetail.setUnit(e.getUnit());
                currentDetail.setQty(e.getQty());
                currentDetail.setPosition1(e.getPosition1());
                currentDetail.setPosition2(e.getPosition2());
                currentDetail.setPosition3(e.getPosition3());
                currentDetail.setPosition4(e.getPosition4());
                currentDetail.setWarehouse(e.getWarehouse());
                currentDetail.setWarehouse2(e.getWarehouse());
                currentDetail.setPosition12(e.getPosition1());
                currentDetail.setPosition22(e.getPosition2());
                currentDetail.setPosition32(e.getPosition3());
                currentDetail.setPosition42(e.getPosition4());
            } else {
                showErrorMsg("Error", "异动明细已存在");
            }
        }
    }

    public boolean isExist(AssetCard e) {
        boolean aa = false;
        for (AssetAdjustDetail ad : detailList) {
            if (e.getFormid().equals(ad.getAssetno())) {
                aa = true;
                break;
            }
        }
        return aa;
    }

    public void handleDialogReturnWhenDetailAllNew(SelectEvent event) {
        if (newEntity.getDeptno2() == null || "".equals(newEntity.getDeptno2())) {
            showErrorMsg("Error", "请输入领用部门");
            return;
        }
        if (newEntity.getUserno2() == null || "".equals(newEntity.getUserno2())) {
            showErrorMsg("Error", "请输入变更人");
            return;
        }
        if (event.getObject() != null) {
            List<AssetCard> cardList = (List<AssetCard>) event.getObject();
            for (AssetCard e : cardList) {
                if (!isExist(e)) {
                    this.createDetail();
                    currentDetail.setAssetCard(e);
                    currentDetail.setAssetno(e.getFormid());
                    currentDetail.setAssetItem(e.getAssetItem());
                    currentDetail.setDeptno(e.getDeptno());
                    currentDetail.setDeptname(e.getDeptname());
                    currentDetail.setUserno(e.getUserno());
                    currentDetail.setUsername(e.getUsername());
                    currentDetail.setUnit(e.getUnit());
                    currentDetail.setQty(e.getQty());
                    currentDetail.setPosition1(e.getPosition1());
                    currentDetail.setPosition2(e.getPosition2());
                    currentDetail.setPosition3(e.getPosition3());
                    currentDetail.setPosition4(e.getPosition4());
                    currentDetail.setWarehouse(e.getWarehouse());
                    currentDetail.setDeptno2(newEntity.getDeptno2());
                    currentDetail.setDeptname2(newEntity.getDeptname2());
                    currentDetail.setUserno2(newEntity.getUserno2());
                    currentDetail.setUsername2(newEntity.getUsername2());
                    currentDetail.setWarehouse2(e.getWarehouse());
                    currentDetail.setPosition12(e.getPosition1());
                    currentDetail.setPosition22(e.getPosition2());
                    currentDetail.setPosition32(e.getPosition3());
                    currentDetail.setPosition42(e.getPosition4());
                    super.doConfirmDetail();
                } else {
                    showErrorMsg("Error", "异动编号为" + e.getFormid() + "不能重复存在,");
                }
            }
        }
    }

    public void handleDialogReturnWhenDetailAllEdit(SelectEvent event) {
        if (currentEntity.getDeptno2() == null || "".equals(currentEntity.getDeptno2())) {
            showErrorMsg("Error", "请输入领用部门");
            return;
        }
        if (currentEntity.getUserno2() == null || "".equals(currentEntity.getUserno2())) {
            showErrorMsg("Error", "请输入变更人");
            return;
        }
        if (event.getObject() != null) {
            List<AssetCard> cardList = (List<AssetCard>) event.getObject();
            for (AssetCard e : cardList) {
                if (!isExist(e)) {
                    this.createDetail();
                    currentDetail.setAssetCard(e);
                    currentDetail.setAssetno(e.getFormid());
                    currentDetail.setAssetItem(e.getAssetItem());
                    currentDetail.setDeptno(e.getDeptno());
                    currentDetail.setDeptname(e.getDeptname());
                    currentDetail.setUserno(e.getUserno());
                    currentDetail.setUsername(e.getUsername());
                    currentDetail.setUnit(e.getUnit());
                    currentDetail.setQty(e.getQty());
                    currentDetail.setPosition1(e.getPosition1());
                    currentDetail.setPosition2(e.getPosition2());
                    currentDetail.setPosition3(e.getPosition3());
                    currentDetail.setPosition4(e.getPosition4());
                    currentDetail.setWarehouse(e.getWarehouse());
                    currentDetail.setDeptno2(currentEntity.getDeptno2());
                    currentDetail.setDeptname2(currentEntity.getDeptname2());
                    currentDetail.setUserno2(currentEntity.getUserno2());
                    currentDetail.setUsername2(currentEntity.getUsername2());
                    currentDetail.setWarehouse2(e.getWarehouse());
                    currentDetail.setPosition12(e.getPosition1());
                    currentDetail.setPosition22(e.getPosition2());
                    currentDetail.setPosition32(e.getPosition3());
                    currentDetail.setPosition42(e.getPosition4());
                    super.doConfirmDetail();
                } else {
                    showErrorMsg("Error", "异动编号为" + e.getFormid() + "不能重复存在,");
                }
            }
        }
    }

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Department d = (Department) event.getObject();
            currentDetail.setDeptno2(d.getDeptno());
            currentDetail.setDeptname2(d.getDept());
        }
    }

    public void handleDialogReturnPosition1WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition12(e);
            currentDetail.setPosition22(null);
            currentDetail.setPosition32(null);
            currentDetail.setPosition42(null);
        }
    }

    public void handleDialogReturnPosition2WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition22(e);
            currentDetail.setPosition32(null);
            currentDetail.setPosition42(null);
        }
    }

    public void handleDialogReturnPosition3WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition32(e);
            currentDetail.setPosition42(null);
        }
    }

    public void handleDialogReturnPosition4WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition42(e);
        }
    }

    public void handleDialogReturnUserWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail.setUserno2(u.getUserid());
            currentDetail.setUsername2(u.getUsername());
            currentDetail.setDeptno2(u.getDeptno());
            currentDetail.setDeptname2(u.getDept().getDept());

        }
    }

    public void handleDialogReturnWarehouseWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse(e);
        }
    }

    public void handleDialogReturnWarehouse2WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse2(e);
        }
    }

    @Override
    public void init() {
        openParams = new HashMap<>();
        superEJB = assetAdjustBean;
        detailEJB = assetAdjustDetailBean;
        model = new AssetAdjustModel(assetAdjustBean, userManagedBean);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        trtype = transactoinTypeBean.findByTrtype("AIC");
        if (trtype == null) {
            showErrorMsg("Error", "AIC异动类别未设置");
        }
        super.init();
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "assetcardSelect":
            case "assetcardMultiSelect":
                openParams.clear();
                if (paramUsed == null) {
                    paramUsed = new ArrayList<>();
                } else {
                    paramUsed.clear();
                }
                paramUsed.add("1");
                openParams.put("used", paramUsed);//只能调拨已领用的卡片
                if (paramPause == null) {
                    paramPause = new ArrayList<>();
                } else {
                    paramPause.clear();
                }
                paramPause.add("0");
                openParams.put("pause", paramPause);//排除报废申请签核中的卡片
                if (openOptions == null) {
                    openOptions = new HashMap();
                    openOptions.put("modal", true);
                    openOptions.put("contentWidth", "1000");
                }
                super.openDialog(view, openOptions, openParams);
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
            case "warehouse2Select":
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
                if (currentDetail == null || currentDetail.getPosition12() == null) {
                    showWarnMsg("Warn", "请先选择公司位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition12().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition3Select":
                if (currentDetail == null || currentDetail.getPosition22() == null) {
                    showWarnMsg("Warn", "请先选择厂区位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition22().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition4Select":
                if (currentDetail == null || currentDetail.getPosition32() == null) {
                    showWarnMsg("Warn", "请先选择厂房位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition32().getId().toString());
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
            if (queryAssetno != null && !"".equals(queryAssetno)) {
                String assetno =assetAdjustDetailBean.getAdjustForimd(queryAssetno);
                this.model.getFilterFields().put("formid", assetno);
            }
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

    @Override
    public void setCurrentEntity(AssetAdjust currentEntity) {
        if (deletedDetailList != null) {
            this.deletedDetailList.clear();
        }
        if (editedDetailList != null) {
            this.editedDetailList.clear();
        }
        if (addedDetailList != null) {
            this.addedDetailList.clear();
        }
        super.setCurrentEntity(currentEntity);
    }

    public String getQueryAssetno() {
        return queryAssetno;
    }

    public void setQueryAssetno(String queryAssetno) {
        this.queryAssetno = queryAssetno;
    }

}
