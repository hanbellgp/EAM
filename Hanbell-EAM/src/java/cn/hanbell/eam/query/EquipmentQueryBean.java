/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.query;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.lazy.AssetCardModel;
import cn.hanbell.eam.lazy.EquimentQueryModel;
import cn.hanbell.eam.web.SuperQueryBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentQueryBean")
@ViewScoped
public class EquipmentQueryBean extends SuperQueryBean<AssetCard> {

    @EJB
    private AssetCardBean assetCardBean;

    private String queryItemno;
    private String queryUserno;
    private String queryUsername;
    private String queryDeptno;
    private String queryDeptname;

    private boolean queryZero = false;

    public EquipmentQueryBean() {
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
        model = new EquimentQueryModel(assetCardBean, userManagedBean);
        this.model.getFilterFields().put("assetItem.category.id =", 3);
        params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        if (params != null) {
            if (params.containsKey("deptno")) {
                queryDeptno = String.valueOf(params.get("deptno")[0]);
            }
        }
        if (queryDeptno != null) {
            model.getFilterFields().put("deptno", queryDeptno);
        }
        model.getSortFields().put("assetItem.itemno", "ASC");
        model.getSortFields().put("formid", "ASC");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();

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
                this.model.getFilterFields().put("assetSpec", this.queryUserno);
            }
            if (this.queryUsername != null && !"".equals(this.queryUsername)) {
                this.model.getFilterFields().put("brand", this.queryUsername);
            }

            this.model.getFilterFields().put("assetItem.category.id =", 3);
            params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
            if (params != null) {
                if (params.containsKey("deptno")) {
                    queryDeptno = String.valueOf(params.get("deptno")[0]);
                }
            }
            if (queryDeptno != null) {
                model.getFilterFields().put("deptno", queryDeptno);
            }
            model.getSortFields().put("assetItem.itemno", "ASC");
            model.getSortFields().put("formid", "ASC");
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
