/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.entity.AssetApplyThrow;
import java.math.BigDecimal;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "applyBatchThrowManagedBean")
@SessionScoped
public class ApplyBatchThrowManagedBean extends AssetApplyThrowManagedBean {

    private String queryUsername;
    private String queryItemno;

    public ApplyBatchThrowManagedBean() {

    }

    @Override
    public void init() {
        super.init();
        this.model.getFilterFields().put("assetApply.status", "V");
        this.model.getFilterFields().put("assetItem.id", 0);
        this.model.getFilterFields().put("distributed", false);
    }

    @Override
    public void initAssetAdjust() {
        if (entityList == null || entityList.isEmpty()) {
            showWarnMsg("Warn", "没有可抛转明细");
        }
        try {
            for (AssetApplyThrow aa : entityList) {
                if (aa.getPurqty().compareTo(aa.getQty()) == 1) {
                    showErrorMsg("Error", "请购数量不能大于申请数量");
                    return;
                }
                if (aa.getPurqty().compareTo(BigDecimal.ZERO) == -1) {
                    showErrorMsg("Error", "请购数量不能小于零");
                    return;
                }
                if (aa.getDisqty().compareTo(aa.getQty()) == 1) {
                    showErrorMsg("Error", "领用数量不能大于申请数量");
                    return;
                }
                if (aa.getDisqty().compareTo(BigDecimal.ZERO) != 1) {
                    showErrorMsg("Error", "领用数量必须大于零");
                    return;
                }
            }
            String formid = assetApplyThrowBean.initAssetAdjust(entityList, userManagedBean.getCurrentUser());
            showInfoMsg("Info", "成功产生异动单" + formid);
        } catch (Exception ex) {
            showErrorMsg("Error", ex.getMessage());
        }
    }

    @Override
    public void initAssetDistribute() {
        if (entityList == null || entityList.isEmpty()) {
            showWarnMsg("Warn", "没有可抛转明细");
        }
        try {
            for (AssetApplyThrow aa : entityList) {
                if (aa.getPurqty().compareTo(aa.getQty()) == 1) {
                    showErrorMsg("Error", "请购数量不能大于申请数量");
                    return;
                }
                if (aa.getPurqty().compareTo(BigDecimal.ZERO) == -1) {
                    showErrorMsg("Error", "请购数量不能小于零");
                    return;
                }
                if (aa.getDisqty().compareTo(aa.getQty()) == 1) {
                    showErrorMsg("Error", "领用数量不能大于申请数量");
                    return;
                }
                if (aa.getDisqty().compareTo(BigDecimal.ZERO) != 1) {
                    showErrorMsg("Error", "领用数量必须大于零");
                    return;
                }
            }
            String formid = assetApplyThrowBean.initAssetDistribute(entityList, userManagedBean.getCurrentUser());
            showInfoMsg("Info", "成功产生领用单" + formid);
        } catch (Exception ex) {
            showErrorMsg("Error", ex.getMessage());
        }
    }

    @Override
    public void query() {
        if (this.model != null && this.model.getFilterFields() != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("assetApply.formid", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                this.model.getFilterFields().put("assetApply.applyDeptno", queryName);
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("assetApply.formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("assetApply.formdateEnd", queryDateEnd);
            }
            if (queryUsername != null && !"".equals(queryUsername)) {
                this.model.getFilterFields().put("requireUsername", queryUsername);
            }
            if (queryItemno != null && !"".equals(queryItemno)) {
                this.model.getFilterFields().put("assetItem.itemno", queryItemno);
            }
            this.model.getFilterFields().put("assetApply.status", "V");
            this.model.getFilterFields().put("distributed", false);
        }
    }

    @Override
    public void reset() {
        super.reset();
        queryUsername = null;
        queryItemno = null;
        this.model.getFilterFields().put("assetApply.status", "V");
        this.model.getFilterFields().put("assetItem.id", 0);
        this.model.getFilterFields().put("distributed", false);
    }

    /**
     * @return the queryUsername
     */
    public String getQueryUsername() {
        return queryUsername;
    }

    /**
     * @param queryUsername the queryUsername to set
     */
    public void setQueryUsername(String queryUsername) {
        this.queryUsername = queryUsername;
    }

    /**
     * @return the queryItemno
     */
    public String getQueryItemno() {
        return queryItemno;
    }

    /**
     * @param queryItemno the queryItemno to set
     */
    public void setQueryItemno(String queryItemno) {
        this.queryItemno = queryItemno;
    }

}
