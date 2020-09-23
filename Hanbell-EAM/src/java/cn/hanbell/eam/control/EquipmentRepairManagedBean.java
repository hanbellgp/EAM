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
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairFile;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import cn.hanbell.eam.entity.EquipmentRepairSpare;
import cn.hanbell.eam.entity.EquipmentTrouble;
import cn.hanbell.eam.entity.SysCode;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.lazy.EquipmentRepairModel;
import cn.hanbell.eam.web.FormMulti3Bean;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.DepartmentBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
@ManagedBean(name = "equipmentRepairManagedBean")
@SessionScoped
public class EquipmentRepairManagedBean extends FormMulti3Bean<EquipmentRepair, EquipmentRepairFile, EquipmentRepairSpare, EquipmentRepairHis> {

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
    private String queryRepairuser;
    private String queryDeptname;
    private List<SystemUser> userList;
    private List<SysCode> troubleFromList;
    private List<SysCode> hitchurgencyList;
    private String maintenanceSupervisor;
    private List<EquipmentTrouble> equipmentTroubleList;
    private String contenct;
    private String note;
    protected List<EquipmentRepairHelpers> detailList4;
    private EquipmentRepairHelpers currentDetail4;

    public EquipmentRepairManagedBean() {
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
        queryRepairuser = getUserName(userManagedBean.getUserid()).getUsername();
        equipmentTroubleList = equipmentTroubleBean.findAll();
        model.getFilterFields().put("rstatus", queryState);
        model.getFilterFields().put("repairuser", userManagedBean.getUserid());
        model.getFilterFields().put("company", userManagedBean.getCompany());
        model.getSortFields().put("hitchtime", "DESC");
        super.init();
    }

    //发起故障时给相应的字段赋值
    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
        newEntity.setHitchtime(getDate());
        newEntity.setRstatus("10");
        newEntity.setRepairuser(userManagedBean.getUserid());
        newEntity.setRepairusername(this.getUserName(userManagedBean.getUserid()).getUsername());
        newEntity.setRepairdeptno(this.getDepartment(userManagedBean.getUserid()).getDeptno());
        newEntity.setRepairdeptname(this.getDepartment(userManagedBean.getUserid()).getDept());
        troubleFromList = sysCodeBean.getTroubleNameList("RD", "faultType");
        hitchurgencyList = sysCodeBean.getTroubleNameList("RD", "hitchurgency");
    }

