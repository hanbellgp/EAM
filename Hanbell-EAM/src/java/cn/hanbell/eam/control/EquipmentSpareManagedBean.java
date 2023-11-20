/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentSpareBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.EquipmentSpareClass;
import cn.hanbell.eam.entity.EquipmentSpareMid;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.entity.Unit;
import cn.hanbell.eam.lazy.EquipmentSpareModel;
import cn.hanbell.eam.web.SuperSingleBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareManagedBean")
@SessionScoped
public class EquipmentSpareManagedBean extends SuperSingleBean<EquipmentSpare> {

    @EJB
    private EquipmentSpareBean equipmentSpareBean;

    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private SystemUserBean systemUserBean;
    private List<SysCode> repairareaList;
    protected List<String> paramPosition = null;
    private String[] selectedUserName;
    private List<String> usernameList;
    private List<SystemUser> systemUser;

    public EquipmentSpareManagedBean() {
        super(EquipmentSpare.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCredate(getDate());
        newEntity.setStatus("N");
        newEntity.setCreator(userManagedBean.getUserid());
        selectedUserName = null;
        repairareaList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "repairarea");
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getScategory() == null) {
            showWarnMsg("Warn", "请选择备件大类");
            return false;
        }
        if (newEntity.getMcategory() == null) {
            showWarnMsg("Warn", "请选择备件中类");
            return false;
        }
        if (newEntity.getUnit() == null) {
            showWarnMsg("Warn", "请选择单位");
            return false;
        }
        if (selectedUserName.length != 0) {
            String userName = "";
            for (int i = 0; i < selectedUserName.length; i++) {
                userName += selectedUserName[i] + ";";
            }
            if (userName != "") {
                userName = userName.substring(0, userName.length() - 1);
            }
            newEntity.setServiceusername(userName);
            selectedUserName = null;
        }
        String BJ = "W-" + newEntity.getScategory() + "-" + newEntity.getMcategory();
        int size = equipmentSpareBean.findBySparenum(BJ,userManagedBean.getCompany()).size() + 1;
        if (size < 10) {
            BJ = BJ + "-00" + size;
        } else if (size < 100) {
            BJ = BJ + "-0" + size;
        } else {
            BJ = BJ + "-" + size;
        }

        newEntity.setSparenum(BJ);
        return super.doBeforePersist(); //To change body of generated methods, choose Tools | Templates.
    }
//作废更改状态为V

    public void invalid() {
        currentEntity.setStatus("V");
        super.update();
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        if (selectedUserName.length != 0) {
            String userName = "";
            for (int i = 0; i < selectedUserName.length; i++) {
                userName += selectedUserName[i] + ";";
            }
            if (userName != "") {
                userName = userName.substring(0, userName.length() - 1);
            }
            currentEntity.setServiceusername(userName);
        }

        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {
        superEJB = equipmentSpareBean;
        model = new EquipmentSpareModel(equipmentSpareBean, userManagedBean);
        this.model.getSortFields().put("sparenum", "ASC");
        this.model.getFilterFields().put("status", "N");
        String deptno = sysCodeBean.findBySyskindAndCode(userManagedBean.getCompany(), "RD", "repairDeptno").getCvalue();
        systemUser = systemUserBean.findByLikeDeptno("%" + deptno + "%");
        usernameList = new ArrayList<>();
        for (int i = 0; i < systemUser.size(); i++) {
            usernameList.add(systemUser.get(i).getUsername());
        }
        super.init();
        openParams = new HashMap<>();
    }

    public void handleDialogReturnUnitWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Unit e = (Unit) event.getObject();
            currentEntity.setUnit(e);
        }
    }

    @Override
    public String edit(String path) {
        if (currentEntity.getServiceusername() != "" && currentEntity.getServiceusername() != null) {
            selectedUserName = currentEntity.getServiceusername().split(";");
        } else {
            selectedUserName = null;
        }
        return super.edit(path); //To change body of generated methods, choose Tools | Templates.
    }

    public void handleDialogReturnUnitWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Unit e = (Unit) event.getObject();
            newEntity.setUnit(e);
        }
    }

    public void handleEquipmentSpareClassReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            EquipmentSpareClass e = (EquipmentSpareClass) event.getObject();
            currentEntity.setScategory(e.getScategory());
            currentEntity.setSname(e.getSname());
        }
    }

    public void handleEquipmentSpareClassReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            EquipmentSpareClass e = (EquipmentSpareClass) event.getObject();
           
              newEntity.setScategory(e.getScategory());
            newEntity.setSname(e.getSname());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            EquipmentSpareMid e = (EquipmentSpareMid) event.getObject();
            newEntity.setMcategory(e.getMcategory());
             newEntity.setMname(e.getMname());
        }
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            EquipmentSpareMid e = (EquipmentSpareMid) event.getObject();
    
              currentEntity.setMcategory(e.getMcategory());
             currentEntity.setMname(e.getMname());
        }
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "equipmentSpareClassSelect":
                super.openDialog(view); //To change body of generated methods, choose Tools | Templates.
                break;
            case "equipmentSpareMidSelect":
                if ((newEntity == null || newEntity.getScategory() == null) && (currentEntity == null || currentEntity.getScategory() == null)) {
                    showWarnMsg("Warn", "请选择备件大类");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                if (newEntity.getScategory() != null) {
                    paramPosition.add(newEntity.getScategory());
                } else {
                    paramPosition.add(currentEntity.getScategory());
                }
                openParams.put("sCategory", paramPosition);
                super.openDialog(view, openParams);
                break;
            case "unitSelect":
                super.openDialog(view);
                break;
            default:
                break;
        }
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("sparenum", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("sparedesc", queryName);
            }
            this.model.getFilterFields().put("status", "N");
            this.model.getSortFields().put("sparenum", "ASC");
        }
    }

    public List<SysCode> getRepairareaList() {
        return repairareaList;
    }

    public void setRepairareaList(List<SysCode> repairareaList) {
        this.repairareaList = repairareaList;
    }

    public List<String> getParamPosition() {
        return paramPosition;
    }

    public void setParamPosition(List<String> paramPosition) {
        this.paramPosition = paramPosition;
    }

    public String[] getSelectedUserName() {
        return selectedUserName;
    }

    public void setSelectedUserName(String[] selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

    public List<String> getUsernameList() {
        return usernameList;
    }

    public void setUsernameList(List<String> usernameList) {
        this.usernameList = usernameList;
    }

}
