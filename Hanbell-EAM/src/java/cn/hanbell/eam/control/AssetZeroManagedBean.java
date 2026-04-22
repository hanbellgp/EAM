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
import static cn.hanbell.eap.entity.BookingKind_.endDate;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "assetZeroManagedBean")
@SessionScoped
public class AssetZeroManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    List<Number> yearsList;
    List<Number> monthList;
    private String stayear;
    private String type;
    private String dept;
    private String month;
    private List<Object[]> equipmentTotalEfficiencyList;
    private List<Object> EPQIDList;
    private String EPQID;

    public AssetZeroManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        Calendar date = Calendar.getInstance();
        int year = Integer.parseInt(String.valueOf(date.get(Calendar.YEAR)));
        Date dt = new Date("Sun Jan 01 00:00:00 CST 2023");
        queryDateBegin = dt;

        yearsList = new ArrayList<>();
        for (int i = year; i >= 2020; i--) {
            yearsList.add(i);
        }
        monthList = new ArrayList<>();
        //获取月份下拉数据，共12月
        for (int i = 1; i <= 12; i++) {
            monthList.add(i);
        }
        List<String> list = equipmentRepairBean.getEPQID();
        EPQIDList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            EPQIDList.add(list.get(i));
        }
        type = "J";
//        month = "1";
//        month = Integer.parseInt(month) < 10 ? "0" + month : month;//月份为10以前的格式调整
//        String time = stayear + "/" + month;//拼接年月
//        type = "半成品方型件";//默认查询半成品方型件的数据
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException, IOException {
        String finalFilePath = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String str = "rpt/全厂设备零故障系统表单导出模版.xlsx";
            String title = "设备零故障系统表单";

            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + str);
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);//获取第一页签
            Sheet sheet1 = workbook.getSheetAt(1);//获取第二页签
            Row row1 = sheet.getRow(0);
            Row row;
            row = sheet.getRow(0);
            if (equipmentTotalEfficiencyList == null || equipmentTotalEfficiencyList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return;
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 28));
            Cell cellTitle = row.getCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue(sdf.format(queryDateBegin) + "至" + sdf.format(queryDateEnd) + "--" + dept + "--" + title + "(" + type + ")");
            List<?> itemList = equipmentTotalEfficiencyList;
            int j = 3;
            DateTimeFormatter formatterMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(sdf.format(queryDateEnd), formatter);
            int year = date.getYear();
            int month = date.getMonthValue();
            int dateshee2 = date.getDayOfMonth();
            List<int[]> sheet2Obj = new ArrayList<>();

            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.getRow(j);
                j++;

                Cell cell0 = row.getCell(0);

                cell0.setCellValue(j - 3);
                cell0 = row.getCell(1);
                cell0.setCellValue(eq[0].toString());
                for (int i = 1; i <= 14; i++) {//sheet1数据赋值
                    cell0 = row.getCell(i + 1);
                    if (eq[i] != null) {
                        cell0.setCellValue(eq[i].toString());
                    }
                }

            }
            for (int i = 1; i <= month; i++) {//sheet2数据赋值
                int[] obj = new int[9];
                Arrays.fill(obj, 0);
                obj[0] = i;
                Date dt = new Date();
                Calendar calendar = Calendar.getInstance();
                // 设置年份和月份
                calendar.set(Calendar.YEAR, year); // 年份
                calendar.set(Calendar.MONTH, i); // 月份，注意月份是从0开始的，所以1月实际上是
                calendar.set(Calendar.DATE, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);//减去一天获取上个月的最后一天
                dt = calendar.getTime(); // 获取修改后的日期
                List<Object[]> sheet2list = equipmentRepairBean.getAssetZeroList(sdf.format(queryDateBegin), sdf.format(dt), type, dept, userManagedBean.getCompany());
                for (Object[] eq : sheet2list) {
                    int day = 0;
                    if (eq[9] != null) {//判断是否有故障/
                        if (i != month) {//如果不是最后选的月份则获取整月的天数
                            LocalDate date1 = LocalDate.parse(eq[9].toString(), formatterMS);
                            YearMonth yearMonth = YearMonth.of(year, i);//获取当前月份的最后一天
                            LocalDate date2 = yearMonth.atEndOfMonth();
                            day = (int) ChronoUnit.DAYS.between(date1, date2);

                        } else {//否则就获取选择的最后天数
                            LocalDate date1 = LocalDate.parse(eq[9].toString(), formatterMS);
                            LocalDate date2 = LocalDate.parse(sdf.format(queryDateEnd), formatter);
                            day = (int) ChronoUnit.DAYS.between(date1, date2);
                        }

                    } else {//如果至今没有故障则用搜索区间的天数
                        LocalDate date1 = LocalDate.parse(sdf.format(queryDateBegin), formatter);
                        LocalDate date2 = LocalDate.parse(sdf.format(queryDateEnd), formatter);
                        // 计算日期差
                        day = (int) ChronoUnit.DAYS.between(date1, date2);

                    }
                    if (day <= 90) {
                        if (day < 0) {
                            obj[1] = obj[1] + 1;
                            obj[5] = obj[5] + 0;
                        } else{
                             obj[1] = obj[1] + 1;
                            obj[5] = obj[5] + day;
                        }

                    } else if (day <= 180) {
                        obj[2] = obj[2] + 1;
                        obj[6] = obj[6] + day;
                    } else if (day <= 365) {
                        obj[3] = obj[3] + 1;
                        obj[7] = obj[7] + day;
                    } else if (day > 365) {
                        obj[4] = obj[4] + 1;
                        obj[8] = obj[8] + day;
                    }

                }
                sheet2Obj.add(obj);
            }

            for (int i = 1; i <= 12; i++) {
                for (int[] is1 : sheet2Obj) {
                    if (i == is1[0]) {
                        for (int k = 0; k < 4; k++) {
                            row1 = sheet1.getRow(3 + k);
                            Cell cell0 = row1.getCell(1 + (2 * (i - 1)));
                            cell0.setCellValue(is1[k + 1]);
                            cell0 = row1.getCell(2 + (2 * (i - 1)));
                            cell0.setCellValue(is1[k + 5]);
                        }

                    }
                }

            }
