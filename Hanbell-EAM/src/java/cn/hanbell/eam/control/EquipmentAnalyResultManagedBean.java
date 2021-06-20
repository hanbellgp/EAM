/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentAnalyResultBean;
import cn.hanbell.eam.ejb.EquipmentAnalyResultDtaBean;
import cn.hanbell.eam.ejb.EquipmentStandardBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentAnalyResult;
import cn.hanbell.eam.entity.EquipmentAnalyResultDta;
import cn.hanbell.eam.entity.EquipmentStandard;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentAnalyResultModel;
import cn.hanbell.eam.web.FormMultiBean;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentAnalyResultManagedBean")
@SessionScoped
public class EquipmentAnalyResultManagedBean extends FormMultiBean<EquipmentAnalyResult, EquipmentAnalyResultDta> {

    @EJB
    private EquipmentAnalyResultBean equipmentAnalyResultBean;
    @EJB
    private EquipmentAnalyResultDtaBean equipmentAnalyResultDtaBean;
    @EJB
    private EquipmentStandardBean equipmentStandardBean;
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

    public EquipmentAnalyResultManagedBean() {
        super(EquipmentAnalyResult.class, EquipmentAnalyResultDta.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCredate(getDate());
        newEntity.setFormdate(getDate());
        newEntity.setStatus("N");
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCreator(userManagedBean.getUserid());
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

    //作废更改单价转态为N
    public void invalid() {
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
        respondeptList = sysCodeBean.getTroubleNameList("RD", "respondept");
        frequencyunitList = sysCodeBean.getTroubleNameList("RD", "frequencyunit");
        manhourunitList = sysCodeBean.getTroubleNameList("RD", "manhourunit");
        queryState = "N";//初始查询待实施的数据
        queryStandardLevel = "一级";//初始查询等级一级的数据
        this.model.getFilterFields().put("status", queryState);
        this.model.getFilterFields().put("standardlevel", queryStandardLevel);
        this.model.getSortFields().put("formid", "ASC");
        super.init();
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        detailList.clear();//清除前面选择的设备基准
        if (event.getObject() != null && newEntity != null) {
            AssetCard e = (AssetCard) event.getObject();
            //  List<EquipmentStandard> eStandard = equipmentStandardBean.getEquipmentStandardList(queryDept, newEntity.getStandardlevel(), queryStandardType, e.getFormid());//获取设备下对应的基准
            List<EquipmentStandard> eStandard = equipmentStandardBean.findByAssetno(e.getFormid());//获取设备下对应的基准
            newEntity.setAssetno(e.getFormid());
            newEntity.setSpareno(e.getAssetItem().getItemno());
            newEntity.setDeptname(e.getDeptname());
            newEntity.setDeptno(e.getDeptno());
            newEntity.setAssetdesc(e.getAssetDesc());
            int seq = 1;
            for (EquipmentStandard eS : eStandard) {
                EquipmentAnalyResultDta eArDta = new EquipmentAnalyResultDta();//将查出的基准一一筛入保全子类
                eArDta.setStandardtype(eS.getStandardtype());
                eArDta.setCompany(newEntity.getCompany());
                eArDta.setRespondept(eS.getRespondept());
                eArDta.setSeq(seq);
                eArDta.setCheckarea(eS.getCheckarea());
                eArDta.setCheckcontent(eS.getCheckcontent());
                eArDta.setJudgestandard(eS.getJudgestandard());
                eArDta.setMethod(eS.getMethod());
                eArDta.setMethodname(eS.getMethodname());
                eArDta.setDowntime(eS.getDowntime());
                eArDta.setManhour(eS.getManhour());
                eArDta.setManpower(eS.getManpower());
                eArDta.setAreaimage(eS.getAreaimage());
                eArDta.setPlandate(eS.getNexttime());
                eArDta.setCreator(userManagedBean.getUserid());
                eArDta.setCredate(getDate());
                eArDta.setStatus("N");
                seq++;
                detailList.add(eArDta);
                addedDetailList.add(eArDta);
            }
        }
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

    @Override
    public void createDetail() {
        super.createDetail(); //To change body of generated methods, choose Tools | Templates.
        currentDetail.setStatus("N");
        currentDetail.setCredate(getDate());
        currentDetail.setCreator(userManagedBean.getUserid());
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
