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
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将资产编号相同几部门相同的数据存在一起
        monthList.forEach(objects -> {
            List<Object> assetno = new ArrayList<>();
            String anKey = "";
            if (objects[0] == null) {
                anKey = 9 + objects[2].toString();
            } else {
                anKey = objects[0].toString() + objects[2].toString();
            }
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        assetno = entry.getValue();
                        assetno.add(objects);
                        moMap.put(anKey, assetno);

                    }
                }

            } else {
                assetno.add(objects);
                moMap.put(anKey, assetno);

            }
        });

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
        cell.setCellValue(stayear + "年设备故障统计表");
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

        if (monthList == null || monthList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00%");
        df.setRoundingMode(RoundingMode.DOWN);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

        int j = 3;
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            List<?> itemList = entry.getValue();;

            List<Object[]> list = (List<Object[]>) itemList;
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            if (list.get(0)[0] != null) {
                cell0.setCellValue(list.get(0)[0].toString());
            }
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(list.get(0)[1].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(list.get(0)[2].toString());
            for (Object[] eq : list) {
                switch (Integer.parseInt(eq[8].toString())) {
                    case 1:
                        cell = row.createCell(3);
                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(4);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(5);
                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(6);
                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 2:
                        cell = row.createCell(7);

                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(8);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(9);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(10);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 3:
                        cell = row.createCell(11);

                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(12);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(13);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(14);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 4:
                        cell = row.createCell(15);

                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(16);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(17);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(18);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 5:
                        cell = row.createCell(19);

                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(20);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(21);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(22);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 6:
                        cell = row.createCell(23);

                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(24);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(25);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(26);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 7:
                        cell = row.createCell(27);

                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(28);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(29);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(30);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 8:
                        cell = row.createCell(31);
                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(32);
                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(33);
                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(34);
                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 9:
                        cell = row.createCell(35);
                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(36);
                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(37);
                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(38);
                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 10:
                        cell = row.createCell(39);
                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(40);

                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);

                        cell = row.createCell(41);
                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(42);
                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 11:
                        cell = row.createCell(43);
                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(44);
                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(45);
                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(46);
                        
                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;
                    case 12:
                        cell = row.createCell(47);
                        cell.setCellValue(Integer.parseInt(eq[4].toString()));
                        cell = row.createCell(48);
                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(49);

                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(50);

                        cell.setCellValue(Double.parseDouble(eq[6].toString()));
                        break;

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
        Map<String, List<Object>> moMap = new LinkedHashMap<>();
        //将资产编号相同几部门相同的数据存在一起
        monthList.forEach(objects -> {
            List<Object> assetno = new ArrayList<>();
            String anKey = "";
            if (objects[0] == null) {
                anKey = 9 + objects[2].toString();
            } else {
                anKey = objects[0].toString() + objects[2].toString();
            }
            if (moMap.containsKey(anKey)) {
                for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
                    if (entry.getKey().equals(anKey)) {
                        assetno = entry.getValue();
                        assetno.add(objects);
                        moMap.put(anKey, assetno);

                    }
                }

            } else {
                assetno.add(objects);
                moMap.put(anKey, assetno);

            }
        });

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
            sheet1.addMergedRegion(new CellRangeAddress(0, 1, 3 + (i * 3) , 5 + (i * 3) ));
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

        if (monthList == null || monthList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00%");
        df.setRoundingMode(RoundingMode.DOWN);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        int[] year = getYear();
        int j = 3;
        for (Map.Entry<String, List<Object>> entry : moMap.entrySet()) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            List<?> itemList = entry.getValue();;

            List<Object[]> list = (List<Object[]>) itemList;
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            if (list.get(0)[0] != null) {
                cell0.setCellValue(list.get(0)[0].toString());
            }
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(list.get(0)[1].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(list.get(0)[2].toString());
            for (Object[] eq : list) {
                for (int i = 0; i < year.length; i++) {
                    if (Integer.parseInt(eq[8].toString()) == year[i]) {
                        int item = 3 * i + 3;
                        cell = row.createCell(item);
                        cell.setCellValue(Double.parseDouble(eq[7].toString()));
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(item + 1);
                        cell.setCellValue(Double.parseDouble(eq[5].toString()));
                        cell = row.createCell(item + 2);
                        cell.setCellValue(Double.parseDouble(eq[6].toString()));

                    }
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
        return new String[]{"资产编号", "设备名称", "报修部门", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)", "故障次数", "故障率", "MTBF(小时/件)", "MTTR(小时/件)"};
    }
//    年单位表头

    private String[] getYearInventoryTitle() {
        int item = Integer.parseInt(endyear) - Integer.parseInt(stayear);
        String[] str = new String[3 * item];
        int size=0;
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
//    年单位表头

    private int[] getYear() {
        int item = Integer.parseInt(endyear) - Integer.parseInt(stayear);
        int[] str = new int[item];
        for (int i = 0; i < item; i++) {
            str[i] = Integer.parseInt(stayear) + i;
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
            str[y] = "故障率";
            str[y + 1] = "MTBF(小时/件)";
            str[y + 2] = "MTTR(小时/件)";
        }
        return str;
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth1() {
        return new int[]{20, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20, 15, 10, 20, 20};
    }

    private int[] getYearInventoryWidth() {
        int item = Integer.parseInt(endyear) - Integer.parseInt(stayear);
        int[] str = new int[item * 3 + 3];
        str[0] = 20;
        str[1] = 20;
        str[2] = 20;
        for (int i = 0; i < item; i++) {
            int y = (i + 1) * 3;
            str[y] = 10;
            str[y + 1] = 20;
            str[y + 2] = 20;
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
