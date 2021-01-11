/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairFileBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.EquipmentRepairSpareBean;
import cn.hanbell.eam.ejb.EquipmentTroubleBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairFile;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import cn.hanbell.eam.entity.EquipmentRepairSpare;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.EquipmentTrouble;
import cn.hanbell.eam.entity.SysCode;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.lazy.EquipmentRepairModel;
import cn.hanbell.eam.web.FormMulti3Bean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentAcceptanceManagedBean")
@SessionScoped
public class EquipmentAcceptanceManagedBean extends FormMulti3Bean<EquipmentRepair, EquipmentRepairFile, EquipmentRepairSpare, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairFileBean equipmentRepairFileBean;
    @EJB
    protected EquipmentRepairSpareBean equipmentRepairSpareBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private EquipmentTroubleBean equipmentTroubleBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;
    private String queryEquipmentName;
    private String imageName;
    private String maintenanceSupervisor;
    private String queryServiceuser;
    private String queryDeptname;
    private double maintenanceCosts;
    private double totalCost;
    private String contenct;
    private String note;
    private List<EquipmentTrouble> equipmentTroubleList;
    private List<EquipmentRepairFile> equipmentRepairFileList;
    private List<SysCode> abrasehitchList;
    private List<SysCode> hitchurgencyList;
    protected List<EquipmentRepairHelpers> detailList4;
    private EquipmentRepairHelpers currentDetail4;

    public EquipmentAcceptanceManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairFile.class, EquipmentRepairSpare.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        openParams = new HashMap<>();
        superEJB = equipmentRepairBean;
        model = new EquipmentRepairModel(equipmentRepairBean, userManagedBean);
        detailEJB = equipmentRepairFileBean;
        detailEJB2 = equipmentRepairSpareBean;
        detailEJB3 = equipmentRepairHisBean;
        queryState = "ALL";
        // queryServiceuser = getUserName(userManagedBean.getUserid());
        equipmentTroubleList = equipmentTroubleBean.findAll();
        model.getFilterFields().put("rstatus", queryState);
        model.getFilterFields().put("company", userManagedBean.getCompany());
        //model.getFilterFields().put("serviceuser", userManagedBean.getUserid());
        model.getSortFields().put("hitchtime", "DESC");
        super.init();
    }

//选择备件数据处理
    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            EquipmentSpare u = (EquipmentSpare) event.getObject();
            currentDetail2.setUprice(u.getUprice());
            currentDetail2.setSpareno(u.getSpareno());
            currentDetail2.setUserno(currentEntity.getServiceuser());
            currentDetail2.setSparenum(u);
            currentDetail2.setUserdate(getDate());
            currentDetail2.setUnit(u.getUnit());
            currentDetail2.setBrand(u.getBrand());
        }
    }

    @Override
    public void deleteDetail2() {

        super.deleteDetail2(); //To change body of generated methods, choose Tools | Templates.
        getPartsCost();
    }

    //保存验收数据
    public void saveAcceptance() {
        createDetail();
        currentEntity.setStatus("N");
        super.update();//To change body of generated methods, choose Tools | Templates.
        addedDetailList2.clear();
    }

    //选中备件确认时检查及处理
    @Override
    public void doConfirmDetail2() {
        if (currentDetail2 == null) {
            return;
        }
        if (currentDetail2.getSpareno() == null) {
            showErrorMsg("Error", "请选择备件");
            return;
        }
        if (currentDetail2.getQty().doubleValue() <= 0) {
            showErrorMsg("Error", "输入的数量必须大于0");
            return;
        }
        currentDetail2.setCompany(currentEntity.getCompany());
        currentDetail2.setStatus("N");
        super.doConfirmDetail2();
        currentEntity.setSparecost(BigDecimal.valueOf(getPartsCost()));
    }

    @Override
    public void deleteDetail() {

        if (currentDetail != null && "报修图片".equals(currentDetail.getFilefrom())) {
            showErrorMsg("Error", "选择的图片是报修图片,不能删除");
            return;
        }
        super.deleteDetail(); //To change body of generated methods, choose Tools | Templates.
    }

    //修改维修验收单
    public String editAcceptance(String path) {
        if (currentEntity == null) {
            if (this.currentEntity == null) {
                showErrorMsg("Error", "请选择一条单据！！！");
                return "";
            }
        }
        if (!currentEntity.getServiceuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有单据对应的维修人才能修改验收单！！！");
            return "";
        }
        if (Integer.parseInt(currentEntity.getRstatus()) != 40) {
            showErrorMsg("Error", "请确认选择的单据是否已发起验收单！！！");
            return "";
        }
        addedDetailList.clear();
        addedDetailList2.clear();
        //获取联络时间
        currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getHitchtime(), 0));

        //获取维修时间
        currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), currentEntity.getExcepttime()));
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();

        getPartsCost();

        return super.edit(path);

    }

    @Override
    public String view(String path) {
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getHitchtime(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), currentEntity.getExcepttime()));
        }
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null && currentEntity.getCompletetime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        hitchurgencyList = sysCodeBean.getTroubleNameList("RD", "hitchurgency");
        //获取故障责任原因
        abrasehitchList = sysCodeBean.getTroubleNameList("RD", "dutycause");
        calculateTotalCost();
        detailList4 = equipmentRepairHelpersBean.findByPId(currentEntity.getFormid());
        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }
