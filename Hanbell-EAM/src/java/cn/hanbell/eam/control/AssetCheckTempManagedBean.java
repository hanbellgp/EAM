/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCheckBean;
import cn.hanbell.eam.ejb.AssetCheckDetailBean;
import cn.hanbell.eam.ejb.AssetCheckTempBean;
import cn.hanbell.eam.entity.AssetCheckTemp;
import cn.hanbell.eam.web.SuperSingleBean;
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

/**
 *
 * @author C2231
 */
@ManagedBean(name = "assetchecktempManagedBean")
@SessionScoped
public class AssetCheckTempManagedBean extends SuperSingleBean<AssetCheckTemp> {

    @EJB
    protected AssetCheckTempBean assetCheckTempBean;

    @EJB
    protected AssetCheckBean assetCheckBean;

    @EJB
    protected AssetCheckDetailBean assetCheckDetailBean;
    private int queryItimes;
    private List<AssetCheckTemp> assetchecktempsList;
    private List<AssetCheckTemp> assetchecktempdetailList;
    private List<AssetCheckTemp> assetchecktempList;

    public AssetCheckTempManagedBean() {
        super(AssetCheckTemp.class);
    }

    @Override
    public void init() {
        superEJB = assetCheckTempBean;
        queryItimes = 2;
        if (assetchecktempList != null) {
            assetchecktempList.clear();
        }
    }

