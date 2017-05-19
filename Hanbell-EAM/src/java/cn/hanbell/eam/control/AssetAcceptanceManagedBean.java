/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetAcceptanceBean;
import cn.hanbell.eam.ejb.AssetAcceptanceDetailBean;
import cn.hanbell.eam.entity.AssetAcceptance;
import cn.hanbell.eam.entity.AssetAcceptanceDetail;
import cn.hanbell.eam.lazy.AssetAcceptanceModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.Department;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetAcceptanceManagedBean")
@SessionScoped
public class AssetAcceptanceManagedBean extends FormMultiBean<AssetAcceptance, AssetAcceptanceDetail> {
    
    @EJB
    private AssetAcceptanceBean assetAcceptanceBean;
    
    @EJB
    private AssetAcceptanceDetailBean assetAcceptanceDetailBean;
    
    public AssetAcceptanceManagedBean() {
        super(AssetAcceptance.class, AssetAcceptanceDetail.class);
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
        currentDetail.setAcceptdate(getDate());
        currentDetail.setAcceptUserno(userManagedBean.getUserid());
        currentDetail.setCurrency("CNY");
        currentDetail.setExchange(BigDecimal.ONE);
        currentDetail.setTaxtype("0");
        currentDetail.setTaxrate(BigDecimal.valueOf(0.17));
        currentDetail.setStatus("40");
    }
    
    @Override
    public void doConfirmDetail() {
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
    public void init() {
        superEJB = assetAcceptanceBean;
        detailEJB = assetAcceptanceDetailBean;
        model = new AssetAcceptanceModel(assetAcceptanceBean, userManagedBean);
        super.init();
    }
    
}