//获取停机时间

    public void getDowntimes() {
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
    }

    //计算总费用
    public void calculateTotalCost() {
        totalCost = 0;
        if (currentEntity.getRepaircost() != null) {
            totalCost += currentEntity.getRepaircost().doubleValue();
        }
        if (currentEntity.getLaborcosts() != null) {
            totalCost += currentEntity.getLaborcosts().doubleValue();
        }
        if (currentEntity.getSparecost() != null) {
            totalCost += currentEntity.getSparecost().doubleValue();
        }
    }
//审批前检查数据是否可以审批

    public String approvalCheck(String path) {
        if (currentEntity == null) {
            showErrorMsg("Error", "请选择单据！");
            return "";
        }
        if (Integer.parseInt(currentEntity.getRstatus()) < 60) {
            showErrorMsg("Error", "当前进度为:" + getStateName(currentEntity.getRstatus()) + ",不能审批");
            return "";
        }
        if (Integer.parseInt(currentEntity.getRstatus()) > 70) {
            showErrorMsg("Error", "当前进度为:" + getStateName(currentEntity.getRstatus()) + ",不能审批");
            return "";
        }

        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        String repairleadersId = systemUserBean.findByDeptno(deptno).get(0).getUserid();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();;
        //维修经理
        String repairmanagerId = sysCodeBean.findBySyskindAndCode("RD", "repairmanager").getCvalue();
        if (!userManagedBean.getUserid().equals(repairleadersId) && !userManagedBean.getUserid().equals(repairmanagerId) && !userManagedBean.getUserid().equals("C2079")) {
            showErrorMsg("Error", "只有维修课长和维修经理才能进行审批操作");
            return "";
        }

        if (Integer.parseInt(currentEntity.getRstatus()) == 70 && !userManagedBean.getUserid().equals(repairleadersId) && !userManagedBean.getUserid().equals("C2079")) {
            showErrorMsg("Error", "当前进度为:" + getStateName(currentEntity.getRstatus()) + ",  维修课长不能审批");
            return "";
        }
        hitchurgencyList = sysCodeBean.getTroubleNameList("RD", "hitchurgency");
        //获取故障责任原因
        abrasehitchList = sysCodeBean.getTroubleNameList("RD", "dutycause");
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getHitchtime(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), currentEntity.getExcepttime()));
        }
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
        detailList4 = equipmentRepairHelpersBean.findByPId(currentEntity.getFormid());
        getPartsCost();
        calculateTotalCost();
        return super.edit(path);

    }

    @Override
    public void toNext() {
        if (this.model != null && !this.model.getDataList().isEmpty()) {
            final int idx = this.model.getDataList().indexOf(this.currentEntity) + 1;
            if (idx < this.model.getDataList().size()) {
                EquipmentRepair eq = (EquipmentRepair) this.model.getDataList().get(idx);
                if (Integer.parseInt(eq.getRstatus()) < 60) {
                    showErrorMsg("Error", "请注意:下一张单据状态为:" + getStateName(eq.getRstatus()) + "。不能审批。");
                    return;
                }
                if (Integer.parseInt(eq.getRstatus()) > 70) {
                    showErrorMsg("Error", "请注意:下一张单据状态为:" + getStateName(eq.getRstatus()) + "。已审批完成。");
                    return;
                }
                if (eq.getServicearrivetime() != null) {
                    //获取联络时间
                    eq.setContactTime(this.getTimeDifference(eq.getServicearrivetime(), eq.getHitchtime(), 0));
                }
                if (eq.getCompletetime() != null && eq.getServicearrivetime() != null) {
                    //获取维修时间
                    eq.setMaintenanceTime(this.getTimeDifference(eq.getCompletetime(), eq.getServicearrivetime(), eq.getExcepttime()));
                }
                //获取总的停机时间
                if (eq.getExcepttime() != null) {
                    eq.setDowntime(this.getTimeDifference(eq.getCompletetime(), eq.getHitchtime(), eq.getExcepttime()));
                }
                this.setCurrentEntity((EquipmentRepair) this.model.getDataList().get(idx));
                detailList2 = equipmentRepairSpareBean.findByPId(currentEntity.getFormid());
                detailList4 = equipmentRepairHelpersBean.findByPId(currentEntity.getFormid());
            }
        }
    }

    @Override
    public void toPrev() {
        if (this.model != null && !this.model.getDataList().isEmpty()) {
            final int idx = this.model.getDataList().indexOf(this.currentEntity) - 1;
            if (idx >= 0) {
                EquipmentRepair eq = (EquipmentRepair) this.model.getDataList().get(idx);
                if (Integer.parseInt(eq.getRstatus()) < 60) {
                    showErrorMsg("Error", "请注意:上一张单据状态为:" + getStateName(eq.getRstatus()) + "。不能审批。");
                    return;
                }
                if (Integer.parseInt(eq.getRstatus()) > 70) {
                    showErrorMsg("Error", "请注意:上一张单据状态为:" + getStateName(eq.getRstatus()) + "。已审批完成。");
                    return;
                }
                if (eq.getServicearrivetime() != null) {
                    //获取联络时间
                    eq.setContactTime(this.getTimeDifference(eq.getServicearrivetime(), eq.getHitchtime(), 0));
                }
                if (eq.getCompletetime() != null && eq.getServicearrivetime() != null) {
                    //获取维修时间
                    eq.setMaintenanceTime(this.getTimeDifference(eq.getCompletetime(), eq.getServicearrivetime(), eq.getExcepttime()));
                }
                //获取总的停机时间
                if (eq.getExcepttime() != null) {
                    eq.setDowntime(this.getTimeDifference(eq.getCompletetime(), eq.getHitchtime(), eq.getExcepttime()));
                }
                this.setCurrentEntity((EquipmentRepair) this.model.getDataList().get(idx));
            }
        }
    }

    //确认审批
    public void confirmApproval() {
        createDetail3();
        currentDetail3.setUserno(userManagedBean.getUserid());
        currentDetail3.setCompany(userManagedBean.getCompany());
        currentDetail3.setCredate(getDate());
        currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
        currentDetail3.setStatus("N");
        currentDetail3.setContenct(contenct);
        currentDetail3.setNote(note);
        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        String repairleadersId = systemUserBean.findByDeptno(deptno).get(0).getUserid();
        //维修经理
        String repairmanagerId = sysCodeBean.findBySyskindAndCode("RD", "repairmanager").getCvalue();
        //维修经理签核的金额
        String repairApprovals = sysCodeBean.findBySyskindAndCode("RD", "repairApprovals").getCvalue();

        //备件费用
        maintenanceCosts = 0;
        detailList2.forEach(equipmentrepair1 -> {
            BigDecimal price = equipmentrepair1.getUprice();
            maintenanceCosts += equipmentrepair1.getQty().doubleValue() * price.doubleValue();
        });
        calculateTotalCost();
        if (currentEntity.getRstatus().equals("60") && userManagedBean.getUserid().equals(repairleadersId) || currentEntity.getRstatus().equals("60") && userManagedBean.getUserid().equals("C2079")) {
            if (contenct.equals("合格")) {

                if (totalCost > Integer.parseInt(repairApprovals) || currentEntity.getRepairarchive().equals("Y")) {
                    currentEntity.setRstatus("70");
                } else {
                    currentEntity.setRstatus("95");
                }
            } else if (contenct.equals("不合格")) {
                currentEntity.setRstatus("40");
            }

        } else if (currentEntity.getRstatus().equals("70") && userManagedBean.getUserid().equals(repairmanagerId) || currentEntity.getRstatus().equals("70") && userManagedBean.getUserid().equals("C2079")) {
            if (contenct.equals("合格")) {
                currentDetail3.setRemark(repairApprovals);
                currentEntity.setRstatus("95");
            } else if (contenct.equals("不合格")) {
                currentEntity.setRstatus("60");
            }
        } else {
            showErrorMsg("Error", "已完成本次审核,单据状态已变更,请返回主页面查看");
            return;
        }
        contenct = null;
        note = null;
        super.doConfirmDetail3();
        currentEntity.setStatus("N");
        super.update();
        final int idx = this.model.getDataList().indexOf(this.currentEntity);

        model.getDataList().remove(idx);
        
        toNext();
    }
    //获取故障紧急度

    public String getHitchurgency(String cValue) {
        SysCode sysCode = sysCodeBean.getTroubleName("RD", "hitchurgency", cValue);
        String troubleName = "";
        if (sysCode == null) {
            return troubleName;
        }
        troubleName = sysCode.getCdesc();
        return troubleName;
    }

