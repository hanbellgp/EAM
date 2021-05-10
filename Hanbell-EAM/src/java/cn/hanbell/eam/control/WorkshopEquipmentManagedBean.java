package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
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
    List<Number> yearsList;
    private String stayear;
    private String type;
    private List<Object[]> workshopEquipmentList;
    private String reportType;

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
        type = "半成品方型件";//默认查询半成品方型件的数据
        reportType = "H";
        // workshopEquipmentList = equipmentRepairBean.getMonthlyReport(stayear, type, reportType);

    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String edition = "";
            if (reportType.equals("G")) {
                edition = "顾问版车间设备月报模板";
            } else {
                edition = "汉钟版车间设备月报模板";
            }
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + "rpt/" + edition + ".xls");
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            Row row1;
            row = sheet.createRow(0);
            row.setHeight((short) 900);
            row1 = sheet.createRow(14);
            row1.setHeight((short) 800);
            if (workshopEquipmentList == null || workshopEquipmentList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return;
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
            Cell cellTitle = row.createCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue(stayear + "年车间设备月报-----" + type);
            Cell cellTime = row1.createCell(13);
            cellTime.setCellStyle(style.get("right"));
            String version = reportType.equals("G") ? "顾问版" : "汉钟版";
            cellTime.setCellValue(version);
            List<?> itemList = workshopEquipmentList;
            int j = 2;
            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.getRow(j);
                j++;
                row.setHeight((short) 400);
                Cell cell0 = row.getCell(0);
                cell0.setCellValue(eq[0].toString());
                for (int i = 1; i < 13; i++) {
                    cell0 = row.getCell(i);
                    if (eq[i] != null) {
                        cell0.setCellValue(Double.parseDouble(eq[i].toString()));
                    }
                }
            }
            OutputStream os = null;
            fileName = stayear + "年车间设备月报-" + type + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        workshopEquipmentList = equipmentRepairBean.getMonthlyReport(stayear, type, reportType);
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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

}
