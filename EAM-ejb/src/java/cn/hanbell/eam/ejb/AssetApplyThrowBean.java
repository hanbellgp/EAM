/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetAdjust;
import cn.hanbell.eam.entity.AssetAdjustDetail;
import cn.hanbell.eam.entity.AssetApplyThrow;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import com.lightshell.comm.SuperEJB;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetApplyThrowBean extends SuperEJBForEAM<AssetApplyThrow> {

    @EJB
    private AssetAdjustBean assetAdjustBean;
    @EJB
    private AssetAdjustDetailBean assetAdjustDetailBean;

    @EJB
    private AssetDistributeBean assetDistributeBean;
    @EJB
    private AssetDistributeDetailBean assetDistributeDetailBean;

    @EJB
    private TransactionTypeBean transactionTypeBean;

    public AssetApplyThrowBean() {
        super(AssetApplyThrow.class);
    }

    public String initAssetAdjust(AssetApplyThrow entity, SystemUser user) throws Exception {

        Date day = BaseLib.getDate();
        String formid = assetAdjustBean.getFormId(day);
        if (formid == null || "".equals(formid)) {
            throw new RuntimeException("产生异动单单号失败");
        }
        //资产异动
        TransactionType AIC = transactionTypeBean.findByTrtype("AIC");
        if (AIC == null) {
            throw new RuntimeException("交易类别设置错误");
        }

        List<AssetAdjustDetail> addedDetail = new ArrayList();
        HashMap<SuperEJB, List<?>> detailAdded = new HashMap();
        detailAdded.put(assetAdjustDetailBean, addedDetail);

        AssetAdjust aa = new AssetAdjust();
        aa.setCompany(entity.getAssetApply().getCompany());
        aa.setFormid(formid);
        aa.setFormdate(day);
        aa.setDeptno(entity.getAssetApply().getApplyDeptno());
        aa.setDeptname(entity.getAssetApply().getApplyDeptname());
        aa.setRemark(entity.getRemark());
        aa.setStatusToNew();
        aa.setCreator(user.getUsername());
        aa.setCredateToNow();

        AssetAdjustDetail aad = new AssetAdjustDetail();
        aad.setPid(formid);
        aad.setSeq(1);
        aad.setTrtype(AIC);
        aad.setAssetItem(entity.getAssetItem());
        aad.setBrand(entity.getBrand());
        aad.setBatch(entity.getBatch());
        aad.setSn(entity.getSn());
        aad.setQty(entity.getDisqty());
        aad.setUnit(entity.getUnit());
        aad.setDeptno2(entity.getRequireDeptno());
        aad.setDeptname2(entity.getRequireDeptname());
        aad.setUserno2(entity.getRequireUserno());
        aad.setUsername2(entity.getRequireUsername());
        aad.setWarehouse2(null);
        aad.setSrcapi("assetapply");
        aad.setSrcformid(formid);
        aad.setSrcseq(1);
        addedDetail.add(aad);

        entity.setDistributed(Boolean.TRUE);
        entity.setRelapi("assetadjust");
        entity.setRelformid(formid);
        entity.setRelseq(1);
        try {
            assetAdjustBean.persist(aa, detailAdded, null, null);
            update(entity);
            return formid;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public String initAssetDistribute(AssetApplyThrow entity, SystemUser user) throws Exception {

        Date day = BaseLib.getDate();
        String formid = assetDistributeBean.getFormId(day);
        if (formid == null || "".equals(formid)) {
            throw new RuntimeException("产生领用单单号失败");
        }
        List<AssetDistributeDetail> addedDetail = new ArrayList();
        HashMap<SuperEJB, List<?>> detailAdded = new HashMap();
        detailAdded.put(assetDistributeDetailBean, addedDetail);

        AssetDistribute ad = new AssetDistribute();
        ad.setCompany(entity.getAssetApply().getCompany());
        ad.setFormid(formid);
        ad.setFormdate(day);
        ad.setDeptno(entity.getAssetApply().getApplyDeptno());
        ad.setDeptname(entity.getAssetApply().getApplyDeptname());
        ad.setRemark(entity.getRemark());
        ad.setStatusToNew();
        ad.setCreator(user.getUsername());
        ad.setCredateToNow();

        AssetDistributeDetail add = new AssetDistributeDetail();
        add.setPid(formid);
        add.setSeq(1);
        add.setAssetItem(entity.getAssetItem());
        add.setBrand(entity.getBrand());
        add.setBatch(entity.getBatch());
        add.setSn(entity.getSn());
        add.setQty(entity.getDisqty());
        add.setUnit(entity.getUnit());
        add.setDeptno(entity.getRequireDeptno());
        add.setDeptname(entity.getRequireDeptname());
        add.setUserno(entity.getRequireUserno());
        add.setUsername(entity.getRequireUsername());
        add.setWarehouse(null);
        add.setSrcapi("assetapply");
        add.setSrcformid(formid);
        add.setSrcseq(1);
        addedDetail.add(add);

        entity.setDistributed(Boolean.TRUE);
        entity.setRelapi("assetdistribute");
        entity.setRelformid(formid);
        entity.setRelseq(1);
        try {
            assetDistributeBean.persist(ad, detailAdded, null, null);
            update(entity);
            return formid;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

}
