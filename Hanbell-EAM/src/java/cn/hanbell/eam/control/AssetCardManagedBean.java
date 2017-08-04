/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetCardModel;
import cn.hanbell.eam.web.FormSingleBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCardManagedBean")
@SessionScoped
public class AssetCardManagedBean extends FormSingleBean<AssetCard> {

    @EJB
    private AssetCardBean assetCardBean;

    private String queryItemno;
    private String queryItemdesc;
    private String queryDeptno;
    private String queryDeptname;
    private String queryUserno;
    private String queryUsername;
    private String queryWarehouseno;

    public AssetCardManagedBean() {
        super(AssetCard.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    protected boolean doBeforeDelete(AssetCard entity) throws Exception {
        if (entity.getUsed()) {
            showErrorMsg("Errro", "已在使用不可删除");
            return false;
        }
        return super.doBeforeDelete(entity);
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity == null) {
            showWarnMsg("Warn", "没有可新增的对象");
            return false;
        }
        if (newEntity.getAssetItem() == null) {
            showErrorMsg("Error", "请输入品号");
            return false;
        }
        if (newEntity.getWarehouse() == null) {
            showErrorMsg("Error", "请输入仓库");
            return false;
        }
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            String formid = assetCardBean.getFormId(newEntity.getCompany(), newEntity.getFormdate(), newEntity.getAssetItem());
            this.newEntity.setFormid(formid);
        }
        return true;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            if (currentEntity.getAssetItem() == null) {
                showErrorMsg("Error", "请输入品号");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentEntity.setAssetItem(e);
            currentEntity.setAssetDesc(e.getItemdesc());
            currentEntity.setAssetSpec(e.getItemspec());
            currentEntity.setUnit(e.getUnit());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    public void handleDialogReturnDeptWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno(d.getDeptno());
            currentEntity.setDeptname(d.getDept());
        }
    }

    public void handleDialogReturnPosition1WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition1(e);
        }
    }

    public void handleDialogReturnPosition2WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition2(e);
        }
    }

    public void handleDialogReturnPosition3WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition3(e);
        }
    }

    public void handleDialogReturnPosition4WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setPosition4(e);
        }
    }

    public void handleDialogReturnUserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setUserno(u.getUserid());
            currentEntity.setUsername(u.getUsername());
        }
    }

    public void handleDialogReturnWarehouseWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentEntity.setWarehouse(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetCardBean;
        model = new AssetCardModel(assetCardBean, userManagedBean);
        model.getFilterFields().put("qty <>", 0);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
    }

    public void print(String rptclazz, String rptdesign) throws Exception {
        if (currentPrgGrant != null && currentPrgGrant.getDoprt()) {
            HashMap<String, Object> reportParams = new HashMap<>();
            reportParams.put("JNDIName", this.currentPrgGrant.getSysprg().getRptjndi());
            if (!this.model.getFilterFields().isEmpty()) {
                reportParams.put("filterFields", BaseLib.convertMapToStringWithClass(this.model.getFilterFields()));
            } else {
                reportParams.put("filterFields", "");
            }
            if (!this.model.getSortFields().isEmpty()) {
                reportParams.put("sortFields", BaseLib.convertMapToString(this.model.getSortFields()));
            } else {
                reportParams.put("sortFields", "");
            }
            //设置报表名称
            String reportFormat;
            if (this.currentPrgGrant.getSysprg().getRptformat() != null) {
                reportFormat = this.currentPrgGrant.getSysprg().getRptformat();
            } else {
                reportFormat = reportOutputFormat;
            }
            this.fileName = this.currentPrgGrant.getSysprg().getApi() + BaseLib.formatDate("yyyyMMddHHss", this.getDate()) + "." + reportFormat;
            String reportName = reportPath + rptdesign;
            String outputName = reportOutputPath + this.fileName;
            this.reportViewPath = reportViewContext + this.fileName;
            try {
                if (this.currentPrgGrant != null && rptclazz != null) {
                    reportClassLoader = Class.forName(rptclazz).getClassLoader();
                }
                //初始配置
                this.reportInitAndConfig();
                //生成报表
                this.reportRunAndOutput(reportName, reportParams, outputName, reportFormat, null);
                //预览报表
                this.preview();
            } catch (Exception ex) {
                throw ex;
            }
        }
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
            if (this.queryItemdesc != null && !"".equals(this.queryItemdesc)) {
                this.model.getFilterFields().put("assetItem.itemdesc", this.queryItemdesc);
            }
            if (this.queryDeptno != null && !"".equals(this.queryDeptno)) {
                this.model.getFilterFields().put("deptno", this.queryDeptno);
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
            if (this.queryWarehouseno != null && !"".equals(this.queryWarehouseno)) {
                this.model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouseno);
            }
            if (queryState != null && "N".equals(queryState)) {
                this.model.getFilterFields().put("used", false);
            } else if (queryState != null && "V".equals(queryState)) {
                this.model.getFilterFields().put("used", true);
                this.model.getFilterFields().put("qty <>", 0);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        model.getFilterFields().put("qty <>", 0);
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
     * @return the queryWarehouseno
     */
    public String getQueryWarehouseno() {
        return queryWarehouseno;
    }

    /**
     * @param queryWarehouseno the queryWarehouseno to set
     */
    public void setQueryWarehouseno(String queryWarehouseno) {
        this.queryWarehouseno = queryWarehouseno;
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
     * @return the queryItemdesc
     */
    public String getQueryItemdesc() {
        return queryItemdesc;
    }

    /**
     * @param queryItemdesc the queryItemdesc to set
     */
    public void setQueryItemdesc(String queryItemdesc) {
        this.queryItemdesc = queryItemdesc;
    }

}
