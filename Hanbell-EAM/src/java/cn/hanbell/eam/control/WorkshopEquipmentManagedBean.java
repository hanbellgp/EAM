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
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + "rpt/"+edition+".xls");
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            Row row1;
            row = sheet.createRow(0);
            row.setHeight((short) 900);
            row1 = sheet.createRow(1);
            row1.setHeight((short) 800);
            if (workshopEquipmentList == null || workshopEquipmentList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return;
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
            Cell cellTitle = row.createCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue(stayear + "年车间设备月报-----" + type);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
            Cell cellTime = row1.createCell(0);
            cellTime.setCellStyle(style.get("date"));
            String version = reportType.equals("G") ? "顾问版" : "汉钟版";
            cellTime.setCellValue(version);
            List<?> itemList = workshopEquipmentList;
            int j = 3;
            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.createRow(j);
                j++;
                row.setHeight((short) 400);
                Cell cell0 = row.createCell(0);
                cell0.setCellStyle(style.get("left"));
                cell0.setCellValue(eq[0].toString());
                Cell cell1 = row.createCell(1);
                cell1.setCellStyle(style.get("cell"));
                cell1.setCellValue(Double.parseDouble(eq[1].toString()));
                Cell cell2 = row.createCell(2);
                cell2.setCellStyle(style.get("cell"));
                cell2.setCellValue(Double.parseDouble(eq[2].toString()));
                Cell cell3 = row.createCell(3);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(Double.parseDouble(eq[3].toString()));
                Cell cell4 = row.createCell(4);
                cell4.setCellStyle(style.get("cell"));
                cell4.setCellValue(Double.parseDouble(eq[4].toString()));
                Cell cell5 = row.createCell(5);
                cell5.setCellStyle(style.get("cell"));
                cell5.setCellValue(Double.parseDouble(eq[5].toString()));
                Cell cell6 = row.createCell(6);
                cell6.setCellStyle(style.get("cell"));
                cell6.setCellValue(Double.parseDouble(eq[6].toString()));
                Cell cell7 = row.createCell(7);
                cell7.setCellStyle(style.get("cell"));
                cell7.setCellValue(Double.parseDouble(eq[7].toString()));
                Cell cell8 = row.createCell(8);
                cell8.setCellStyle(style.get("cell"));
                cell8.setCellValue(Double.parseDouble(eq[8].toString()));
                Cell cell9 = row.createCell(9);
                cell9.setCellStyle(style.get("cell"));
                cell9.setCellValue(Double.parseDouble(eq[9].toString()));
                Cell cell10 = row.createCell(10);
                cell10.setCellStyle(style.get("cell"));
                cell10.setCellValue(Double.parseDouble(eq[10].toString()));
                Cell cell11 = row.createCell(11);
                cell11.setCellStyle(style.get("cell"));
                cell11.setCellValue(Double.parseDouble(eq[11].toString()));
                Cell cell12 = row.createCell(12);
                cell12.setCellStyle(style.get("cell"));
                cell12.setCellValue(Double.parseDouble(eq[12].toString()));

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
