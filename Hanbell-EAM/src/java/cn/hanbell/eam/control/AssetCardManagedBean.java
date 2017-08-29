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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private String queryPosition1;
    private String queryPosition2;
    private String queryPosition3;
    private String queryPosition4;

    private List<String> paramPosition = null;

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
        openParams = new HashMap<>();
        superEJB = assetCardBean;
        model = new AssetCardModel(assetCardBean, userManagedBean);
        model.getFilterFields().put("qty <>", 0);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
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
                if (currentEntity == null || currentEntity.getPosition1() == null) {
                    showWarnMsg("Warn", "请先选择公司位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentEntity.getPosition1().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition3Select":
                if (currentEntity == null || currentEntity.getPosition2() == null) {
                    showWarnMsg("Warn", "请先选择厂区位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentEntity.getPosition2().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition4Select":
                if (currentEntity == null || currentEntity.getPosition3() == null) {
                    showWarnMsg("Warn", "请先选择厂房位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentEntity.getPosition3().getId().toString());//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
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
            if (this.queryPosition1 != null && !"".equals(this.queryPosition1)) {
                this.model.getFilterFields().put("position1.position", this.queryPosition1);
            }
            if (this.queryPosition2 != null && !"".equals(this.queryPosition2)) {
                this.model.getFilterFields().put("position2.position", this.queryPosition2);
            }
            if (this.queryPosition3 != null && !"".equals(this.queryPosition3)) {
                this.model.getFilterFields().put("position3.position", this.queryPosition3);
            }
            if (this.queryPosition4 != null && !"".equals(this.queryPosition4)) {
                this.model.getFilterFields().put("position4.position", this.queryPosition4);
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

    /**
     * @return the queryPosition1
     */
    public String getQueryPosition1() {
        return queryPosition1;
    }

    /**
     * @param queryPosition1 the queryPosition1 to set
     */
    public void setQueryPosition1(String queryPosition1) {
        this.queryPosition1 = queryPosition1;
    }

    /**
     * @return the queryPosition2
     */
    public String getQueryPosition2() {
        return queryPosition2;
    }

    /**
     * @param queryPosition2 the queryPosition2 to set
     */
    public void setQueryPosition2(String queryPosition2) {
        this.queryPosition2 = queryPosition2;
    }

    /**
     * @return the queryPosition3
     */
    public String getQueryPosition3() {
        return queryPosition3;
    }

    /**
     * @param queryPosition3 the queryPosition3 to set
     */
    public void setQueryPosition3(String queryPosition3) {
        this.queryPosition3 = queryPosition3;
    }

    /**
     * @return the queryPosition4
     */
    public String getQueryPosition4() {
        return queryPosition4;
    }

    /**
     * @param queryPosition4 the queryPosition4 to set
     */
    public void setQueryPosition4(String queryPosition4) {
        this.queryPosition4 = queryPosition4;
    }

}
