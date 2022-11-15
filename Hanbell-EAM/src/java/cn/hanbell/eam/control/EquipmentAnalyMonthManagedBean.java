/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentAnalyResultBean;
import cn.hanbell.eam.ejb.EquipmentAnalyResultDtaBean;
import cn.hanbell.eam.entity.EquipmentAnalyResult;
import cn.hanbell.eam.entity.EquipmentAnalyResultDta;
import cn.hanbell.eam.lazy.EquipmentAnalyResultModel;
import cn.hanbell.eam.web.FormMultiBean;
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentAnalyMonthManagedBean")
@SessionScoped
public class EquipmentAnalyMonthManagedBean extends FormMultiBean<EquipmentAnalyResult, EquipmentAnalyResultDta> {

    @EJB
    private EquipmentAnalyResultBean equipmentAnalyResultBean;
    @EJB
    private EquipmentAnalyResultDtaBean equipmentAnalyResultDtaBean;
    private List<Number> yearsList;
    private List<Number> monthList;
    private String stayear;
    private String staMonth;
    private List<Object[]> equipmentAnalyMonthList;

    public EquipmentAnalyMonthManagedBean() {
        super(EquipmentAnalyResult.class, EquipmentAnalyResultDta.class);
    }

    @Override
    public void init() {
        superEJB = equipmentAnalyResultBean;
        model = new EquipmentAnalyResultModel(equipmentAnalyResultBean, userManagedBean);
        detailEJB = equipmentAnalyResultDtaBean;
        Calendar date = Calendar.getInstance();
        int year = Integer.parseInt(String.valueOf(date.get(Calendar.YEAR)));
        int month = Integer.parseInt(String.valueOf(date.get(Calendar.MONTH) + 1));
        yearsList = new ArrayList<>();
        monthList = new ArrayList<>();
        for (int i = 2020; i <= year; i++) {
            yearsList.add(i);
        }
        for (int i = 1; i < 13; i++) {
            monthList.add(i);
        }

        staMonth = String.valueOf(month);//获取当天月份
        stayear = String.valueOf(year);//初始化数据年份为当前年份
        super.init();
    }

    @Override
    public void query() {
        String yearMonth;
        if (Integer.parseInt(staMonth) < 10) {
            yearMonth = stayear + "-0" + staMonth;
        } else {
            yearMonth = stayear + "-" + staMonth;
        }
        equipmentAnalyMonthList = equipmentAnalyResultDtaBean.getEquipmentAnalyResultDtaList(queryFormId, yearMonth);

    }
//导出界面的EXCEL数据处理