//获取零件费用
    public Double getPartsCost() {
        maintenanceCosts = 0;
        detailList2.forEach(equipmentrepair -> {
            BigDecimal price = equipmentrepair.getUprice();
            maintenanceCosts += equipmentrepair.getQty().doubleValue() * price.doubleValue();
        });
        return maintenanceCosts;
    }
//加载文件

    @Override
    protected void upload() throws IOException {
        try {
            final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            request.setCharacterEncoding("UTF-8");
            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
            imageName = String.valueOf(date.getTime());
            final InputStream in = this.file.getInputstream();
            final File dir = new File(this.getAppResPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            imageName = imageName + this.getFileName();
            final OutputStream out = new FileOutputStream(new File(dir.getAbsolutePath() + "//" + imageName));
            int read = 0;
            final byte[] bytes = new byte[1024];
            while (true) {
                read = in.read(bytes);
                if (read < 0) {
                    break;
                }
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", e.toString());
            FacesContext.getCurrentInstance().addMessage((String) null, msg);
        }
    }

    //处理上传图片数据
    public void handleFileUploadWhenDetailNew(FileUploadEvent event) throws IOException {
        super.handleFileUploadWhenNew(event);
        if (this.fileName != null) {
            this.createDetail();
            int seq = detailList.size() + 1;
            EquipmentRepairFile equipmentrepairfile = new EquipmentRepairFile();
            equipmentrepairfile.setCompany(userManagedBean.getCompany());
            equipmentrepairfile.setFilepath("../../resources/app/res/" + imageName);
            equipmentrepairfile.setFilename(fileName);
            equipmentrepairfile.setFilefrom("维修图片");
            equipmentrepairfile.setStatus("Y");
            equipmentrepairfile.setSeq(seq);
            equipmentrepairfile.setPid(currentEntity.getFormid());
            detailList.add(equipmentrepairfile);
            addedDetailList.add(equipmentrepairfile);

        }
    }

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        if (model != null) {
            this.model.getFilterFields().clear();

            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }

            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("formid", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                model.getFilterFields().put("assetno.formid", queryName);
            }
            if (queryEquipmentName != null && !"".equals(queryEquipmentName)) {
                if (queryEquipmentName.equals("其") || queryEquipmentName.equals("他") || queryEquipmentName.equals("其他")) {
                    model.getFilterFields().put("itemno =", "9");
                } else {
                    model.getFilterFields().put("assetno.assetDesc", queryEquipmentName);
                }
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("repairdeptname", queryDeptname);
            }
            if (queryServiceuser != null && !"".equals(queryServiceuser)) {
                model.getFilterFields().put("serviceusername", queryServiceuser);
            }
            model.getFilterFields().put("company", userManagedBean.getCompany());
            model.getFilterFields().put("rstatus", queryState);
            model.getSortFields().put("hitchtime", "DESC");

        }
    }

    //获取故障来源
    public String getTroubleName(String cValue) {
        SysCode sysCode = sysCodeBean.getTroubleName("RD", "faultType", cValue);
        String troubleName = "";
        if (sysCode == null) {
            return troubleName;
        }
        troubleName = sysCode.getCdesc();
        return troubleName;
    }

    //获取显示的进度
    public String getStateName(String str) {
        switch (str) {
            case "10":
                return "已报修";
            case "15":
                return "已受理";
            case "20":
                return "维修到达";
            case "25":
                return "维修中";
            case "28":
                return "维修暂停";
            case "30":
                return "维修完成";
            case "40":
                return "维修验收";
            case "50":
                return "责任回复";
            case "60":
                return "课长审核";
            case "70":
                return "经理审核";
            case "95":
                return "报修结案";
            case "98":
                return "已作废";
            default:
                return "";
        }
    }
