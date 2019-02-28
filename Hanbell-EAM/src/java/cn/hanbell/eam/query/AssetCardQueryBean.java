/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.lazy.AssetCardModel;
import cn.hanbell.eam.web.SuperQueryBean;
import cn.hanbell.eap.entity.Department;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCardQueryBean")
@ViewScoped
public class AssetCardQueryBean extends SuperQueryBean<AssetCard> {

    @EJB
    private AssetCardBean assetCardBean;

    private AssetCategory queryCategory;

    private String queryItemno;
    private String queryUserno;
    private String queryUsername;

    private String queryDeptno;
    private String queryDeptname;

    private Integer queryUsed = -1;
    private Integer queryScrap = -1;
    private Integer queryPause = -1;

    private boolean queryZero = false;

    public AssetCardQueryBean() {
        super(AssetCard.class);
        queryCategory = new AssetCategory();
        queryCategory.setId(-1);
    }

    public void closeMultiSelect() {
        if (entityList != null && !entityList.isEmpty()) {
            RequestContext.getCurrentInstance().closeDialog(entityList);
        } else {
            showErrorMsg("Error", "没有选择数据源");
        }
    }

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null) {
            Department d = (Department) event.getObject();
            queryDeptno = d.getDeptno();
            queryDeptname = d.getDept();
        }
    }

    public void handleDialogReturnCategoryWhenNew(SelectEvent event) {
        if (event.getObject() != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            setQueryCategory(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetCardBean;
        model = new AssetCardModel(assetCardBean, userManagedBean);
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("itemno")) {
                queryItemno = params.get("itemno")[0];
            }
            if (params.containsKey("used")) {
                queryUsed = Integer.valueOf(params.get("used")[0]);
            }
            if (params.containsKey("scrap")) {
                queryScrap = Integer.valueOf(params.get("scrap")[0]);
            }
            if (params.containsKey("pause")) {
                queryPause = Integer.valueOf(params.get("pause")[0]);
            }
        }
        if (queryItemno != null && !"".equals(queryItemno)) {
            model.getFilterFields().put("assetItem.itemno", queryItemno);
        }
        if (queryUsed == 0) {
            model.getFilterFields().put("used", false);
        } else if (queryUsed > 0) {
            model.getFilterFields().put("used", true);
        }
        if (queryPause == 0) {
            model.getFilterFields().put("pause", false);
        } else if (queryPause > 0) {
            model.getFilterFields().put("pause", true);
        }
        if (queryScrap == 0) {
            model.getFilterFields().put("scrap", false);
        } else if (queryScrap > 0) {
            model.getFilterFields().put("scrap", true);
        }
        //默认不含零数量
        if (!queryZero) {
            this.model.getFilterFields().put("qty <>", 0);
        }
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryCategory.getName() != null && !"".equals(this.queryCategory.getName())) {
                this.model.getFilterFields().put("assetItem.category", this.queryCategory);
            }
            if (this.queryItemno != null && !"".equals(this.queryItemno)) {
                this.model.getFilterFields().put("assetItem.itemno", this.queryItemno);
            }
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("formid", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("assetDesc", this.queryName);
            }
            if (this.queryDeptno != null && !"".equals(this.queryDeptno)) {
                this.model.getFilterFields().put("deptno", this.getQueryDeptno());
            }
            if (this.queryDeptname != null && !"".equals(this.queryDeptname)) {
                this.model.getFilterFields().put("deptname", this.queryDeptname);
            }
            if (this.queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("userno", this.queryUserno);
            }
            if (this.queryUsername != null && !"".equals(this.queryUsername)) {
                this.model.getFilterFields().put("username", this.queryUsername);
            }
            if (queryUsed == 0) {
                model.getFilterFields().put("used", false);
            } else if (queryUsed > 0) {
                model.getFilterFields().put("used", true);
            }
            if (queryPause == 0) {
                model.getFilterFields().put("pause", false);
            } else if (queryPause > 0) {
                model.getFilterFields().put("pause", true);
            }
            if (queryScrap == 0) {
                model.getFilterFields().put("scrap", false);
            } else if (queryScrap > 0) {
                model.getFilterFields().put("scrap", true);
            }
            if (!queryZero) {
                this.model.getFilterFields().put("qty <>", 0);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        queryCategory = new AssetCategory();
        queryCategory.setId(-1);
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

    /**
     * @return the queryUserno
     */
    public String getQueryUserno() {
        return queryUserno;
    }

    /**
     * @param queryUserno the queryUserno to set
     */
    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
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
     * @return the queryDeptno
     */
    public String getQueryDeptno() {
        return queryDeptno;
    }

    /**
     * @param queryDeptno the queryDeptno to set
     */
    public void setQueryDeptno(String queryDeptno) {
        this.queryDeptno = queryDeptno;
    }

    /**
     * @return the queryDeptname
     */
    public String getQueryDeptname() {
        return queryDeptname;
    }

    /**
     * @param queryDeptname the queryDeptname to set
     */
    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

    /**
     * @return the queryCategory
     */
    public AssetCategory getQueryCategory() {
        return queryCategory;
    }

    /**
     * @param queryCategory the queryCategory to set
     */
    public void setQueryCategory(AssetCategory queryCategory) {
        this.queryCategory = queryCategory;
    }

    /**
     * @return the queryZero
     */
    public boolean isQueryZero() {
        return queryZero;
    }

    /**
     * @param queryZero the queryZero to set
     */
    public void setQueryZero(boolean queryZero) {
        this.queryZero = queryZero;
    }

}
