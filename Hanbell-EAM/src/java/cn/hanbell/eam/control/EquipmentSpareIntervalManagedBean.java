package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import cn.hanbell.eam.web.FormMultiBean;
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareIntervalManagedBean")
@SessionScoped
public class EquipmentSpareIntervalManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHelpers> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;
    @EJB
    private EquipmentSpareStockBean equipmentSpareStockBean;

    private String EPQID;
    private List<Object> EPQIDList;

    public EquipmentSpareIntervalManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHelpers.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        detailEJB = equipmentRepairHelpersBean;
        List<String> list = equipmentRepairBean.getEPQID();
        EPQIDList = new ArrayList<>();
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        for (int i = 0; i < list.size(); i++) {
            EPQIDList.add(list.get(i));
        }
    }

    @Override
    public void print() throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        List<Object[]> list = equipmentSpareStockBean.getUseSpare(EPQID, sf.format(queryDateBegin), sf.format(queryDateEnd));
        List<Object[]> recordList = equipmentSpareStockBean.getEquipmentRecord(EPQID, sf.format(queryDateBegin), sf.format(queryDateEnd));
        if (list.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请重新输入条件查询！");
            return;
        }
        Map<String, List<Object[]>> map = new LinkedHashMap<>();
        for (Object[] obj : list) {
            if (map.containsKey(obj[2].toString())) {//map中存在此备件编号，将数据存放当前key的map中
                map.get(obj[2].toString()).add(obj);
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(obj);
                map.put(obj[2].toString(), tmpList);
            }
        }
        fileName = "设备故障履历表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);

        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        String[] title1 = getInventoryTitle();
        String[] title2 = getInventoryTitle2();
        Row row;
        Row row1;
        Row row2;
        row1 = sheet1.createRow(2);
        row1.setHeight((short) 800);
        row = sheet1.createRow(0);
        row.setHeight((short) 900);
        sheet1.addMergedRegion(new CellRangeAddress(0, 1, 0, 9));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue(EPQID + "---设备故障履历表");
        int j = 3;
        int seq = 1;

        for (int i = 0; i < title1.length; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> val = entry.getValue();
            row1 = sheet1.createRow(j);

            Cell cell0 = row1.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(seq);
            cell0 = row1.createCell(1);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(entry.getKey());

            for (int i = 0; i < 8; i++) {
                cell0 = row1.createCell(i + 2);
                cell0.setCellStyle(style.get("cell"));
            }
            for (int i = 0; i < val.size(); i++) {
                cell0 = row1.createCell(i + 2);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(val.get(i)[3].toString());
            }
            j++;
            seq++;
        }
        sheet1.addMergedRegion(new CellRangeAddress(j + 3, j + 3, 0, 5));
        row = sheet1.createRow(j + 3);
        cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("异常分析说明");
        row2 = sheet1.createRow(4 + j);
        for (int i = 0; i < title2.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title2[i]);
        }
        for (int i = 0; i < recordList.size(); i++) {
            row2 = sheet1.createRow(j + 5);
            Cell cell0 = row2.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(i + 1);
            for (int k = 0; k < recordList.get(i).length; k++) {
                cell0 = row2.createCell(k + 1);
                cell0.setCellStyle(style.get("cell"));
                if (recordList.get(i)[k] != null) {
                    cell0.setCellValue(recordList.get(i)[k].toString());
                }

            }
            j++;
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
        return new String[]{"项目", "更换零件名称", "第1回", "第2回", "第3回", "第4回", "第5回", "第6回", "第7回", "第8回"};
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle2() {
        return new String[]{"项目", "发生日", "原因", "再发防止对策内容", "完成日", "实施者"};
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

    public String getEPQID() {
        return EPQID;
    }

    public void setEPQID(String EPQID) {
        this.EPQID = EPQID;
    }

    public List<Object> getEPQIDList() {
        return EPQIDList;
    }

    public void setEPQIDList(List<Object> EPQIDList) {
        this.EPQIDList = EPQIDList;
    }

}
