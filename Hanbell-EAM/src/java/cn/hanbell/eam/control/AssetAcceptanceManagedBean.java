/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetAcceptanceBean;
import cn.hanbell.eam.ejb.AssetAcceptanceDetailBean;
import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.TransactionTypeBean;
import cn.hanbell.eam.entity.AssetAcceptance;
import cn.hanbell.eam.entity.AssetAcceptanceDetail;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetAcceptanceModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.Department;
import com.lightshell.comm.BaseLib;
import com.lightshell.comm.Tax;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetAcceptanceManagedBean")
@SessionScoped
public class AssetAcceptanceManagedBean extends FormMultiBean<AssetAcceptance, AssetAcceptanceDetail> {

    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private AssetAcceptanceBean assetAcceptanceBean;

    @EJB
    private AssetAcceptanceDetailBean assetAcceptanceDetailBean;

    @EJB
    private TransactionTypeBean transactoinTypeBean;

    private TransactionType trtype;

    public AssetAcceptanceManagedBean() {
        super(AssetAcceptance.class, AssetAcceptanceDetail.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    public void createDetail() {
        super.createDetail();
        currentDetail.setTrtype(trtype);
        currentDetail.setAcceptdate(getDate());
        currentDetail.setAcceptUserno(userManagedBean.getUserid());
        currentDetail.setCurrency("CNY");
        currentDetail.setExchange(BigDecimal.ONE);
        currentDetail.setTaxtype("0");
        currentDetail.setTaxrate(BigDecimal.valueOf(0.17));
        currentDetail.setTaxkind("VAT17");
        currentDetail.setAmts(BigDecimal.ZERO);
        currentDetail.setExtax(BigDecimal.ZERO);
        currentDetail.setTaxes(BigDecimal.ZERO);
        currentDetail.setStatus("40");
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (super.doBeforeUnverify()) {
            for (AssetAcceptanceDetail d : detailList) {
                //判断是不是自动产生资产卡片
                if (true) {
                    BigDecimal acqty = BigDecimal.ZERO;
                    List<AssetCard> acs = assetCardBean.findByFiltersAndNotUsed(d.getPid(), d.getSeq());
                    for (AssetCard ac : acs) {
                        acqty = acqty.add(ac.getQty());
                    }
                    if (acqty.compareTo(d.getQcqty()) != 0) {
                        showErrorMsg("Error", "卡片可还原数量不足");
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (super.doBeforeVerify()) {
            for (AssetAcceptanceDetail d : detailList) {
                if (!d.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "仓库成本属性错误");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void doConfirmDetail() {
        currentDetail.setAmts(currentDetail.getQcqty().multiply(currentDetail.getPrice()));
        Tax t = BaseLib.getTaxes(currentDetail.getTaxtype(), currentDetail.getTaxkind(), currentDetail.getTaxrate().multiply(BigDecimal.valueOf(100)), this.currentDetail.getAmts(), 2);
        currentDetail.setExtax(t.getExtax());
        currentDetail.setTaxes(t.getTaxes());
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

    public void handleDialogReturnAssetItemWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setUnit(e.getUnit());
        }
    }

    public void handleDialogReturnWarehouseWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetAcceptanceBean;
        detailEJB = assetAcceptanceDetailBean;
        model = new AssetAcceptanceModel(assetAcceptanceBean, userManagedBean);
        trtype = transactoinTypeBean.findByTrtype("PAA");
        if (trtype == null) {
            showErrorMsg("Error", "PAA验收类别未设置");
        }
        super.init();
    }

}
