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
import cn.hanbell.eap.ejb.DepartmentBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import com.lightshell.comm.SuperEJB;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentMaintenanceManagedBean")
@SessionScoped
public class EquipmentMaintenanceManagedBean extends FormMulti3Bean<EquipmentRepair, EquipmentRepairFile, EquipmentRepairSpare, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairFileBean equipmentRepairFileBean;
    @EJB
    protected EquipmentRepairSpareBean equipmentRepairSpareBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private EquipmentTroubleBean equipmentTroubleBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    @EJB
    private DepartmentBean departmentBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;

    private String queryEquipmentName;
    private String imageName;
    private String maintenanceSupervisor;
    private String queryServiceuser;
    private String queryDeptname;
    private double maintenanceCosts;
    private double totalCost;
    private List<EquipmentTrouble> equipmentTroubleList;
    private List<SysCode> hitchurgencyList;
    private List<SysCode> abrasehitchList;
    private List<String> paramPosition = null;
    private List<EquipmentRepairFile> imageList;
    private List<EquipmentRepairFile> fileList;
    protected Class<EquipmentRepairHelpers> detailClass4;
    protected SuperEJB detailEJB4;
    protected EquipmentRepairHelpers newDetail4;
    protected List<EquipmentRepairHelpers> detailList4;
    protected List<EquipmentRepairHelpers> editedDetailList4;
    protected List<EquipmentRepairHelpers> deletedDetailList4;
    private EquipmentRepairHelpers currentDetail4;
    private List<EquipmentRepairHelpers> addedDetailList4;
    private String note;
    private boolean checkRepeat;
    private String disabledShow;
    private String userName;

    public EquipmentMaintenanceManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairFile.class, EquipmentRepairSpare.class, EquipmentRepairHis.class);
        this.detailClass4 = EquipmentRepairHelpers.class;
    }

    @Override
    public void construct() {
        this.setAddedDetailList4(new ArrayList<>());
        this.setEditedDetailList4(new ArrayList<>());
        this.setDeletedDetailList4(new ArrayList<>());
        this.setDetailList4(new ArrayList<>());
        super.construct();
    }

    @Override
    public void destory() {
        if (this.getAddedDetailList4() != null) {
            this.getAddedDetailList4().clear();
        }
        if (this.getEditedDetailList4() != null) {
            this.getEditedDetailList4().clear();
        }
        if (this.getDeletedDetailList4() != null) {
            this.getDeletedDetailList4().clear();
        }
        this.setAddedDetailList4(null);
        this.setEditedDetailList4(null);
        this.setDeletedDetailList4(null);
        super.destory();
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
        detailEJB4 = equipmentRepairHelpersBean;
        queryState = "ALL";
        queryServiceuser = getUserName(userManagedBean.getUserid()).getUsername();
        equipmentTroubleList = equipmentTroubleBean.findAll();
        this.detailAdded.put(this.detailEJB4, this.addedDetailList4);
        this.detailEdited.put(this.detailEJB4, this.editedDetailList4);
        this.detailDeleted.put(this.detailEJB4, this.deletedDetailList3);
        model.getFilterFields().put("rstatus", queryState);
        model.getFilterFields().put("company", userManagedBean.getCompany());
        model.getFilterFields().put("serviceuser", userManagedBean.getUserid());
        model.getSortFields().put("hitchtime", "DESC");
        super.init();
    }

