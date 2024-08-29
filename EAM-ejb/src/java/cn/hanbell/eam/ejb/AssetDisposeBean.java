/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetDispose;
import cn.hanbell.eam.entity.AssetDisposeDetail;
import cn.hanbell.eam.entity.AssetTransaction;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemProgram;
import com.lightshell.comm.SuperEJB;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetDisposeBean extends SuperEJBForEAM<AssetDispose> {

    @EJB
    private SystemProgramBean systemProgramBean;

    @EJB
    private AssetDisposeDetailBean assetDisposeDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private TransactionTypeBean transactionTypeBean;

    private List<AssetDisposeDetail> detailList;
    private List<AssetInventory> inventoryList;

    public AssetDisposeBean() {
        super(AssetDispose.class);
    }

    public AssetDispose findByFormId(String formid) {
        Query query = getEntityManager().createNamedQuery("AssetDispose.findByFormid");
        query.setParameter("formid", formid);
        try {
            Object o = query.getSingleResult();
            return (AssetDispose) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetdispose");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    public String initAssetDispose(AssetDispose e, List<AssetDisposeDetail> detailList) {
        if (e == null || detailList == null) {
            return null;
        }
        LinkedHashMap<SuperEJB, List<?>> detailAdded = new LinkedHashMap<>();
        detailAdded.put(assetDisposeDetailBean, detailList);
        try {
            String formid = getFormId(e.getFormdate());
            e.setFormid(formid);
            for (AssetDisposeDetail d : detailList) {
                d.setPid(formid);
            }
            persist(e, detailAdded, null, null);
            return formid;
        } catch (Exception ex) {
            log4j.error("initAssetDispose", ex);
            return null;
        }
    }

    @Override
    public AssetDispose unverify(AssetDispose entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //出库
            TransactionType ALO = transactionTypeBean.findByTrtype("ALO");
            if (ALO == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetDispose e = getEntityManager().merge(entity);
            setDetailList(assetDisposeDetailBean.findByPId(e.getFormid()));
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetDisposeDetail d : detailList) {
                //更新库存数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(ALO.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);
                //更新卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    if (ac == null) {
                        throw new RuntimeException("找不到" + d.getAssetno() + "对应的报废记录");
                    }
                    //还原报废来源数量
                    ac.setQty(ac.getQty().add(d.getQty()));
                    assetCardBean.update(ac);

                }
            }
            assetInventoryBean.subtract(inventoryList);//减负数等于加
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AssetDispose verify(AssetDispose entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //出库
            TransactionType ALO = transactionTypeBean.findByTrtype("ALO");
            if (ALO == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetDispose e = getEntityManager().merge(entity);
            setDetailList(assetDisposeDetailBean.findByPId(e.getFormid()));
            for (AssetDisposeDetail d : detailList) {
                //更新库存交易出库
                AssetTransaction st = new AssetTransaction();
                st.setCompany(e.getCompany());
                st.setTrtype(ALO);
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
                }
                st.setPrice(d.getAmts());
                st.setWarehouse(d.getWarehouse());
                st.setIocode(ALO.getIocode());
                st.setSrcapi(d.getSrcapi());
                st.setSrcformid(d.getSrcformid());
                st.setSrcseq(d.getSrcseq());
                st.setRelapi(d.getRelapi());
                st.setRelformid(d.getRelformid());
                st.setRelseq(d.getRelseq());
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
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(ALO.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);
                //更新卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    if (ac == null) {
                        throw new RuntimeException("找不到" + d.getAssetno() + "对应的卡片");
                    }
                    //扣减报废来源数量
                    ac.setQty(ac.getQty().subtract(d.getQty()));
                    ac.setRelapi("assetdispose");
                    ac.setRelformid(d.getPid());
                    ac.setRelseq(d.getSeq());
                    assetCardBean.update(ac);
                }
            }
            //更新库存
            assetInventoryBean.add(inventoryList);
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the detailList
     */
    public List<AssetDisposeDetail> getDetailList() {
        return detailList;
    }

    /**
     * @param detailList the detailList to set
     */
    public void setDetailList(List<AssetDisposeDetail> detailList) {
        this.detailList = detailList;
    }

}