    @Override
    public void print() throws ParseException {

        fileName = "自主保全月台账" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        Row row2;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 900);
        row1 = sheet1.createRow(1);
        row1.setHeight((short) 800);
        row2 = sheet1.createRow(2);
        row2.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }

        if (equipmentAnalyMonthList == null || equipmentAnalyMonthList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }

        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 34));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("自主保全台账");
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, 34));
        Cell cellTime = row1.createCell(0);
        cellTime.setCellStyle(style.get("date"));
        cellTime.setCellValue(stayear + "-" + staMonth);
        int j = 3;
        List<?> itemList = equipmentAnalyMonthList;

        List<Object[]> list = (List<Object[]>) itemList;
        for (Object[] eq : list) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            for (int i = 0; i < eq.length; i++) {
                Cell cell0 = row.createCell(i);
                cell0.setCellStyle(style.get("cell"));
                if (eq[i] != null) {
                    cell0.setCellValue(eq[i].toString());
                }

            }

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
        return new String[]{"资产编号", "保养区域", "保养内容", "周期", "1", "2", "3", "4)", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 20, 10, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
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
     * 设置表头名称字段
     */
    private String[] getInventoryTitle2() {
        return new String[]{"所属部门", "设备编号", "设备名称", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth2() {
        return new int[]{15, 20, 20, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
    }

    /**
     * 导出自主保全实施表
     */
    public void printImplementation() {
        fileName = "自主保全实施表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getInventoryWidth2();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        String yearMonth;
        if (Integer.parseInt(staMonth) < 10) {
            yearMonth = stayear + "-0" + staMonth;
        } else {
            yearMonth = stayear + "-" + staMonth;
        }
        //创建标题行
        Row row;
        Row row1;
        Row row2;
        //表格一
        String[] title1 = getInventoryTitle2();
        row = sheet1.createRow(0);
        row.setHeight((short) 900);
        row1 = sheet1.createRow(1);
        row1.setHeight((short) 800);
        row2 = sheet1.createRow(2);
        row2.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        List<?> itemList = equipmentAnalyResultBean.getImplementation(yearMonth);
        List<Object[]> list = (List<Object[]>) itemList;
        Map<String, List<Object[]>> map = new HashMap<>();
        list.forEach(result -> {
            if (map.containsKey(result[0].toString())) {//判断是否已存在对应键号
                map.get(result[0].toString()).add(result);//直接在对应的map中添加数据
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(result);
                map.put(result[0].toString(), tmpList);//新增一个键号
            }
        });
        if (list == null || list.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }

        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 33));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("自主保全实施表");
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, 33));
        Cell cellTime = row1.createCell(0);
        cellTime.setCellStyle(style.get("date"));
        cellTime.setCellValue(stayear + "-" + staMonth);
        int j = 3;
        int item = 0;
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> list2 = entry.getValue();//取出对应的值
            if (item == 0) {
                sheet1.addMergedRegion(new CellRangeAddress(j, j + (list2.size() * 2) - 1, 0, 0));
            } else {
                sheet1.addMergedRegion(new CellRangeAddress(item, item + (list2.size() * 2) - 1, 0, 0));
            }
            item = j + (list2.size() * 2);
            for (Object[] eq : list2) {
                sheet1.addMergedRegion(new CellRangeAddress(j, j + 1, 1, 1));
                sheet1.addMergedRegion(new CellRangeAddress(j, j + 1, 2, 2));
                row = sheet1.createRow(j);
                Cell cell0 = row.createCell(0);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(entry.getKey());//将部门赋值
                j = j + 2;
                for (int i = 1; i <= 31; i++) {
                    cell0 = row.createCell(i + 2);
                    cell0.setCellStyle(style.get("cell"));
                    if (eq[i * 2 + 1] != null) {
                        cell0.setCellValue(Integer.parseInt(eq[i * 2 + 1].toString()));
                    }
                }
                for (int i = 1; i <= 2; i++) {
                    cell0 = row.createCell(i);
                    cell0.setCellStyle(style.get("cell"));
                    if (eq[i] != null) {
                        cell0.setCellValue(eq[i].toString());
                    }
                }
                row = sheet1.createRow(j - 1);

                for (int i = 1; i <= 31; i++) {
                    Cell cell1 = row.createCell(i + 2);
                    cell1.setCellStyle(style.get("cell"));
                    if (eq[i * 2 + 2] != null) {
                        cell1.setCellValue(Integer.parseInt(eq[i * 2 + 2].toString()));
                    }
                }
                for (int i = 0; i <= 2; i++) {
                   Cell cell1 = row.createCell(i);
                    cell1.setCellStyle(style.get("cell"));
                }
//                for (int i = 1; i <= 31; i++) {
//                    row = sheet1.createRow(10);
//                    cell0 = row.createCell(i+3);
//                    cell0.setCellStyle(style.get("cell"));
//                    if (eq[i+3] != null) {
//                        cell0.setCellValue(eq[i + 3].toString());
//                    }
//                }
            }
        }
//        for (Object[] eq : list) {
//            row = sheet1.createRow(j);
//            j++;
//            row.setHeight((short) 400);
//            for (int i = 0; i < eq.length; i++) {
//                Cell cell0 = row.createCell(i);
//                cell0.setCellStyle(style.get("cell"));
//                if (eq[i] != null) {
//                    cell0.setCellValue(eq[i].toString());
//                }
//
//            }
//
//        }
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

    public List<Number> getYearsList() {
        return yearsList;
    }

    public void setYearsList(List<Number> yearsList) {
        this.yearsList = yearsList;
    }

    public List<Number> getMonthList() {
        return monthList;
    }

    public void setMonthList(List<Number> monthList) {
        this.monthList = monthList;
    }

    public String getStayear() {
        return stayear;
    }

    public void setStayear(String stayear) {
        this.stayear = stayear;
    }

    public String getStaMonth() {
        return staMonth;
    }

    public void setStaMonth(String staMonth) {
        this.staMonth = staMonth;
    }

    public List<Object[]> getEquipmentAnalyMonthList() {
        return equipmentAnalyMonthList;
    }

    public void setEquipmentAnalyMonthList(List<Object[]> equipmentAnalyMonthList) {
        this.equipmentAnalyMonthList = equipmentAnalyMonthList;
    }

}
