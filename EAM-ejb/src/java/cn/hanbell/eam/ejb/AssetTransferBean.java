/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetTransaction;
import cn.hanbell.eam.entity.AssetTransfer;
import cn.hanbell.eam.entity.AssetTransferDetail;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eap.ejb.CompanyBean;
import cn.hanbell.eap.entity.Company;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class AssetTransferBean extends SuperEJBForEAM<AssetTransfer> {

    //EJBForEAP
    @EJB
    private CompanyBean companyBean;

    @EJB
    private AssetTransferDetailBean assetTransferDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private TransactionTypeBean transactionTypeBean;

    @EJB
    private AssetCardBean assetCardBean;

    private List<AssetTransferDetail> detailList;
    private List<AssetInventory> inventoryList;

    public AssetTransferBean() {
        super(AssetTransfer.class);
    }

    @Override
    public AssetTransfer unverify(AssetTransfer entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //移转出库
            TransactionType ATO = transactionTypeBean.findByTrtype("ATO");
            //移转入库
            TransactionType ATI = transactionTypeBean.findByTrtype("ATI");
            if (ATO == null || ATI == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetTransfer e = getEntityManager().merge(entity);
            detailList = assetTransferDetailBean.findByPId(e.getFormid());
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetTransferDetail d : detailList) {
                //更新库存数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getAssetCard().getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(ATO.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);

                //更新库存数量
                AssetInventory ti = new AssetInventory();
                ti.setCompany(d.getCompany());
                ti.setAssetItem(d.getAssetItem());
                ti.setBrand(d.getBrand());
                ti.setBatch(d.getBatch());
                ti.setSn(d.getSn());
                ti.setWarehouse(d.getWarehouse());
                ti.setPreqty(BigDecimal.ZERO);
                ti.setQty(d.getQty().multiply(BigDecimal.valueOf(ATI.getIocode())));//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                //进行资产移转必须要有来源卡片信息
                //还原转出卡片
                AssetCard oc = assetCardBean.findByAssetno(d.getAssetno());
                oc.setQty(oc.getQty().add(d.getQty()));
                oc.setUsed(true);
                oc.setScrap(false);
                oc.setRelapi("");
                oc.setRelformid("");
                oc.setRelseq(0);
                assetCardBean.update(oc);
                //删除转入卡片
                AssetCard nc = assetCardBean.findByAssetno(d.getRemark());
                if (nc != null) {
                    assetCardBean.delete(nc);
                }
            }
            //更新库存
            assetInventoryBean.subtract(inventoryList);
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AssetTransfer verify(AssetTransfer entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            String formid;
            //移转出库
            TransactionType ATO = transactionTypeBean.findByTrtype("ATO");
            //移转入库
            TransactionType ATI = transactionTypeBean.findByTrtype("ATI");
            if (ATO == null || ATI == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetTransfer e = getEntityManager().merge(entity);
            detailList = assetTransferDetailBean.findByPId(e.getFormid());
            for (AssetTransferDetail d : detailList) {
                //更新库存交易出库
                AssetTransaction st = new AssetTransaction();
                st.setCompany(e.getCompany());
                st.setTrtype(ATO);
                st.setFormid(e.getFormid());
                st.setFormdate(e.getFormdate());
                st.setSeq(d.getSeq());
                st.setAssetItem(d.getAssetItem());
                st.setBrand(d.getBrand());
                st.setBatch(d.getBatch());
                st.setSn(d.getSn());
                st.setQty(d.getQty());
                st.setUnit(d.getUnit());
                if (d.getAssetCard() != null) {
                    st.setAssetCard(d.getAssetCard());
                    st.setAssetno(d.getAssetno());
                    st.setPrice(d.getAssetCard().getAmts());
                } else {
                    st.setPrice(BigDecimal.ZERO);
                }
                st.setWarehouse(d.getAssetCard().getWarehouse());
                st.setIocode(ATO.getIocode());
                st.setSrcapi(d.getSrcapi());
                st.setSrcformid(d.getSrcformid());
                st.setSrcseq(d.getSrcseq());
                st.setStatus(e.getStatus());
                st.setCfmuser(e.getCfmuser());
                st.setCfmdate(e.getCfmdate());
                assetTransactionBean.setDefaultValue(st);
                assetTransactionBean.persist(st);
                //更新库存数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getAssetCard().getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(ATO.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);

                //进行资产移转必须要有来源卡片信息
                AssetCard oc = assetCardBean.findByAssetno(d.getAssetno());
                oc.setQty(oc.getQty().subtract(d.getQty()));
                if (oc.getQty().compareTo(BigDecimal.ZERO) == 0) {
                    oc.setScrap(true);
                }
                oc.setRelapi("assettransfer");
                oc.setRelformid(d.getPid());
                oc.setRelseq(d.getSeq());
                assetCardBean.update(oc);

                //获得转入公司的资产编号前缀码
                Company c = companyBean.findByCompany(d.getCompany());
                if (d.getAssetItem().getCategory().getNoauto()) {
                    formid = assetCardBean.getFormId(c.getAssetcode(), e.getFormdate(), d.getAssetItem());
                } else {
                    formid = e.getFormid() + "-" + formatString(String.valueOf(d.getSeq()), "0000");
                }
                AssetCard nc = new AssetCard();
                nc.setCompany(d.getCompany());
                nc.setFormid(formid);
                nc.setFormdate(e.getFormdate());
                nc.setAssetDesc(d.getAssetCard().getAssetDesc());
                nc.setAssetSpec(d.getAssetCard().getAssetSpec());
                nc.setUnit(d.getUnit());
                nc.setAssetItem(d.getAssetCard().getAssetItem());
                nc.setDeptno(d.getDeptno());
                nc.setDeptname(d.getDeptname());
                nc.setUserno(d.getUserno());
                nc.setUsername(d.getUsername());
                if (d.getPosition1() != null) {
                    nc.setPosition1(d.getPosition1());
                }
                if (d.getPosition2() != null) {
                    nc.setPosition2(d.getPosition2());
                }
                if (d.getPosition3() != null) {
                    nc.setPosition3(d.getPosition3());
                }
                if (d.getPosition4() != null) {
                    nc.setPosition4(d.getPosition4());
                }
                nc.setWarehouse(d.getWarehouse());
                nc.setQty(d.getQty());
                nc.setAmts(d.getSurplusValue());
                nc.setBuyDate(e.getFormdate());
                nc.setRemark(d.getAssetno());
                nc.setStatus("V");
                nc.setUsed(true);
                nc.setScrap(false);
                nc.setUsingDate(e.getFormdate());
                nc.setSrcapi("assettransfer");
                nc.setSrcformid(d.getPid());
                nc.setSrcseq(d.getSeq());
                assetCardBean.persist(nc);

                //更新库存交易入库
                AssetTransaction tt = new AssetTransaction();
                tt.setCompany(d.getCompany());
                tt.setTrtype(ATI);
                tt.setFormid(e.getFormid());
                tt.setFormdate(e.getFormdate());
                tt.setSeq(d.getSeq());
                tt.setAssetItem(d.getAssetItem());
                tt.setBrand(d.getBrand());
                tt.setBatch(d.getBatch());
                tt.setSn(d.getSn());
                tt.setQty(d.getQty());
                tt.setUnit(d.getUnit());
                tt.setAssetCard(nc);
                tt.setAssetno(formid);
                tt.setPrice(d.getSurplusValue());
                tt.setWarehouse(d.getWarehouse());
                tt.setIocode(ATI.getIocode());
                tt.setSrcapi(d.getSrcapi());
                tt.setSrcformid(d.getSrcformid());
                tt.setSrcseq(d.getSrcseq());
                tt.setStatus(e.getStatus());
                tt.setCfmuser(e.getCfmuser());
                tt.setCfmdate(e.getCfmdate());
                assetTransactionBean.setDefaultValue(tt);
                assetTransactionBean.persist(tt);
                //更新库存数量
                AssetInventory ti = new AssetInventory();
                ti.setCompany(d.getCompany());
                ti.setAssetItem(d.getAssetItem());
                ti.setBrand(d.getBrand());
                ti.setBatch(d.getBatch());
                ti.setSn(d.getSn());
                ti.setWarehouse(d.getWarehouse());
                ti.setPreqty(BigDecimal.ZERO);
                ti.setQty(d.getQty().multiply(BigDecimal.valueOf(ATI.getIocode())));//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                d.setRemark(formid);
                assetTransferDetailBean.update(d);
            }
            //更新库存
            assetInventoryBean.add(inventoryList);
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setDetail(Object value) {
        detailList = assetTransferDetailBean.findByPId(value);
    }

    /**
     * @return the detailList
     */
    public List<AssetTransferDetail> getDetailList() {
        return detailList;
    }

    /**
     * 根据公司编号获取公司
     *
     * @param company
     * @return
     */
    public String getCompany(String company) {
        Company com = companyBean.findByCompany(company);
        return com.getName();
    }
}
