/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.lazy.AssetCardModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCardQueryBean")
@ViewScoped
public class AssetCardQueryBean extends SuperQueryBean<AssetCard> {

    @EJB
    private AssetCardBean assetCardBean;

    private String queryItemno;
    private String queryUsername;

    private Integer queryUsed = -1;

    public AssetCardQueryBean() {
        super(AssetCard.class);
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
        }
        if (queryItemno != null && !"".equals(queryItemno)) {
            model.getFilterFields().put("assetItem.itemno", queryItemno);
        }
        if (queryUsed == 0) {
            model.getFilterFields().put("used", false);
        } else if (queryUsed > 0) {
            model.getFilterFields().put("used", true);
        }
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("formid", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("assetDesc", this.queryName);
            }
            if (this.queryItemno != null && !"".equals(this.queryItemno)) {
                this.model.getFilterFields().put("assetItem.itemno", this.queryItemno);
            }
            if (this.getQueryUsername() != null && !"".equals(this.queryUsername)) {
                this.model.getFilterFields().put("username", this.getQueryUsername());
            }
            if (queryUsed == 0) {
                model.getFilterFields().put("used", false);
            } else if (queryUsed > 0) {
                model.getFilterFields().put("used", true);
            }
        }
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

}
