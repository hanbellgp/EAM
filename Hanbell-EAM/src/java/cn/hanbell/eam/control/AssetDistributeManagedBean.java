/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetDistributeBean;
import cn.hanbell.eam.ejb.AssetDistributeDetailBean;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.lazy.AssetDistributeModel;
import cn.hanbell.eam.web.FormMultiBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetDistributeManagedBean")
@SessionScoped
public class AssetDistributeManagedBean extends FormMultiBean<AssetDistribute, AssetDistributeDetail> {

    @EJB
    private AssetDistributeBean assetDistributeBean;
    @EJB
    private AssetDistributeDetailBean assetDistributeDetailBean;

    public AssetDistributeManagedBean() {
        super(AssetDistribute.class, AssetDistributeDetail.class);
    }

    @Override
    public void init() {
        superEJB = assetDistributeBean;
        detailEJB = assetDistributeDetailBean;
        model = new AssetDistributeModel(assetDistributeBean, userManagedBean);
        super.init();
    }

}
