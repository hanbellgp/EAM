/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.entity.AssetInventory;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetDistributeNotThrowBean")
@SessionScoped
public class AssetDistributeNotThrowBean extends AssetDistributeManagedBean {

    /**
     * Creates a new instance of AssetDistributeNotThrowBean
     */
    public AssetDistributeNotThrowBean() {
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (currentEntity == null) {
            showWarnMsg("Warn", "没有可更新数据");
            return false;
        }
        AssetDistribute e = assetDistributeBean.findById(currentEntity.getId());
        if ("V".equals(e.getStatus()) || "T".equals(e.getStatus())) {
            showWarnMsg("Warn", "状态已变更");
            return false;
        }
        if (detailList != null && !detailList.isEmpty()) {
            detailList.clear();
        }
        detailList = detailEJB.findByPId(currentEntity.getFormid());
        int i;
        boolean flag;
        String wareh;
        AssetInventory ai;
        AssetCard ac;
        List<AssetDistributeDetail> details = new ArrayList<>();
        for (AssetDistributeDetail add : detailList) {
            if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                showErrorMsg("Error", add.getAssetItem().getItemno() + "需要指定资产编号");
                return false;
            }
            if (add.getAssetCard() != null) {
                ac = assetCardBean.findByAssetno(add.getAssetno());
                if ((ac == null) || ac.getUsed()) {
                    showErrorMsg("Error", add.getAssetno() + "不存在或已被领用");
                    return false;
                }
            }//刀工量仪在领用时才产生卡片信息，此处不用判断是否存在卡片
            //数量累加后再判断库存可利用量
            flag = true;
            for (AssetDistributeDetail d : details) {
                if (d.getAssetItem().getItemno().equals(add.getAssetItem().getItemno())) {
                    d.setQty(d.getQty().add(add.getQty()));
                    flag = false;
                }
            }
            if (flag) {
                details.add(add);
            }
        }
        for (AssetDistributeDetail add : details) {
            ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse().getWarehouseno());
            if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可利用量不足");
                return false;
            }
            //不检查ERP库存
        }
        return true;
    }

    @Override
    public void verify() {
        if (null != getCurrentEntity()) {
            try {
                if (doBeforeVerify()) {
                    currentEntity.setStatus("T");
                    currentEntity.setCfmuser(getUserManagedBean().getCurrentUser().getUsername());
                    currentEntity.setCfmdateToNow();
                    superEJB.verify(currentEntity);
                    doAfterVerify();
                    showInfoMsg("Info", "更新成功");
                } else {
                    showErrorMsg("Error", "审核前检查失败");
                }
            } catch (Exception ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        } else {
            showWarnMsg("Warn", "没有可更新数据");
        }
    }

}
