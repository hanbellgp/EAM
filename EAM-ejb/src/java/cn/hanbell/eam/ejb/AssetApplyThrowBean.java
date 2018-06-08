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
        if (aa.getRemark() == null) {
            aa.setRemark(entity.getRequireDeptname() + "_" + entity.getRequireUsername());
        }
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
        aad.setWarehouse2(entity.getAssetItem().getCategory().getWarehouse2());
        aad.setSrcapi("assetapply");
        aad.setSrcformid(entity.getAssetApply().getFormid());
        aad.setSrcseq(entity.getSeq());
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

    public String initAssetAdjust(List<AssetApplyThrow> entities, SystemUser user) throws Exception {

        if (entities == null || entities.isEmpty()) {
            throw new NullPointerException();
        }

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

        List<AssetApplyThrow> detailList = new ArrayList();

        List<AssetAdjustDetail> addedAAD = new ArrayList();
        HashMap<SuperEJB, List<?>> aadAdded = new HashMap();
        aadAdded.put(assetAdjustDetailBean, addedAAD);

        AssetApplyThrow e = entities.get(0);

        AssetAdjust aa = new AssetAdjust();
        aa.setCompany(e.getAssetApply().getCompany());
        aa.setFormid(formid);
        aa.setFormdate(day);
        aa.setDeptno(e.getAssetApply().getApplyDeptno());
        aa.setDeptname(e.getAssetApply().getApplyDeptname());
        aa.setRemark(e.getRemark());
        aa.setStatusToNew();
        aa.setCreator(user.getUsername());
        aa.setCredateToNow();

        int seq = 0;
        for (AssetApplyThrow entity : entities) {
            seq++;
            AssetAdjustDetail aad = new AssetAdjustDetail();
            aad.setPid(formid);
            aad.setSeq(seq);
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
            aad.setWarehouse2(entity.getAssetItem().getCategory().getWarehouse2());
            aad.setSrcapi("assetapply");
            aad.setSrcformid(entity.getAssetApply().getFormid());
            aad.setSrcseq(entity.getSeq());

            addedAAD.add(aad);

            entity.setDistributed(Boolean.TRUE);
            entity.setRelapi("assetadjust");
            entity.setRelformid(formid);
            entity.setRelseq(seq);

            detailList.add(entity);

            if (aa.getRemark() == null) {
                aa.setRemark(entity.getRequireDeptname() + "_" + entity.getRequireUsername());
            }
        }

        try {
            assetAdjustBean.persist(aa, aadAdded, null, null);
            update(detailList);
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
        //ad.setDeptno(entity.getAssetApply().getApplyDeptno());
        //ad.setDeptname(entity.getAssetApply().getApplyDeptname());
        //领用部门改成需求部门更加符合预算管控
        ad.setDeptno(entity.getAssetApply().getRequireDeptno());
        ad.setDeptname(entity.getAssetApply().getRequireDeptname());
        ad.setRemark(entity.getRemark());
        if (ad.getRemark() == null) {
            ad.setRemark(entity.getRequireDeptname() + "_" + entity.getRequireUsername());
        }
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
        add.setWarehouse(entity.getAssetItem().getCategory().getWarehouse());
        add.setWarehouse2(entity.getAssetItem().getCategory().getWarehouse2());
        add.setSrcapi("assetapply");
        add.setSrcformid(entity.getAssetApply().getFormid());
        add.setSrcseq(entity.getSeq());
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

    public String initAssetDistribute(List<AssetApplyThrow> entities, SystemUser user) throws Exception {

        if (entities == null || entities.isEmpty()) {
            throw new NullPointerException();
        }

        Date day = BaseLib.getDate();
        String formid = assetDistributeBean.getFormId(day);
        if (formid == null || "".equals(formid)) {
            throw new RuntimeException("产生领用单单号失败");
        }

        List<AssetApplyThrow> detailList = new ArrayList();

        List<AssetDistributeDetail> addedADD = new ArrayList();
        HashMap<SuperEJB, List<?>> addAdded = new HashMap();
        addAdded.put(assetDistributeDetailBean, addedADD);

        AssetApplyThrow e = entities.get(0);

        AssetDistribute ad = new AssetDistribute();
        ad.setCompany(e.getAssetApply().getCompany());
        ad.setFormid(formid);
        ad.setFormdate(day);
        //领用部门改成需求部门更加符合预算管控
        ad.setDeptno(e.getAssetApply().getRequireDeptno());
        ad.setDeptname(e.getAssetApply().getRequireDeptname());
        ad.setRemark(e.getRemark());
        ad.setStatusToNew();
        ad.setCreator(user.getUsername());
        ad.setCredateToNow();

        int seq = 0;
        for (AssetApplyThrow entity : entities) {
            seq++;
            AssetDistributeDetail add = new AssetDistributeDetail();
            add.setPid(formid);
            add.setSeq(seq);
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
            add.setWarehouse(entity.getAssetItem().getCategory().getWarehouse());
            add.setWarehouse2(entity.getAssetItem().getCategory().getWarehouse2());
            add.setSrcapi("assetapply");
            add.setSrcformid(entity.getAssetApply().getFormid());
            add.setSrcseq(entity.getSeq());

            addedADD.add(add);

            entity.setDistributed(Boolean.TRUE);
            entity.setRelapi("assetdistribute");
            entity.setRelformid(formid);
            entity.setRelseq(seq);

            detailList.add(entity);

            if (ad.getRemark() == null) {
                ad.setRemark(entity.getRequireDeptname() + "_" + entity.getRequireUsername());
            }
        }

        try {
            assetDistributeBean.persist(ad, addAdded, null, null);
            update(detailList);
            return formid;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

}
