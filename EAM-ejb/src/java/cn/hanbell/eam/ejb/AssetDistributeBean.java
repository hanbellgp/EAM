/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetTransaction;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemProgram;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
public class AssetDistributeBean extends SuperEJBForEAM<AssetDistribute> {

    @EJB
    private SystemProgramBean systemProgramBean;

    @EJB
    private AssetDistributeDetailBean assetDistributeDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private TransactionTypeBean transactionTypeBean;

    private List<AssetDistributeDetail> detailList;

    private List<AssetInventory> inventoryList;

    public AssetDistributeBean() {
        super(AssetDistribute.class);
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetdistribute");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    public List<AssetDistribute> findNeedThrow() {
        Query query = getEntityManager().createNamedQuery("AssetDistribute.findNeedThrow");
        try {
            return query.getResultList();
        } catch (Exception ex) {
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
                //更新库存数量
                AssetInventory ti = new AssetInventory();
                ti.setCompany(e.getCompany());
                ti.setAssetItem(d.getAssetItem());
                ti.setBrand(d.getBrand());
                ti.setBatch(d.getBatch());
                ti.setSn(d.getSn());
                ti.setWarehouse(d.getWarehouse2());
                ti.setPreqty(BigDecimal.ZERO);
                ti.setQty(d.getQty().multiply(BigDecimal.valueOf(AIB.getIocode())));//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                //更新卡片信息或删除卡片信息
                if (d.getAssetCard() != null) {
                    //来源是验收作业的就更新
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    ac.setWarehouse(d.getWarehouse());
                    ac.setUsed(false);
                    ac.setUsingDate(null);
                    assetCardBean.update(ac);
                } else if (!d.getAssetItem().getCategory().getNoauto()) {
                    //来源是领用作业的就删除
                    AssetCard ac = assetCardBean.findByAssetno(d.getPid() + "-" + formatString(String.valueOf(d.getSeq()), "0000"));
                    if (ac != null) {
                        assetCardBean.delete(ac);
                    }
                }

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
            detailList = assetDistributeDetailBean.findByPId(e.getFormid());
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
                if (d.getAssetCard() != null) {
                    st.setAssetCard(d.getAssetCard());
                    st.setAssetno(d.getAssetno());
                    st.setPrice(d.getAssetCard().getAmts());
                } else {
                    st.setPrice(BigDecimal.ZERO);
                }
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
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(AIA.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);

                //更新库存交易入库
                AssetTransaction tt = new AssetTransaction();
                tt.setCompany(e.getCompany());
                tt.setTrtype(AIB);
                tt.setFormid(e.getFormid());
                tt.setFormdate(e.getFormdate());
                tt.setSeq(d.getSeq());
                tt.setRequireDeptno(d.getDeptno());
                tt.setRequireDeptname(d.getDeptname());
                tt.setRequireUserno(d.getUserno());
                tt.setRequireUsername(d.getUsername());
                tt.setAssetItem(d.getAssetItem());
                tt.setBrand(d.getBrand());
                tt.setBatch(d.getBatch());
                tt.setSn(d.getSn());
                tt.setQty(d.getQty());
                tt.setUnit(d.getUnit());
                if (d.getAssetCard() != null) {
                    tt.setAssetCard(d.getAssetCard());
                    tt.setAssetno(d.getAssetno());
                    tt.setPrice(d.getAssetCard().getAmts());
                } else {
                    tt.setPrice(BigDecimal.ZERO);
                }
                tt.setWarehouse(d.getWarehouse2());
                tt.setIocode(AIB.getIocode());
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
                ti.setCompany(e.getCompany());
                ti.setAssetItem(d.getAssetItem());
                ti.setBrand(d.getBrand());
                ti.setBatch(d.getBatch());
                ti.setSn(d.getSn());
                ti.setWarehouse(d.getWarehouse2());
                ti.setPreqty(BigDecimal.ZERO);
                ti.setQty(d.getQty().multiply(BigDecimal.valueOf(AIB.getIocode())));//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                //有卡片的更新卡片信息，没卡片的生成卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    ac.setDeptno(d.getDeptno());
                    ac.setDeptname(d.getDeptname());
                    ac.setUserno(d.getUserno());
                    ac.setUsername(d.getUsername());
                    if (d.getPosition1() != null) {
                        ac.setPosition1(d.getPosition1());
                    }
                    if (d.getPosition2() != null) {
                        ac.setPosition2(d.getPosition2());
                    }
                    if (d.getPosition3() != null) {
                        ac.setPosition3(d.getPosition3());
                    }
                    if (d.getPosition4() != null) {
                        ac.setPosition4(d.getPosition4());
                    }
                    ac.setWarehouse(d.getWarehouse2());
                    ac.setUsed(true);
                    ac.setUsingDate(e.getFormdate());
                    ac.setRelapi("assetdistribute");
                    ac.setRelformid(d.getPid());
                    ac.setRelseq(d.getSeq());
                    assetCardBean.update(ac);
                } else if (!d.getAssetItem().getCategory().getNoauto()) {
                    AssetCard ac = new AssetCard();
                    ac.setCompany(e.getCompany());
                    ac.setFormid(d.getPid() + "-" + formatString(String.valueOf(d.getSeq()), "0000"));
                    ac.setFormdate(e.getFormdate());
                    ac.setAssetDesc(d.getAssetItem().getItemdesc());
                    ac.setAssetSpec(d.getAssetItem().getItemspec());
                    ac.setUnit(d.getUnit());
                    ac.setAssetItem(d.getAssetItem());
                    ac.setDeptno(d.getDeptno());
                    ac.setDeptname(d.getDeptname());
                    ac.setUserno(d.getUserno());
                    ac.setUsername(d.getUsername());
                    if (d.getPosition1() != null) {
                        ac.setPosition1(d.getPosition1());
                    }
                    if (d.getPosition2() != null) {
                        ac.setPosition2(d.getPosition2());
                    }
                    if (d.getPosition3() != null) {
                        ac.setPosition3(d.getPosition3());
                    }
                    if (d.getPosition4() != null) {
                        ac.setPosition4(d.getPosition4());
                    }
                    ac.setWarehouse(d.getWarehouse2());
                    ac.setQty(d.getQty());
                    if (d.getAssetItem().getStdcost() != null) {
                        ac.setAmts(d.getAssetItem().getStdcost());
                    } else {
                        ac.setAmts(BigDecimal.ZERO);
                    }
                    ac.setBuyDate(e.getFormdate());
                    ac.setUsed(true);
                    ac.setUsingDate(e.getFormdate());
                    ac.setStatus("V");
                    ac.setCreator(e.getCreator());
                    ac.setCredate(e.getCredate());
                    ac.setSrcapi("assetdistribute");
                    ac.setSrcformid(d.getPid());
                    ac.setSrcseq(d.getSeq());
                    assetCardBean.persist(ac);
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
    public List<AssetDistributeDetail> getDetailList() {
        return detailList;
    }

}
