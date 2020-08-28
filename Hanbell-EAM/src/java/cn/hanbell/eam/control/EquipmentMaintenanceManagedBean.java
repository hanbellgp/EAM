/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairFileBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.EquipmentRepairSpareBean;
import cn.hanbell.eam.ejb.EquipmentTroubleBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairFile;
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
    private String queryEquipmentName;
    private String imageName;
    private String maintenanceSupervisor;
    private String queryServiceuser;
    private String queryDeptname;
    private double maintenanceCosts;
    private List<EquipmentTrouble> equipmentTroubleList;

    public EquipmentMaintenanceManagedBean() {
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
        queryServiceuser = getUserName(userManagedBean.getUserid());
        model.getFilterFields().put("rstatus", queryState);
        model.getFilterFields().put("company", userManagedBean.getCompany());
        model.getFilterFields().put("serviceuser", userManagedBean.getUserid());
        model.getSortFields().put("credate", "DESC");
        super.init();
    }

//保存验收数据
    public void saveAcceptance() {
        if (currentDetail3 != null) {
            currentDetail3.setCompany(userManagedBean.getCompany());
            currentDetail3.setUserno(userManagedBean.getUserid());
            currentDetail3.setStatus("N");
            currentDetail3.setContenct("发起验收");
            currentDetail3.setPid(currentEntity.getFormid());
            currentDetail3.setCredate(getDate());
            doConfirmDetail3();
        }
        createDetail();
        currentEntity.setRstatus("40");//更新状态
        super.update();//To change body of generated methods, choose Tools | Templates.

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

    //记录维修记录检查是否已维修完成
    public String recordMaintenanceProcess(String path) {
        if (currentEntity == null) {
            showErrorMsg("Error", "请选择单据！");
            return "";
        }
        if (Integer.parseInt(currentEntity.getRstatus()) < 20) {
            showErrorMsg("Error", "维修人员未到达，维修未完成不能记录维修过程！");
            return "";
        }
        if (Integer.parseInt(currentEntity.getRstatus()) >= 30) {
            showErrorMsg("Error", "该单据已记录过维修过程！");
            return "";
        }
        if (!currentEntity.getServiceuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有对应的维修人员才能填写维修过程！");
            return "";
        }
        return super.edit(path); //To change body of generated methods, choose Tools | Templates.
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
    }

    //保存时更新状态
    public void updateRstatus() {
        currentEntity.setRstatus("30");
        currentEntity.setCompletetime(getDate());
        super.update(); //To change body of generated methods, choose Tools | Templates.
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

    //转派单据
    public void handleTransferDocuments(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentEntity.setServiceuser(u.getUserid());
            currentEntity.setServiceusername(u.getUsername());
        }
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
        if (Integer.parseInt(currentEntity.getRstatus()) < 30) {
            showErrorMsg("Error", "只有维修完成,才能发起验收单");
            return "";
        }
        if (Integer.parseInt(currentEntity.getRstatus()) > 30) {
            showErrorMsg("Error", "该单据已发起过验收单");
            return "";
        }
        if (!currentEntity.getServiceuser().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "只有该单据对应的维修人，才能发起验收");
            return "";
        }
        addedDetailList.clear();
        addedDetailList2.clear();
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

        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        equipmentTroubleList = equipmentTroubleBean.findAll();
        currentEntity.setSparecost(BigDecimal.valueOf(getPartsCost()));
        createDetail3();
        return super.edit(path);

    }

    //获取人工费用
    public void getLaborcost(String maintenanceTime) {
        String[] maintenanceTimes = maintenanceTime.split("天");
        String day = maintenanceTimes[0];
        maintenanceTimes = maintenanceTimes[1].split("小时");
        String hours = maintenanceTimes[0];
        maintenanceTimes = maintenanceTimes[1].split("分");
        String min = maintenanceTimes[0];
        int hour = 0;
        if (Integer.parseInt(day) == 0 && Integer.parseInt(hours) == 0 && Integer.parseInt(min) < 30) {
            hour += 1;
        }
        if (Integer.parseInt(day) != 0) {
            hour += Integer.parseInt(day) * 24;
        }
        if (Integer.parseInt(hours) != 0) {
            hour += Integer.parseInt(hours);
        }
        if (Integer.parseInt(min) >= 30) {
            hour += 1;
        }
        currentEntity.setLaborcost(BigDecimal.valueOf(hour * 20));
    }

    @Override
    public String view(String path) {
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getCredate(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
            //根据维修时间获取人工费用
            getLaborcost(currentEntity.getMaintenanceTime());
        }
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
        getPartsCost();
        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }
