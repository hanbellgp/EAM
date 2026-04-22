/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetCheckDetailBean;
import cn.hanbell.eam.ejb.AssetPositionBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCheck;
import cn.hanbell.eam.entity.AssetCheckDetail;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eap.ejb.DepartmentBean;
import cn.hanbell.eap.ejb.MailNotificationBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.Department;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
 *
 * @author C0160
 */
@ManagedBean(name = "assetCheckInitManagedBean")
@SessionScoped
public class AssetCheckInitManagedBean extends AssetCheckManagedBean {

    @EJB
    private AssetPositionBean assetPositionBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private DepartmentBean departmentBean;
    @EJB
    private WarehouseBean warehouseBean;
    @EJB
    private AssetCardBean assetCardBean;
    @EJB
    private AssetCheckDetailBean assetCheckDetailBean;
    @EJB
    private MailNotificationBean eapMailBean;
    private Date queryFormDate;
    private String queryFormType;
    private String queryFormKind;
    private String queryDeptno;
    private String queryUserno;
    private String queryPosition;
    private String messageCheck;
    private boolean queryMergeDept = true;

    private List<Department> deptList;
    private List<Department> selectedDept;
    private List<Warehouse> warehouseList;
    private List<Warehouse> selectedWarehouse;
    private List<AssetPosition> positionList;
    private List<AssetPosition> selectedPosition;

    /**
     * Creates a new instance of AssetCheckInitBean
     */
    public AssetCheckInitManagedBean() {
    }

    @Override
    public void init() {
        super.init();
        deptList = departmentBean.findAll();
        warehouseList = warehouseBean.findAll();
        positionList = assetPositionBean.findByCompany(userManagedBean.getCompany());
        selectedDept = new ArrayList<>();
        selectedWarehouse = new ArrayList<>();
        selectedPosition = new ArrayList<>();
    }

