/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetAdjust;
import cn.hanbell.eam.entity.AssetAdjustDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetTransaction;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemProgram;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
public class AssetAdjustBean extends SuperEJBForEAM<AssetAdjust> {

    @EJB
    private SystemProgramBean systemProgramBean;

    @EJB
    private AssetAdjustDetailBean assetAdjustDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private AssetCardBean assetCardBean;

    private List<AssetAdjustDetail> detailList;

    private List<AssetInventory> inventoryList;

    public AssetAdjustBean() {
        super(AssetAdjust.class);
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetadjust");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    @Override
    public void setDetail(Object value) {
        detailList = assetAdjustDetailBean.findByPId(value);
    }

    @Override
    public AssetAdjust unverify(AssetAdjust entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            AssetAdjust e = getEntityManager().merge(entity);
            detailList = assetAdjustDetailBean.findByPId(e.getFormid());
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetAdjustDetail d : detailList) {
                //更新库存转出数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(-1)));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);
                //更新库存转入数量
                AssetInventory ti = new AssetInventory();
                ti.setCompany(e.getCompany());
                ti.setAssetItem(d.getAssetItem());
                ti.setBrand(d.getBrand());
                ti.setBatch(d.getBatch());
                ti.setSn(d.getSn());
                ti.setWarehouse(d.getWarehouse2());
                ti.setPreqty(BigDecimal.ZERO);
                ti.setQty(d.getQty());//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);
                //更新卡片信息
                if (d.getAssetCard() != null) {
                    if (d.getAssetItem().getCategory().getNoauto()) {
                        AssetCard ac = assetCardBean.findByFiltersAndUsed(d.getPid(), d.getSeq());
                        if (ac != null) {
                            ac.setDeptno(d.getDeptno());
                            ac.setDeptname(d.getDeptname());
                            ac.setUserno(d.getUserno());
                            ac.setUsername(d.getUsername());
                            ac.setPosition1(d.getPosition1());
                            ac.setPosition2(d.getPosition2());
                            ac.setPosition3(d.getPosition3());
                            ac.setPosition4(d.getPosition4());
                            ac.setPosition5(d.getPosition5());
                            ac.setPosition6(d.getPosition6());
                            ac.setWarehouse(d.getWarehouse());
                            assetCardBean.update(ac);
                        }
                    } else {
                        //还原调拨来源数量
                        AssetCard ac = assetCardBean.findByFiltersAndUsed(d.getPid(), d.getSeq());
                        if (ac == null) {
                            throw new RuntimeException("找不到" + d.getPid() + "对应的领用记录");
                        } else {
                            ac.setQty(ac.getQty().add(d.getQty()));
                            ac.setRelapi(null);
                            ac.setRelformid(null);
                            ac.setRelseq(0);
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
    public AssetAdjust verify(AssetAdjust entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            AssetAdjust e = getEntityManager().merge(entity);
            detailList = assetAdjustDetailBean.findByPId(e.getFormid());
            for (AssetAdjustDetail d : detailList) {
                //更新库存交易转出
                AssetTransaction st = new AssetTransaction();
                st.setCompany(e.getCompany());
                st.setTrtype(d.getTrtype());
                st.setFormid(e.getFormid());
                st.setFormdate(e.getFormdate());
                st.setSeq(d.getSeq());
                st.setRequireDeptno(d.getDeptno());
                st.setRequireDeptname(d.getDeptname());
                st.setRequireUserno(d.getUserno());
                st.setRequireUsername(d.getUsername());
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
                st.setIocode(-1);
                st.setSrcapi(d.getSrcapi());
                st.setSrcformid(d.getSrcformid());
                st.setSrcseq(d.getSrcseq());
                st.setStatus(e.getStatus());
                st.setCfmuser(e.getCfmuser());
                st.setCfmdate(e.getCfmdate());
                assetTransactionBean.setDefaultValue(st);
                assetTransactionBean.persist(st);

                //更新库存交易转入
                AssetTransaction tt = new AssetTransaction();
                tt.setCompany(e.getCompany());
                tt.setTrtype(d.getTrtype());
                tt.setFormid(e.getFormid());
                tt.setFormdate(e.getFormdate());
                tt.setSeq(d.getSeq());
                tt.setRequireDeptno(d.getDeptno2());
                tt.setRequireDeptname(d.getDeptname2());
                tt.setRequireUserno(d.getUserno2());
                tt.setRequireUsername(d.getUsername2());
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
                tt.setIocode(1);
                tt.setSrcapi(d.getSrcapi());
                tt.setSrcformid(d.getSrcformid());
                tt.setSrcseq(d.getSrcseq());
                tt.setStatus(e.getStatus());
                tt.setCfmuser(e.getCfmuser());
                tt.setCfmdate(e.getCfmdate());
                assetTransactionBean.setDefaultValue(tt);
                assetTransactionBean.persist(tt);

                //更新库存转出数量
                AssetInventory si = new AssetInventory();
                si.setCompany(e.getCompany());
                si.setAssetItem(d.getAssetItem());
                si.setBrand(d.getBrand());
                si.setBatch(d.getBatch());
                si.setSn(d.getSn());
                si.setWarehouse(d.getWarehouse());
                si.setPreqty(BigDecimal.ZERO);
                si.setQty(d.getQty().multiply(BigDecimal.valueOf(-1)));//出库就 x(-1)
                si.setStatusToNew();
                inventoryList.add(si);

                //更新库存转入数量
                AssetInventory ti = new AssetInventory();
                ti.setCompany(e.getCompany());
                ti.setAssetItem(d.getAssetItem());
                ti.setBrand(d.getBrand());
                ti.setBatch(d.getBatch());
                ti.setSn(d.getSn());
                ti.setWarehouse(d.getWarehouse2());
                ti.setPreqty(BigDecimal.ZERO);
                ti.setQty(d.getQty());//出库就 x(-1)
                ti.setStatusToNew();
                inventoryList.add(ti);

                //有卡片的更新卡片信息，没卡片的生成卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
                    if (ac == null) {
                        throw new RuntimeException("找不到" + d.getAssetno() + "对应的卡片");
                    }
                    if (d.getAssetItem().getCategory().getNoauto()) {
                        //更新卡片信息
                        ac.setDeptno(d.getDeptno2());
                        ac.setDeptname(d.getDeptname2());
                        ac.setUserno(d.getUserno2());
                        ac.setUsername(d.getUsername2());
                        if (d.getPosition12() != null) {
                            ac.setPosition1(d.getPosition12());
                        }
                        if (d.getPosition22() != null) {
                            ac.setPosition2(d.getPosition22());
                        }
                        if (d.getPosition32() != null) {
                            ac.setPosition3(d.getPosition32());
                        }
                        if (d.getPosition42() != null) {
                            ac.setPosition4(d.getPosition42());
                        }
                        if (d.getPosition52() != null) {
                            ac.setPosition5(d.getPosition52());
                        }
                        if (d.getPosition62() != null) {
                            ac.setPosition6(d.getPosition62());
                        }
                        ac.setWarehouse(d.getWarehouse2());;
                        ac.setRelapi("assetadjust");
                        ac.setRelformid(d.getPid());
                        ac.setRelseq(d.getSeq());
                        assetCardBean.update(ac);
                    } else {
                        //扣减调拨来源数量
                        ac.setQty(ac.getQty().subtract(d.getQty()));
                        ac.setRelapi("assetadjust");
                        ac.setRelformid(d.getPid());
                        ac.setRelseq(d.getSeq());
                        assetCardBean.update(ac);

                        //产生调拨作业卡片
                        AssetCard nc = new AssetCard();
                        nc.setCompany(e.getCompany());
                        nc.setFormid(d.getPid() + "-" + formatString(String.valueOf(d.getSeq()), "0000"));
                        nc.setFormdate(e.getFormdate());
                        nc.setAssetDesc(d.getAssetItem().getItemdesc());
                        nc.setAssetSpec(d.getAssetItem().getItemspec());
                        nc.setUnit(d.getUnit());
                        nc.setAssetItem(d.getAssetItem());
                        nc.setDeptno(d.getDeptno2());
                        nc.setDeptname(d.getDeptname2());
                        nc.setUserno(d.getUserno2());
                        nc.setUsername(d.getUsername2());
                        if (d.getPosition12() != null) {
                            nc.setPosition1(d.getPosition12());
                        }
                        if (d.getPosition22() != null) {
                            nc.setPosition2(d.getPosition22());
                        }
                        if (d.getPosition32() != null) {
                            nc.setPosition3(d.getPosition32());
                        }
                        if (d.getPosition42() != null) {
                            nc.setPosition4(d.getPosition42());
                        }
                        if (d.getPosition52() != null) {
                            ac.setPosition5(d.getPosition52());
                        }
                        if (d.getPosition62() != null) {
                            ac.setPosition6(d.getPosition62());
                        }
                        nc.setWarehouse(d.getWarehouse2());
                        nc.setQty(d.getQty());
                        if (d.getAssetItem().getStdcost() != null) {
                            nc.setAmts(d.getAssetItem().getStdcost());
                        } else {
                            nc.setAmts(BigDecimal.ZERO);
                        }
                        nc.setBuyDate(e.getFormdate());
                        nc.setUsed(true);
                        nc.setUsingDate(e.getFormdate());
                        nc.setStatus("V");
                        nc.setCreator(e.getCreator());
                        nc.setCredate(e.getCredate());
                        nc.setSrcapi("assetadjust");
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
    public List<AssetAdjustDetail> getDetailList() {
        return detailList;
    }

}
