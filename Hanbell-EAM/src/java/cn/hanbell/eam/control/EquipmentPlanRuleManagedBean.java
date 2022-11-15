/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
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
import java.text.SimpleDateFormat;
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
@ManagedBean(name = "equipmentPlanRuleManagedBean")
@SessionScoped
public class EquipmentPlanRuleManagedBean extends FormMultiBean<EquipmentAnalyResult, EquipmentAnalyResultDta> {
    
    @EJB
    private EquipmentAnalyResultBean equipmentAnalyResultBean;
    @EJB
    private EquipmentAnalyResultDtaBean equipmentAnalyResultDtaBean;
    @EJB
    private EquipmentStandardBean equipmentStandardBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private AssetCardBean assetCardBean;
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
    private Object[] obj;
    private List<EquipmentStandard> equipmentStandardsList;
    private EquipmentStandard equipmentStandard;
    
    public EquipmentPlanRuleManagedBean() {
        super(EquipmentAnalyResult.class, EquipmentAnalyResultDta.class);
    }
    
    @Override
    public void create() {
        super.create();
        newEntity.setCredate(getDate());
        newEntity.setFormdate(getDate());
        newEntity.setStatus("N");
        newEntity.setStandardlevel("二级");//默认等级为二级的保全单
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCreator(userManagedBean.getUserid());
    }
    
    @Override
    public void init() {
        superEJB = equipmentAnalyResultBean;
        model = new EquipmentAnalyResultModel(equipmentAnalyResultBean, userManagedBean);
        detailEJB = equipmentAnalyResultDtaBean;
     
        standardtypeList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "standardtype");
        standardlevelList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "standardlevel");
        respondeptList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "respondept");
        frequencyunitList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "frequencyunit");
        manhourunitList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "manhourunit");
        standardlevelList.remove(0);//计划保全不能筛选一级基准
        queryStandardLevel = "二级";
        equipmentStandardsList = new ArrayList<>();
        equipmentStandard = new EquipmentStandard();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy");
        super.init();
    }
    
    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        detailList.clear();//清除前面选择的设备基准
        if (event.getObject() != null && newEntity != null) {
            AssetCard e = (AssetCard) event.getObject();
            openOptions = new HashMap<>();
            if (queryDept != null && !"".equals(queryDept)) {
                openOptions.put("respondept", queryDept);
            }
            if (newEntity.getStandardlevel() != null && !"".equals(newEntity.getStandardlevel())) {
                openOptions.put("standardlevel", newEntity.getStandardlevel());
            }
            if (queryStandardType != null && !"".equals(queryStandardType)) {
                openOptions.put("standardtype", queryStandardType);
            }
            if (e.getFormid() != null && !"".equals(e.getFormid())) {
                openOptions.put("assetno", e.getFormid());
            }
            // List<EquipmentStandard> eStandard = equipmentStandardBean.getEquipmentStandardList(queryDept, newEntity.getStandardlevel(), queryStandardType, e.getFormid());//获取设备下对应的基准
            List<EquipmentStandard> eStandard = equipmentStandardBean.findByFilters(openOptions); //List<EquipmentStandard> eStandard = equipmentStandardBean.findByAssetno(e.getFormid(),newEntity.getStandardlevel());//获取设备下对应的基准
            AssetCard assetCard = assetCardBean.findByAssetno(e.getFormid());
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
                eArDta.setDownunit(eS.getDownunit());
                eArDta.setManhour(eS.getManhour());
                eArDta.setManpower(eS.getManpower());
                eArDta.setAreaimage(eS.getAreaimage());
                eArDta.setPlandate(eS.getNexttime());
                eArDta.setCreator(userManagedBean.getUserid());
                eArDta.setCredate(getDate());
                eArDta.setStatus("N");
                if (eS.getRespondept().equals("现场")) {
                    eArDta.setAnalysisuser(assetCard.getUserno());
                } else if (eS.getRespondept().equals("维修")) {
                    eArDta.setAnalysisuser(assetCard.getRepairuser());
                }
                seq++;
                detailList.add(eArDta);
                addedDetailList.add(eArDta);
            }
        }
    }
    
    @Override
    public void doConfirmDetail() {
        super.doConfirmDetail();//To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void update() {
        equipmentStandardBean.update(equipmentStandardsList);
    }
    
    @Override
    public String edit(String path) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-mm-dd");
        equipmentStandardsList = equipmentStandardBean.findByAssetnoAndStandardlevel(obj[0].toString(), obj[3].toString(), sd.format(getDate()));
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
        SimpleDateFormat sd = new SimpleDateFormat("yyyy");
        entityList = equipmentAnalyResultBean.getEquipmentStandardList(sd.format(getDate()), queryStandardLevel, queryDept, queryFormId);
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
    
    public List<SysCode> getStandardlevelList() {
        return standardlevelList;
    }
    
    public void setStandardlevelList(List<SysCode> standardlevelList) {
        this.standardlevelList = standardlevelList;
    }
    
    public Object[] getObj() {
        return obj;
    }
    
    public void setObj(Object[] obj) {
        this.obj = obj;
    }
    
    public List<SysCode> getStandardtypeList() {
        return standardtypeList;
    }
    
    public void setStandardtypeList(List<SysCode> standardtypeList) {
        this.standardtypeList = standardtypeList;
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
    
    public List<EquipmentStandard> getEquipmentStandardsList() {
        return equipmentStandardsList;
    }
    
    public void setEquipmentStandardsList(List<EquipmentStandard> equipmentStandardsList) {
        this.equipmentStandardsList = equipmentStandardsList;
    }
    
    public EquipmentStandard getEquipmentStandard() {
        return equipmentStandard;
    }
    
    public void setEquipmentStandard(EquipmentStandard equipmentStandard) {
        this.equipmentStandard = equipmentStandard;
    }
    
}