    public void initAssetCheck() {
        if (queryFormType == null || "".equals(queryFormType)) {
            showErrorMsg("Error", "请先选择盘点方式");
            return;
        }
        if (queryCategory == null) {
            showErrorMsg("Error", "请先选择类别");
            return;
        }
        if (selectedWarehouse == null || selectedWarehouse.isEmpty()) {

            showErrorMsg("Error", "请先选择仓库");
            return;
        }
        if (queryFormType.equals("AC")) {
            if ("0000".equals(queryFormKind)) {
                if (selectedDept == null || selectedDept.isEmpty()) {
                    showErrorMsg("Error", "请先选择部门");
                    return;
                }
            } else if (selectedPosition == null || selectedPosition.isEmpty()) {
                showErrorMsg("Error", "按位置分组时请选择位置");
                return;
            }
        }
        String company = userManagedBean.getCurrentCompany().getCompany();
        String creator = userManagedBean.getCurrentUser().getUsername();
        Map<String, Object> filters = new HashMap<>();
        Map<String, String> sorts = new LinkedHashMap<>();
        List<String> depts = new ArrayList<>();
        List<String> warehouses = new ArrayList<>();
        List<AssetCard> assetCardList = new ArrayList<>();
        List<AssetCheckDetail> cDta = assetCheckDetailBean.getAssetCheckDetailList(userManagedBean.getCompany());
        //仓库筛选条件
        selectedWarehouse.stream().forEach((w) -> {
            warehouses.add(w.getWarehouseno());
        });
        String formid = "";
        String ret = "";
        if (queryFormType.equals("AC")) {
            //卡片盘点
            //排序条件
            switch (queryFormKind) {
                case "1000":
                    sorts.put("position1.position", "ASC");
                    break;
                case "0100":
                    sorts.put("position2.position", "ASC");
                    break;
                default:
            }
            sorts.put("deptno", "ASC");
            sorts.put("userno", "ASC");
            sorts.put("warehouse.warehouseno", "ASC");
            if (queryCategory.getNoauto()) {
                sorts.put("formid", "ASC");
            } else {
                sorts.put("formdate", "DESC");
                sorts.put("formid", "ASC");
            }
            if (!"0000".equals(queryFormKind)) {
                //按存放位置盘点
                try {
                    if (selectedDept == null || selectedDept.isEmpty()) {
                        for (AssetPosition p : selectedPosition) {
                            filters.clear();
                            filters.put("company =", company);
                            filters.put("assetItem.category.category =", queryCategory.getCategory());
                            filters.put("warehouse.warehouseno IN ", warehouses);
                            switch (queryFormKind) {
                                case "1000":
                                    filters.put("position1.position =", p.getPosition());
                                    break;
                                case "0100":
                                    filters.put("position2.position =", p.getPosition());
                                    break;
                                default:
                            }
                            if (queryState.equals("N")) {
                                //不含数量为零
                                filters.put("qty <>", 0);
                            }
                            formid = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, p.getPosition() + "_" + p.getName(), "", creator, filters, sorts);
                            if (formid != null && !"".equals(formid)) {
                                ret += formid + ";";
                            }
                        }
                    } else {
                        for (AssetPosition p : selectedPosition) {
                            for (Department d : selectedDept) {
                                filters.clear();
                                filters.put("company =", company);
                                filters.put("assetItem.category.category =", queryCategory.getCategory());
                                filters.put("deptno =", d.getDeptno());
                                filters.put("warehouse.warehouseno IN ", warehouses);
                                switch (queryFormKind) {
                                    case "1000":
                                        filters.put("position1.position =", p.getPosition());
                                        break;
                                    case "0100":
                                        filters.put("position2.position =", p.getPosition());
                                        break;
                                    default:
                                }
                                if (queryState.equals("N")) {
                                    //不含数量为零
                                    filters.put("qty <>", 0);
                                }
                                formid = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, p.getPosition() + "_" + p.getName() + "_" + d.getDeptno() + "_" + d.getDept(), "", creator, filters, sorts);
                                if (formid != null && !"".equals(formid)) {
                                    ret += formid + ";";
                                }
                            }
                        }
                    }
                    if (!"".equals(ret)) {
                        assetCardList = assetCardBean.findByFilters(filters, sorts);
                        senDinventoryDuplicateTable(assetCardList, cDta, ret);
                        showInfoMsg("Info", "成功产生盘点单" + ret);
                        reset();
                    } else {
                        showErrorMsg("Error", "产生盘点单失败");
                    }
                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            } else if (queryMergeDept) {
                selectedDept.stream().forEach((d) -> {
                    depts.add(d.getDeptno());
                });
                filters.clear();
                filters.put("company =", company);
                filters.put("assetItem.category.category =", queryCategory.getCategory());
                filters.put("deptno IN ", depts);
                filters.put("warehouse.warehouseno IN ", warehouses);
                if (queryState.equals("N")) {
                    //不含数量为零
                    filters.put("qty <>", 0);
                }
                try {
                    ret = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, depts.toString(), "", creator, filters, sorts);
                    if (ret != null && !"".equals(ret)) {
                        assetCardList = assetCardBean.findByFilters(filters, sorts);
                        senDinventoryDuplicateTable(assetCardList, cDta, ret);
                        showInfoMsg("Info", "成功产生盘点单" + ret);
                        reset();
                    } else {
                        showErrorMsg("Error", "产生盘点单失败");
                    }
                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            } else {
                try {
                    for (Department d : selectedDept) {
                        filters.clear();
                        filters.put("company =", company);
                        filters.put("assetItem.category.category =", queryCategory.getCategory());
                        filters.put("deptno =", d.getDeptno());
                        filters.put("warehouse.warehouseno IN ", warehouses);
                        if (queryState.equals("N")) {
                            //不含数量为零
                            filters.put("qty <>", 0);
                        }
                        formid = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, d.getDeptno() + "_" + d.getDept(), "", creator, filters, sorts);
                        if (formid != null && !"".equals(formid)) {
                            ret += formid + ";";
                        }
                    }
                    if (!"".equals(ret)) {
                        assetCardList = assetCardBean.findByFilters(filters, sorts);
                        senDinventoryDuplicateTable(assetCardList, cDta, ret);
                        showInfoMsg("Info", "成功产生盘点单" + ret);
                        reset();
                    } else {
                        showErrorMsg("Error", "产生盘点单失败");
                    }
                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            }
        } else {
            //仓库盘点
            sorts.put("warehouse.warehouseno", "ASC");
            sorts.put("assetItem.itemno", "ASC");
            try {
                for (Warehouse wh : selectedWarehouse) {
                    filters.clear();
                    filters.put("company =", company);
                    filters.put("assetItem.category.category =", queryCategory.getCategory());
                    filters.put("warehouse.warehouseno = ", wh.getWarehouseno());
                    if (queryState.equals("N")) {
                        //不含数量为零
                        filters.put("qty <>", 0);
                    }
                    formid = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, wh.getWarehouseno() + wh.getName(), "", creator, filters, sorts);
                    if (formid != null && !"".equals(formid)) {
                        ret += formid + ";";
                    }
                }
                if (!"".equals(ret)) {
                    assetCardList = assetCardBean.findByFilters(filters, sorts);
                    senDinventoryDuplicateTable(assetCardList, cDta, ret);
                    showInfoMsg("Info", "成功产生盘点单" + ret);
                    reset();
                } else {
                    showErrorMsg("Error", "产生盘点单失败");
                }
            } catch (Exception ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        }
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{20, 20, 20, 20, 15, 15};
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"盘点单号", "资产编号", "设备编号", "设备名称", "使用人", "数量"};
    }

    //发送重复生成的资产编号信息邮件
    private void senDinventoryDuplicateTable(List<AssetCard> assetCardList, List<AssetCheckDetail> cDta, String formid) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
            Date thisDate = new Date();//获取当前时间
            String strDate = sdf.format(thisDate);
            //创建标题行
            Row row;
            Row row1;
            //表格一
            String[] title1 = getInventoryTitle();
            row = sheet1.createRow(0);
            row.setHeight((short) 900);
            row1 = sheet1.createRow(1);
            row1.setHeight((short) 800);
            for (int i = 0; i < title1.length; i++) {
                Cell cell = row1.createCell(i);
                cell.setCellStyle(style.get("head"));
                cell.setCellValue(title1[i]);
            }
            sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
            Cell cellTitle = row.createCell(0);
            cellTitle.setCellStyle(style.get("title"));
            cellTitle.setCellValue("盘点重复生成表");
            int j = 2;
            for (AssetCard assetCard : assetCardList) {
                for (AssetCheckDetail cDtaList : cDta) {
                    if (assetCard.getFormid().equals(cDtaList.getAssetno())) {//相同资产编号视为同重复生成的盘点单号
                        row = sheet1.createRow(j);
                        j++;
                        row.setHeight((short) 400);
                        Cell cell0 = row.createCell(0);
                        cell0.setCellValue(cDtaList.getPid());
                        cell0 = row.createCell(1);
                        cell0.setCellValue(cDtaList.getAssetno());
                        cell0 = row.createCell(2);
                        cell0.setCellValue(cDtaList.getAssetItem().getItemno());
                        cell0 = row.createCell(3);
                        cell0.setCellValue(cDtaList.getAssetItem().getItemdesc());
                        cell0 = row.createCell(4);
                        cell0.setCellValue(cDtaList.getUsername());
                        cell0 = row.createCell(5);
                        cell0.setCellValue(cDtaList.getQty().intValue());
                    }
                }
            }
            if (j==2) {//代表没有重复生成的，不发送邮件
                return;
            }
            String path = "../" + strDate + "_" + formid + "盘点重复生成表.xls";//新建文件保存路径
            FileOutputStream out = null;
            File file = new File(path);
            try {
                if (file.exists() && file.isFile()) {
                    file.delete();
                    file = new File(path);
                }

                //写入新File
                out = new FileOutputStream(file);
                workbook.write(out);
                eapMailBean.clearReceivers();
                String thisEmail = systemUserBean.findByUserId(userManagedBean.getUserid()).getEmail();//获取生成盘点单人员邮箱
                eapMailBean.addTo(thisEmail);
                List<SysCode> codeList = sysCodeBean.getTroubleNameList("Email", "CheckRepetition");
                for (SysCode sysCode : codeList) {
                    eapMailBean.addCc(sysCode.getCvalue());
                }
                eapMailBean.setMailSubject("盘点生成重复表");
                eapMailBean.setHTMLMailContent("附件为盘点生成重复表,表内数据跟此次生成的盘点单" + formid + "中的盘点资产重复明细数据。请知悉");
                //添加到邮件发送
                eapMailBean.clearAttachments();//清空已上传文件
                eapMailBean.addAttachments(file);
                eapMailBean.notify(new cn.hanbell.eap.comm.MailNotify());

            } catch (Exception e) {
                showErrorMsg("Error", e.toString());
            } finally {
                if (null != out) {
                    out.flush();
                    out.close();
                }
            }

        } catch (Exception e) {
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

    public void getMessageCheckTop() {
        messageCheck = "是否确认生成盘点单！！！";
        if (queryFormType == null || "".equals(queryFormType)) {
            return;
        }
        if (queryCategory == null) {
            return;
        }
        if (selectedWarehouse == null || selectedWarehouse.isEmpty() || selectedWarehouse.size() == 0) {
            return;
        }
        if (queryFormType.equals("AC")) {
            if ("0000".equals(queryFormKind)) {
                if (selectedDept == null || selectedDept.isEmpty()) {
                    return;
                }
            } else if (selectedPosition == null || selectedPosition.isEmpty()) {

                return;
            }
        }
        String company = userManagedBean.getCurrentCompany().getCompany();
        String creator = userManagedBean.getCurrentUser().getUsername();
        Map<String, Object> filters = new HashMap<>();
        Map<String, String> sorts = new LinkedHashMap<>();
        List<String> depts = new ArrayList<>();
        List<String> warehouses = new ArrayList<>();
        selectedWarehouse.stream().forEach((w) -> {
            warehouses.add(w.getWarehouseno());
        });
        if (queryFormType.equals("AC")) {
            //卡片盘点
            //排序条件
            switch (queryFormKind) {
                case "1000":
                    sorts.put("position1.position", "ASC");
                    break;
                case "0100":
                    sorts.put("position2.position", "ASC");
                    break;
                default:
            }
            sorts.put("deptno", "ASC");
            sorts.put("userno", "ASC");
            sorts.put("warehouse.warehouseno", "ASC");
            if (queryCategory.getNoauto()) {
                sorts.put("formid", "ASC");
            } else {
                sorts.put("formdate", "DESC");
                sorts.put("formid", "ASC");
            }
            if (!"0000".equals(queryFormKind)) {
                //按存放位置盘点
                try {
                    if (selectedDept == null || selectedDept.isEmpty()) {
                        for (AssetPosition p : selectedPosition) {
                            filters.clear();
                            filters.put("company =", company);
                            filters.put("assetItem.category.category =", queryCategory.getCategory());
                            filters.put("warehouse.warehouseno IN ", warehouses);
                            switch (queryFormKind) {
                                case "1000":
                                    filters.put("position1.position =", p.getPosition());
                                    break;
                                case "0100":
                                    filters.put("position2.position =", p.getPosition());
                                    break;
                                default:
                            }
                            if (queryState.equals("N")) {
                                //不含数量为零
                                filters.put("qty <>", 0);
                            }
                        }
                    } else {
                        for (AssetPosition p : selectedPosition) {
                            for (Department d : selectedDept) {
                                filters.clear();
                                filters.put("company =", company);
                                filters.put("assetItem.category.category =", queryCategory.getCategory());
                                filters.put("deptno =", d.getDeptno());
                                filters.put("warehouse.warehouseno IN ", warehouses);
                                switch (queryFormKind) {
                                    case "1000":
                                        filters.put("position1.position =", p.getPosition());
                                        break;
                                    case "0100":
                                        filters.put("position2.position =", p.getPosition());
                                        break;
                                    default:
                                }
                                if (queryState.equals("N")) {
                                    //不含数量为零
                                    filters.put("qty <>", 0);
                                }

                            }
                        }
                    }

                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            } else if (queryMergeDept) {
                selectedDept.stream().forEach((d) -> {
                    depts.add(d.getDeptno());
                });
                filters.clear();
                filters.put("company =", company);
                filters.put("assetItem.category.category =", queryCategory.getCategory());
                filters.put("deptno IN ", depts);
                filters.put("warehouse.warehouseno IN ", warehouses);
                if (queryState.equals("N")) {
                    //不含数量为零
                    filters.put("qty <>", 0);
                }
            } else {
                for (Department d : selectedDept) {
                    filters.clear();
                    filters.put("company =", company);
                    filters.put("assetItem.category.category =", queryCategory.getCategory());
                    filters.put("deptno =", d.getDeptno());
                    filters.put("warehouse.warehouseno IN ", warehouses);
                    if (queryState.equals("N")) {
                        //不含数量为零
                        filters.put("qty <>", 0);
                    }
                }
            }
        } else {
            //仓库盘点
            sorts.put("warehouse.warehouseno", "ASC");
            sorts.put("assetItem.itemno", "ASC");
            for (Warehouse wh : selectedWarehouse) {
                filters.clear();
                filters.put("company =", company);
                filters.put("assetItem.category.category =", queryCategory.getCategory());
                filters.put("warehouse.warehouseno = ", wh.getWarehouseno());
            }

        }

        List<AssetCard> assetCardList = assetCardBean.findByFilters(filters, sorts);
        Map<String, Object> filtersCheck = new HashMap<>();
        filtersCheck.put("company =", company);
        filtersCheck.put("status", "N");
        filtersCheck.put("formid", "AJ");
        filtersCheck.put("category.id", queryCategory.getId());
        List<AssetCheck> assetCheckList = assetCheckBean.findByFilters(filtersCheck);//根据条件查出本公司所有为结案的单子
        String[] assetStr = new String[assetCardList.size()];
        for (int i = 0; i < assetCardList.size(); i++) {//获取所有编号
            assetStr[i] = assetCardList.get(i).getFormid();
        }
        for (AssetCheck aCheck : assetCheckList) {//检查是否存在已生成的盘点单中有改编号
            List<AssetCheckDetail> assetCheckDetails = assetCheckDetailBean.findByPId(aCheck.getFormid());
            for (AssetCheckDetail aDetail : assetCheckDetails) {
                boolean contains = Arrays.asList(assetStr).contains(aDetail.getAssetno());//是否包含对应的编号
                if (contains) {
                    messageCheck = "资产编号:" + aDetail.getAssetno() + "已存在盘点单:" + aCheck.getFormid() + "中。是否继续生成该盘点单";
                    showErrorMsg("Error", messageCheck);
                    return;
                }
            }
        }
        messageCheck = "已检查完毕,是否确认生成盘点单.";
        showErrorMsg("Error", messageCheck);
        return;

    }

    @Override
    public void query() {
        if (queryDeptno != null && !"".equals(queryDeptno)) {
            Map<String, Object> filters = new HashMap<>();
            Map<String, String> sorts = new LinkedHashMap<>();
            filters.put("deptno", queryDeptno);
            sorts.put("deptno", "ASC");
            deptList = departmentBean.findByFilters(filters, sorts);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.queryCategory = null;
        selectedDept.clear();
        selectedWarehouse.clear();
        selectedPosition.clear();
    }

    public void resetDest() {
        super.reset();
    }

    /**
     * @return the queryFormDate
     */
    public Date getQueryFormDate() {
        return queryFormDate;
    }

    /**
     * @param queryFormDate the queryFormDate to set
     */
    public void setQueryFormDate(Date queryFormDate) {
        this.queryFormDate = queryFormDate;
    }

    /**
     * @return the queryFormType
     */
    public String getQueryFormType() {
        return queryFormType;
    }

    /**
     * @param queryFormType the queryFormType to set
     */
    public void setQueryFormType(String queryFormType) {
        this.queryFormType = queryFormType;
    }

    /**
     * @return the queryFormKind
     */
    public String getQueryFormKind() {
        return queryFormKind;
    }

    /**
     * @param queryFormKind the queryFormKind to set
     */
    public void setQueryFormKind(String queryFormKind) {
        this.queryFormKind = queryFormKind;
    }

    /**
     * @return the queryDeptno
     */
    public String getQueryDeptno() {
        return queryDeptno;
    }

    /**
     * @param queryDeptno the queryDeptno to set
     */
    public void setQueryDeptno(String queryDeptno) {
        this.queryDeptno = queryDeptno;
    }

    /**
     * @return the queryUserno
     */
    public String getQueryUserno() {
        return queryUserno;
    }

    /**
     * @param queryUserno the queryUserno to set
     */
    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

    /**
     * @return the queryPosition
     */
    public String getQueryPosition() {
        return queryPosition;
    }

    /**
     * @param queryPosition the queryPosition to set
     */
    public void setQueryPosition(String queryPosition) {
        this.queryPosition = queryPosition;
    }

    /**
     * @return the queryMergeDept
     */
    public boolean isQueryMergeDept() {
        return queryMergeDept;
    }

    /**
     * @param queryMergeDept the queryMergeDept to set
     */
    public void setQueryMergeDept(boolean queryMergeDept) {
        this.queryMergeDept = queryMergeDept;
    }

    /**
     * @return the deptList
     */
    public List<Department> getDeptList() {
        return deptList;
    }

    /**
     * @param deptList the deptList to set
     */
    public void setDeptList(List<Department> deptList) {
        this.deptList = deptList;
    }

    /**
     * @return the selectedDept
     */
    public List<Department> getSelectedDept() {
        return selectedDept;
    }

    /**
     * @param selectedDept the selectedDept to set
     */
    public void setSelectedDept(List<Department> selectedDept) {
        this.selectedDept = selectedDept;
    }

    /**
     * @return the warehouseList
     */
    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    /**
     * @param warehouseList the warehouseList to set
     */
    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    /**
     * @return the selectedWarehouse
     */
    public List<Warehouse> getSelectedWarehouse() {
        return selectedWarehouse;
    }

    /**
     * @param selectedWarehouse the selectedWarehouse to set
     */
    public void setSelectedWarehouse(List<Warehouse> selectedWarehouse) {
        this.selectedWarehouse = selectedWarehouse;
    }

    /**
     * @return the positionList
     */
    public List<AssetPosition> getPositionList() {
        return positionList;
    }

    /**
     * @param positionList the positionList to set
     */
    public void setPositionList(List<AssetPosition> positionList) {
        this.positionList = positionList;
    }

    /**
     * @return the selectedPosition
     */
    public List<AssetPosition> getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * @param selectedPosition the selectedPosition to set
     */
    public void setSelectedPosition(List<AssetPosition> selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public String getMessageCheck() {
        return messageCheck;
    }

    public void setMessageCheck(String messageCheck) {
        this.messageCheck = messageCheck;
    }

}
