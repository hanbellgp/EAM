/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCheckBean;
import cn.hanbell.eam.ejb.AssetCheckDetailBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCheck;
import cn.hanbell.eam.entity.AssetCheckDetail;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.lazy.AssetCheckModel;
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
@ManagedBean(name = "assetCheckManagedBean")
@SessionScoped
public class AssetCheckManagedBean extends FormMultiBean<AssetCheck, AssetCheckDetail> {

    @EJB
    protected AssetCheckDetailBean assetCheckDetailBean;

    @EJB
    protected AssetCheckBean assetCheckBean;

    /**
     * Creates a new instance of AssetCheckManagedBean
     */
    public AssetCheckManagedBean() {
        super(AssetCheck.class, AssetCheckDetail.class);
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail != null) {
            currentDetail.setDiffqty(currentDetail.getActqty().subtract(currentDetail.getQty()));
        }
        super.doConfirmDetail();
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
            currentDetail.setAssetItem(e.getAssetItem());
            currentDetail.setQty(e.getQty());
            currentDetail.setActqty(e.getQty());
            currentDetail.setDiffqty(BigDecimal.ZERO);
            currentDetail.setUnit(e.getUnit());
            currentDetail.setDeptno(e.getDeptno());
            currentDetail.setDeptname(e.getDeptname());
            currentDetail.setUserno(e.getUserno());
            currentDetail.setUsername(e.getUsername());
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

    @Override
    public void init() {
        superEJB = assetCheckBean;
        detailEJB = assetCheckDetailBean;
        model = new AssetCheckModel(assetCheckBean, userManagedBean);
        super.init();
    }

}