//保存前作的数据处理
    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getAssetno() == null) {
            showErrorMsg("Error", "请选择资产编号！");
            return false;
        }
        if (newEntity.getServiceuser() == null) {
            showErrorMsg("Error", "请选择维修人！");
            return false;
        }
        if (currentEntity.getRepairmethodtype().equals("2")) {
            newEntity.setServicearrivetime(getDate());
            newEntity.setServiceuser(userManagedBean.getUserid());
            newEntity.setRstatus("20");
            newEntity.setServiceusername(getUserName(userManagedBean.getUserid()).getUsername());
        }

        String formid = this.superEJB.getFormId(newEntity.getFormdate(), "PR", "YYMM", 4);
        this.newEntity.setFormid(formid);
        if (this.addedDetailList != null && !this.addedDetailList.isEmpty()) {
            this.addedDetailList.stream().forEach((detail) -> {
                detail.setPid(newEntity.getFormid());

            });
        }

        if (this.editedDetailList != null && !this.editedDetailList.isEmpty()) {
            this.editedDetailList.stream().forEach((detail) -> {
                detail.setPid(newEntity.getFormid());
            });
        }
        showInfoMsg("Info", "报修记录号为:" + currentEntity.getFormid());
        return true;
    }

    //作废
    public void invalid() {

        if (currentEntity == null) {
            showErrorMsg("Error", "请选择需要作废的单据！");
            return;
        }
        if (!currentEntity.getRepairuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有对应的报修人才能作废此单据！");
            return;
        }
        if (Integer.parseInt(currentEntity.getRstatus()) > 20) {
            showErrorMsg("Error", "该单据不能作废！");
            return;
        }

        currentEntity.setRstatus("98");
        update();
    }

    //确认维修完成
    public void confirmCompleted() {

        if (currentEntity == null) {
            showErrorMsg("Error", "请选择单据！");
            return;
        }
        if (!currentEntity.getRepairdeptno().equals(getDepartment(userManagedBean.getUserid()).getDeptno())) {
            showErrorMsg("Error", "只有对应的报修人，及报修人所在部门的人员才能确认！");
            return;
        }
        if (Integer.parseInt(currentEntity.getRstatus()) < 20 || Integer.parseInt(currentEntity.getRstatus()) >= 30) {
            showErrorMsg("Error", "该单据未维修，或单据已确认维修完成！");
            return;
        }
        createDetail3();
        currentDetail3.setCompany(userManagedBean.getCompany());
        currentDetail3.setUserno(userManagedBean.getUserid());
        currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
        currentDetail3.setStatus("N");

        currentDetail3.setPid(currentEntity.getFormid());
        currentDetail3.setCredate(getDate());

        //当维修为现场维修时,确认完成就结案
        if (currentEntity.getRepairmethodtype().equals("2")) {
            currentEntity.setRstatus("95");
            currentDetail3.setContenct("维修结案");
        } else {
            currentEntity.setRstatus("30");
            currentDetail3.setContenct("维修完成");
        }
        doConfirmDetail3();
        int min = 0;
        //获取暂停的时间
        for (int i = 0; i < detailList3.size(); i++) {
            if (detailList3.get(i).getContenct().equals("暂停维修")) {
                String data = this.getTimeDifference(detailList3.get(i + 1).getCredate(), detailList3.get(i).getCredate(), 0);//获取暂停到结束的时间
                min += Integer.parseInt(this.getMin(data));
            }
        }
        currentEntity.setExcepttime(min);
        currentEntity.setCompletetime(getDate());
        update();
    }

    //责任回复
    public String responsibilitySet(String path) {
        if (currentEntity == null) {
            showErrorMsg("Error", "请选择单据！");
            return "";
        }
        if (!currentEntity.getRstatus().equals("50")) {
            showErrorMsg("Error", "当前状态为：" + getStateName(currentEntity.getRstatus()) + ",不能填写责任回复");
            return "";
        }
        if (!currentEntity.getHitchdutyuser().equals(userManagedBean.getUserid()) || "2079".equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有对应的责任人才能填写责任回复！");
            return "";
        }
        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        //获取联络时间
        currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getCredate(), 0));

        //获取维修时间
        currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
        //根据维修时间获取人工费用
        getLaborcost(currentEntity.getMaintenanceTime());
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
        detailList4 = equipmentRepairHelpersBean.findByPId(currentEntity.getFormid());
        return super.edit(path);
    }

    //保存审核数据
    public void saveAudit() {
        if (currentEntity.getRstatus().equals("50")) {
            createDetail3();
            currentDetail3.setUserno(userManagedBean.getUserid());
            currentDetail3.setCompany(userManagedBean.getCompany());
            currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
            currentDetail3.setCredate(getDate());
            currentDetail3.setStatus("N");
            currentDetail3.setContenct(contenct);
            currentDetail3.setNote(note);
            if (currentDetail3.getContenct().equals("接受")) {
                currentEntity.setRstatus("60");
            } else {
                currentEntity.setRstatus("40");
            }
            contenct = null;
            note = null;
            super.doConfirmDetail3();
            currentEntity.setStatus("N");
            super.update();//To change body of generated methods, choose Tools | Templates.
        } else {
            showErrorMsg("Error", "已完成本次审核！");
        }

    }

    //获取时间差
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

    private String getMin(String str) {
        if (!"".equals(str)) {
            String[] maintenanceTimes = str.split("小时");
            String hours = maintenanceTimes[0];
            maintenanceTimes = maintenanceTimes[1].split("分");
            String min = maintenanceTimes[0];
            if (Integer.parseInt(hours) != 0) {
                min += Integer.parseInt(hours) * 60;
                return min;
            }
            return min;
        }
        return "0";
    }

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
            equipmentrepairfile.setFilefrom("报修图片");
            equipmentrepairfile.setStatus("N");
            equipmentrepairfile.setSeq(seq);
            equipmentrepairfile.setCredate(getDate());
            detailList.add(equipmentrepairfile);
            addedDetailList.add(equipmentrepairfile);
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            AssetCard e = (AssetCard) event.getObject();
            newEntity.setAssetno(e);
            newEntity.setServiceuser(e.getRepairuser());
            newEntity.setServiceusername(e.getRepairusername());
            newEntity.setItemno(e.getAssetItem().getItemno());
        }
    }

    public void handleDialogReturnSysuserWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            newEntity.setServiceuser(u.getUserid());
            newEntity.setServiceusername(u.getUsername());
        }
    }

    public void handleDialogReturnSysuserWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setServiceuser(u.getUserid());
            currentEntity.setServiceusername(u.getUsername());
        }
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

    //确认维修到达时间
    public void confirmRepairmanArrivalTime() {
        if (this.currentEntity == null) {
            showErrorMsg("Error", "没有选择单据");
            return;
        }
        if (!currentEntity.getRepairuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有对应的报修人才能确认到达时间");
            return;
        }
        if (currentEntity.getServicearrivetime() != null) {
            showErrorMsg("Error", "已确认过维修人到达时间");
            return;
        }
        createDetail3();
        currentDetail3.setCompany(userManagedBean.getCompany());
        currentDetail3.setUserno(userManagedBean.getUserid());
        currentDetail3.setCurnode(getStateName(currentEntity.getRstatus()));
        currentDetail3.setStatus("N");
        currentDetail3.setContenct("维修到达");
        currentDetail3.setPid(currentEntity.getFormid());
        currentDetail3.setCredate(getDate());
        doConfirmDetail3();
        currentEntity.setRstatus("20");
        currentEntity.setServicearrivetime(getDate());
        currentEntity.setStatus("N");
        super.update();
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() {

        fileName = "报修目录表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
            String credate = sdf.format(equipmentrepair.getCredate().getTime());
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
            cell11.setCellValue(getTroubleName(equipmentrepair.getTroublefrom()));

            Cell cell12 = row.createCell(12);
            cell12.setCellStyle(style.get("cell"));
            cell12.setCellValue(equipmentrepair.getHitchdesc());

            Cell cell13 = row.createCell(13);
            cell13.setCellStyle(style.get("cell"));
            cell13.setCellValue(equipmentrepair.getHitchalarm());

            Cell cell14 = row.createCell(14);
            cell14.setCellStyle(style.get("cell"));
            String hitchtype = "";
            if ("0".equals(equipmentrepair.getHitchtype())) {
                hitchtype = "一般故障";
            } else if ("1".equals(equipmentrepair.getHitchtype())) {
                hitchtype = "严重故障";
            }
            cell14.setCellValue(hitchtype);
            Cell cell15 = row.createCell(15);
            cell15.setCellStyle(style.get("cell"));
            cell15.setCellValue(equipmentrepair.getRepairmethod());

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
        return new String[]{"报修单号", "资产编号", "资产件号", "资产名称", "使用人", "使用部门", "进度", "维修人", "报修时间", "维修到达时间", "维修完成时间", "故障来源", "故障描述", "故障报警", "故障类型", "维修方式说明"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 15, 10, 15, 10, 10, 20, 20, 20, 15, 15, 15, 15, 20};
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
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("assetno.deptname", queryDeptname);
            }
            if (queryEquipmentName != null && !"".equals(queryEquipmentName)) {
                model.getFilterFields().put("assetno.assetDesc", queryEquipmentName);
            }

            if (queryRepairuser != null && !"".equals(queryRepairuser)) {
                model.getFilterFields().put("repairusername", queryRepairuser);
            }
            model.getFilterFields().put("company", userManagedBean.getCompany());
            model.getFilterFields().put("rstatus", queryState);
            model.getSortFields().put("hitchtime", "DESC");

        }
    }
