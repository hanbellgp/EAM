/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetAcceptance;
import cn.hanbell.eam.entity.AssetAcceptanceDetail;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetTransaction;
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
public class AssetAcceptanceBean extends SuperEJBForEAM<AssetAcceptance> {

    @EJB
    private CompanyBean companyBean;

    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private AssetAcceptanceDetailBean assetAcceptanceDetailBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;
    @EJB
    private AssetTransactionBean assetTransactionBean;

    private List<AssetAcceptanceDetail> detailList;

    private List<AssetInventory> inventoryList;

    public AssetAcceptanceBean() {
        super(AssetAcceptance.class);
    }

    @Override
    public AssetAcceptance unverify(AssetAcceptance entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            AssetAcceptance e = getEntityManager().merge(entity);
            setDetailList(assetAcceptanceDetailBean.findByPId(e.getFormid()));
            //删除资产卡片
            List<AssetCard> cardList = assetCardBean.findBySrcformid(e.getFormid());
            if (cardList != null && !cardList.isEmpty()) {
                assetCardBean.delete(cardList);
            }
            //删除库存交易
            List<AssetTransaction> transactionList = assetTransactionBean.findByFormid(e.getFormid());
            if (transactionList != null && !transactionList.isEmpty()) {
                assetTransactionBean.delete(transactionList);
            }
            //更新库存数量
            for (AssetAcceptanceDetail d : detailList) {
                AssetInventory i = new AssetInventory();
                i.setCompany(e.getCompany());
                i.setAssetItem(d.getAssetItem());
                i.setBrand(d.getBrand());
                i.setBatch(d.getBatch());
                i.setSn(d.getSn());
                i.setWarehouse(d.getWarehouse());
                i.setPreqty(BigDecimal.ZERO);
                i.setQty(d.getQty().multiply(BigDecimal.valueOf(d.getTrtype().getIocode())));//出库就 x(-1)
                inventoryList.add(i);
            }
            assetInventoryBean.subtract(inventoryList);//出库变负值,减负值等于加
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AssetAcceptance verify(AssetAcceptance entity) {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        } else {
            inventoryList.clear();
        }
        try {
            AssetAcceptance e = getEntityManager().merge(entity);
            setDetailList(assetAcceptanceDetailBean.findByPId(e.getFormid()));
            for (AssetAcceptanceDetail d : detailList) {
                //更新库存交易
                AssetTransaction t = new AssetTransaction();
                t.setCompany(e.getCompany());
                t.setTrtype(d.getTrtype());
                t.setFormid(e.getFormid());
                t.setFormdate(e.getFormdate());
                t.setSeq(d.getSeq());
                t.setAssetItem(d.getAssetItem());
                t.setBrand(d.getBrand());
                t.setBatch(d.getBatch());
                t.setSn(d.getSn());
                t.setQty(d.getQcqty());
                t.setUnit(d.getUnit());
                t.setPrice(d.getPrice());
                t.setWarehouse(d.getWarehouse());
                t.setIocode(d.getTrtype().getIocode());
                t.setSrcapi(d.getSrcapi());
                t.setSrcformid(d.getSrcformid());
                t.setSrcseq(d.getSrcseq());
                t.setStatus(e.getStatus());
                t.setCfmuser(e.getCfmuser());
                t.setCfmdate(e.getCfmdate());
                assetTransactionBean.setDefaultValue(t);
                assetTransactionBean.persist(t);
                //更新库存数量
                AssetInventory i = new AssetInventory();
                i.setCompany(e.getCompany());
                i.setAssetItem(d.getAssetItem());
                i.setBrand(d.getBrand());
                i.setBatch(d.getBatch());
                i.setSn(d.getSn());
                i.setWarehouse(d.getWarehouse());
                i.setPreqty(BigDecimal.ZERO);
                i.setQty(d.getQty().multiply(BigDecimal.valueOf(d.getTrtype().getIocode())));//出库就 x(-1)
                i.setStatusToNew();
                inventoryList.add(i);
                if (d.getAssetItem().getCategory().getNoauto()) {
                    //是自动编号的就产生资产卡片
                    Company c = companyBean.findByCompany(e.getCompany());
                    for (int n = 0; n < d.getQcqty().intValue(); n++) {
                        AssetCard ac = new AssetCard();
                        ac.setCompany(e.getCompany());
                        ac.setFormid(assetCardBean.getFormId(c.getAssetcode(), e.getFormdate(), d.getAssetItem()));
                        ac.setFormdate(e.getFormdate());
                        ac.setAssetDesc(d.getAssetItem().getItemdesc());
                        ac.setAssetSpec(d.getAssetItem().getItemspec());
                        ac.setUnit(d.getUnit());
                        ac.setAssetItem(d.getAssetItem());
                        ac.setQty(BigDecimal.ONE);
                        ac.setAmts(d.getPrice());
                        ac.setBuyDate(e.getFormdate());
                        ac.setStatus("V");
                        ac.setCreator(e.getCreator());
                        ac.setCredate(e.getCredate());
                        ac.setSrcapi("assetacceptacnce");
                        ac.setSrcformid(d.getPid());
                        ac.setSrcseq(d.getSeq());
                        assetCardBean.persist(ac);
                    }
                }
            }
            assetInventoryBean.add(inventoryList);//出库变负值,加负值等于减
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the detailList
     */
    public List<AssetAcceptanceDetail> getDetailList() {
        return detailList;
    }

    /**
     * @param detailList the detailList to set
     */
    public void setDetailList(List<AssetAcceptanceDetail> detailList) {
        this.detailList = detailList;
    }

}
