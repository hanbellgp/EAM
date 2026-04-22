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
import cn.hanbell.oa.entity.HKCW025;
import cn.hanbell.oa.entity.HKCW025Detail;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    //将领用单抛转OA状态为不抛转ERP
    public boolean doThrowOa() {
        HKCW025 m = new HKCW025();
        List<HKCW025> hkcw025 = hKCW025Bean.getOaFormid(currentEntity.getFormid());
        if (hkcw025.size() > 0) {
            showErrorMsg("Error", "抛转失败,OA已存在该领用单流程在进行.");
            return false;
        }
        HKCW025Detail d;
        List<HKCW025Detail> detailList = new ArrayList<>();
        LinkedHashMap<String, List<?>> details = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        details.put("Detail", detailList);
        m.setFormid(currentEntity.getFormid());
        m.setFormdate(sdf.format(currentEntity.getFormdate()));
        m.setFacno(currentEntity.getCompany());
        m.setDeptno(currentEntity.getDeptno());
        m.setDeptname(currentEntity.getDeptname());
        m.setRemark(currentEntity.getRemark());
        m.setCreator(currentEntity.getCreator());
        m.setStatus(currentEntity.getStatus());
        m.setIsERP("N");
            List<AssetDistributeDetail> dta = assetDistributeDetailBean.findByPId(currentEntity.getFormid());
        try {
            if (!doBeforeVerify()) {
                //抛转前查询是否库存充足
                return false;
            }
            workFlowBean.initUserInfo(userManagedBean.getUserid());
            for (AssetDistributeDetail aDetail : dta) {
                d = new HKCW025Detail();
                d.setPid(aDetail.getPid());
                d.setSeq(aDetail.getSeq() + "");
                if (aDetail.getAssetCard() != null) {
                    d.setAssetid(aDetail.getAssetCard().getId() + "");
                }
                d.setAssetno(aDetail.getAssetno());
                d.setItdsc(aDetail.getAssetItem().getItemdesc());
                d.setItemno(aDetail.getAssetItem().getItemno());
                d.setQty(aDetail.getQty() + "");
                d.setUnit(aDetail.getUnit());
                d.setDeptno1(aDetail.getDeptno());
                d.setDeptname1(aDetail.getDeptname());
                d.setWarehouseno(aDetail.getWarehouse().getWarehouseno());
                d.setWarehousena(aDetail.getWarehouse().getName());
                d.setWarehouseno2(aDetail.getWarehouse2().getWarehouseno());
                d.setWarehousena2(aDetail.getWarehouse2().getName());
                if (aDetail.getPosition1() != null) {
                    d.setPosition1(aDetail.getPosition1().getId() + "");
                    d.setPosition1Name(aDetail.getPosition1().getName());
                }
                if (aDetail.getPosition2() != null) {
                    d.setPosition2(aDetail.getPosition2().getId() + "");
                    d.setPosition2Name(aDetail.getPosition2().getName());
                }
                if (aDetail.getPosition3() != null) {
                    d.setPosition3(aDetail.getPosition3().getId() + "");
                    d.setPosition3Name(aDetail.getPosition3().getName());
                }
                if (aDetail.getPosition4() != null) {
                    d.setPosition4(aDetail.getPosition4().getId() + "");
                    d.setPosition4Name(aDetail.getPosition4().getName());
                }
                if (aDetail.getPosition5() != null) {
                    d.setPosition5(aDetail.getPosition5().getId() + "");
                    d.setPosition5Name(aDetail.getPosition5().getName());
                }
                if (aDetail.getPosition6() != null) {
                    d.setPosition6(aDetail.getPosition6().getId() + "");
                    d.setPosition6Name(aDetail.getPosition6().getName());
                }
                if (aDetail.getAssetCard() != null) {
                    d.setAssetname(aDetail.getAssetCard().getAssetDesc());
                }
                d.setSerialNo(aDetail.getSeq() + "");
                m.setCfmuser_lbl(aDetail.getUsername());
                m.setCfmuser_txt(aDetail.getUserno());
                m.setCfmuser(aDetail.getUserno());
                d.setUserno(aDetail.getUserno());
                d.setUsername(aDetail.getUsername());
                detailList.add(d);
            }
            String formInstance = workFlowBean.buildXmlForEFGP("HK_CW025", m, details);
            String subject = currentEntity.getFormid() + "领用单";
            String msg = workFlowBean.invokeProcess(workFlowBean.HOST_ADD, workFlowBean.HOST_PORT, "PKG_HK_CW025", formInstance, subject);
            String[] rm = msg.split("\\$");
            if (rm.length == 2) {
                if (rm[0].equals("200")) {
                    showInfoMsg("Info", "抛转成功" + rm[1]);
                    currentEntity.setOaformid(rm[1]);
                    currentEntity.setCfmuser(userManagedBean.getUserid());
                    currentEntity.setCfmdate(getDate());
                    assetDistributeBean.update(currentEntity);
                    return true;
                } else {
                    showErrorMsg("Error", "抛转失败");
                    return false;
                }
            } else {
                showErrorMsg("Error", "抛转失败");
                return false;
            }
        } catch (Exception ex) {
            showErrorMsg("Error", "抛转失败:" + ex.getMessage());
            return false;
        }
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