//获取故障来源的名字

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

    //获取部门
    public Department getDepartment(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        Department dept = departmentBean.findByDeptno(s.getDeptno());
        return dept;
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

    public String getQueryEquipmentName() {
        return queryEquipmentName;
    }

    public void setQueryEquipmentName(String queryEquipmentName) {
        this.queryEquipmentName = queryEquipmentName;
    }

    public String getQueryRepairuser() {
        return queryRepairuser;
    }

    public void setQueryRepairuser(String queryRepairuser) {
        this.queryRepairuser = queryRepairuser;
    }

    public String getQueryDeptname() {
        return queryDeptname;
    }

    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

    public List<SystemUser> getUserList() {
        return userList;
    }

    public void setUserList(List<SystemUser> userList) {
        this.userList = userList;
    }

    public List<SysCode> getTroubleFromList() {
        return troubleFromList;
    }

    public void setTroubleFromList(List<SysCode> troubleFromList) {
        this.troubleFromList = troubleFromList;
    }

    public List<SysCode> getHitchurgencyList() {
        return hitchurgencyList;
    }

    public void setHitchurgencyList(List<SysCode> hitchurgencyList) {
        this.hitchurgencyList = hitchurgencyList;
    }

    public List<EquipmentTrouble> getEquipmentTroubleList() {
        return equipmentTroubleList;
    }

    public void setEquipmentTroubleList(List<EquipmentTrouble> equipmentTroubleList) {
        this.equipmentTroubleList = equipmentTroubleList;
    }

    public String getMaintenanceSupervisor() {
        return maintenanceSupervisor;
    }

    public void setMaintenanceSupervisor(String maintenanceSupervisor) {
        this.maintenanceSupervisor = maintenanceSupervisor;
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

}
