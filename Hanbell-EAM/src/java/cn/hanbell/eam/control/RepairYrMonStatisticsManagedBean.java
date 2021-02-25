package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import com.lightshell.comm.BaseLib;
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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "repairYrMonStatisticsManagedBean")
@SessionScoped
public class RepairYrMonStatisticsManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHelpers> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;

    List<Number> yearsList;
    private String stayear;
    private String endyear;

    public RepairYrMonStatisticsManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHelpers.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        detailEJB = equipmentRepairHelpersBean;

        Calendar date = Calendar.getInstance();
        int year = Integer.parseInt(String.valueOf(date.get(Calendar.YEAR)));
        yearsList = new ArrayList<>();
        for (int i = 2020; i <= year + 1; i++) {
            yearsList.add(i);
        }
    }

    //月单位故障统计
    @Override
    public void print() throws ParseException {

        List<Object[]> monthList = equipmentRepairBean.getYearMTBFAndMTTR(stayear);

        fileName = "月单位故障统计" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getInventoryWidth1();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        Row row1;
        Row row2;
        //表格一
        String[] title2 = getInventoryTitle2();
        String[] title3 = getInventoryTitle3();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);
        row1 = sheet1.createRow(1);
        row2 = sheet1.createRow(2);
        Cell cell = row.createCell(0);
        cell.setCellStyle(style.get("head"));
        cell.setCellValue(stayear + "年设备故障月统计表");
        sheet1.createFreezePane(3, 0);//固定第一行十五个列
        for (int i = 0; i < 12; i++) {
            sheet1.addMergedRegion(new CellRangeAddress(0, 1, 3 + (i * 3) + i, 6 + (i * 3) + i));
        }
        for (int i = 3; i < title2.length + 3; i++) {
            Cell cell1 = row.createCell(i);
            cell1.setCellStyle(style.get("head"));
            cell1.setCellValue(title2[i - 3]);

        }
        for (int i = 3; i < title2.length + 3; i++) {
            Cell cell3 = row1.createCell(i);
            cell3.setCellStyle(style.get("head"));
        }

        for (int i = 0; i < title3.length; i++) {
            Cell cell1 = row2.createCell(i);
            cell1.setCellStyle(style.get("head"));
            cell1.setCellValue(title3[i]);
        }
        sheet1.addMergedRegion(new CellRangeAddress(0, 1, 0, 2));
        row2.setHeight((short) (18 * 40));
        if (monthList == null || monthList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        int j = 3;
        for (Object[] eq : monthList) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            for (int i = 0; i < eq.length; i++) {
                Cell cell1 = row.createCell(i);
                cell1.setCellStyle(style.get("cell"));
                if (eq[i] != null) {
                    if (i > 2) {
                        cell1.setCellValue(Double.parseDouble(eq[i].toString()));
                    } else {
                        cell1.setCellValue(eq[i].toString());
                    }
                } else {
                    cell1.setCellValue(0);
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

//    年单位故障统计
    public void printYear() throws ParseException {

        List<Object[]> monthList = equipmentRepairBean.getEveryYearMTBFAndMTTR(stayear, endyear);
        if (monthList == null || monthList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        fileName = "年单位故障统计" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getYearInventoryWidth();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        Row row1;
        Row row2;
        //表格一

        String[] title2 = getYearInventoryTitle();
        String[] title3 = getYearInventoryTitle1();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);
        row1 = sheet1.createRow(1);
        row2 = sheet1.createRow(2);
        Cell cell = row.createCell(0);
        cell.setCellStyle(style.get("head"));
        cell.setCellValue(stayear + "-" + endyear + "年设备故障统计表");
        sheet1.createFreezePane(3, 0);//固定第一行十五个列
        for (int i = 0; i < Integer.parseInt(endyear) - Integer.parseInt(stayear); i++) {
            sheet1.addMergedRegion(new CellRangeAddress(0, 1, 3 + (i * 3), 5 + (i * 3)));
        }
        for (int i = 3; i < title2.length + 3; i++) {
            Cell cell1 = row.createCell(i);
            cell1.setCellStyle(style.get("head"));
            cell1.setCellValue(title2[i - 3]);

        }
        for (int i = 3; i < title2.length + 3; i++) {
            Cell cell3 = row1.createCell(i);
            cell3.setCellStyle(style.get("head"));
        }

        for (int i = 0; i < title3.length; i++) {
            Cell cell1 = row2.createCell(i);
            cell1.setCellStyle(style.get("head"));
            cell1.setCellValue(title3[i]);
        }
        sheet1.addMergedRegion(new CellRangeAddress(0, 1, 0, 2));
        row.setHeight((short) 800);
        int j = 3;
        for (Object[] eq : monthList) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            for (int i = 0; i < eq.length; i++) {
                Cell cell1 = row.createCell(i);
                cell1.setCellStyle(style.get("cell"));
                if (eq[i] != null) {
                    if (i > 2) {
                        cell1.setCellValue(Double.parseDouble(eq[i].toString()));
                    } else {
                        cell1.setCellValue(eq[i].toString());
                    }
                } else {
                    cell1.setCellValue(0);
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
    private String[] getInventoryTitle2() {
        return new String[]{"1月", "", "", "", "2月", "", "", "", "3月", "", "", "", "4月", "", "", "", "5月", "", "", "", "6月", "", "", "", "7月", "", "", "", "8月", "", "", "", "9月", "", "", "", "10月", "", "", "", "11月", "", "", "", "12月", "", "", ""};
    }

    private String[] getInventoryTitle3() {
        return new String[]{"资产编号", "设备名称", "报修部门", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)", "故障次数", "故障率(%)", "MTBF (分钟/件)", "MTTR (分钟/件)"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth1() {
        return new int[]{20, 15, 20, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
    }
//    年单位表头

    private String[] getYearInventoryTitle() {
        int item = Integer.parseInt(endyear) - Integer.parseInt(stayear);
        String[] str = new String[3 * item];
        int size = 0;
        for (int i = 0; i < item * 3; i++) {
            if (i % 3 == 0) {
                str[i] = Integer.parseInt(stayear) + size + "年";
                size++;
            } else {
                str[i] = "";
            }
        }
        return str;
    }

    private String[] getYearInventoryTitle1() {
        int item = Integer.parseInt(endyear) - Integer.parseInt(stayear);
        String[] str = new String[item * 3 + 3];
        str[0] = "资产编号";
        str[1] = "设备名称";
        str[2] = "报修部门";
        for (int i = 0; i < item; i++) {
            int y = (i + 1) * 3;
            str[y] = "故障率(%)";
            str[y + 1] = "MTBF (分钟/件)";
            str[y + 2] = "MTTR (分钟/件)";
        }
        return str;
    }

    private int[] getYearInventoryWidth() {
        int item = Integer.parseInt(endyear) - Integer.parseInt(stayear);
        int[] str = new int[item * 3 + 3];
        str[0] = 20;
        str[1] = 15;
        str[2] = 20;
        for (int i = 0; i < item; i++) {
            int y = (i + 1) * 3;
            str[y] = 10;
            str[y + 1] = 10;
            str[y + 2] = 10;
        }
        return str;
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

    public String getEndyear() {
        return endyear;
    }

    public void setEndyear(String endyear) {
        this.endyear = endyear;
    }

}
