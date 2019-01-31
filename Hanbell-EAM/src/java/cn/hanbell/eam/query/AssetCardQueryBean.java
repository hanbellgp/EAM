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
import cn.hanbell.eap.entity.Department;
import java.util.ArrayList;
import java.util.List;
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

    private String queryItemno;
    private String queryUsername;

    protected String queryDeptno;
    protected String queryDeptnoname;

    private Integer queryUsed = -1;
    private Integer queryScrap = -1;

    public AssetCardQueryBean() {
        super(AssetCard.class);
    }

    public void closeMultiSelect() {
        if (entityList != null && !entityList.isEmpty()) {
            RequestContext.getCurrentInstance().closeDialog(entityList);
        } else {
            showErrorMsg("Error", "没有选择数据源");
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
                queryScrap = Integer.valueOf(params.get("used")[0]);
            }
//            if (params.containsKey("queryDeptno")) {
//                queryDeptno = String.valueOf(params.get("queryDeptno")[0]);
//            }
//            if (params.containsKey("queryDeptnoname")) {
//                queryDeptnoname = String.valueOf(params.get("queryDeptnoname")[0]);
//            }
        }
        if (queryItemno != null && !"".equals(queryItemno)) {
            model.getFilterFields().put("assetItem.itemno", queryItemno);
        }
        if (queryUsed == 0) {
            model.getFilterFields().put("used", false);
        } else if (queryUsed > 0) {
            model.getFilterFields().put("used", true);
        }
        if (queryScrap == 0) {
            model.getFilterFields().put("scrap", false);
        } else if (queryScrap > 0) {
            model.getFilterFields().put("scrap", true);
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
//            if (this.queryDeptno != null && !"".equals(this.queryDeptno)) {
//                this.model.getFilterFields().put("deptno", this.getQueryDeptno());
//            }
//            if (this.queryDeptnoname != null && !"".equals(this.queryDeptnoname)) {
//                this.model.getFilterFields().put("deptname", this.queryDeptnoname);
//            }
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

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null) {
            Department d = (Department) event.getObject();
            queryDeptno = d.getDeptno();
            queryDeptnoname = d.getDept();
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
     * @return the queryDeptnoname
     */
    public String getQueryDeptnoname() {
        return queryDeptnoname;
    }

    /**
     * @param queryDeptnoname the queryDeptnoname to set
     */
    public void setQueryDeptnoname(String queryDeptnoname) {
        this.queryDeptnoname = queryDeptnoname;
    }

}
