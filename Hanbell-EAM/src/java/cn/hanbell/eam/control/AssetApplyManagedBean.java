/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetApplyBean;
import cn.hanbell.eam.ejb.AssetApplyDetailBean;
import cn.hanbell.eam.entity.AssetApply;
import cn.hanbell.eam.entity.AssetApplyDetail;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.lazy.AssetApplyModel;
import cn.hanbell.eam.web.FormMultiBean;
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
@ManagedBean(name = "assetApplyManagedBean")
@SessionScoped
public class AssetApplyManagedBean extends FormMultiBean<AssetApply, AssetApplyDetail> {

    @EJB
    private AssetApplyBean assetApplyBean;

    @EJB
    private AssetApplyDetailBean assetApplyDetailBean;

    public AssetApplyManagedBean() {
        super(AssetApply.class, AssetApplyDetail.class);
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
        currentDetail.setRequireDeptno(currentEntity.getRequireDeptno());
        currentDetail.setRequireDeptname(currentEntity.getRequireDeptname());
        currentDetail.setRequireUserno(currentEntity.getRequireUserno());
        currentDetail.setRequireUsername(currentEntity.getRequireUsername());
        currentDetail.setCurrency("CNY");
        currentDetail.setExchange(BigDecimal.ONE);
        currentDetail.setStatus("10");
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (super.doBeforeUnverify()) {
            for (AssetApplyDetail aad : detailList) {
                if (aad.getDistributed()) {
                    showErrorMsg("Error", aad.getAssetItem().getItemno() + "已领用");
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
            showErrorMsg("Error", "没有明细对象");
            return;
        }
        if (currentDetail.getRequireDeptno() == null) {
            showErrorMsg("Error", "请输入需求部门");
            return;
        }
        if (currentDetail.getRequireUserno() == null) {
            showErrorMsg("Error", "请输入需求人");
            return;
        }
        if (currentDetail.getAssetItem() == null) {
            showErrorMsg("Error", "请输入件号");
            return;
        }
        super.doConfirmDetail();
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setApplyDeptno(d.getDeptno());
            currentEntity.setApplyDeptname(d.getDept());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    public void handleDialogReturnApplyUserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setApplyUserno(u.getUserid());
            currentEntity.setApplyUsername(u.getUsername());
        }
    }

    public void handleDialogReturnApplyUserWhenNew(SelectEvent event) {
        handleDialogReturnApplyUserWhenEdit(event);
    }

    public void handleDialogReturnRequireDeptWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setRequireDeptno(d.getDeptno());
            currentEntity.setRequireDeptname(d.getDept());
        }
    }

    public void handleDialogReturnRequireDeptWhenNew(SelectEvent event) {
        handleDialogReturnRequireDeptWhenEdit(event);
    }

    public void handleDialogReturnRequireUserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setRequireUserno(u.getUserid());
            currentEntity.setRequireUsername(u.getUsername());
        }
    }

    public void handleDialogReturnRequireUserWhenNew(SelectEvent event) {
        handleDialogReturnRequireUserWhenEdit(event);
    }

    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setItemdesc(e.getItemdesc());
            currentDetail.setItemspec(e.getItemspec());
            currentDetail.setUnit(e.getUnit());
        }
    }

    @Override
    public void handleDialogReturnWhenDetailNew(SelectEvent event) {
        handleDialogReturnWhenDetailEdit(event);
    }

    public void handleDialogReturnRequireDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Department d = (Department) event.getObject();
            currentDetail.setRequireDeptno(d.getDeptno());
            currentDetail.setRequireDeptname(d.getDept());
        }
    }

    public void handleDialogReturnRequireDeptWhenDetailNew(SelectEvent event) {
        handleDialogReturnRequireDeptWhenDetailEdit(event);
    }

    public void handleDialogReturnRequireUserWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail.setRequireUserno(u.getUserid());
            currentDetail.setRequireUsername(u.getUsername());
        }
    }

    public void handleDialogReturnRequireUserWhenDetailNew(SelectEvent event) {
        handleDialogReturnRequireUserWhenDetailEdit(event);
    }

    @Override
    public void init() {
        superEJB = assetApplyBean;
        detailEJB = assetApplyDetailBean;
        model = new AssetApplyModel(assetApplyBean, userManagedBean);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
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
