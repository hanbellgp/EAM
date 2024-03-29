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
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
import org.apache.poi.ss.util.CellRangeAddress;
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
        standardtypeList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "standardtype");
        standardlevelList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "standardlevel");
        standardlevelList.remove(0);//计划保全不能筛选一级基准
        respondeptList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "respondept");
        frequencyunitList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "frequencyunit");
        manhourunitList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "manhourunit");
        queryState = "N";//初始查询待实施的数据
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        if (queryDateBegin != null) {
            model.getFilterFields().put("formdateBegin", queryDateBegin);
        }
        if (queryDateEnd != null) {
            model.getFilterFields().put("formdateEnd", queryDateEnd);
        }
        queryStandardLevel = "二级";//初始查询等级二级的数据
        this.model.getFilterFields().put("status", queryState);
        this.model.getFilterFields().put("deptno", userManagedBean.getCurrentUser().getDeptno().substring(0, 3));
        this.model.getFilterFields().put("standardlevel", queryStandardLevel);
        this.model.getSortFields().put("formid", "ASC");
        super.init();
    }
   @Override
    public void print() throws ParseException {

        fileName = "计划保全记录" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        Row row1;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 900);
        row1 = sheet1.createRow(1);
        row1.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        List<EquipmentAnalyResult> eResult = equipmentAnalyResultBean.findByFilters(model.getFilterFields(), model.getSortFields());
        if (eResult == null || eResult.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("计划保全记录");
        int j = 2;
        for (EquipmentAnalyResult eq : eResult) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(date1.format(eq.getFormdate()));
            cell0 = row.createCell(1);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getAssetno());
            cell0 = row.createCell(2);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getMesid());
            cell0 = row.createCell(3);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getAssetdesc());
            cell0 = row.createCell(4);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getDeptname());
            cell0 = row.createCell(5);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getStandardlevel());
            cell0 = row.createCell(6);
            cell0.setCellStyle(style.get("cell"));
            if (eq.getStartdate() != null) {
                cell0.setCellValue(date.format(eq.getStartdate()));
            }
            cell0 = row.createCell(7);
            cell0.setCellStyle(style.get("cell"));
            if (eq.getEnddate() != null) {
                cell0.setCellValue(date.format(eq.getEnddate()));
            }
            cell0 = row.createCell(8);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getDowntime());
           
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
        return new String[]{"保养计划日期", "资产编号", "MES编号", "设备名称", "所属部门", "基准等级", "开始时间", "结束时间",  "是否停机"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 20, 20, 10, 20, 20, 10, 10};
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
        headFont.setFontHeightInPoints((short) 11);
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headStyle.setFont(headFont);
        styles.put("head", headStyle);

        // 正文样式
        CellStyle cellStyle = wb.createCellStyle();
        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short) 10);
        cellStyle.setFont(cellFont);
        cellStyle.setWrapText(true);//设置自动换行
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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

        CellStyle leftStyle = wb.createCellStyle();
        leftStyle.setWrapText(true);//设置自动换行
        leftStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        leftStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        leftStyle.setBorderLeft(CellStyle.BORDER_THIN);
        leftStyle.setBorderBottom(CellStyle.BORDER_THIN);
        leftStyle.setBorderRight(CellStyle.BORDER_THIN);
        styles.put("left", leftStyle);

        CellStyle rightStyle = wb.createCellStyle();
        rightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        rightStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        rightStyle.setBorderRight(CellStyle.BORDER_THIN);
        rightStyle.setBorderBottom(CellStyle.BORDER_THIN);
        styles.put("right", rightStyle);

        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font headFont2 = wb.createFont();
        headFont2.setFontHeightInPoints((short) 20);
        headFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(headFont2);
        styles.put("title", titleStyle);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font dateFont = wb.createFont();
        dateFont.setFontHeightInPoints((short) 11);
        dateFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        dateStyle.setFont(headFont);
        styles.put("date", dateStyle);
        return styles;
    }
    @Override
    public void doConfirmDetail() {
        currentDetail.setSdate(currentEntity.getCfmdate());
        currentDetail.setEdate(getDate());
        currentEntity.setCfmdate(getDate());
        super.doConfirmDetail();//To change body of generated methods, choose Tools | Templates.
    }

    public void doConfirmDetail2() {
        super.doConfirmDetail();//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String edit(String path) {
        if (currentEntity.getStartdate()== null) {
            currentEntity.setStartdate(getDate());
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
            currentDetail.setAnalysisuser(u.getUserid());
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
