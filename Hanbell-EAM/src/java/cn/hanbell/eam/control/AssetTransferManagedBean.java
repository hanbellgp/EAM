/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetTransferBean;
import cn.hanbell.eam.ejb.AssetTransferDetailBean;
import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetTransfer;
import cn.hanbell.eam.entity.AssetTransferDetail;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetTransferModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetTransferManagedBean")
@SessionScoped
public class AssetTransferManagedBean extends FormMultiBean<AssetTransfer, AssetTransferDetail> {

    @EJB
    protected AssetTransferBean assetDistributeBean;
    @EJB
    protected AssetTransferDetailBean assetDistributeDetailBean;
    @EJB
    protected AssetInventoryBean assetInventoryBean;
    @EJB
    protected AssetCardBean assetCardBean;
    @EJB
    protected WarehouseBean warehouseBean;

    protected List<String> paramItemno = null;
    protected List<String> paramPosition = null;
    protected List<String> paramUsed = null;
    protected List<String> paramHascost = null;

    public AssetTransferManagedBean() {
        super(AssetTransfer.class, AssetTransferDetail.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (super.doBeforePersist()) {
            for (AssetTransferDetail d : detailList) {
                if (d.getAssetItem().getCategory().getNoauto() && d.getAssetCard() == null) {
                    showErrorMsg("Error", d.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (d.getUsed() == d.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
                if (d.getCompany().equals(d.getAssetCard().getCompany())) {
                    showErrorMsg("Error", "转入转出公司不能相同");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUpdate() throws Exception {
        if (super.doBeforeUpdate()) {
            for (AssetTransferDetail d : detailList) {
                if (d.getAssetItem().getCategory().getNoauto() && d.getAssetCard() == null) {
                    showErrorMsg("Error", d.getAssetItem().getItemno() + "需要输入编号");
                    return false;
                }
                if (d.getUsed() == d.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "来源仓成本属性错误");
                    return false;
                }
                if (d.getCompany().equals(d.getAssetCard().getCompany())) {
                    showErrorMsg("Error", "转入转出公司不能相同");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (super.doBeforeUnverify()) {
            AssetInventory ai;
            AssetCard ac = null;
            for (AssetTransferDetail add : detailList) {
                ai = assetInventoryBean.findAssetInventory(add.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可还原量不足");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (super.doBeforeVerify()) {
            int i;
            boolean flag;
            String wareh;
            AssetInventory ai;
            AssetCard ac;
            List<AssetTransferDetail> details = new ArrayList<>();
            for (AssetTransferDetail atd : detailList) {
                if (atd.getAssetCard() == null) {
                    showErrorMsg("Error", atd.getAssetItem().getItemno() + "需要指定资产编号");
                    return false;
                }
                if (atd.getAssetCard() != null) {
                    ac = assetCardBean.findByAssetno(atd.getAssetno());
                    if ((ac == null) || (ac.getQty().compareTo(BigDecimal.ZERO) < 1)) {
                        showErrorMsg("Error", atd.getAssetno() + "不存在或没有可转移数量");
                        return false;
                    }
                }//刀工量仪在领用时才产生卡片信息，此处不用判断是否存在卡片
                //数量累加后再判断库存可利用量
                flag = true;
                for (AssetTransferDetail d : details) {
                    if (d.getAssetItem().getItemno().equals(atd.getAssetItem().getItemno())) {
                        d.setQty(d.getQty().add(atd.getQty()));
                        flag = false;
                    }
                }
                if (flag) {
                    details.add(atd);
                }
            }
            //用累加数量进行库存可用量判断
            for (AssetTransferDetail add : details) {
                ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), add.getAssetItem().getItemno(), "", "", "", add.getWarehouse().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(add.getQty()) == -1) {
                    showErrorMsg("Error", add.getAssetItem().getItemno() + "库存可利用量不足");
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail == null) {
            return;
        }
        if (currentDetail.getDeptno() == null || "".equals(currentDetail.getDeptno())) {
            showErrorMsg("Error", "请输入使用部门");
            return;
        }
        if (currentDetail.getUserno() == null || "".equals(currentDetail.getUserno())) {
            showErrorMsg("Error", "请输入使用人");
            return;
        }
        if (currentDetail.getAssetItem() == null) {
            showErrorMsg("Error", "请输入件号");
            return;
        }
        if (currentDetail.getAssetCard() == null) {
            showErrorMsg("Error", "请输入编号");
            return;
        }
        if (currentDetail.getWarehouse() == null) {
            showErrorMsg("Error", "请输入仓库");
            return;
        }
        if (currentDetail.getQty().compareTo(BigDecimal.ZERO) != 1) {
            showErrorMsg("Error", "请输入数量");
            return;
        }
        if (currentDetail.getCompany().equals(currentDetail.getAssetCard().getCompany())) {
            showErrorMsg("Error", "转入转出公司不能相同");
            return;
        }
        super.doConfirmDetail();
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno(d.getDeptno());
            currentEntity.setDeptname(d.getDept());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setUnit(e.getUnit());
        }
    }

    public void handleDialogReturnAssetCardWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetCard e = (AssetCard) event.getObject();
            currentDetail.setAssetCard(e);
            currentDetail.setAssetno(e.getFormid());
            currentDetail.setQty(e.getQty());
            currentDetail.setDeptno(e.getDeptno());
            currentDetail.setDeptname(e.getDeptname());
            currentDetail.setUserno(e.getUserno());
            currentDetail.setUsername(e.getUsername());
            currentDetail.setSurplusValue(e.getAmts());
            currentDetail.setUsed(e.getUsed());
            currentDetail.setWarehouse(e.getWarehouse());
        }
    }

    public void handleDialogReturnDeptWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Department d = (Department) event.getObject();
            currentDetail.setDeptno(d.getDeptno());
            currentDetail.setDeptname(d.getDept());
        }
    }

    public void handleDialogReturnPosition1WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition1(e);
        }
    }

    public void handleDialogReturnPosition2WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition2(e);
        }
    }

    public void handleDialogReturnPosition3WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition3(e);
        }
    }

    public void handleDialogReturnPosition4WhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            currentDetail.setPosition4(e);
        }
    }

    public void handleDialogReturnUserWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            SystemUser u = (SystemUser) event.getObject();
            currentDetail.setUserno(u.getUserid());
            currentDetail.setUsername(u.getUsername());
            currentDetail.setDeptno(u.getDeptno());
            currentDetail.setDeptname(u.getDept().getDept());
        }
    }

    public void handleDialogReturnWarehouseWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetDistributeBean;
        detailEJB = assetDistributeDetailBean;
        model = new AssetTransferModel(assetDistributeBean, userManagedBean);
        model.getFilterFields().put("status", "N");
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
        openParams = new HashMap<>();
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "assetcardSelect":
                openParams.clear();
                if (paramItemno == null) {
                    paramItemno = new ArrayList<>();
                } else {
                    paramItemno.clear();
                }
                paramItemno.add(currentDetail.getAssetItem().getItemno());
                openParams.put("itemno", paramItemno);
                if (paramUsed == null) {
                    paramUsed = new ArrayList<>();
                } else {
                    paramUsed.clear();
                }
                paramUsed.add("1");
                openParams.put("used", paramUsed);
                if (openOptions == null) {
                    openOptions = new HashMap();
                    openOptions.put("modal", true);
                    openOptions.put("contentWidth", "900");
                }
                super.openDialog("assetcardSelect", openOptions, openParams);
                break;
            case "warehouseSelect":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("0");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            case "assetposition1Select":
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add("0");//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition2Select":
                if (currentDetail == null || currentDetail.getPosition1() == null) {
                    showWarnMsg("Warn", "请先选择公司位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition1().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition3Select":
                if (currentDetail == null || currentDetail.getPosition2() == null) {
                    showWarnMsg("Warn", "请先选择厂区位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition2().getId().toString());
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            case "assetposition4Select":
                if (currentDetail == null || currentDetail.getPosition3() == null) {
                    showWarnMsg("Warn", "请先选择厂房位置");
                    return;
                }
                openParams.clear();
                if (paramPosition == null) {
                    paramPosition = new ArrayList<>();
                } else {
                    paramPosition.clear();
                }
                paramPosition.add(currentDetail.getPosition3().getId().toString());//最高阶
                openParams.put("pid", paramPosition);
                super.openDialog("assetpositionSelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
    }

    public void printSingle() {
        if (currentEntity == null || currentEntity.equals("")) {
            showErrorMsg("Error", "请选择需要导出的转移单！");
            return;
        }
        if (detailList == null || detailList.isEmpty()) {
            showErrorMsg("Error", "你选择的转移单无明细数据");
            return;
        }
        fileName = "EAM转移单明细" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        Row row2;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(1);
        row.setHeight((short) 800);
        row2 = sheet1.createRow(0);
        row2.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
        Cell cellTitle = row2.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("资产转移单明细");

        int j = 2;
        for (AssetTransferDetail assetTransferDetail : detailList) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(assetTransferDetail.getSeq());
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(assetTransferDetail.getAssetItem().getItemno());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(assetTransferDetail.getAssetno());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(assetTransferDetail.getAssetCard().getAssetDesc());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(assetTransferDetail.getQty().toString());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(assetTransferDetail.getSurplusValue().toString());

            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(assetTransferDetail.getAssetCard().getCompany());

            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            cell7.setCellValue(assetTransferDetail.getAssetCard().getDeptname());
            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            cell8.setCellValue(assetTransferDetail.getAssetCard().getUserno());

            Cell cell9 = row.createCell(9);
            cell9.setCellStyle(style.get("cell"));
            cell9.setCellValue(assetTransferDetail.getAssetCard().getWarehouse().getName());

            Cell cell10 = row.createCell(10);
            cell10.setCellStyle(style.get("cell"));
            cell10.setCellValue(assetTransferDetail.getCompany());

            Cell cell11 = row.createCell(11);
            cell11.setCellStyle(style.get("cell"));
            cell11.setCellValue(assetTransferDetail.getDeptname());

            Cell cell12 = row.createCell(12);
            cell12.setCellStyle(style.get("cell"));
            cell12.setCellValue(assetTransferDetail.getUsername());

            Cell cell13 = row.createCell(13);
            cell13.setCellStyle(style.get("cell"));
            cell13.setCellValue(assetTransferDetail.getWarehouse().getName());

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

    @Override
    public void query() {
        if (this.model != null && this.model.getFilterFields() != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
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
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"序号", "件号", "资产编码", "资产名称", "数量", "残值", "原公司别", "转出部门名称", "原使用人", "来源仓库", "新公司别", "转入部门名称", "新使用人", "转入仓库"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{10, 15, 20, 15, 10, 15, 10, 15, 15, 15, 15, 15, 15, 15};
    }
}
