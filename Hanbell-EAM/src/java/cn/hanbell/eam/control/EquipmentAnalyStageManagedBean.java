/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.EquipmentAnalyStageBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentAnalyStage;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.lazy.EquipmentAnalyStageModel;
import cn.hanbell.eam.web.SuperSingleBean;
import cn.hanbell.eap.entity.Department;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentAnalyStageManagedBean")
@SessionScoped
public class EquipmentAnalyStageManagedBean extends SuperSingleBean<EquipmentAnalyStage> {

    @EJB
    private EquipmentAnalyStageBean equipmentAnalyStageBean;
    @EJB
    private AssetCardBean assetCardBean;
    private String queryCompany;
    private String queryDept;
    private String queryFactory;
    private List<Object[]> tagetDtaList;
    private List<EquipmentAnalyStage> tagetViewList;
    private String textCNC;

    public EquipmentAnalyStageManagedBean() {
        super(EquipmentAnalyStage.class);
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        if (!currentEntity.getStage().equals("step7") && currentEntity.getActdate() != null) {//当不是step7时抓取下一条数据更新&&有实际填写时间
            List<EquipmentAnalyStage> eStageList = new ArrayList<>();
            EquipmentAnalyStage eStage = new EquipmentAnalyStage();
            openOptions.clear();
            openOptions.put("formid.formid", currentEntity.getFormid().getFormid());
            int step = Integer.parseInt(currentEntity.getStage().substring(4, 5)) + 1;
            openOptions.put("stage", "step" + step);
            eStageList = equipmentAnalyStageBean.findByFilters(openOptions);
            if (!eStageList.isEmpty()) {
                eStage = eStageList.get(0);
                eStage.setStatus("X");
                currentEntity.setStatus("Y");
                equipmentAnalyStageBean.update(eStage);
            }
        } else {
            currentEntity.setStatus("Y");
        }

        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {
        superEJB = equipmentAnalyStageBean;
        model = new EquipmentAnalyStageModel(equipmentAnalyStageBean, userManagedBean);
        openParams = new HashMap<>();
        openOptions = new HashMap<>();
        queryDept = null;
        model.getFilterFields().put("company", userManagedBean.getCompany());
        model.getSortFields().put("formid.formid", "ASC");
        model.getSortFields().put("stage", "ASC");
        super.init();
    }

    @Override
    public void handleFileUploadWhenNew(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        if (file != null) {
            entityList = new ArrayList<>();
            String assetno = "";//记录未成功上传的设备编号，不包括已存在的
            int size = 0;//记录成功导入调数
            int abnormalSize = 0;//记录异常的行数
            int columnSize = 0;//记录异常的列数
            String assetnoThere = "";//已存在的编号
            try {
                InputStream inputStream = file.getInputstream();
                Workbook excel = WorkbookFactory.create(inputStream);
                Sheet sheet = excel.getSheetAt(0);
                Cell c = null;
                AssetCard aCard = new AssetCard();
                tagetDtaList = new ArrayList<>();//预览数据
                tagetDtaList.clear();//清空前面上传的数据
                Row r = null;
                for (int i = 0; i < 1000; i++) {
                    r = sheet.getRow(i + 2);
                    abnormalSize = i + 3;
                    if (r != null) {
                        c = sheet.getRow(i + 2).getCell(0);
                        aCard = assetCardBean.findByAssetno(c.toString());
                        openOptions.clear();
                        openOptions.put("formid.formid", c.toString());
                        int count = equipmentAnalyStageBean.getRowCount(openOptions);//看是否已经存在阶段，只要存在就不添加
                        if (aCard != null && count == 0) {
                            Object[] objDta = new Object[9];
                            objDta[0] = c.toString();
                            size++;
                            List<EquipmentAnalyStage> eList = new ArrayList<>();
                            for (int j = 1; j <= 7; j++) {
                                if (c != null) {//每个step都是一条数据
                                    c = r.getCell(j);
                                    if (c.getDateCellValue() != null) {//确保每条数据都有计划日期
                                        objDta[j] = c.getDateCellValue();
                                        columnSize = j + 1;
                                        EquipmentAnalyStage stage = new EquipmentAnalyStage();
                                        stage.setCompany(aCard.getCompany());
                                        stage.setFormid(aCard);
                                        stage.setFormdate(getDate());
                                        stage.setPlandate(c.getDateCellValue());
                                        stage.setStage("step" + j);
                                        if (j == 1) {
                                            stage.setStatus("X");
                                        } else {
                                            stage.setStatus("N");
                                        }
                                        stage.setCfmdate(getDate());
                                        stage.setCfmuser(userManagedBean.getUserid());
                                        eList.add(stage);
                                    }
                                } else {
                                    break;//当没有数据时跳出本次循环
                                }
                            }
                            if (eList.size() == 7) {
                                entityList.addAll(eList);
                                tagetDtaList.add(objDta);
                            }
                        } else {
                            if (count != 0) {
                                assetnoThere += c.toString() + ",";
                            } else {
                                assetno += c.toString() + ",";
                            }
                        }

                    } else {
                        break;//当获取的行数未有数据时将跳出循环
                    }
                }
                if (assetno != "" || assetnoThere != "") {
                    assetno = assetno.substring(0, assetno.length() - 1);
                    textCNC = "上传成功，共成功上传数据" + size + "行." + "未查找到编号:" + assetno + ", 以下编号已存在:" + assetnoThere;
                } else {
                    textCNC = "上传成功，共成功上传数据" + size + "行." + "所有数据全部上传";
                }
            } catch (Exception ex) {
                textCNC = "数据上传失败！第" + abnormalSize + "行," + "第" + columnSize + "列.数据异常,请检查！！！";
            }
        }
    }

    public void updateData() {
        if (!tagetDtaList.isEmpty()) {
            equipmentAnalyStageBean.update(entityList);
            entityList.clear();
            tagetDtaList.clear();
            showInfoMsg("Info", "保存成功");
        } else {
            showInfoMsg("Error", "保存失败，没有数据源.请选上传！！！");
        }

    }

    public String getStatusName(String status) {
        String statusName = "";
        switch (status) {
            case "step1":
                statusName = "未展开";
                break;
            case "step2":
                statusName = "step1";
                break;
            case "step3":
                statusName = "step2";
                break;
            case "step4":
                statusName = "step3";
                break;
            case "step5":
                statusName = "step4";
                break;
            case "step6":
                statusName = "step5";
                break;
            case "step7":
                statusName = "step6";
                break;
        }
        return statusName;
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {

        return new String[]{"序号", "设备名称", "资产编号", "MES编号", "车间/制程", "级别", "厂别", "保全现状", "自主保全各阶段实施计划", "", "", "", ""};
    }

    /**
     * 设置表头名称字段2
     */
    private String[] getInventoryTitle2() {

        return new String[]{"", "", "", "", "", "", "", "", "计划时间", "实际时间", "进度落后原因分析", "执行改善对策", "担当"};
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

    @Override
    public String view(String path) {
        if (currentEntity != null) {
            openOptions.clear();
            Map sortMap = new HashMap();
            sortMap.put("stage", "ASC");
            openOptions.put("formid.formid", currentEntity.getFormid().getFormid());
            tagetViewList = equipmentAnalyStageBean.findByFilters(openOptions, sortMap);
        }

        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryCompany != null && !"".equals(queryCompany)) {
                this.model.getFilterFields().put("company", queryCompany);
            } else {
                model.getFilterFields().put("company", userManagedBean.getCompany());
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("formid.assetDesc", queryName);
            }
            if (queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("formid.formid", queryFormId);
            }
            if (queryDept != null && !"".equals(this.queryDept)) {
                this.model.getFilterFields().put("formid.deptname", queryDept);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("equipmentLevelId.paramvalue", queryState);
            }
            if (queryFactory != null && !"".equals(this.queryFactory)) {
                this.model.getFilterFields().put("formid.position3.name", queryFactory);
            }

            this.model.getSortFields().put("formid.formid", "ASC");
            this.model.getSortFields().put("stage", "ASC");
        }
    }

    public void downloadTemplate() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String str = "rpt/保全阶段EXCEL导入模板.xls";
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + str);
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            row = sheet.getRow(0);
            row = sheet.getRow(1);
            OutputStream os = null;
            fileName = "保全阶段EXCEL导入模板---" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{5, 15, 20, 10, 15, 10, 15, 10, 15, 15, 30, 30, 30, 10, 10, 10, 10, 20, 10, 10, 25, 25, 25, 10, 10, 10, 15, 10};
    }

    //导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {

        fileName = "各设备保全各阶段实施计划表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getInventoryWidth();

        //创建标题行
        Row row;
        Row row2;
        Row row3;
        String[] title1 = getInventoryTitle();
        String[] title2 = getInventoryTitle2();
        row = sheet1.createRow(1);
        row2 = sheet1.createRow(2);
        row3 = sheet1.createRow(0);
        for (int k = 0; k < title2.length; k++) {
            sheet1.autoSizeColumn(k);
        }

        sheet1.getRow(1).setHeightInPoints(20);
        sheet1.getRow(2).setHeightInPoints(20);
        for (int i = 0; i < 6; i++) {
            sheet1.setColumnWidth(i, 12 * 256);
        }

        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        for (int i = 0; i < title2.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title2[i]);
        }
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));//合并单头
        Cell cellTitle = row3.createCell(0);
        if (queryDept != null && !"".equals(queryDept)) {
            cellTitle.setCellValue(queryDept + "---设备保全各阶段实施计划表");
        } else {
            cellTitle.setCellValue("各课设备保全各阶段实施计划表");
        }

        cellTitle.setCellStyle(style.get("title"));
        //合并单元格
        for (int i = 0; i < 8; i++) {
            sheet1.addMergedRegion(new CellRangeAddress(1, 2, i, i));
        }
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 8, 12));
        //设置宽度
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        int j = 3;
        int x = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<EquipmentAnalyStage> eList = equipmentAnalyStageBean.findByFilters(model.getFilterFields(), model.getSortFields());

        Map<String, List<EquipmentAnalyStage>> strMap = new LinkedHashMap<>();
        for (EquipmentAnalyStage result : eList) {
            String assetno = result.getFormid().getFormid();
            if (!strMap.containsKey(assetno)) {//是否包含该部门
                List<EquipmentAnalyStage> mapList = new ArrayList<>();
                mapList.add(result);
                strMap.put(assetno, mapList);//没有则新的KEY
            } else {
                List<EquipmentAnalyStage> mapList = strMap.get(assetno);
                mapList.add(result);
                strMap.put(assetno, mapList);//覆盖原有的value
            }
        }
        for (Map.Entry<String, List<EquipmentAnalyStage>> entry : strMap.entrySet()) {
            row = sheet1.createRow(j);
            List<EquipmentAnalyStage> mapList = entry.getValue();
            int z = 0;
            x++;
            for (EquipmentAnalyStage equipmentAnalyStage : mapList) {
                row = sheet1.createRow(j + z);
                for (int i = 0; i < 7; i++) {
                    Cell cell0 = row.createCell(i);
                    cell0.setCellStyle(style.get("cell"));
                }
                if (z == 0) {
                    Cell cell0 = row.createCell(0);
                    cell0.setCellStyle(style.get("cell"));
                    cell0.setCellValue(x);
                    cell0 = row.createCell(1);
                    cell0.setCellStyle(style.get("cell"));
                    cell0.setCellValue(equipmentAnalyStage.getFormid().getAssetDesc() + "--" + equipmentAnalyStage.getFormid().getRemark());
                    cell0 = row.createCell(2);
                    cell0.setCellStyle(style.get("cell"));
                    cell0.setCellValue(equipmentAnalyStage.getFormid().getFormid());
                    cell0 = row.createCell(3);
                    cell0.setCellStyle(style.get("cell"));
                    cell0.setCellValue(equipmentAnalyStage.getFormid().getRemark());
                    cell0 = row.createCell(4);
                    cell0.setCellStyle(style.get("cell"));
                    cell0.setCellValue(equipmentAnalyStage.getFormid().getProcess());
                    cell0 = row.createCell(5);
                    cell0.setCellStyle(style.get("cell"));
                    if (equipmentAnalyStage.getEquipmentLevelId() != null) {
                        cell0.setCellValue(equipmentAnalyStage.getEquipmentLevelId().getParamvalue());
                    }
                    cell0 = row.createCell(6);
                    cell0.setCellStyle(style.get("cell"));
                    if (equipmentAnalyStage.getFormid().getPosition3() != null) {
                        cell0.setCellValue(equipmentAnalyStage.getFormid().getPosition3().getName());
                    }

                }
                Cell cell0 = row.createCell(7);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(equipmentAnalyStage.getStage());
                cell0 = row.createCell(8);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(sdf.format(equipmentAnalyStage.getPlandate()));
                cell0 = row.createCell(9);
                cell0.setCellStyle(style.get("cell"));
                if (equipmentAnalyStage.getActdate() != null) {
                    cell0.setCellValue(sdf.format(equipmentAnalyStage.getActdate()));
                }
                cell0 = row.createCell(10);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(equipmentAnalyStage.getBackreason());
                cell0 = row.createCell(11);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(equipmentAnalyStage.getImprove());
                cell0 = row.createCell(12);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(equipmentAnalyStage.getAssume());
                z++;
            }
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 0, 0));
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 1, 1));
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 2, 2));
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 3, 3));
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 4, 4));
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 5, 5));
            sheet1.addMergedRegion(new CellRangeAddress(j, j + 6, 6, 6));

            j = j + 7;
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

    public void handleDialogReturnDeptWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Department d = (Department) event.getObject();
            queryDept = d.getDept();
        }
    }

    public String getQueryCompany() {
        return queryCompany;
    }

    public void setQueryCompany(String queryCompany) {
        this.queryCompany = queryCompany;
    }

    public String getQueryDept() {
        return queryDept;
    }

    public void setQueryDept(String queryDept) {
        this.queryDept = queryDept;
    }

    public String getQueryFactory() {
        return queryFactory;
    }

    public void setQueryFactory(String queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<Object[]> getTagetDtaList() {
        return tagetDtaList;
    }

    public void setTagetDtaList(List<Object[]> tagetDtaList) {
        this.tagetDtaList = tagetDtaList;
    }

    public List<EquipmentAnalyStage> getTagetViewList() {
        return tagetViewList;
    }

    public void setTagetViewList(List<EquipmentAnalyStage> tagetViewList) {
        this.tagetViewList = tagetViewList;
    }

    public String getTextCNC() {
        return textCNC;
    }

    public void setTextCNC(String textCNC) {
        this.textCNC = textCNC;
    }

}