    @Override
    public void print() {
        if ("".equals(queryFormId) || queryFormId == null) {
            showErrorMsg("Error", "盘点单号不能为空," + "格式错误");
            return;
        }
        fileName = "盘点数据表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("盘点单号：" + queryFormId);
        // 设置表格宽度
        int[] wt1 = getInventoryWidth2();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        //表格一
        String[] title1 = getInventoryTitle2();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String credate = "";
        if (queryDateBegin != null) {
            credate = sdf.format(queryDateBegin);
        }
        assetchecktempList = assetCheckTempBean.getAssetCheckTempList(queryItimes, queryFormId, credate);
        if (assetchecktempList == null || assetchecktempList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        int j = 1;
        int count = 1;
        List<?> itemlList = assetchecktempList;
        List<Object[]> list = (List<Object[]>) itemlList;
        for (Object[] obj : list) {
            row = sheet1.createRow(j);
            row.setHeight((short) 400);
            j++;
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(count);
            count++;
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(obj[1].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(obj[2].toString());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(obj[3].toString());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(Double.parseDouble(obj[4].toString()));
            String itimestype = "";
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            if (Integer.parseInt(obj[5].toString()) == 0) {
                itimestype = "初盘";
            }
            if (Integer.parseInt(obj[5].toString()) == 1) {
                itimestype = "复盘";
            }
            cell5.setCellValue(itimestype);
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(obj[6].toString());
            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            cell7.setCellValue(obj[7].toString());
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

    public void printinventorydiff() {
        if ("".equals(queryFormId) || queryFormId == null) {
            showErrorMsg("Error", "盘点单号不能为空," + "格式错误");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String credate = "";
        if (queryDateBegin != null) {
            credate = sdf.format(queryDateBegin);
        }
        assetchecktempdetailList = assetCheckTempBean.getAssetCheckTempDetailList(queryFormId, credate);
        if (assetchecktempdetailList == null || assetchecktempdetailList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        fileName = "盘点差异表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("盘点单号：" + queryFormId);
        // 设置表格宽度
        int[] wt1 = getInventoryWidth();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);

        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }

        List<?> itemList = assetchecktempdetailList;
        int j = 1;
        int count = 1;
        List<Object[]> list = (List<Object[]>) itemList;
        for (Object[] obj : list) {
            row = sheet1.createRow(j);
            row.setHeight((short) 400);
            j++;
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(count);
            count++;
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(obj[1].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(obj[2].toString());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(obj[3].toString());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(obj[4].toString());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(obj[5].toString());
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(Double.parseDouble(obj[6].toString()));
            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            cell7.setCellValue(Double.parseDouble(obj[7].toString()));
            cell8.setCellValue(Double.parseDouble(obj[8].toString()));
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

    public void printdiff() {
        if ("".equals(queryFormId) || queryFormId == null) {
            showErrorMsg("Error", "盘点单号不能为空," + "格式错误");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String credate = "";
        if (queryDateBegin != null) {
            credate = sdf.format(queryDateBegin);
        }
        assetchecktempsList = assetCheckTempBean.getAssetCheckTempsList(queryFormId, credate);
        if (assetchecktempsList == null || assetchecktempsList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        fileName = "初复盘差异表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("盘点单号：" + queryFormId);
        // 设置表格宽度
        int[] wt1 = getInventoryWidth1();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        //表格一
        String[] title1 = getInventoryTitle1();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }

        int j = 1;
        int count = 1;
        List<?> itemList = assetchecktempsList;
        List<Object[]> list = (List<Object[]>) itemList;

        for (Object[] obj : list) {
            row = sheet1.createRow(j);
            row.setHeight((short) 400);
            j++;
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(count);
            count++;

            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(obj[1].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(obj[2].toString());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(obj[3].toString());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(obj[4].toString());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(obj[5].toString());
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            Cell cell9 = row.createCell(9);
            cell9.setCellStyle(style.get("cell"));
            cell6.setCellValue(Double.parseDouble(obj[6].toString()));
            cell7.setCellValue(Double.parseDouble(obj[7].toString()));
            cell8.setCellValue(Double.parseDouble(obj[8].toString()));
            cell9.setCellValue(obj[9].toString());
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

    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"序号", "品号", "名称", "编号", "部门", "使用人", "实盘", "复盘", "差异"};
    }

    private String[] getInventoryTitle1() {
        return new String[]{"序号", "品号", "名称", "编号", "部门", "使用人", "初盘", "复盘", "差异", "初盘地址"};
    }

    private String[] getInventoryTitle2() {
        return new String[]{"序号", "品号", "名称", "编号", "数量", "类型", "部门", "使用人"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{10, 15, 20, 15, 20, 10, 10, 10, 10};
    }

    private int[] getInventoryWidth1() {
        return new int[]{10, 15, 20, 15, 20, 10, 10, 10, 10, 20};
    }

    private int[] getInventoryWidth2() {
        return new int[]{10, 15, 20, 15, 10, 10, 20, 15};
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

    @Override
    public void query() {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String credate = "";

        if (queryDateBegin != null) {

            credate = simpleDateFormat.format(queryDateBegin);
        }
        assetchecktempList = assetCheckTempBean.getAssetCheckTempList(queryItimes, queryFormId, credate);
//        assetchecktempdetailList = assetCheckTempBean.getAssetCheckTempDetailList(queryItimes, queryFormId, credate);
//        assetchecktempsList = assetCheckTempBean.getAssetCheckTempsList(queryItimes, queryFormId, credate);

    }

    @Override
    public void reset() {
        super.reset();
        this.queryFormId = null;
        this.queryDateBegin = null;
        this.queryItimes = 2;
    }

    public int getqueryItimes() {
        return queryItimes;
    }

    public void setqueryItimes(int queryItimes) {
        this.queryItimes = queryItimes;
    }

    public List<AssetCheckTemp> getAssetchecktempsList() {
        return assetchecktempsList;
    }

    public void setAssetchecktempsList(List<AssetCheckTemp> assetchecktempsList) {
        this.assetchecktempsList = assetchecktempsList;
    }

    public List<AssetCheckTemp> getAssetchecktempList() {
        return assetchecktempList;
    }

    public void setAssetchecktempList(List<AssetCheckTemp> assetchecktempList) {
        this.assetchecktempList = assetchecktempList;
    }

    public List<AssetCheckTemp> getAssetchecktempdetailList() {
        return assetchecktempdetailList;
    }

    public void setAssetchecktempdetailList(List<AssetCheckTemp> assetchecktempdetailList) {
        this.assetchecktempdetailList = assetchecktempdetailList;
    }

}
