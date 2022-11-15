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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
@ManagedBean(name = "equipmentTotalEfficiencyManagedBean")
@SessionScoped
public class EquipmentTotalEfficiencyManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    List<Number> yearsList;
    List<Number> monthList;
    private String stayear;
    private String type;
    private String month;
    private List<Object[]> equipmentTotalEfficiencyList;
    private List<Object> EPQIDList;
    private String EPQID;

    public EquipmentTotalEfficiencyManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        Calendar date = Calendar.getInstance();
        int year = Integer.parseInt(String.valueOf(date.get(Calendar.YEAR)));
    
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
//        month = "1";
//        month = Integer.parseInt(month) < 10 ? "0" + month : month;//月份为10以前的格式调整
//        String time = stayear + "/" + month;//拼接年月
//        type = "半成品方型件";//默认查询半成品方型件的数据
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String str = "";
            String title = "";
            if (type.equals("H")) {
                str = "rpt/设备总合效率管理表H模板.xls";
                title = "工程设备总合效率管理表(汉钟设备管理版)";
            } else {
                str = "rpt/设备总合效率管理表G模板.xls";
                title = "工程设备总合效率管理表(顾问MES连线版)";
            }
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + str);
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            row = sheet.getRow(0);
            if (equipmentTotalEfficiencyList == null || equipmentTotalEfficiencyList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return;
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 28));
            Cell cellTitle = row.getCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue(stayear + "年" + month + "月---" + EPQID + "---" + title);
            List<?> itemList = equipmentTotalEfficiencyList;
            int j = 6;
            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.getRow(j);
                j++;
                for (int i = 0; i <= 31; i++) {
                    Cell cell0 = row.getCell(i);
                    cell0 = row.getCell(i);
                    if (eq[i] != null) {
                        cell0.setCellValue(Double.parseDouble(eq[i].toString()));
                    }
                }

            }
            sheet.setForceFormulaRecalculation(true);  //强制执行该sheet中所有公式
            OutputStream os = null;
            if (EPQID.contains("/")) {
                EPQID = EPQID.replace("/", "-");//部分机型带有/文件名中不可带有/进行转换为-
            }
            fileName = stayear + "年" + month + "月---" + EPQID + "---设备总合效率管理表---" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"设备代号", "开始时间", "结束时间", "持续时间(分)", "异常信息", "备注原因"};
    }

    //导出界面的EXCEL数据处理
    public void printlen() {

        fileName = "设备异常" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("异常数据总计表");
        // 生成一个表格
        HSSFSheet sheet2 = workbook.createSheet("异常数据明细表");
        //创建标题行
        Row row;
        row = sheet1.createRow(0);
        Row row2;
        row2 = sheet2.createRow(0);
        row.setHeight((short) 800);
        Cell cell = row.createCell(0);
        cell.setCellStyle(style.get("head"));
        cell.setCellValue("设备代号");
        cell = row.createCell(1);
        cell.setCellStyle(style.get("head"));
        cell.setCellValue("异常时间总和");
        String[] title1 = getInventoryTitle();
        for (int i = 0; i < title1.length; i++) {
            Cell cell1 = row2.createCell(i);
            cell1.setCellStyle(style.get("head"));
            cell1.setCellValue(title1[i]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (queryDateBegin == null || queryDateEnd == null) {
            showErrorMsg("Error", "清先选择开始和结束时间");
            return;
        }
        List<Object[]> letList = equipmentRepairBean.getLEN(EPQID, sdf.format(queryDateBegin), sdf.format(queryDateEnd));
        int j = 1;
        String sEQp = "";
        for (Object[] obj : letList) {
            sEQp = sEQp + "'" + obj[0].toString() + "',";
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(obj[0].toString());
            cell0 = row.createCell(1);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(obj[1].toString());

        }
        sEQp = sEQp.substring(0, sEQp.length() - 1);
        int z = 1;
        List<Object[]> letDtaList = equipmentRepairBean.getLENDta(sEQp, sdf.format(queryDateBegin), sdf.format(queryDateEnd));
        for (Object[] obj : letDtaList) {
            row = sheet2.createRow(z);
            z++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(obj[0].toString());
            cell0 = row.createCell(1);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(obj[1].toString());
            cell0 = row.createCell(2);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(obj[2].toString());
            cell0 = row.createCell(3);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(obj[3].toString());
            cell0 = row.createCell(4);
            cell0.setCellStyle(style.get("cell"));
            if (obj[4] != null) {
                cell0.setCellValue(obj[4].toString());
            }
            cell0 = row.createCell(5);
            cell0.setCellStyle(style.get("cell"));
            if (obj[5] != null) {
                cell0.setCellValue(obj[5].toString());
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

    //导出OEE数据
    public void printOEE() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String str = "";
            String title = "";
            if (type.equals("H")) {
                str = "rpt/设备总合效率OEE.xls";
                title = "OEE表(汉钟设备管理版)";
            } else {
                str = "rpt/设备总合效率OEE.xls";
                title = "OEE(顾问MES连线版)";
            }
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + str);
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            row = sheet.getRow(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat zdf = new SimpleDateFormat("yyyy/MM/dd");
            if (queryDateBegin == null) {
                showErrorMsg("Error", "请在开始时间选择导出的日期！！！");
                return;
            }
            List<Object[]> oeeList = equipmentRepairBean.getEquipmentTotalEfficiencyDayOEE(zdf.format(queryDateBegin), EPQID);
            List<String> deptName = equipmentRepairBean.getEPQIDDeptname(EPQID);
           // List<Object[]> oeeList = equipmentRepairBean.getEquipmentTotalEfficiencyDayOEE("2022/01", EPQID);
            if (oeeList == null || oeeList.isEmpty()) {
                showErrorMsg("Error", "当前日前无生产数据！请知悉");
                return;
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 31));
            Cell cellTitle = row.getCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue(sdf.format(queryDateBegin) + "---" + deptName.get(0) + "---" + title);
            List<?> itemList = oeeList;
            int j = 6;
            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.getRow(j);
                j++;
                for (int i = 0; i <= 30; i++) {
                    Cell cell0 = row.getCell(i);
                    cell0 = row.getCell(i);
                    if (eq[i] != null) {
                        if (i == 0) {
                            cell0.setCellValue(eq[i].toString());
                        } else {
                            cell0.setCellValue(Double.parseDouble(eq[i].toString()));
                        }

                    }
                }

            }
            sheet.setForceFormulaRecalculation(true);  //强制执行该sheet中所有公式
            OutputStream os = null;
            if (EPQID.contains("/")) {
                EPQID = EPQID.replace("/", "-");//部分机型带有/文件名中不可带有/进行转换为-
            }
            fileName = sdf.format(queryDateBegin) + "---" + deptName.get(0) + "---设备总合效率OEE表---" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        String month1 = Integer.parseInt(month) < 10 ? "0" + month : month;
        String time = stayear + "/" + month1;
        equipmentTotalEfficiencyList = equipmentRepairBean.getEquipmentTotalEfficiency(time, EPQID);
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

}
