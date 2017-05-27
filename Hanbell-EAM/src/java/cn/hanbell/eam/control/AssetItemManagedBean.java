/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetItemBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.lazy.AssetItemModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C0160
 */
@ManagedBean(name = "assetItemManagedBean")
@SessionScoped
public class AssetItemManagedBean extends SuperSingleBean<AssetItem> {

    @EJB
    private AssetItemBean assetItemBean;

    public AssetItemManagedBean() {
        super(AssetItem.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setProptype("3");
        newEntity.setUnittype("1");
        newEntity.setQcpass(false);
        newEntity.setUnitexch(BigDecimal.ONE);
        newEntity.setInvtype(true);
        newEntity.setBbstype("000");
        newEntity.setPurmax(BigDecimal.ZERO);
        newEntity.setPurmin(BigDecimal.ZERO);
        newEntity.setInvmax(BigDecimal.ZERO);
        newEntity.setInvmin(BigDecimal.ZERO);
    }

    @Override
    protected boolean doBeforeDelete(AssetItem entity) throws Exception {
        if (super.doBeforeDelete(entity)) {
            if (!assetItemBean.allowDelete(entity.getItemno())) {
                showErrorMsg("Error", "已有交易记录不可删除");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (super.doBeforePersist()) {
            if (newEntity.getCategory() == null) {
                showErrorMsg("Error", "请选择分类");
                return false;
            }
            if (newEntity.getItemno() == null || "".equals(newEntity.getItemno())) {
                showErrorMsg("Error", "请输入品号");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            if (currentEntity.getCategory() == null) {
                showErrorMsg("Error", "请选择分类");
                return false;
            }
            if (currentEntity.getItemno() == null || "".equals(newEntity.getItemno())) {
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
            AssetCategory e = (AssetCategory) event.getObject();
            currentEntity.setCategory(e);
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            newEntity.setCategory(e);
        }
    }

    public void handleDialogReturnUnitWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            //Unit e =(Unit)event.getObject();
            //currentEntity.setUnit(fileName);
        }
    }

    public void handleDialogReturnUnitWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            //Unit e =(Unit)event.getObject();
            //newEntity.setUnit(fileName);
        }
    }

    @Override
    public void init() {
        superEJB = assetItemBean;
        model = new AssetItemModel(assetItemBean);
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("itemno", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("itemdesc", queryName);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
        }
    }

}
