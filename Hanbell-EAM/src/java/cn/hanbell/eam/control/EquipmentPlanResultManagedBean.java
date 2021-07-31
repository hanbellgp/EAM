/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentAnalyResultBean;
import cn.hanbell.eam.ejb.EquipmentAnalyResultDtaBean;
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentAnalyResult;
import cn.hanbell.eam.entity.EquipmentAnalyResultDta;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentAnalyResultModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.SystemUser;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentPlanResultManagedBean")
@SessionScoped
public class EquipmentPlanResultManagedBean extends FormMultiBean<EquipmentAnalyResult, EquipmentAnalyResultDta> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private EquipmentAnalyResultBean equipmentAnalyResultBean;
    @EJB
    private EquipmentAnalyResultDtaBean equipmentAnalyResultDtaBean;

    @EJB
    private SysCodeBean sysCodeBean;
    private String queryEquipmentName;
    private String queryDept;
    private String queryStandardType;
    private String queryStandardLevel;
    private String queryUserno;
    private List<SysCode> standardtypeList;
    private List<SysCode> standardlevelList;
    private List<SysCode> respondeptList;
    private List<SysCode> frequencyunitList;
    private List<SysCode> manhourunitList;

    public EquipmentPlanResultManagedBean() {
        super(EquipmentAnalyResult.class, EquipmentAnalyResultDta.class);
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        int seq = 0;//获取已完成的不是异常的数量
        int abnormal = 0;//异常的数量
        for (EquipmentAnalyResultDta eDta : detailList) {
            if (eDta.getAnalysisresult() != null && eDta.getAnalysisresult().equals("异常")) {
                currentEntity.setAnalysisresult("异常");
                abnormal++;
            } else if (eDta.getAnalysisresult() != null) {
                seq++;
            }
        }
        if (seq + abnormal == detailList.size()) {//判断是否该张单子已实施完毕
            if (currentEntity.getEnddate() == null) {//判断保养结束日期是否已经赋值，没有则赋值
                currentEntity.setEnddate(getDate());
            }
            currentEntity.setStatus("V");
        } else if (seq != 0 || abnormal != 0) {//判断是否正在实施
            currentEntity.setStatus("S");
        }
        if (seq == detailList.size()) {
            currentEntity.setAnalysisresult("正常");
        }
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    //作废更状态为N
    public void invalid() {
        if (currentEntity == null) {
            showErrorMsg("Error", "请选择要作废的保全记录");
            return;
        }
        currentEntity.setStatus("Z");
        super.update();
    }

    @Override
    public void init() {
        superEJB = equipmentAnalyResultBean;
        model = new EquipmentAnalyResultModel(equipmentAnalyResultBean, userManagedBean);
        detailEJB = equipmentAnalyResultDtaBean;
        standardtypeList = sysCodeBean.getTroubleNameList("RD", "standardtype");
        standardlevelList = sysCodeBean.getTroubleNameList("RD", "standardlevel");
        standardlevelList.remove(0);//计划保全不能筛选一级基准
        respondeptList = sysCodeBean.getTroubleNameList("RD", "respondept");
        frequencyunitList = sysCodeBean.getTroubleNameList("RD", "frequencyunit");
        manhourunitList = sysCodeBean.getTroubleNameList("RD", "manhourunit");
        queryState = "N";//初始查询待实施的数据
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        queryStandardLevel = "二级";//初始查询等级二级的数据
        this.model.getFilterFields().put("status", queryState);
        this.model.getFilterFields().put("standardlevel", queryStandardLevel);
        this.model.getSortFields().put("formid", "ASC");
        super.init();
    }

    @Override
    public void doConfirmDetail() {
        //从第一个项目开始按顺序进行保全作业
        if (currentDetail.getSeq() == 1 && currentDetail.getEdate() == null) {
            currentDetail.setEdate(getDate());
            currentEntity.setStartdate(getDate());//保养开始日期
        }
        if (currentDetail.getSeq() != 1 && detailList.get(currentDetail.getSeq() - 2).getEdate() == null) {
            showErrorMsg("Error", "请按顺序作业!!!");
            currentDetail.setAnalysisresult(null);
            return;
        }
        if (currentDetail.getSeq() != 1 && detailList.get(currentDetail.getSeq() - 2).getEdate() != null) {
            currentDetail.setSdate(detailList.get(currentDetail.getSeq() - 2).getEdate());
            currentDetail.setEdate(getDate());
        }
        super.doConfirmDetail();//To change body of generated methods, choose Tools | Templates.
    }

    public void doConfirmDetail2() {
        super.doConfirmDetail();//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String edit(String path) {
        //给第一个保全时间赋初值
        if (!detailList.isEmpty()) {
            if (detailList.get(0).getSdate() == null) {
                detailList.get(0).setSdate(getDate());
            }
        }
        return super.edit(path); //To change body of generated methods, choose Tools | Templates.
    }

    //派工
    public String dispatching(String path) {
        return super.edit(path); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createDetail() {
        super.createDetail(); //To change body of generated methods, choose Tools | Templates.
        currentDetail.setStatus("N");
        currentDetail.setCredate(getDate());
        currentDetail.setCreator(userManagedBean.getUserid());
    }

    public void handleDialogReturnSysuserWhenNew(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail.setAnalysisuser(u.getUsername());
        }
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            this.model.getSortFields().clear();
            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            }
            if (queryStandardLevel != null && !"".equals(this.queryStandardLevel)) {
                this.model.getFilterFields().put("standardlevel", queryStandardLevel);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
            if (queryEquipmentName != null && !"".equals(this.queryEquipmentName)) {
                this.model.getFilterFields().put("assetdesc", queryEquipmentName);
            }
            if (queryDept != null && !"".equals(this.queryDept)) {
                this.model.getFilterFields().put("deptname", queryDept);
            }

        }
    }

    public String getStutusName(String status) {
        if (status.equals("N")) {
            return "待实施";
        } else if (status.equals("S")) {
            return "实施中";
        } else if (status.equals("V")) {
            return "已完成";
        } else {
            return "已作废";
        }

    }

    public String getQueryUserno() {
        return queryUserno;
    }

    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

    public String getQueryEquipmentName() {
        return queryEquipmentName;
    }

    public void setQueryEquipmentName(String queryEquipmentName) {
        this.queryEquipmentName = queryEquipmentName;
    }

    public String getQueryDept() {
        return queryDept;
    }

    public void setQueryDept(String queryDept) {
        this.queryDept = queryDept;
    }

    public String getQueryStandardType() {
        return queryStandardType;
    }

    public void setQueryStandardType(String queryStandardType) {
        this.queryStandardType = queryStandardType;
    }

    public String getQueryStandardLevel() {
        return queryStandardLevel;
    }

    public void setQueryStandardLevel(String queryStandardLevel) {
        this.queryStandardLevel = queryStandardLevel;
    }

    public List<SysCode> getStandardtypeList() {
        return standardtypeList;
    }

    public void setStandardtypeList(List<SysCode> standardtypeList) {
        this.standardtypeList = standardtypeList;
    }

    public List<SysCode> getStandardlevelList() {
        return standardlevelList;
    }

    public void setStandardlevelList(List<SysCode> standardlevelList) {
        this.standardlevelList = standardlevelList;
    }

    public List<SysCode> getRespondeptList() {
        return respondeptList;
    }

    public void setRespondeptList(List<SysCode> respondeptList) {
        this.respondeptList = respondeptList;
    }

    public List<SysCode> getFrequencyunitList() {
        return frequencyunitList;
    }

    public void setFrequencyunitList(List<SysCode> frequencyunitList) {
        this.frequencyunitList = frequencyunitList;
    }

    public List<SysCode> getManhourunitList() {
        return manhourunitList;
    }

    public void setManhourunitList(List<SysCode> manhourunitList) {
        this.manhourunitList = manhourunitList;
    }

}
