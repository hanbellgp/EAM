/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetScrap;
import cn.hanbell.eam.entity.AssetScrapDetail;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class AssetScrapBean extends SuperEJBForEAM<AssetScrap> {

    @EJB
    private SystemProgramBean systemProgramBean;

    @EJB
    private AssetScrapDetailBean assetScrapDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private TransactionTypeBean transactionTypeBean;

    private List<AssetScrapDetail> detailList;
    private List<AssetInventory> inventoryList;

    public AssetScrapBean() {
        super(AssetScrap.class);
    }

    public AssetScrap findByFormId(String formid) {
        Query query = getEntityManager().createNamedQuery("AssetScrap.findByFormid");
        query.setParameter("formid", formid);
        try {
            Object o = query.getSingleResult();
            return (AssetScrap) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetscrap");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    public String initAssetScrap(AssetScrap e, List<AssetScrapDetail> detailList) {
        if (e == null || detailList == null) {
            return null;
        }
        LinkedHashMap<SuperEJB, List<?>> detailAdded = new LinkedHashMap<>();
        detailAdded.put(assetScrapDetailBean, detailList);
        try {
            String formid = getFormId(e.getFormdate());
            e.setFormid(formid);
            for (AssetScrapDetail d : detailList) {
                d.setPid(formid);
            }
            persist(e, detailAdded, null, null);
            return formid;
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void setDetail(Object value) {
        detailList = assetScrapDetailBean.findByPId(value);
    }

    @Override
    public AssetScrap unverify(AssetScrap entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //出库
            TransactionType AKO = transactionTypeBean.findByTrtype("AKO");
            //入库
            TransactionType AKI = transactionTypeBean.findByTrtype("AKI");
            if (AKO == null || AKI == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetScrap e = getEntityManager().merge(entity);
            detailList = assetScrapDetailBean.findByPId(e.getFormid());
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetScrapDetail d : detailList) {
                //更新库存数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(AKO.getIocode())));//出库就 x(-1)
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
                ti.setQty(d.getQty().multiply(BigDecimal.valueOf(AKI.getIocode())));//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                //更新卡片信息
                if (d.getAssetCard() != null) {
                    if (d.getAssetItem().getCategory().getNoauto()) {
                        AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                        ac.setWarehouse(d.getWarehouse());
                        ac.setScrap(Boolean.FALSE);
                        ac.setScrapDate(null);
                        assetCardBean.update(ac);
                    } else {
                        //还原报废来源数量
                        AssetCard ac = assetCardBean.findByFiltersAndUsed(d.getPid(), d.getSeq());
                        if (ac == null) {
                            throw new RuntimeException("找不到" + d.getPid() + "对应的报废记录");
                        } else {
                            ac.setQty(ac.getQty().add(d.getQty()));
                            assetCardBean.update(ac);
                        }
                        //删除调拨作业产生卡片
                        AssetCard nc = assetCardBean.findByAssetno(d.getPid() + "-" + formatString(String.valueOf(d.getSeq()), "0000"));
                        if (nc == null) {
                            throw new RuntimeException("找不到" + d.getPid() + "对应的卡片记录");
                        } else {
                            assetCardBean.delete(nc);
                        }
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
    public AssetScrap verify(AssetScrap entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            //出库
            TransactionType AKO = transactionTypeBean.findByTrtype("AKO");
            //入库
            TransactionType AKI = transactionTypeBean.findByTrtype("AKI");
            if (AKO == null || AKI == null) {
                throw new RuntimeException("交易类别设置错误");
            }
            AssetScrap e = getEntityManager().merge(entity);
            detailList = assetScrapDetailBean.findByPId(e.getFormid());
            for (AssetScrapDetail d : detailList) {
                //更新库存交易出库
                AssetTransaction st = new AssetTransaction();
                st.setCompany(e.getCompany());
                st.setTrtype(AKO);
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
                    st.setRequireDeptno(d.getAssetCard().getDeptno());
                    st.setRequireDeptname(d.getAssetCard().getDeptname());
                    st.setRequireUserno(d.getAssetCard().getUserno());
                    st.setRequireUsername(d.getAssetCard().getUsername());
                } else {
                    st.setPrice(BigDecimal.ZERO);
                }
                st.setWarehouse(d.getWarehouse());
                st.setIocode(AKO.getIocode());
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
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(AKO.getIocode())));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);

                //更新库存交易入库
                AssetTransaction tt = new AssetTransaction();
                tt.setCompany(e.getCompany());
                tt.setTrtype(AKI);
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
                tt.setIocode(AKI.getIocode());
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
                ti.setQty(d.getQty().multiply(BigDecimal.valueOf(AKI.getIocode())));//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                //有卡片的更新卡片信息，没卡片的生成卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    if (ac == null) {
                        throw new RuntimeException("找不到" + d.getAssetno() + "对应的卡片");
                    }
                    if (d.getAssetItem().getCategory().getNoauto()) {
                        ac.setWarehouse(d.getWarehouse2());
                        ac.setScrap(Boolean.TRUE);
                        ac.setScrapDate(e.getFormdate());
                        ac.setRelapi("assetscrap");
                        ac.setRelformid(d.getPid());
                        ac.setRelseq(d.getSeq());
                        assetCardBean.update(ac);
                    } else {
                        //扣减报废来源数量
                        ac.setQty(ac.getQty().subtract(d.getQty()));
                        ac.setRelapi("assetscrap");
                        ac.setRelformid(d.getPid());
                        ac.setRelseq(d.getSeq());
                        assetCardBean.update(ac);

                        //产生报废作业卡片
                        AssetCard nc = new AssetCard();
                        nc.setCompany(e.getCompany());
                        nc.setFormid(d.getPid() + "-" + formatString(String.valueOf(d.getSeq()), "0000"));
                        nc.setFormdate(e.getFormdate());
                        nc.setAssetDesc(d.getAssetItem().getItemdesc());
                        nc.setAssetSpec(d.getAssetItem().getItemspec());
                        nc.setUnit(d.getUnit());
                        nc.setAssetItem(d.getAssetItem());
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
                        nc.setWarehouse(d.getWarehouse2());
                        nc.setQty(d.getQty());
                        if (d.getAssetItem().getStdcost() != null) {
                            nc.setAmts(d.getAssetItem().getStdcost());
                        } else {
                            nc.setAmts(BigDecimal.ZERO);
                        }
                        nc.setBuyDate(d.getBuyDate());
                        nc.setUsed(true);
                        nc.setScrap(Boolean.TRUE);
                        nc.setScrapDate(e.getFormdate());
                        nc.setStatus("V");
                        nc.setCreator(e.getCreator());
                        nc.setCredate(e.getCredate());
                        nc.setSrcapi("assetscrap");
                        nc.setSrcformid(d.getPid());
                        nc.setSrcseq(d.getSeq());
                        assetCardBean.persist(nc);

                    }
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
    public List<AssetScrapDetail> getDetailList() {
        return detailList;
    }

}
