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
import cn.hanbell.eam.entity.AssetTransaction;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemProgram;
import java.math.BigDecimal;
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
    private AssetAdjustDetailBean assetDistributeDetailBean;

    @EJB
    private AssetTransactionBean assetTransactionBean;

    @EJB
    private AssetCardBean assetCardBean;

    private List<AssetAdjustDetail> detailList;

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
    public AssetAdjust unverify(AssetAdjust entity) {
        try {
            AssetAdjust e = getEntityManager().merge(entity);
            setDetailList(assetDistributeDetailBean.findByPId(e.getFormid()));
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            for (AssetAdjustDetail d : detailList) {
                //更新卡片信息
                if (d.getAssetCard() != null) {
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

                }
            }
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AssetAdjust verify(AssetAdjust entity) {
        try {
            AssetAdjust e = getEntityManager().merge(entity);
            setDetailList(assetDistributeDetailBean.findByPId(e.getFormid()));
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
                st.setIocode(d.getTrtype().getIocode());
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
                tt.setIocode(d.getTrtype().getIocode());
                tt.setSrcapi(d.getSrcapi());
                tt.setSrcformid(d.getSrcformid());
                tt.setSrcseq(d.getSrcseq());
                tt.setStatus(e.getStatus());
                tt.setCfmuser(e.getCfmuser());
                tt.setCfmdate(e.getCfmdate());
                assetTransactionBean.setDefaultValue(tt);
                assetTransactionBean.persist(tt);

                //有卡片的更新卡片信息，没卡片的生成卡片信息
                if (d.getAssetCard() != null) {
                    AssetCard ac = assetCardBean.findByAssetno(d.getAssetno());
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

                }
            }
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

    /**
     * @param detailList the detailList to set
     */
    public void setDetailList(List<AssetAdjustDetail> detailList) {
        this.detailList = detailList;
    }

}
