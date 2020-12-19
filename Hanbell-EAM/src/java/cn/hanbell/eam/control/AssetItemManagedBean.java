/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetItemBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.Unit;
import cn.hanbell.eam.lazy.AssetItemModel;
import cn.hanbell.eam.web.SuperSingleBean;
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
import org.primefaces.event.SelectEvent;

/**
 * @author C0160
 */
@ManagedBean(name = "assetItemManagedBean")
@SessionScoped
public class AssetItemManagedBean extends SuperSingleBean<AssetItem> {

    @EJB
    private AssetItemBean assetItemBean;

    public AssetItemManagedBean() {
        super(AssetItem.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setProptype("3");
        newEntity.setUnittype("1");
        newEntity.setQcpass(false);
        newEntity.setUnitexch(BigDecimal.ONE);
        newEntity.setInvtype(true);
        newEntity.setBbstype("000");
        newEntity.setPurmax(BigDecimal.ZERO);
        newEntity.setPurmin(BigDecimal.ZERO);
        newEntity.setInvmax(BigDecimal.ZERO);
        newEntity.setInvmin(BigDecimal.ZERO);
        newEntity.setStatus("V");
    }

    @Override
    protected boolean doBeforeDelete(AssetItem entity) throws Exception {
        if (super.doBeforeDelete(entity)) {
            if (!assetItemBean.allowDelete(entity.getItemno())) {
                showErrorMsg("Error", "已有交易记录不可删除");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (super.doBeforePersist()) {
            if (newEntity.getCategory() == null) {
                showErrorMsg("Error", "请选择分类");
                return false;
            }
            if (newEntity.getItemno() == null || "".equals(newEntity.getItemno())) {
                showErrorMsg("Error", "请输入品号");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            if (currentEntity.getCategory() == null) {
                showErrorMsg("Error", "请选择分类");
                return false;
            }
            if (currentEntity.getItemno() == null || "".equals(newEntity.getItemno())) {
                showErrorMsg("Error", "请输入品号");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            currentEntity.setCategory(e);
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            newEntity.setCategory(e);
        }
    }

    public void handleDialogReturnUnitWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Unit e = (Unit) event.getObject();
            currentEntity.setUnit(e.getUnit());
        }
    }

    public void handleDialogReturnUnitWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Unit e = (Unit) event.getObject();
            newEntity.setUnit(e.getUnit());
        }
    }

    @Override
    public void init() {
        superEJB = assetItemBean;
        model = new AssetItemModel(assetItemBean);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("itemno", "ASC");
        super.init();
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("itemno", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("itemdesc", queryName);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
        }
    }

    //导出界面的EXCEL数据处理
    @Override
    public void print() {

        fileName = "办公用品登记表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);

        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        List<AssetItem> itemList = assetItemBean.findByFilters(model.getFilterFields(), model.getSortFields());
        int j = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (AssetItem item : itemList) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getCategory().getCategory());
            cell0 = row.createCell(1);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getCategory().getName());
            cell0 = row.createCell(2);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getItemno()); 
            cell0 = row.createCell(3);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getItemdesc());
            cell0 = row.createCell(4);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getItemspec());
            cell0 = row.createCell(5);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(Double.valueOf(item.getPurprice().toString()));
            cell0 = row.createCell(6);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getUnit());
            cell0 = row.createCell(7);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(item.getStatus());
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
        return new String[]{"类别", "名称", "品号", "品名", "规格", "单价", "单位", "状态"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{10, 20, 15, 35, 35, 10, 10, 10};
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

}