//            XSSFWorkbook workbookChar = new XSSFWorkbook(is);
//
//            // 2. 获取目标工作表（假设图表在第一个Sheet中）
//            XSSFSheet sheetChar = workbookChar.getSheetAt(1);

            sheet.setForceFormulaRecalculation(true);  //强制执行该sheet中所有公式
            OutputStream os = null;
            fileName = sdf.format(queryDateBegin) + "--" + sdf.format(queryDateEnd) + "---零故障统计表--" + type + "---" + (dept.equals("Null")  ? "全部" : dept) + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xlsx";
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
        try {

            if (queryDateBegin == null || queryDateEnd == null) {
                showErrorMsg("Error", "请选择需要查询的时间区间");
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            equipmentTotalEfficiencyList = equipmentRepairBean.getAssetZeroList(sdf.format(queryDateBegin), sdf.format(queryDateEnd), type, dept, userManagedBean.getCompany());
        } catch (ParseException ex) {
            Logger.getLogger(AssetZeroManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public List<Object[]> getEquipmentTotalEfficiencyList() {
        return equipmentTotalEfficiencyList;
    }

    public void setEquipmentTotalEfficiencyList(List<Object[]> equipmentTotalEfficiencyList) {
        this.equipmentTotalEfficiencyList = equipmentTotalEfficiencyList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getEPQIDList() {
        return EPQIDList;
    }

    public void setEPQIDList(List<Object> EPQIDList) {
        this.EPQIDList = EPQIDList;
    }

    public List<Number> getMonthList() {
        return monthList;
    }

    public void setMonthList(List<Number> monthList) {
        this.monthList = monthList;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getEPQID() {
        return EPQID;
    }

    public void setEPQID(String EPQID) {
        this.EPQID = EPQID;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

}
