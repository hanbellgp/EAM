/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetCheck;
import cn.hanbell.eam.entity.AssetCheckDetail;
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
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetCheckBean extends SuperEJBForEAM<AssetCheck> {

    @EJB
    private SystemProgramBean systemProgramBean;
    @EJB
    private AssetCardBean assetCardBean;
    @EJB
    private AssetCheckDetailBean assetCheckDetailBean;
    @EJB
    private AssetInventoryBean assetInventoryBean;
    @EJB
    private AssetTransactionBean assetTransactionBean;
    @EJB
    private TransactionTypeBean transactionTypeBean;

    private List<AssetCheckDetail> detailList;
    private List<AssetInventory> inventoryList;

    public AssetCheckBean() {
        super(AssetCheck.class);
    }

    /**
     * @return the addedDetail
     */
    public List<AssetCheckDetail> getDetailList() {
        return detailList;
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetcheck");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    public String init(String company, Date formdate, String formtype, String formkind, AssetCategory category, String deptno, String userno, String creator, Map<String, Object> filters, Map<String, String> sorts) {
        AssetCheck ac;
        AssetCheckDetail acd;
        List<AssetCheckDetail> addedDetail = new ArrayList<>();
        if (formtype.equals("AC")) {
            List<AssetCard> assetCardList = assetCardBean.findByFilters(filters, sorts);
            if (assetCardList == null || assetCardList.isEmpty()) {
                return null;
            }
            boolean flag;
            int seq = 0;
            if (category.getNoauto()) {
                //资产类
                for (AssetCard c : assetCardList) {
                    seq++;
                    acd = new AssetCheckDetail();
                    acd.setSeq(seq);
                    acd.setAssetCard(c);
                    acd.setAssetno(c.getFormid());
                    acd.setAssetItem(c.getAssetItem());
                    acd.setBrand(c.getBrand());
                    acd.setBatch(c.getBatch());
                    acd.setSn(c.getSn());
                    acd.setQty(c.getQty());
                    acd.setActqty(c.getQty());
                    acd.setDiffqty(BigDecimal.ZERO);
                    acd.setUnit(c.getUnit());
                    acd.setPosition1(c.getPosition1());
                    acd.setPosition2(c.getPosition2());
                    acd.setPosition3(c.getPosition3());
                    acd.setPosition4(c.getPosition4());
                    acd.setPosition5(c.getPosition5());
                    acd.setPosition6(c.getPosition6());
                    acd.setDeptno(c.getDeptno());
                    acd.setDeptname(c.getDeptname());
                    acd.setUserno(c.getUserno());
                    acd.setUsername(c.getUsername());
                    acd.setWarehouse(c.getWarehouse());
                    addedDetail.add(acd);
                    c.setRelseq(seq);
                }
            } else {
                //非资产
                for (AssetCard c : assetCardList) {
                    acd = new AssetCheckDetail();
                    acd.setAssetCard(c);
                    acd.setAssetno(c.getFormid());
                    acd.setAssetItem(c.getAssetItem());
                    acd.setBrand(c.getBrand());
                    acd.setBatch(c.getBatch());
                    acd.setSn(c.getSn());
                    acd.setQty(c.getQty());
                    acd.setActqty(c.getQty());
                    acd.setDiffqty(BigDecimal.ZERO);
                    acd.setUnit(c.getUnit());
                    acd.setPosition1(c.getPosition1());
                    acd.setPosition2(c.getPosition2());
                    acd.setPosition3(c.getPosition3());
                    acd.setPosition4(c.getPosition4());
                    acd.setPosition5(c.getPosition5());
                    acd.setPosition6(c.getPosition6());
                    acd.setDeptno(c.getDeptno());
                    acd.setDeptname(c.getDeptname());
                    acd.setUserno(c.getUserno());
                    acd.setUsername(c.getUsername());
                    acd.setWarehouse(c.getWarehouse());
                    //相同品号、部门和使用人进行合并
                    flag = true;
                    for (AssetCheckDetail d : addedDetail) {
                        if (d.getAssetItem().getItemno().equals(acd.getAssetItem().getItemno()) && Objects.equals(d.getDeptno(), acd.getDeptno()) && Objects.equals(d.getUserno(), acd.getUserno())) {
                            d.setQty(d.getQty().add(acd.getQty()));
                            d.setActqty(d.getQty());
                            flag = false;
                        }
                    }
                    if (flag) {
                        seq++;
                        acd.setSeq(seq);
                        addedDetail.add(acd);
                        c.setRelseq(seq);
                    } else {
                        //记录需要删除
                        c.setStatus("X");
                        c.setRelseq(0);
                    }
                }
            }
            ac = new AssetCheck();
            ac.setCompany(company);
            ac.setFormdate(formdate);
            ac.setFormtype(formtype);
            ac.setFormkind(formkind);
            ac.setCategory(category);
            ac.setDeptno(deptno);
            ac.setUserno(userno);
            ac.setStatusToNew();
            ac.setCreator(creator);
            ac.setCredateToNow();
            try {
                String formid = initAssetCheck(ac, addedDetail);
                if (formid != null && !"".equals(formid)) {
                    for (AssetCard e : assetCardList) {
                        e.setRelapi("assetcheck");
                        e.setRelformid(formid);
                    }
                    assetCardBean.update(assetCardList);
                }
                return formid;
            } catch (Exception ex) {
                return ex.getMessage();
            }
        } else {
            List<AssetInventory> assetInventoryList = assetInventoryBean.findByFilters(filters, sorts);
            if (assetInventoryList == null || assetInventoryList.isEmpty()) {
                return null;
            }
            boolean flag;
            int seq = 0;
            for (AssetInventory i : assetInventoryList) {
                seq++;
                acd = new AssetCheckDetail();
                acd.setSeq(seq);
                acd.setAssetCard(null);
                acd.setAssetno(null);
                acd.setAssetItem(i.getAssetItem());
                acd.setBrand(i.getBrand());
                acd.setBatch(i.getBatch());
                acd.setSn(i.getSn());
                acd.setQty(i.getQty());
                acd.setActqty(i.getQty());
                acd.setDiffqty(BigDecimal.ZERO);
                acd.setUnit(i.getAssetItem().getUnit());
                acd.setWarehouse(i.getWarehouse());
                addedDetail.add(acd);
            }
            ac = new AssetCheck();
            ac.setCompany(company);
            ac.setFormdate(formdate);
            ac.setFormtype(formtype);
            ac.setFormkind(formkind);
            ac.setCategory(category);
            ac.setDeptno(deptno);
            ac.setUserno(userno);
            ac.setStatusToNew();
            ac.setCreator(creator);
            ac.setCredateToNow();
            try {
                String formid = initAssetCheck(ac, addedDetail);
                return formid;
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
    }

    public String initAssetCheck(AssetCheck e, List<AssetCheckDetail> detailList) {
        if (e == null || detailList == null) {
            return null;
        }
        LinkedHashMap<SuperEJB, List<?>> detailAdded = new LinkedHashMap<>();
        detailAdded.put(assetCheckDetailBean, detailList);
        try {
            String formid = getFormId(e.getFormdate());
            e.setFormid(formid);
            int seq = 0;
            for (AssetCheckDetail d : detailList) {
                seq++;
                d.setPid(formid);
                d.setSeq(seq);//重新设置序号
            }
            persist(e, detailAdded, null, null);
            return formid;
        } catch (Exception ex) {
            log4j.error("AssetCheckBean执行initAssetCheck时异常", ex);
            return null;
        }
    }

    @Override
    public void setDetail(Object value) {
        detailList = assetCheckDetailBean.findByPId(value);
    }

    @Override
    public AssetCheck unverify(AssetCheck entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            int i;
            AssetCheck e = getEntityManager().merge(entity);
            detailList = assetCheckDetailBean.findByPId(e.getFormid());
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetCheckDetail d : detailList) {
                i = d.getDiffqty().compareTo(BigDecimal.ZERO);
                if (i != 0) {
                    //更新库存数量
                    AssetInventory si = new AssetInventory();
                    si.setCompany(e.getCompany());
                    si.setAssetItem(d.getAssetItem());
                    si.setBrand(d.getBrand());
                    si.setBatch(d.getBatch());
                    si.setSn(d.getSn());
                    si.setWarehouse(d.getWarehouse());
                    si.setPreqty(BigDecimal.ZERO);
                    si.setQty(d.getDiffqty());//盘亏负数盘盈正数
                    si.setStatusToNew();
                    inventoryList.add(si);
                }
                //更新卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    if (ac == null) {
                        throw new RuntimeException("找不到" + d.getAssetno() + "对应的卡片");
                    }
                    //更新卡片数量
                    ac.setQty(d.getQty());
                    ac.setRelapi("assetcheck");
                    ac.setRelformid(d.getPid());
                    ac.setRelseq(d.getSeq());
                    assetCardBean.update(ac);
                }
            }
            //更新库存
            assetInventoryBean.subtract(inventoryList);
            return e;
        } catch (Exception ex) {
            log4j.error("AssetCheckBean执行unverify时异常", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AssetCheck verify(AssetCheck entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        //盘盈入库
        TransactionType AJI = transactionTypeBean.findByTrtype("AJI");
        //盘亏出库
        TransactionType AJO = transactionTypeBean.findByTrtype("AJO");
        if (AJI == null || AJO == null) {
            throw new RuntimeException("交易类别设置错误");
        }
        try {
            int i;
            AssetCheck e = getEntityManager().merge(entity);
            detailList = assetCheckDetailBean.findByPId(e.getFormid());
            for (AssetCheckDetail d : detailList) {
                i = d.getDiffqty().compareTo(BigDecimal.ZERO);
                if (i != 0) {
                    //更新库存交易出库
                    AssetTransaction st = new AssetTransaction();
                    st.setCompany(e.getCompany());
                    st.setFormid(e.getFormid());
                    st.setFormdate(e.getFormdate());
                    st.setSeq(d.getSeq());
                    st.setAssetItem(d.getAssetItem());
                    st.setBrand(d.getBrand());
                    st.setBatch(d.getBatch());
                    st.setSn(d.getSn());
                    st.setQty(d.getDiffqty());
                    st.setUnit(d.getUnit());
                    if (d.getAssetCard() != null) {
                        st.setAssetCard(d.getAssetCard());
                        st.setAssetno(d.getAssetno());
                    }
                    st.setPrice(BigDecimal.ZERO);
                    st.setWarehouse(d.getWarehouse());
                    st.setSrcapi(d.getSrcapi());
                    st.setSrcformid(d.getSrcformid());
                    st.setSrcseq(d.getSrcseq());
                    st.setRelapi(d.getRelapi());
                    st.setRelformid(d.getRelformid());
                    st.setRelseq(d.getRelseq());
                    st.setStatus(e.getStatus());
                    st.setCfmuser(e.getCfmuser());
                    st.setCfmdate(e.getCfmdate());
                    if (i < 1) {
                        //盘亏
                        st.setTrtype(AJO);
                        st.setIocode(AJO.getIocode());
                    } else {
                        //盘盈
                        st.setTrtype(AJI);
                        st.setIocode(AJI.getIocode());
                    }
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
                    si.setQty(d.getDiffqty());//盘亏负数盘盈正数
                    si.setStatusToNew();
                    inventoryList.add(si);
                }
                //更新卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(e.getCompany(),d.getAssetno());
                    if (ac == null) {
                        log4j.error("AssetCheckBean执行verify时异常", "找不到" + d.getAssetno() + "对应的卡片");
                        throw new RuntimeException("找不到" + d.getAssetno() + "对应的卡片");
                    }
                    //更新卡片数量
                    ac.setQty(d.getActqty());
                    ac.setRelapi("assetcheck");
                    ac.setRelformid(d.getPid());
                    ac.setRelseq(d.getSeq());
                    assetCardBean.update(ac);
                }
            }
            //更新库存
            assetInventoryBean.add(inventoryList);
            List<AssetCard> deletedAssetCard = assetCardBean.findByRelformidAndNeedDelete(e.getFormid());
            if (deletedAssetCard != null && !deletedAssetCard.isEmpty()) {
                assetCardBean.delete(deletedAssetCard);
            }
            return e;
        } catch (Exception ex) {
            log4j.error("AssetCheckBean执行verify时异常", ex);
            throw new RuntimeException(ex);
        }
    }

}
