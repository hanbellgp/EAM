/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetCardModel;
import cn.hanbell.eam.web.FormSingleBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCardManagedBean")
@SessionScoped
public class AssetCardManagedBean extends FormSingleBean<AssetCard> {

    @EJB
    private AssetCardBean assetCardBean;

    private String queryDeptno;
    private String queryDeptname;
    private String queryUserno;
    private String queryUsername;
    private String queryWarehouseno;

    public AssetCardManagedBean() {
        super(AssetCard.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    protected boolean doBeforeDelete(AssetCard entity) throws Exception {
        if (entity.getUsed()) {
            showErrorMsg("Errro", "已在使用不可删除");
            return false;
        }
        return super.doBeforeDelete(entity);
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity == null) {
            showWarnMsg("Warn", "没有可新增的对象");
            return false;
        }
        if (newEntity.getAssetItem() == null) {
            showErrorMsg("Error", "请输入品号");
            return false;
        }
        if (newEntity.getWarehouse() == null) {
            showErrorMsg("Error", "请输入仓库");
            return false;
        }
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            String formid = assetCardBean.getFormId(newEntity.getCompany(), newEntity.getFormdate(), newEntity.getAssetItem());
            this.newEntity.setFormid(formid);
        }
        return true;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            if (currentEntity.getAssetItem() == null) {
                showErrorMsg("Error", "请输入品号");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentEntity.setAssetItem(e);
            currentEntity.setAssetDesc(e.getItemdesc());
            currentEntity.setAssetSpec(e.getItemspec());
            currentEntity.setUnit(e.getUnit());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    public void handleDialogReturnDeptWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno(d.getDeptno());
            currentEntity.setDeptname(d.getDept());
        }
    }

    public void handleDialogReturnPosition1WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition1(e);
        }
    }

    public void handleDialogReturnPosition2WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition2(e);
        }
    }

    public void handleDialogReturnPosition3WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition3(e);
        }
    }

    public void handleDialogReturnPosition4WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition4(e);
        }
    }

    public void handleDialogReturnUserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setUserno(u.getUserid());
            currentEntity.setUsername(u.getUsername());
        }
    }

    public void handleDialogReturnWarehouseWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentEntity.setWarehouse(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetCardBean;
        model = new AssetCardModel(assetCardBean, userManagedBean);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("formid", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("assetDesc", this.queryName);
            }
            if (this.queryDeptno != null && !"".equals(this.queryDeptno)) {
                this.model.getFilterFields().put("deptno", this.queryDeptno);
            }
            if (this.queryDeptname != null && !"".equals(this.queryDeptname)) {
                this.model.getFilterFields().put("deptname", this.queryDeptname);
            }
            if (this.queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("userno", this.queryUserno);
            }
            if (this.queryUsername != null && !"".equals(this.queryUsername)) {
                this.model.getFilterFields().put("username", this.queryUsername);
            }
            if (this.queryWarehouseno != null && !"".equals(this.queryWarehouseno)) {
                this.model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouseno);
            }
            if (queryState != null && "N".equals(queryState)) {
                this.model.getFilterFields().put("used", false);
            } else if (queryState != null && "V".equals(queryState)) {
                this.model.getFilterFields().put("used", true);
            }
        }
    }

    /**
     * @return the queryDeptno
     */
    public String getQueryDeptno() {
        return queryDeptno;
    }

    /**
     * @param queryDeptno the queryDeptno to set
     */
    public void setQueryDeptno(String queryDeptno) {
        this.queryDeptno = queryDeptno;
    }

    /**
     * @return the queryDeptname
     */
    public String getQueryDeptname() {
        return queryDeptname;
    }

    /**
     * @param queryDeptname the queryDeptname to set
     */
    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

    /**
     * @return the queryUserno
     */
    public String getQueryUserno() {
        return queryUserno;
    }

    /**
     * @param queryUserno the queryUserno to set
     */
    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

    /**
     * @return the queryUsername
     */
    public String getQueryUsername() {
        return queryUsername;
    }

    /**
     * @param queryUsername the queryUsername to set
     */
    public void setQueryUsername(String queryUsername) {
        this.queryUsername = queryUsername;
    }

    /**
     * @return the queryWarehouseno
     */
    public String getQueryWarehouseno() {
        return queryWarehouseno;
    }

    /**
     * @param queryWarehouseno the queryWarehouseno to set
     */
    public void setQueryWarehouseno(String queryWarehouseno) {
        this.queryWarehouseno = queryWarehouseno;
    }

}
