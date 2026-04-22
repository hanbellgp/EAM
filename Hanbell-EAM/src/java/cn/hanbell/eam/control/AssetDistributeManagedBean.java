/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetDistributeBean;
import cn.hanbell.eam.ejb.AssetDistributeDetailBean;
import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eam.entity.AssetDistributeDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetDistributeModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import cn.hanbell.oa.entity.HKCW025Detail;
import cn.hanbell.oa.entity.HKCW025;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetDistributeManagedBean")
@SessionScoped
public class AssetDistributeManagedBean extends FormMultiBean<AssetDistribute, AssetDistributeDetail> {

    @EJB
    protected AssetDistributeBean assetDistributeBean;
    @EJB
    protected AssetDistributeDetailBean assetDistributeDetailBean;

    @EJB
    protected AssetInventoryBean assetInventoryBean;
    @EJB
    protected AssetCardBean assetCardBean;
    @EJB
    protected WarehouseBean warehouseBean;
    @EJB
    protected cn.hanbell.oa.ejb.WorkFlowBean workFlowBean;
    @EJB
    protected cn.hanbell.oa.ejb.HKCW025Bean hKCW025Bean;
    @EJB
    protected cn.hanbell.oa.ejb.HKCW026Bean hKCW026Bean;
    protected List<String> paramItemno = null;
    protected List<String> paramPosition = null;
    protected List<String> paramUsed = null;
    protected List<String> paramHascost = null;

