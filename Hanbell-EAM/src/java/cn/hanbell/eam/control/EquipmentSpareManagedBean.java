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
    private List<SysCode> repairareaList;
    protected List<String> paramPosition = null;

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
        repairareaList = sysCodeBean.getTroubleNameList("RD", "repairarea");
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        String BJ = "W-" + newEntity.getScategory().getScategory() + "-" + newEntity.getMcategory().getMcategory();
        int size = equipmentSpareBean.findBySparenum(BJ).size() + 1;
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

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {
        superEJB = equipmentSpareBean;
        model = new EquipmentSpareModel(equipmentSpareBean, userManagedBean);
        super.init();
        openParams = new HashMap<>();
    }

    public void handleDialogReturnUnitWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Unit e = (Unit) event.getObject();
            currentEntity.setUnit(e);
        }
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
            currentEntity.setScategory(e);
        }
    }

    public void handleEquipmentSpareClassReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            EquipmentSpareClass e = (EquipmentSpareClass) event.getObject();
            newEntity.setScategory(e);
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            EquipmentSpareMid e = (EquipmentSpareMid) event.getObject();
            newEntity.setMcategory(e);
        }
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            EquipmentSpareMid e = (EquipmentSpareMid) event.getObject();
            currentEntity.setMcategory(e);
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
                    paramPosition.add(newEntity.getScategory().getScategory());
                } else {
                    paramPosition.add(currentEntity.getScategory().getScategory());
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

}
