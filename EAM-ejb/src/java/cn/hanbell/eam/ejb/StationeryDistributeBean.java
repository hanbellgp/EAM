/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.entity.AssetInventory;
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
public class StationeryDistributeBean extends SuperEJBForEAM<AssetDistribute> {

    @EJB
    private SystemProgramBean systemProgramBean;

    @EJB
    private StationeryDistributeDetailBean assetDistributeDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private TransactionTypeBean transactionTypeBean;

    private List<AssetDistributeDetail> detailList;

    private List<AssetInventory> inventoryList;

    public StationeryDistributeBean() {
        super(AssetDistribute.class);
    }

    public AssetDistribute findByFormId(String formid) {
        Query query = getEntityManager().createNamedQuery("AssetDistribute.findByFormid");
        query.setParameter("formid", formid);
        try {
            Object o = query.getSingleResult();
            return (AssetDistribute) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "stationerydistribute");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    public String initStationeryDistribute(AssetDistribute e, List<AssetDistributeDetail> detailList) {
        if (e == null || detailList == null) {
            return null;
        }
        LinkedHashMap<SuperEJB, List<?>> detailAdded = new LinkedHashMap<>();
        detailAdded.put(assetDistributeDetailBean, detailList);
        try {
            String formid = getFormId(e.getFormdate());
            e.setFormid(formid);
            for (AssetDistributeDetail d : detailList) {
                d.setPid(formid);
            }
            persist(e, detailAdded, null, null);
            return formid;
        } catch (Exception ex) {
            log4j.error("initStationeryDistribute", ex);
            return null;
        }
    }

    @Override
    public void setDetail(Object value) {
        detailList = assetDistributeDetailBean.findByPId(value);
    }

    @Override
    public AssetDistribute unverify(AssetDistribute entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //领用出库
            TransactionType AIA = transactionTypeBean.findByTrtype("AIA");
            //领用入库
            TransactionType AIB = transactionTypeBean.findByTrtype("AIB");
            if (AIA == null || AIB == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetDistribute e = getEntityManager().merge(entity);
            detailList = assetDistributeDetailBean.findByPId(e.getFormid());
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetDistributeDetail d : detailList) {
                //更新库存数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(AIA.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);
            }
            assetInventoryBean.subtract(inventoryList);
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AssetDistribute verify(AssetDistribute entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //领用出库
            TransactionType AIA = transactionTypeBean.findByTrtype("AIA");
            //领用入库
            TransactionType AIB = transactionTypeBean.findByTrtype("AIB");
            if (AIA == null || AIB == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetDistribute e = getEntityManager().merge(entity);
            setDetail(e.getFormid());
            for (AssetDistributeDetail d : detailList) {
                //更新库存交易出库
                AssetTransaction st = new AssetTransaction();
                st.setCompany(e.getCompany());
                st.setTrtype(AIA);
                st.setFormid(e.getFormid());
                st.setFormdate(e.getFormdate());
                st.setSeq(d.getSeq());
                st.setAssetItem(d.getAssetItem());
                st.setBrand(d.getBrand());
                st.setBatch(d.getBatch());
                st.setSn(d.getSn());
                st.setQty(d.getQty());
                st.setUnit(d.getUnit());
                st.setPrice(d.getAmts());
                st.setWarehouse(d.getWarehouse());
                st.setIocode(AIA.getIocode());
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
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty());
                si.setStatusToNew();
                inventoryList.add(si);
            }
            //更新库存
            assetInventoryBean.subtract(inventoryList);
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the detailList
     */
    public List<AssetDistributeDetail> getDetailList() {
        return detailList;
    }

}