    public AssetDistributeManagedBean() {
        super(AssetDistribute.class, AssetDistributeDetail.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (detailList.isEmpty()) {
            showErrorMsg("Error", "请输入对应的领用明细！！！");
            return false;
        }
        if (super.doBeforePersist()) {
            if (newEntity.getDeptno() == null || "".equals(newEntity.getDeptno())) {
                showErrorMsg("Error", "请输入领用部门");
                return false;
            }
            for (AssetDistributeDetail add : detailList) {
                if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (!add.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
                if (add.getWarehouse2().getHascost()) {
                    showErrorMsg("Error", "目的仓成本属性错误");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            if (currentEntity.getDeptno() == null || "".equals(currentEntity.getDeptno())) {
                showErrorMsg("Error", "请输入领用部门");
                return false;
            }
            for (AssetDistributeDetail add : detailList) {
                if (add.getAssetItem().getCategory().getNoauto() && add.getAssetCard() == null) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (!add.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
                if (add.getWarehouse2().getHascost()) {
                    showErrorMsg("Error", "目的仓成本属性错误");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (super.doBeforeUnverify()) {
            AssetInventory ai;
            AssetCard ac;
            for (AssetDistributeDetail add : detailList) {
                ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse2().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可还原量不足");
                    return false;
                }
                if (add.getAssetCard() != null) {
                    ac = assetCardBean.findByFilters(currentEntity.getCompany(), add.getAssetno(), add.getAssetItem().getItemno(), add.getDeptno(), add.getUserno());
                } else {
                    //无需自动编号的系统采用单号+4位流水作为识别
                    ac = assetCardBean.findByAssetno(add.getPid() + "-" + assetCardBean.formatString(String.valueOf(add.getSeq()), "0000"));
                }
                if ((ac == null) || !ac.getUsed()) {
                    showErrorMsg("Error", add.getAssetno() + "不存在或未领用或被他人领用");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //将领用单抛转OA状态为抛转ERP
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
        m.setIsERP("Y");
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
    protected boolean doBeforeVerify() throws Exception {
        if (detailList.isEmpty()) {
            showErrorMsg("Error", "未添加领用明细不能审核！！！");
            return false;
        }
        if (super.doBeforeVerify()) {
            if (currentEntity.getDeptno() == null || "".equals(currentEntity.getDeptno())) {
                showErrorMsg("Error", "请输入领用部门");
                return false;
            }
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
                    if (d.getAssetItem().getItemno().equals(add.getAssetItem().getItemno()) && d.getWarehouse().getWarehouseno().equals(add.getWarehouse().getWarehouseno())) {
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
                //检查ERP库存
                wareh = warehouseBean.findERPWarehouse(currentEntity.getCompany(), add.getWarehouse().getId());
                if (!assetInventoryBean.isLessThenInvbal(currentEntity.getCompany(), "1", add.getAssetItem().getItemno(), wareh, add.getQty())) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "ERP系统" + wareh + "库存可利用量不足");
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail == null) {
            return;
        }
        if (currentDetail.getAssetno() != null && currentDetail.getAssetno() != "") {//有资产编号的增加查询是否有OA单子在进行
            List<Object> list = hKCW026Bean.isThereOA(currentDetail.getAssetno());//获取是否有OA单子
            if (list.size() > 0) {//大于0代表有流程没走完不让添加并返回错误信息
                Object firstElement = list.get(0);
                Object[] array = (Object[]) firstElement;
                showErrorMsg("Error", "该资产编号已有OA流程在进行OA单号:" + array[1] + ",对应EAM单号:" + array[0]);
                return;
            }
        }

        if (currentDetail.getDeptno() == null || "".equals(currentDetail.getDeptno())) {
            showErrorMsg("Error", "请输入领用部门");
            return;
        }
        if (currentDetail.getUserno() == null || "".equals(currentDetail.getUserno())) {
            showErrorMsg("Error", "请输入领用人");
            return;
        }
        if (currentDetail.getAssetItem() == null) {
            showErrorMsg("Error", "请输入件号");
            return;
        }
        if (currentDetail.getAssetItem().getCategory().getNoauto() && currentDetail.getAssetCard() == null) {
            showErrorMsg("Error", "请输入编号");
            return;
        }
        if (currentDetail.getWarehouse() == null) {
            showErrorMsg("Error", "请输入来源仓");
            return;
        }
        if (currentDetail.getWarehouse2() == null) {
            showErrorMsg("Error", "请输入目的仓");
            return;
        }
        if (currentDetail.getQty().compareTo(BigDecimal.ZERO) != 1) {
            showErrorMsg("Error", "请输入数量");
            return;
        }
        for (AssetDistributeDetail aDetail : detailList) {
            if (!currentDetail.getUserno().equals(aDetail.getUserno())) {
                showErrorMsg("Error", "请确认明细中领用人相同!");
                return;
            }
        }
        super.doConfirmDetail();
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno(d.getDeptno());
            currentEntity.setDeptname(d.getDept());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setUnit(e.getUnit());
        }
    }

    public void handleDialogReturnAssetCardWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetCard e = (AssetCard) event.getObject();
            currentDetail.setAssetCard(e);
            currentDetail.setAssetno(e.getFormid());
        }
    }

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Department d = (Department) event.getObject();
            currentDetail.setDeptno(d.getDeptno());
            currentDetail.setDeptname(d.getDept());
        }
    }

    public void handleDialogReturnPosition1WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition1(e);
        }
    }

    public void handleDialogReturnPosition2WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition2(e);
        }
    }

    public void handleDialogReturnPosition3WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition3(e);
        }
    }