//获取停机时间

    public void getDowntimes() {
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
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
            
            SimpleDateFormat f=new SimpleDateFormat("yyyyMMddHHmmss");
            imageName = f.format(getDate());
            final InputStream in = this.file.getInputstream();
            final File dir = new File(this.getAppResPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
             String [] type=fileName.split("\\.");
             imageName+="."+type[1];
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
            equipmentrepairfile.setFilepath("../../resources/app/res/"+imageName);
            equipmentrepairfile.setFilename(fileName);
            equipmentrepairfile.setFilefrom("维修图片");
            equipmentrepairfile.setStatus("Y");
            equipmentrepairfile.setSeq(seq);
            equipmentrepairfile.setPid(currentEntity.getFormid());
            equipmentrepairfile.setCredate(getDate());
            detailList.add(equipmentrepairfile);
            addedDetailList.add(equipmentrepairfile);

        }
    }

    @Override
    public void createDetail() {
        super.createDetail(); //To change body of generated methods, choose Tools | Templates.
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() {

        fileName = "equimentMaintenance" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
            cell1.setCellValue(equipmentrepair.getItemno().getFormid());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(equipmentrepair.getItemno().getAssetItem().getItemno());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(equipmentrepair.getItemno().getAssetDesc());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(equipmentrepair.getItemno().getUsername());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(equipmentrepair.getItemno().getDeptname());

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
            cell11.setCellValue(equipmentrepair.getExcepttime());
            
             Cell cell12 = row.createCell(12);
            cell12.setCellStyle(style.get("cell"));
            cell12.setCellValue(getTimeDifference(equipmentrepair.getCompletetime(), equipmentrepair.getServicearrivetime(), 0));
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
        return new String[]{"报修单号", "资产编号", "资产件号", "资产名称", "使用人", "使用部门", "进度", "维修人", "报修时间", "维修到达时间", "维修完成时间","非工作时间(分)","维修时间", "故障来源", "故障描述", "故障报警", "故障类型", "维修方式说明"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 15, 10, 15, 10, 10, 20, 20, 20,15,15, 15, 15, 15, 15, 20};
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
                model.getFilterFields().put("assetno", queryName);
            }
            if (queryEquipmentName != null && !"".equals(queryEquipmentName)) {
                model.getFilterFields().put("itemno.assetDesc", queryEquipmentName);
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("itemno.deptname", queryDeptname);
            }
            if (queryServiceuser != null && !"".equals(queryServiceuser)) {
                model.getFilterFields().put("serviceusername", queryServiceuser);
            }
            model.getFilterFields().put("company", userManagedBean.getCompany());
            model.getFilterFields().put("rstatus", queryState);
            model.getSortFields().put("credate", "DESC");

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
        String queryStateName = "";
        switch (str) {
            case "10":
                queryStateName = "已报修";
                break;
            case "20":
                queryStateName = "维修到达";
                break;
            case "30":
                queryStateName = "维修完成";
                break;
            case "40":
                queryStateName = "维修验收";
                break;
            case "50":
                queryStateName = "维修审核";
                break;
            case "95":
                queryStateName = "报修结案";
                break;
            case "98":
                queryStateName = "作废";
                break;
        }
        return queryStateName;
    }
//处理俩时间显示的格式

    public String getTimeDifference(Date strDate, Date endDate, int downTimes) {
        long l = strDate.getTime() - endDate.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
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
                s = 0;
            }
            if (hour < 0) {
                hour = 0;
                min = 0;
                day = 0;
                s = 0;
            }
        }
        return "" + day + "天" + hour + "小时" + min + "分";
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

}
