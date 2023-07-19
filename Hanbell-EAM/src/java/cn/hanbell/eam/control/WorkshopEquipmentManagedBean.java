package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.EquipmentWorkTimeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.DepartmentBean;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "workshopEquipmentManagedBean")
@SessionScoped
public class WorkshopEquipmentManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    @EJB
    protected EquipmentWorkTimeBean equipmentWorkTimeBean;
        @EJB
    protected DepartmentBean departmentBean;
    

    List<Number> yearsList;
    private String stayear;
    private String type;
    private List<Object[]> workshopEquipmentList;
    private String deptno;
    private List<Object> deptList;

    public WorkshopEquipmentManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        Calendar date = Calendar.getInstance();
        int year = Integer.parseInt(String.valueOf(date.get(Calendar.YEAR)));
        yearsList = new ArrayList<>();
        for (int i = 2020; i <= year; i++) {
            yearsList.add(i);
        }
        stayear = String.valueOf(year);//初始化数据年份为当前年份
        deptList = new ArrayList<>();
        // workshopEquipmentList = equipmentRepairBean.getMonthlyReport(stayear, type, reportType);
        deptList = equipmentWorkTimeBean.getDept(userManagedBean.getCompany());
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String edition = "";
            edition = "课设备管理月报20237模版";
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + "rpt/" + edition + ".xls");
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            Sheet sheet1;
            sheet = workbook.getSheetAt(0);
            sheet1 = workbook.getSheet("图表");
            Row row;
            Row row1;
            Row row3;
            row = sheet.createRow(0);
            row.setHeight((short) 900);
            row1 = sheet.createRow(14);
            row1.setHeight((short) 800);
            if (workshopEquipmentList == null || workshopEquipmentList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return;
            }
            String deptName =departmentBean.findByDeptno(deptno).getDept();
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
            Cell cellTitle = row.createCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue(stayear + "课设备管理月报-----" + type+"------"+deptName);
            Cell cellTime = row1.createCell(13);
            cellTime.setCellStyle(style.get("right"));
            String version = deptno.equals("G") ? "" : "";
            cellTime.setCellValue(version);
            List<?> itemList = workshopEquipmentList;
            int j = 2;
            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.getRow(j);
                j++;
                row.setHeight((short) 400);
                for (int i = 1; i < 13; i++) {
                    Cell cell0 = row.getCell(i+1);
                    if (eq[i] != null) {
                        cell0.setCellValue(Double.parseDouble(eq[i].toString()));
                    }
                }
            }
            sheet.setForceFormulaRecalculation(true);  //强制执行该sheet中所有公式
            //获取超过60分钟的故障明细
//            List<?> itemList2 = equipmentRepairBean.getFaultDetail(type);
//            List<Object[]> faultList = (List<Object[]>) itemList2;
//            int faultCount = 87;
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            for (Object[] faDet : faultList) {
//                row3 = sheet1.createRow(faultCount);
//                row3.setHeight((short) 400);
//                faultCount++;
//                Cell cell;
//                for (int i = 0; i < 8; i++) {
//                    cell = row3.createCell(i);
//                    if (faDet[i] != null) {
//                        switch (i) {
//                            case 0:
//                                cell.setCellValue(sdf.format(faDet[i]));
//                                break;
//                            case 4:
//                                cell.setCellValue(Integer.parseInt(faDet[i].toString()));
//                                break;
//                            default:
//                                cell.setCellValue(faDet[i].toString());
//                                break;
//                        }
//                    } else {
//                        cell.setCellValue("");
//                    }
//                    cell.setCellStyle(style.get("cell"));
//                }
//            }
            OutputStream os = null;
            fileName = stayear + "课设备管理月报-" + type + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
            String fileFullName = reportOutputPath + fileName;
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
        } catch (IOException | InvalidFormatException e) {
            showErrorMsg("Error", e.toString());
        }
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

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        workshopEquipmentList = equipmentRepairBean.getMonthlyReport(stayear, deptno, userManagedBean.getCompany(), type);
    }

    public List<Number> getYearsList() {
        return yearsList;
    }

    public void setYearsList(List<Number> yearsList) {
        this.yearsList = yearsList;
    }

    public String getStayear() {
        return stayear;
    }

    public void setStayear(String stayear) {
        this.stayear = stayear;
    }

    public List<Object[]> getWorkshopEquipmentList() {
        return workshopEquipmentList;
    }

    public void setWorkshopEquipmentList(List<Object[]> workshopEquipmentList) {
        this.workshopEquipmentList = workshopEquipmentList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public List<Object> getDeptList() {
        return deptList;
    }

}