    public void handleDialogReturnPosition4WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition4(e);
        }
    }

    public void handleDialogReturnUserWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail.setUserno(u.getUserid());
            currentDetail.setUsername(u.getUsername());
        }
    }

    public void handleDialogReturnWarehouseWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse(e);
        }
    }

    public void handleDialogReturnWarehouse2WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse2(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetDistributeBean;
        detailEJB = assetDistributeDetailBean;
        model = new AssetDistributeModel(assetDistributeBean, userManagedBean);
        model.getFilterFields().put("status", "N");
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
        }
        super.init();
        openParams = new HashMap<>();
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "assetcardSelect":
                openParams.clear();
                if (paramItemno == null) {
                    paramItemno = new ArrayList<>();
                } else {
                    paramItemno.clear();
                }
                paramItemno.add(currentDetail.getAssetItem().getItemno());
                openParams.put("itemno", paramItemno);
                if (paramUsed == null) {
                    paramUsed = new ArrayList<>();
                } else {
                    paramUsed.clear();
                }
                paramUsed.add("0");
                openParams.put("used", paramUsed);
                if (openOptions == null) {
                    openOptions = new HashMap();
                    openOptions.put("modal", true);
                    openOptions.put("contentWidth", "900");
                }
                super.openDialog("assetcardSelect", openOptions, openParams);
                break;
            case "warehouseSelect":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("1");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            case "warehouse2Select":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("0");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            case "assetposition1Select":
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add("0");//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition2Select":
                if (currentDetail == null || currentDetail.getPosition1() == null) {
                    showWarnMsg("Warn", "请先选择公司位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition1().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition3Select":
                if (currentDetail == null || currentDetail.getPosition2() == null) {
                    showWarnMsg("Warn", "请先选择厂区位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition2().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition4Select":
                if (currentDetail == null || currentDetail.getPosition3() == null) {
                    showWarnMsg("Warn", "请先选择厂房位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition3().getId().toString());//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
    }

    @Override
    public void query() {
        if (this.model != null && this.model.getFilterFields() != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            } else {
                if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
                    model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
                }
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
        }
    }

    @Override
    protected void setToolBar() {
        if (currentEntity != null && getCurrentPrgGrant() != null && currentEntity.getStatus() != null) {
            switch (currentEntity.getStatus()) {
                case "T":
                    this.doEdit = false;
                    this.doDel = false;
                    this.doCfm = false;
                    this.doUnCfm = false;
                    break;
                case "V":
                    this.doEdit = getCurrentPrgGrant().getDoedit() && false;
                    this.doDel = getCurrentPrgGrant().getDodel() && false;
                    this.doCfm = false;
                    this.doUnCfm = getCurrentPrgGrant().getDouncfm() && true;
                    break;
                default:
                    this.doEdit = getCurrentPrgGrant().getDoedit() && true;
                    this.doDel = getCurrentPrgGrant().getDodel() && true;
                    this.doCfm = getCurrentPrgGrant().getDocfm() && true;
                    this.doUnCfm = false;
            }
        } else {
            this.doEdit = false;
            this.doDel = false;
            this.doCfm = false;
            this.doUnCfm = false;
        }
    }

    public void splitDetail() {
        if (currentDetail != null && currentDetail.getId() != null) {
            int n = detailList.indexOf(currentDetail);
            if (n < 0) {
                return;
            }
            AssetDistributeDetail add = detailList.get(n);
            if (add.getAssetItem().getCategory().getNoauto() && add.getQty().compareTo(BigDecimal.ONE) == 1) {
                int j = currentDetail.getQty().intValue();
                for (int i = 1; i < j; i++) {
                    this.createDetail();
                    newDetail.setPid(add.getPid());
                    newDetail.setAssetCard(add.getAssetCard());
                    newDetail.setAssetno(add.getAssetno());
                    newDetail.setAssetItem(add.getAssetItem());
                    newDetail.setBrand(add.getBrand());
                    newDetail.setBatch(add.getBatch());
                    newDetail.setSn(add.getSn());
                    newDetail.setQty(BigDecimal.ONE);
                    newDetail.setUnit(add.getUnit());
                    newDetail.setPosition1(add.getPosition1());
                    newDetail.setPosition2(add.getPosition2());
                    newDetail.setPosition3(add.getPosition3());
                    newDetail.setPosition4(add.getPosition4());
                    newDetail.setPosition5(add.getPosition5());
                    newDetail.setPosition6(add.getPosition6());
                    newDetail.setDeptno(add.getDeptno());
                    newDetail.setDeptname(add.getDeptname());
                    newDetail.setUserno(add.getUserno());
                    newDetail.setUsername(add.getUsername());
                    newDetail.setWarehouse(add.getWarehouse());
                    newDetail.setWarehouse2(add.getWarehouse2());
                    newDetail.setSrcapi(add.getSrcapi());
                    newDetail.setSrcformid(add.getSrcformid());
                    newDetail.setSrcseq(add.getSrcseq());
                    newDetail.setRelapi(add.getRelapi());
                    newDetail.setRelformid(add.getRelformid());
                    newDetail.setRelseq(add.getRelseq());
                    newDetail.setRemark(add.getRemark());
                    this.doConfirmDetail();
                }
            }
        } else {
            showErrorMsg("Error", "没有可拆分明细资料");
        }
    }

}