//保存验收数据
    public void saveAcceptance() {
        createDetail();
        if (currentEntity.getRstatus().equals("30") || currentEntity.getRstatus().equals("40")) {
            currentEntity.setRstatus("40");//更新状态
        } else {
            currentEntity.setRstatus("25");//更新状态
        }
        if (!deletedDetailList4.isEmpty()) {
            equipmentRepairHelpersBean.delete(deletedDetailList4);
        }
        super.update();//To change body of generated methods, choose Tools | Templates.\
    }

    //提交
    public void submit() {
        if (Integer.parseInt(currentEntity.getRstatus()) < 30) {
            showErrorMsg("Error", "当前进度为：" + getStateName(currentEntity.getRstatus()) + ", 不能提交");
            return;
        }
        if (currentEntity.getRepaircost() == null) {
            showErrorMsg("Error", "其他费用不能为空！");
            return;
        }
        if (currentEntity.getExcepttime() == null) {
            showErrorMsg("Error", "非工作时间不能为空！");
            return;
        }
        if (currentEntity.getStopworktime() == null) {
            showErrorMsg("Error", "停工时间不能为空！");
            return;
        }
        if (currentEntity.getAbrasehitch().equals("NULL")) {
            showErrorMsg("Error", "请选择故障责任原因！");
            return;
        }
        if (currentEntity.getHitchtype().equals("NULL")) {
            showErrorMsg("Error", "请选择故障类型！");
            return;
        }
        if (currentEntity.getHitchsort1().equals("NULL")) {
            showErrorMsg("Error", "请选择故障类型1！");
            return;
        }
        if (detailList4.size() == 1) {
            showErrorMsg("Error", "请注意，未添加辅助人员！");
        }
        if (currentDetail3 != null) {
            currentDetail3.setCompany(userManagedBean.getCompany());
            currentDetail3.setUserno(userManagedBean.getUserid());
            currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
            currentDetail3.setStatus("N");
            currentDetail3.setContenct("已发起");
            currentDetail3.setPid(currentEntity.getFormid());
            currentDetail3.setCredate(getDate());
            doConfirmDetail3();
        }
        createDetail();
        if (currentEntity.getHitchdutyuser() == null) {
            currentEntity.setRstatus("60");//更新状态
        } else {
            currentEntity.setRstatus("50");//更新状态
        }

        super.update();//To change body of generated methods, choose Tools | Templates.
    }

    //记录维修时间
    public void recordTime() {
        createDetail3();
        currentDetail3.setCompany(userManagedBean.getCompany());
        currentDetail3.setUserno(userManagedBean.getUserid());
        currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
        currentDetail3.setStatus("N");
        currentDetail3.setPid(currentEntity.getFormid());
        currentDetail3.setCredate(getDate());
        if (Integer.parseInt(currentEntity.getRstatus()) <= 25) {
            currentDetail3.setContenct("维修暂停");
            currentEntity.setRstatus("28");
        } else {
            currentDetail3.setContenct("维修开始");
            currentEntity.setRstatus("25");
        }
        doConfirmDetail3();
        currentEntity.setStatus("N");
        update();
    }

    //作废
    public void invalid() {
        if (currentEntity == null) {
            showErrorMsg("Error", "请选择需要作废的单据！");
            return;
        }
        if (Integer.parseInt(currentEntity.getRstatus()) > 20) {
            showErrorMsg("Error", "该单据不能作废！");
            return;
        }
        if (!currentEntity.getServiceuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有对应的维修人才能作废！");
            return;
        }
        currentEntity.setStatus("N");//简化查询条件,此处不再提供修改状态(M)
        currentEntity.setRstatus("98");
        update();
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
    public void deleteDetail() {
        if (currentDetail != null && "报修图片".equals(currentDetail.getFilefrom())) {
            showErrorMsg("Error", "选择的图片是报修图片,不能删除");
            return;
        }
        super.deleteDetail(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteDetail2() {
        super.deleteDetail2(); //To change body of generated methods, choose Tools | Templates.
        getPartsCost();
    }

    //选中备件确认时检查及处理
    @Override
    public void doConfirmDetail2() {
        if (currentDetail2.getSpareno() == null) {
            showErrorMsg("Error", "请选择备件");
            return;
        }
        if (currentDetail2.getQty().doubleValue() <= 0) {
            showErrorMsg("Error", "输入的数量必须大于0");
            return;
        }
        currentDetail2.setPid(currentEntity.getFormid());
        currentDetail2.setCompany(currentEntity.getCompany());
        currentDetail2.setStatus("N");
        super.doConfirmDetail2();
        currentEntity.setSparecost(BigDecimal.valueOf(getPartsCost()));
        calculateTotalCost();
    }

    //记录审核时间表
    public void openDialogTime(String view) {
        if (currentEntity.getRstatus().equals("28")) {
            this.handleRecordApprovalComments(null);
            return;
        }
        super.openDialog(view);
    }

    //获取按钮是否显示
    public String getButtonDisplay() {
        if (currentEntity == null) {
            return "none";
        }
        if (currentEntity.getId() == null) {
            return "none";
        }
        if (!userManagedBean.getUserid().equals(currentEntity.getServiceuser())) {
            return "none";
        }
        if (currentEntity.getRstatus().equals("10") || currentEntity.getRstatus().equals("20")) {
            return "";
        }
        if (currentEntity.getRstatus().equals("25") || currentEntity.getRstatus().equals("28")) {
            return "";
        }
        return "none";
    }

    //转派单据前端验证
    public void openDialogCheck(String view) {
        if (this.currentEntity == null) {
            showErrorMsg("Error", "没有选择需要转派的单据");
            return;
        }
        if (Integer.parseInt(currentEntity.getRstatus()) > 20) {
            showErrorMsg("Error", "该单据已维修完成,不能转派");
            return;
        }
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        String userId = systemUserBean.findByDeptno(deptno).get(0).getUserid();
        if (currentEntity.getServiceuser().equals(userManagedBean.getUserid()) || userManagedBean.getUserid().equals(userId)) {
            super.openDialog(view); //To change body of generated methods, choose Tools | Templates.
        } else {
            showErrorMsg("Error", "只有对应的维修人或维修课长才能转派单据");
        }

    }

    //获取责任人及责任部门
    public void screeningOpenDialog(String view) {
        if (paramPosition == null) {
            paramPosition = new ArrayList<>();
        } else {
            paramPosition.clear();
        }
        paramPosition.add(currentEntity.getRepairdeptno());
        openParams.put("deptno", paramPosition);
        super.openDialog("sysuserSelect", openParams);
    }

    //转派单据
    public void handleTransferDocuments(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            userName = currentEntity.getServiceusername();
            currentEntity.setServiceuser(u.getUserid());
            currentEntity.setServiceusername(u.getUsername());
            currentEntity.setStatus("N");

        }
    }

    //确认单据转派
    public void confirmTransfer(SelectEvent event) {
        if (userName==null||userName.equals(currentEntity.getServiceusername())) {
            showErrorMsg("Error", "不能转派给自己，请重新选择");
            return;
        }
        createDetail3();
        currentDetail3.setCompany(userManagedBean.getCompany());
        currentDetail3.setUserno(userManagedBean.getUserid());
        currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
        currentDetail3.setStatus("N");
        currentDetail3.setNote(note);
        currentDetail3.setContenct("转派");
        currentDetail3.setPid(currentEntity.getFormid());
        currentDetail3.setCredate(getDate());
        currentDetail3.setOptuser(getUserName(userManagedBean.getUserid()).getUsername());
        doConfirmDetail3();
        note = null;
        userName=null;
        update();
        showInfoMsg("Info", " 已成功转派给：" + currentEntity.getServiceusername());
    }

    //记录审批意见
    public void handleRecordApprovalComments(SelectEvent event) {
        determine();
        super.update();
    }

    //发起维修验收
    public String initiateMaintenanceAcceptance(String path) {
        if (currentEntity == null) {
            if (this.currentEntity == null) {
                showErrorMsg("Error", "请选择一条数据");
                return "";
            }
        }

        if (Integer.parseInt(currentEntity.getRstatus()) < 20) {
            showErrorMsg("Error", "维修未到达，不能填写验收数据");
            return "";
        }

        if (Integer.parseInt(currentEntity.getRstatus()) > 40) {
            showErrorMsg("Error", "该单据已发起过验收单");
            return "";
        }
        if (!currentEntity.getServiceuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有该单据对应的维修人，才能发起验收");
            return "";
        }
        addedDetailList.clear();
        addedDetailList2.clear();
        addedDetailList4.clear();
        deletedDetailList.clear();
        deletedDetailList4.clear();
        deletedDetailList2.clear();
        //获取联络时间
        if (currentEntity.getServicearrivetime() != null) {
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getHitchtime(), 0));
        }
        //获取维修时间
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
        }

        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }

        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        //获取故障类型
        hitchurgencyList = sysCodeBean.getTroubleNameList("RD", "hitchurgency");
        //获取故障责任原因
        abrasehitchList = sysCodeBean.getTroubleNameList("RD", "dutycause");
        currentEntity.setSparecost(BigDecimal.valueOf(getPartsCost()));

        createDetail3();
        //检查是否存在主维修人，不存在添加
        if (equipmentRepairHelpersBean.findByPId(currentEntity.getFormid()).isEmpty() && Integer.parseInt(currentEntity.getRstatus()) >= 30) {
            EquipmentRepairHelpers equipmentRepairHelpers = new EquipmentRepairHelpers();
            equipmentRepairHelpers.setPid(currentEntity.getFormid());
            equipmentRepairHelpers.setSeq(1);
            equipmentRepairHelpers.setCompany(currentEntity.getCompany());
            equipmentRepairHelpers.setCurnode(currentEntity.getServiceuser());
            equipmentRepairHelpers.setCurnode2(currentEntity.getServiceusername());
            String[] maintenanceTimes = currentEntity.getMaintenanceTime().split("小时");
            String hours = maintenanceTimes[0];
            maintenanceTimes = maintenanceTimes[1].split("分");
            int min = Integer.parseInt(maintenanceTimes[0]);
            if (Integer.parseInt(hours) != 0) {
                min += Integer.parseInt(hours) * 60;
            }
            equipmentRepairHelpers.setCredate(getDate());
            equipmentRepairHelpers.setUserno(String.valueOf(min));
            equipmentRepairHelpers.setRtype("0");
            equipmentRepairHelpers.setStatus("N");
            addedDetailList4.add(equipmentRepairHelpers);
            detailList4.add(equipmentRepairHelpers);
        }
        getTotalLaborcost();
        calculateTotalCost();
        if (detailList3.size() > 0) {
            disabledShow = "";
        } else {
            disabledShow = "none";
        }
        return super.edit(path);
    }

    //获取维修人的总人工费用
    public void getTotalLaborcost() {

        if (!detailList4.isEmpty()) {
            int min = 0;
            for (EquipmentRepairHelpers equipmentrepairHelpers : detailList4) {
                min += Integer.parseInt(equipmentrepairHelpers.getUserno());
            }
            currentEntity.setLaborcost(sysCodeBean.findBySyskindAndCode("RD", "laborcost").getCvalue());
            BigDecimal b1 = new BigDecimal(Double.toString(Double.parseDouble(currentEntity.getLaborcost())));
            BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(String.valueOf(min))));
            currentEntity.setLaborcosts(b1.multiply(b2));
        }
    }

    @Override
    public String view(String path) {
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getHitchtime(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
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
        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }
//获取停机时间

    public void getDowntimes() {
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
    }

//获取零件费用
    public Double getPartsCost() {
        maintenanceCosts = 0;
        detailList2.forEach(equipmentrepair -> {
            BigDecimal price = equipmentrepair.getUprice();
            maintenanceCosts += equipmentrepair.getQty().doubleValue() * price.doubleValue();
        });
        currentEntity.setSparecost(BigDecimal.valueOf(maintenanceCosts));
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

            equipmentrepairfile.setStatus("Y");
            equipmentrepairfile.setSeq(seq);
            equipmentrepairfile.setPid(currentEntity.getFormid());
            equipmentrepairfile.setCredate(getDate());
            String temp = fileName.split("\\.")[1];
            //判断上传的文件是否是图片
            if ("gif".equals(temp) || "jpg".equals(temp) || "png".equals(temp)) {
                equipmentrepairfile.setFilefrom("维修图片");

            } else {
                equipmentrepairfile.setFilefrom("维修文件");
            }
            detailList.add(equipmentrepairfile);
            addedDetailList.add(equipmentrepairfile);
        }
    }

    @Override
    public void createDetail() {
        super.createDetail(); //To change body of generated methods, choose Tools | Templates.
    }

    public void createDetail4() throws InstantiationException, IllegalAccessException {
        if (Integer.parseInt(currentEntity.getRstatus()) < 30) {
            this.showWarnMsg("Warn", "未维修完成，不能添加维修人员！！");
            return;
        }
        if (this.getNewDetail4() == null) {
            (this.newDetail4 = this.detailClass4.newInstance()).setSeq(this.getMaxSeq(this.detailList4));
        }
        this.setCurrentDetail4(this.newDetail4);
    }

    public void deleteDetail4() {

        if (this.currentDetail4 != null) {
            if (currentDetail4.getRtype().equals("0")) {
                this.showWarnMsg("Warn", "主要维修人不能删除");
                return;
            }
            try {
                this.deleteDetail4(this.currentDetail4);
                this.setCurrentDetail4(null);
                getTotalLaborcost();
                calculateTotalCost();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage((String) null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", e.getMessage()));
            }
        } else {
            this.showWarnMsg("Warn", "\u6ca1\u6709\u53ef\u5220\u9664\u6570\u636e");
        }
    }

    public void deleteDetail4(EquipmentRepairHelpers entity) {

        if (entity != null) {
            try {
                if (this.addedDetailList4.contains(entity)) {
                    this.addedDetailList4.remove(entity);
                } else {
                    if (this.editedDetailList4.contains(entity)) {
                        this.editedDetailList4.remove(entity);
                    }
                    if (!this.deletedDetailList4.contains(entity)) {
                        this.deletedDetailList4.add(entity);
                    }
                }
                if (this.detailList4.contains(entity)) {
                    this.getDetailList4().remove(entity);
                }
                this.showInfoMsg("Info", "\u5220\u9664\u6210\u529f");
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage((String) null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", e.getMessage()));
            }
        } else {
            this.showWarnMsg("Warn", "\u6ca1\u6709\u53ef\u5220\u9664\u6570\u636e");
        }
    }

    public void doConfirmDetail4() {
        if (currentDetail4.getCurnode2().equals(currentEntity.getServiceusername())) {
            this.showWarnMsg("Warn", "已存在主维修人，无需添加！！！");
            return;
        }
        if (this.newDetail4 != null && this.newDetail4.equals(this.currentDetail4)) {
            if (!this.addedDetailList4.contains(this.newDetail4) && !this.detailList4.contains(this.newDetail4)) {
                this.addedDetailList4.add(this.newDetail4);
                this.detailList4.add(this.newDetail4);
                this.setNewDetail4(null);
                this.setCurrentDetail4(null);
            }
        } else if (this.currentDetail4 != null && !this.addedDetailList4.contains(this.currentDetail4)) {
            this.editedDetailList4.add(this.currentDetail4);
            this.setCurrentDetail4(null);
        }
        getTotalLaborcost();//获取人工总费用
        calculateTotalCost();
    }

    @Override
    protected boolean doAfterUpdate() throws Exception {
        this.getAddedDetailList4().clear();
        this.getEditedDetailList4().clear();
        this.getDeletedDetailList4().clear();
        this.setNewDetail4(null);
        this.setCurrentDetail4(null);
        return super.doAfterUpdate();
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (this.currentEntity != null) {
            if (this.addedDetailList4 != null && !this.addedDetailList4.isEmpty()) {
                this.addedDetailList4.forEach(detail -> {
                    detail.setPid(this.currentEntity.getFormid());
                });
            }
            if (this.editedDetailList4 != null && !this.editedDetailList4.isEmpty()) {
                this.editedDetailList4.forEach(detail -> {
                    detail.setPid((this.currentEntity).getFormid());
                });
            }
            return super.doBeforeUpdate();
        }
        return false;
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() {

        fileName = "维修目录表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getInventoryWidth();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        List<EquipmentRepair> equipmentrepairList = equipmentRepairBean.getEquipmentRepairList(model.getFilterFields(), model.getSortFields());
        int j = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (EquipmentRepair equipmentrepair : equipmentrepairList) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(equipmentrepair.getFormid());
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(equipmentrepair.getAssetno().getFormid());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(equipmentrepair.getAssetno().getAssetItem().getItemno());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(equipmentrepair.getAssetno().getAssetDesc());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(equipmentrepair.getAssetno().getUsername());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(equipmentrepair.getAssetno().getDeptname());

            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(getStateName(equipmentrepair.getRstatus()));

            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            cell7.setCellValue(equipmentrepair.getServiceusername());
            String credate = sdf.format(equipmentrepair.getHitchtime().getTime());
            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            cell8.setCellValue(credate);

            Cell cell9 = row.createCell(9);
            cell9.setCellStyle(style.get("cell"));
            if (equipmentrepair.getServicearrivetime() != null) {
                String servicearrivetime = sdf.format(equipmentrepair.getServicearrivetime().getTime());
                cell9.setCellValue(servicearrivetime);
            }

            Cell cell10 = row.createCell(10);
            cell10.setCellStyle(style.get("cell"));
            if (equipmentrepair.getCompletetime() != null) {
                String completetime = sdf.format(equipmentrepair.getCompletetime().getTime());
                cell10.setCellValue(completetime);
            }
            Cell cell11 = row.createCell(11);
            cell11.setCellStyle(style.get("cell"));
            if (equipmentrepair.getExcepttime() != null) {
                cell11.setCellValue(equipmentrepair.getExcepttime());
            }
            Cell cell12 = row.createCell(12);
            cell12.setCellStyle(style.get("cell"));
            if (equipmentrepair.getCompletetime() != null && equipmentrepair.getServicearrivetime() != null) {
                cell12.setCellValue(getTimeDifference(equipmentrepair.getCompletetime(), equipmentrepair.getServicearrivetime(), 0));
            }
            Cell cell13 = row.createCell(13);
            cell13.setCellStyle(style.get("cell"));
            cell13.setCellValue(getTroubleName(equipmentrepair.getTroublefrom()));

            Cell cell14 = row.createCell(14);
            cell14.setCellStyle(style.get("cell"));
            cell14.setCellValue(equipmentrepair.getHitchdesc());

            Cell cell15 = row.createCell(15);
            cell15.setCellStyle(style.get("cell"));
            cell15.setCellValue(equipmentrepair.getHitchalarm());

            Cell cell16 = row.createCell(16);
            cell16.setCellStyle(style.get("cell"));
            String hitchtype = "";
            if ("0".equals(equipmentrepair.getHitchtype())) {
                hitchtype = "一般故障";
            } else if ("1".equals(equipmentrepair.getHitchtype())) {
                hitchtype = "严重故障";
            }
            cell16.setCellValue(hitchtype);
            Cell cell17 = row.createCell(17);
            cell17.setCellStyle(style.get("cell"));
            cell17.setCellValue(equipmentrepair.getRepairmethod());

        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(fileFullName);
            workbook.write(os);
            this.reportViewPath = reportViewContext + fileName;
            this.preview();
        } catch (Exception ex) {
            showErrorMsg("Error", ex.getMessage());
        } finally {
            try {
                if (null != os) {
                    os.flush();
                    os.close();
                }
            } catch (IOException ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        }
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"报修单号", "资产编号", "资产件号", "资产名称", "使用人", "使用部门", "进度", "维修人", "报修时间", "维修到达时间", "维修完成时间", "非工作时间(分)", "维修时间", "故障来源", "故障描述", "故障报警", "故障类型", "维修方式说明"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 15, 10, 15, 10, 10, 20, 20, 20, 15, 15, 15, 15, 15, 15, 20};
    }

    /**
     * 设置导出EXCEL表格样式
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new LinkedHashMap<>();
        // 文件头样式
        CellStyle headStyle = wb.createCellStyle();
        headStyle.setWrapText(true);//设置自动换行
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());//单元格背景颜色
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderTop(CellStyle.BORDER_THIN);
        headStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font headFont = wb.createFont();
        headFont.setFontHeightInPoints((short) 12);
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headStyle.setFont(headFont);
        styles.put("head", headStyle);

        // 正文样式
        CellStyle cellStyle = wb.createCellStyle();
        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short) 10);
        cellStyle.setFont(cellFont);
        cellStyle.setWrapText(true);//设置自动换行
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());//单元格背景颜色
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", cellStyle);

        return styles;
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
                model.getFilterFields().put("assetno.assetDesc", queryEquipmentName);
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("assetno.deptname", queryDeptname);
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

    //选择责任人后数据处理
    public void handleDialogSysuserWhenNew(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setHitchdutyuser(u.getUserid());
            currentEntity.setHitchdutyusername(u.getUsername());
            currentEntity.setHitchdutydeptno(this.getDepartment(u.getUserid()).getDeptno());
            currentEntity.setHitchdutydeptname(this.getDepartment(u.getUserid()).getDept());
        }
    }

    public void handleDialogUserWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail4.setCurnode(u.getUserid());
            currentDetail4.setCurnode2(u.getUsername());
            String[] maintenanceTimes = currentEntity.getMaintenanceTime().split("小时");
            String hours = maintenanceTimes[0];
            maintenanceTimes = maintenanceTimes[1].split("分");
            int min = Integer.parseInt(maintenanceTimes[0]);
            if (Integer.parseInt(hours) != 0) {
                min += Integer.parseInt(hours) * 60;
            }
            currentDetail4.setUserno(String.valueOf(min));
            currentDetail4.setStatus("N");
            currentDetail4.setCredate(getDate());
            currentDetail4.setRtype("1");
            currentDetail4.setCompany(userManagedBean.getCompany());
        }
    }

    //获取部门
    public Department getDepartment(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        Department dept = departmentBean.findByDeptno(s.getDeptno());
        return dept;
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

    public String getButtonName() {
        if (currentEntity == null) {
            return "开始维修";
        }
        if (currentEntity.getId() == null) {
            return "开始维修";
        }
        switch (currentEntity.getRstatus()) {
            case "10":
                return "报修受理";
            case "25":
                return "暂停维修";
            case "28":
                return "开始维修";
            case "20":
                return "暂停维修";
            default:
                break;
        }
        return "none";
    }

    //获取审批名字
    public String getNoteName() {
        switch (currentEntity.getRstatus()) {
            case "10":
                return "预计到达时间及原因";
            case "20":
                return "暂停原因";
            case "25":
                return "暂停原因";
            default:
                break;
        }
        return "";
    }

    //关闭时添加记录
    public void determine() {
        createDetail3();
        currentDetail3.setCompany(userManagedBean.getCompany());
        currentDetail3.setUserno(userManagedBean.getUserid());
        currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
        currentDetail3.setStatus("N");
        currentDetail3.setPid(currentEntity.getFormid());
        currentDetail3.setCredate(getDate());
        currentDetail3.setNote(note);
        currentEntity.setStatus("N");
        if (currentEntity.getRstatus().equals("10")) {
            currentDetail3.setContenct("已受理");
            currentEntity.setRstatus("15");
        } else if (currentEntity.getRstatus().equals("25") || currentEntity.getRstatus().equals("20")) {
            currentDetail3.setContenct("暂停维修");
            currentEntity.setRstatus("28");
        } else {
            currentDetail3.setContenct("开始维修");
            currentEntity.setRstatus("25");
        }
        doConfirmDetail3();
        note = null;
    }

    //根据用户ID获取用户姓名
    public SystemUser getUserName(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        return s;
    }
    //根据用户ID查询用户手机号码

    public String getPhone(String userId) {
        return getUserName(userId).getPhone();
    }

//    //获取存入的remark的boolean类型
//    public boolean getRemarkBoolean() {
//        boolean bool;
//                
//        if (currentEntity.getRemark() != null) {
//            checkRepeat=Boolean.parseBoolean(currentEntity.getRemark());
//            
//            return checkRepeat;
//        } else if (currentEntity.getRemark() == null) {
//            checkRepeat=false;
//            if (checkRepeat) {
//                currentEntity.setRemark("true");
//            }else{
//                currentEntity.setRemark("false");
//            }
//        }
//        return  checkRepeat;
//    }
    @Override
    public void create() {
        super.create();
        if (this.detailList4 != null && !this.detailList4.isEmpty()) {
            this.detailList4.clear();
        }
        if (this.addedDetailList4 != null && !this.addedDetailList4.isEmpty()) {
            this.addedDetailList4.clear();
        }
        if (this.editedDetailList4 != null && !this.editedDetailList4.isEmpty()) {
            this.editedDetailList4.clear();
        }
        if (this.deletedDetailList4 != null && !this.deletedDetailList4.isEmpty()) {
            this.deletedDetailList4.clear();
        }
    }

    @Override
    public void setCurrentEntity(final EquipmentRepair currentEntity) {
        super.setCurrentEntity(currentEntity);
        if (this.detailList4 != null) {
            this.detailList4.clear();
        }
        if (currentEntity != null && currentEntity.getFormid() != null) {
            this.setDetailList4(this.detailEJB4.findByPId(currentEntity.getFormid()));
        }
        if (this.detailList4 == null && this.detailList4 == null) {
            this.detailList4 = new ArrayList<>();
        }
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

    public List<String> getParamPosition() {
        return paramPosition;
    }

    public void setParamPosition(List<String> paramPosition) {
        this.paramPosition = paramPosition;
    }

    public EquipmentRepairHelpers getCurrentDetail4() {
        return currentDetail4;
    }

    public void setCurrentDetail4(EquipmentRepairHelpers currentDetail4) {
        this.currentDetail4 = currentDetail4;
    }

    public List<EquipmentRepairHelpers> getAddedDetailList4() {
        return addedDetailList4;
    }

    public void setAddedDetailList4(List<EquipmentRepairHelpers> addedDetailList4) {
        this.addedDetailList4 = addedDetailList4;
    }

    public EquipmentRepairHelpers getNewDetail4() {
        return newDetail4;
    }

    public void setNewDetail4(EquipmentRepairHelpers newDetail4) {
        this.newDetail4 = newDetail4;
    }

    public List<EquipmentRepairHelpers> getDetailList4() {
        return detailList4;
    }

    public void setDetailList4(List<EquipmentRepairHelpers> detailList4) {
        this.detailList4 = detailList4;
    }

    public List<EquipmentRepairHelpers> getEditedDetailList4() {
        return editedDetailList4;
    }

    public void setEditedDetailList4(List<EquipmentRepairHelpers> editedDetailList4) {
        this.editedDetailList4 = editedDetailList4;
    }

    public List<EquipmentRepairHelpers> getDeletedDetailList4() {
        return deletedDetailList4;
    }

    public void setDeletedDetailList4(List<EquipmentRepairHelpers> deletedDetailList4) {
        this.deletedDetailList4 = deletedDetailList4;
    }

    public List<EquipmentRepairFile> getImageList() {
        return imageList;
    }

    public void setImageList(List<EquipmentRepairFile> imageList) {
        this.imageList = imageList;
    }

    public List<EquipmentRepairFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<EquipmentRepairFile> fileList) {
        this.fileList = fileList;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isCheckRepeat() {
        return checkRepeat;
    }

    public void setCheckRepeat(boolean checkRepeat) {
        this.checkRepeat = checkRepeat;
    }

    public List<SysCode> getAbrasehitchList() {
        return abrasehitchList;
    }

    public void setAbrasehitchList(List<SysCode> abrasehitchList) {
        this.abrasehitchList = abrasehitchList;
    }

    public String getDisabledShow() {
        return disabledShow;
    }

    public void setDisabledShow(String disabledShow) {
        this.disabledShow = disabledShow;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
