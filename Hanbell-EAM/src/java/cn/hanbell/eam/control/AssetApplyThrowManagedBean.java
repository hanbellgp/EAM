/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetApplyThrowBean;
import cn.hanbell.eam.entity.AssetApplyThrow;
import cn.hanbell.eam.lazy.AssetApplyThrowModel;
import cn.hanbell.eam.web.SuperSingleBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetApplyThrowManagedBean")
@SessionScoped
public class AssetApplyThrowManagedBean extends SuperSingleBean<AssetApplyThrow> {

    @EJB
    protected AssetApplyThrowBean assetApplyThrowBean;

    private boolean doThrow;
    private boolean doPurchase;

    public AssetApplyThrowManagedBean() {
        super(AssetApplyThrow.class);
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            //if (currentEntity.getPurqty().compareTo(currentEntity.getQty()) == 1) {
            //    showErrorMsg("Error", "请购数量不能大于申请数量");
            //    return false;
            //}
            if (currentEntity.getPurqty().compareTo(BigDecimal.ZERO) == -1) {
                showErrorMsg("Error", "请购数量不能小于零");
                return false;
            }
            if (currentEntity.getDisqty().compareTo(currentEntity.getQty()) == 1) {
                showErrorMsg("Error", "领用数量不能大于申请数量");
                return false;
            }
            if (currentEntity.getDisqty().compareTo(BigDecimal.ZERO) != 1) {
                showErrorMsg("Error", "领用数量必须大于零");
                return false;
            }
            return true;
        }
        return false;
    }

    public void handleDialogReturnRequireDeptWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setRequireDeptno(d.getDeptno());
            currentEntity.setRequireDeptname(d.getDept());
        }
    }

    public void handleDialogReturnRequireUserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setRequireUserno(u.getUserid());
            currentEntity.setRequireUsername(u.getUsername());
        }
    }

    @Override
    public void init() {
        superEJB = assetApplyThrowBean;
        model = new AssetApplyThrowModel(assetApplyThrowBean, userManagedBean);
        model.getFilterFields().put("distributed", false);
        model.getSortFields().put("assetApply.formid", "DESC");
        super.init();
    }

    public void initAssetAdjust() {
        if (currentEntity == null) {
            showWarnMsg("Warn", "没有可抛转明细");
        }
        try {
            if (!doBeforeUpdate()) {
                return;
            }
            String formid = assetApplyThrowBean.initAssetAdjust(currentEntity, userManagedBean.getCurrentUser());
            setToolBar();
            showInfoMsg("Info", "成功产生异动单" + formid);
        } catch (Exception ex) {
            showErrorMsg("Error", ex.getMessage());
        }
    }

    public void initAssetDistribute() {
        if (currentEntity == null) {
            showWarnMsg("Warn", "没有可抛转明细");
        }
        try {
            if (!doBeforeUpdate()) {
                return;
            }
            String formid = assetApplyThrowBean.initAssetDistribute(currentEntity, userManagedBean.getCurrentUser());
            setToolBar();
            showInfoMsg("Info", "成功产生领用单" + formid);
        } catch (Exception ex) {
            showErrorMsg("Error", ex.getMessage());
        }
    }

    public void initAssetPurchase() {
        showWarnMsg("Warn", "暂不支持抛转请购");
    }

    @Override
    public void query() {
        if (this.model != null && this.model.getFilterFields() != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("assetApply.formid", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                this.model.getFilterFields().put("assetApply.applyDeptno", queryName);
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("assetApply.formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("assetApply.formdateEnd", queryDateEnd);
            }
        }
    }

    @Override
    protected void setToolBar() {
        if (currentEntity != null && getCurrentPrgGrant() != null && currentEntity.getStatus() != null) {
            switch (currentEntity.getStatus()) {
                case "10":
                    this.doEdit = getCurrentPrgGrant().getDoedit() && true;
                    this.doDel = false;
                    this.doCfm = false;
                    this.doUnCfm = false;
                    this.doPurchase = !currentEntity.getPurchased() && !currentEntity.getDistributed() && true;
                    this.doThrow = !currentEntity.getDistributed() && true;
                    break;
                case "20":
                case "30":
                    this.doEdit = false;
                    this.doDel = false;
                    this.doCfm = false;
                    this.doUnCfm = false;
                    this.doPurchase = false;
                    this.doThrow = !currentEntity.getDistributed() && true;
                    break;
                default:
                    this.doEdit = false;
                    this.doDel = false;
                    this.doCfm = false;
                    this.doUnCfm = false;
                    this.doThrow = false;
                    this.doPurchase = false;
            }
        } else {
            this.doEdit = false;
            this.doDel = false;
            this.doCfm = false;
            this.doUnCfm = false;
            this.doThrow = false;
            this.doPurchase = false;
        }
    }

    /**
     * @return the doThrow
     */
    public boolean isDoThrow() {
        return doThrow;
    }

    /**
     * @param doThrow the doThrow to set
     */
    public void setDoThrow(boolean doThrow) {
        this.doThrow = doThrow;
    }

    /**
     * @return the doPurchase
     */
    public boolean isDoPurchase() {
        return doPurchase;
    }

    /**
     * @param doPurchase the doPurchase to set
     */
    public void setDoPurchase(boolean doPurchase) {
        this.doPurchase = doPurchase;
    }

}