//处理俩时间显示的格式

    public String getTimeDifference(Date strDate, Date endDate, int downTimes) {
        long l = strDate.getTime() - endDate.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (downTimes != 0) {
            int days = downTimes / (60 * 24);
            downTimes -= days * 60 * 24;
            int hours = (int) Math.floor(downTimes / 60);
            downTimes -= days * 60;
            int minute = (int) (downTimes % 60);
            day = day - days;

            hour = hour - hours;
            if (hour < 0) {
                day = day - 1;
                hour = hour + 24;
            }
            min = min - minute;
            if (min < 0) {
                hour = hour - 1;
                min = min + 60;
            }
            if (day < 0) {
                hour = 0;
                min = 0;
                day = 0;

            }
            if (hour < 0) {
                hour = 0;
                min = 0;
                day = 0;
            }

        }
        if (day > 0) {
            hour += 24 * day;
        }
        return hour + "小时" + min + "分";
    }
    //获取人工费用

    public void getLaborcost(String maintenanceTime) {
        String[] maintenanceTimes = maintenanceTime.split("小时");
        String hours = maintenanceTimes[0];
        maintenanceTimes = maintenanceTimes[1].split("分");
        String min = maintenanceTimes[0];
        int hour = 0;
        if (Integer.parseInt(hours) == 0 && Integer.parseInt(min) < 30) {
            hour += 1;
        }

        if (Integer.parseInt(hours) != 0) {
            hour += Integer.parseInt(hours);
        }
        if (Integer.parseInt(min) >= 30) {
            hour += 1;
        }
        currentEntity.setLaborcosts(BigDecimal.valueOf(hour * 20));
    }

    //根据用户ID获取用户姓名
    public String getUserName(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        return s.getUsername();
    }

    public String getQueryEquipmentName() {
        return queryEquipmentName;
    }

    public void setQueryEquipmentName(String queryEquipmentName) {
        this.queryEquipmentName = queryEquipmentName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getMaintenanceSupervisor() {
        return maintenanceSupervisor;
    }

    public void setMaintenanceSupervisor(String maintenanceSupervisor) {
        this.maintenanceSupervisor = maintenanceSupervisor;
    }

    public String getQueryServiceuser() {
        return queryServiceuser;
    }

    public void setQueryServiceuser(String queryServiceuser) {
        this.queryServiceuser = queryServiceuser;
    }

    public String getQueryDeptname() {
        return queryDeptname;
    }

    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

    public List<EquipmentTrouble> getEquipmentTroubleList() {
        return equipmentTroubleList;
    }

    public void setEquipmentTroubleList(List<EquipmentTrouble> equipmentTroubleList) {
        this.equipmentTroubleList = equipmentTroubleList;
    }

    public double getMaintenanceCosts() {
        return maintenanceCosts;
    }

    public void setMaintenanceCosts(double maintenanceCosts) {
        this.maintenanceCosts = maintenanceCosts;
    }

    public String getContenct() {
        return contenct;
    }

    public void setContenct(String contenct) {
        this.contenct = contenct;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<EquipmentRepairFile> getEquipmentRepairFileList() {
        return equipmentRepairFileList;
    }

    public void setEquipmentRepairFileList(List<EquipmentRepairFile> equipmentRepairFileList) {
        this.equipmentRepairFileList = equipmentRepairFileList;
    }

    public List<SysCode> getHitchurgencyList() {
        return hitchurgencyList;
    }

    public void setHitchurgencyList(List<SysCode> hitchurgencyList) {
        this.hitchurgencyList = hitchurgencyList;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<EquipmentRepairHelpers> getDetailList4() {
        return detailList4;
    }

    public void setDetailList4(List<EquipmentRepairHelpers> detailList4) {
        this.detailList4 = detailList4;
    }

    public EquipmentRepairHelpers getCurrentDetail4() {
        return currentDetail4;
    }

    public void setCurrentDetail4(EquipmentRepairHelpers currentDetail4) {
        this.currentDetail4 = currentDetail4;
    }

    public List<SysCode> getAbrasehitchList() {
        return abrasehitchList;
    }

    public void setAbrasehitchList(List<SysCode> abrasehitchList) {
        this.abrasehitchList = abrasehitchList;
    }

}
