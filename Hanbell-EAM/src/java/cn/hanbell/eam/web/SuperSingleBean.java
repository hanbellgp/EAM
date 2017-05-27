/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.web;

import com.lightshell.comm.SuperEntity;
import cn.hanbell.eam.control.UserManagedBean;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemGrantPrg;
import com.lightshell.comm.SuperSingleManagedBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 *
 * @author KevinDong
 * @param <T>
 */
public abstract class SuperSingleBean<T extends SuperEntity> extends SuperSingleManagedBean<T> {

    @EJB
    protected SystemProgramBean sysprgBean;

    @ManagedProperty(value = "#{userManagedBean}")
    protected UserManagedBean userManagedBean;

    protected String persistenceUnitName;
    protected String appDataPath;
    protected String appResPath;
    protected SystemGrantPrg currentPrgGrant;

    protected Map<String, Object> openOptions;
    protected Map<String, List<String>> openParams;

    /**
     * @param entityClass
     */
    public SuperSingleBean(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    protected void buildJsonObject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void buildJsonArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void construct() {
        FacesContext fc = FacesContext.getCurrentInstance();
        appDataPath = fc.getExternalContext().getRealPath("/") + fc.getExternalContext().getInitParameter("cn.hanbell.web.appdatapath");
        appResPath = fc.getExternalContext().getRealPath("/") + fc.getExternalContext().getInitParameter("cn.hanbell.web.apprespath");
        reportPath = fc.getExternalContext().getRealPath("/") + fc.getExternalContext().getInitParameter("cn.hanbell.web.reportpath");
        reportOutputFormat = fc.getExternalContext().getInitParameter("cn.hanbell.web.reportoutputformat");
        reportOutputPath = fc.getExternalContext().getRealPath("/") + fc.getExternalContext().getInitParameter("cn.hanbell.web.reportoutputpath");
        reportViewContext = fc.getExternalContext().getInitParameter("cn.hanbell.web.reportviewcontext");
        int beginIndex = fc.getViewRoot().getViewId().lastIndexOf("/") + 1;
        int endIndex = fc.getViewRoot().getViewId().lastIndexOf(".");
        if (userManagedBean.getSystemGrantPrgList() != null && !userManagedBean.getSystemGrantPrgList().isEmpty()) {
            userManagedBean.getSystemGrantPrgList().stream().filter((p) -> (p.getSysprg().getApi().equals(fc.getViewRoot().getViewId().substring(beginIndex, endIndex)))).forEachOrdered((p) -> {
                currentPrgGrant = p;
            });
        }
        if (getCurrentPrgGrant() != null) {
            this.doAdd = getCurrentPrgGrant().getDoadd();
            this.doPriv = getCurrentPrgGrant().getDopriv();
            this.doPrt = getCurrentPrgGrant().getDoprt();
        }
        super.construct();
    }

    @Override
    public void create() {
        super.create();
        if (newEntity != null) {
            newEntity.setStatus("N");
            newEntity.setCreator(getUserManagedBean().getCurrentUser().getUsername());
            newEntity.setCredateToNow();
        }
        setCurrentEntity(newEntity);
    }

    @Override
    public String getAppDataPath() {
        return this.appDataPath;
    }

    @Override
    public String getAppImgPath() {
        return this.appResPath;
    }

    public String getAppResPath() {
        return this.appResPath;
    }

    @Override
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    @Override
    public void print() throws Exception {
        if (currentEntity == null) {
            showMsg(FacesMessage.SEVERITY_WARN, "Warn", "没有可打印数据");
            return;
        }
        //设置报表参数
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", currentEntity.getId());
        params.put("JNDIName", this.currentPrgGrant.getSysprg().getRptjndi());
        //设置报表名称
        String reportFormat;
        if (this.currentPrgGrant.getSysprg().getRptformat() != null) {
            reportFormat = this.currentPrgGrant.getSysprg().getRptformat();
        } else {
            reportFormat = reportOutputFormat;
        }
        String reportName = reportPath + this.currentPrgGrant.getSysprg().getRptdesign();
        String outputName = reportOutputPath + currentPrgGrant.getSysprg().getApi() + currentEntity.getId() + "." + reportFormat;
        this.reportViewPath = reportViewContext + currentPrgGrant.getSysprg().getApi() + currentEntity.getId() + "." + reportFormat;
        try {
            if (this.currentPrgGrant != null && this.currentPrgGrant.getSysprg().getRptclazz() != null) {
                reportClassLoader = Class.forName(this.currentPrgGrant.getSysprg().getRptclazz()).getClassLoader();
            }
            //初始配置
            this.reportInitAndConfig();
            //生成报表
            this.reportRunAndOutput(reportName, params, outputName, reportFormat, null);
            //预览报表
            this.preview();
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public void preview() throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect(this.reportViewPath);
    }

    @Override
    public void pull() {

    }

    @Override
    public void push() {
        buildJsonArray();
    }

    @Override
    protected void setToolBar() {
        if (currentEntity != null && getCurrentPrgGrant() != null && currentEntity.getStatus() != null) {
            switch (currentEntity.getStatus()) {
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

    public String update(String path) {
        try {
            update();
            return path;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void unverify() {
        if (null != getCurrentEntity()) {
            try {
                if (doBeforeUnverify()) {
                    currentEntity.setStatus("N");//简化查询条件,此处不再提供修改状态(M)
                    currentEntity.setOptuser(getUserManagedBean().getCurrentUser().getUsername());
                    currentEntity.setOptdateToNow();
                    currentEntity.setCfmuser(null);
                    currentEntity.setCfmdate(null);
                    superEJB.unverify(currentEntity);
                    doAfterUnverify();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "更新成功!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "还原前检查失败!"));
                }
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(null, e.getMessage()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "没有可更新数据!"));
        }
    }

    @Override
    public void verify() {
        if (null != getCurrentEntity()) {
            try {
                if (doBeforeVerify()) {
                    currentEntity.setStatus("V");
                    currentEntity.setCfmuser(getUserManagedBean().getCurrentUser().getUsername());
                    currentEntity.setCfmdateToNow();
                    superEJB.verify(currentEntity);
                    doAfterVerify();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "更新成功!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "审核前检查失败!"));
                }
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(null, e.getMessage()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "没有可更新数据!"));
        }
    }

    /**
     * @return the userManagedBean
     */
    public UserManagedBean getUserManagedBean() {
        return userManagedBean;
    }

    /**
     * @param userManagedBean the userManagedBean to set
     */
    public void setUserManagedBean(UserManagedBean userManagedBean) {
        this.userManagedBean = userManagedBean;
    }

    /**
     * @return the currentPrgGrant
     */
    public SystemGrantPrg getCurrentPrgGrant() {
        return currentPrgGrant;
    }

}
