package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.EquipmentAnalyStageBean;
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentAnalyStage;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import cn.hanbell.eam.lazy.EquipmentAnalyStageModel;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.Department;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentAnalyInformationManagedBean")
@SessionScoped
public class equipmentAnalyInformationManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHis> {

    @EJB
    protected EquipmentAnalyStageBean equipmentAnalyStageBean;
    @EJB
    protected AssetCardBean assetCardBean;
    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    List<Number> yearsList;
    List<Number> monthList;
    private String stayear;
    private String type;
    private String month;
    private String queryDept;
    private List<Object[]> equipmentTotalEfficiencyList;
    private List<Object> EPQIDList;
    private String EPQID;
    private List<Object[]> aInofrmationList;

    public equipmentAnalyInformationManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentAnalyStageBean;
        model = new EquipmentAnalyStageModel(equipmentAnalyStageBean, userManagedBean);
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {

        return new String[]{"序号", "资产编号", "名称", "MES编号", "规格", "使用部门", "位置信息", "设备级别", "自主保全", "", "", "", "计划保全", "", "", ""};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{5, 17, 20, 10, 20, 20, 35, 7, 10, 10, 10, 10, 10, 10, 10, 10, 10, 20, 10, 10, 25, 25, 25, 10, 10, 10, 15, 10};
    }

    /**
     * 设置表头名称字段2
     */
    private String[] getInventoryTitle2() {

        return new String[]{"", "", "", "", "", "", "", "", "自主保全STEP", "期 间", "点检表", "点检基准书", "计划保全STEP", "期 间", "点检表", "点检基准书"};
    }

    //导出自助保全统计表
    public void printParameter() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String str = "";
            String title = "";

            str = "rpt/现场各课各级设备自主保全阶段统计表模板.xls";
            title = "现场各课各级设备自主保全阶段统计";

            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + str);
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            row = sheet.getRow(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            queryDept = null;//不管部门选择哪个都为空，该方法为各课数据所以不查部门
            List<Object[]> oeeList = equipmentAnalyStageBean.getAssetnoList("", queryDept);

            // List<Object[]> oeeList = equipmentRepairBean.getEquipmentTotalEfficiencyDayOEE("2022/01", EPQID);
            if (oeeList == null || oeeList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请知悉");
                return;
            }
            int j = 4;
            List<Object[]> list = (List<Object[]>) oeeList;
            int z = 0;
            for (Object[] eq : list) {
                row = sheet.createRow(j);
                j++;
                z++;
                Cell cell0 = row.createCell(0);
                cell0 = row.getCell(0);
                cell0.setCellStyle(style.get("cell"));
                if (eq[0].equals(999)) {
                    cell0.setCellValue("合计");
                    sheet.addMergedRegion(new CellRangeAddress(j - 1, j - 1, 0, 1));
                } else {
                    cell0.setCellValue(z);
                }

                for (int i = 0; i <= 37; i++) {
                    cell0 = row.createCell(i + 1);
                    cell0 = row.getCell(i + 1);
                    cell0.setCellStyle(style.get("cell"));
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
            fileName = "现场各课各级设备自主保全阶段统计表---" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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

    //导出OEE数据
    public void printParameterClass() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");
            String str = "";
            String title = "";
            if (queryDept == null || "".equals(queryDept)) {
                showErrorMsg("Error", "请选择使用部门！！！");
                return;
            }
            str = "rpt/课级设备自主保全阶段统计表模板.xls";
            title = queryDept + "自主保全各阶段设备统计表";

            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + str);
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            row = sheet.createRow(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
            Cell cellTitle = row.createCell(0);
            cellTitle.setCellValue(title);
            cellTitle.setCellStyle(style.get("title"));
            List<Object[]> oeeList = equipmentAnalyStageBean.getAssetnoList("", queryDept);

            // List<Object[]> oeeList = equipmentRepairBean.getEquipmentTotalEfficiencyDayOEE("2022/01", EPQID);
            if (oeeList == null || oeeList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请知悉");
                return;
            }
            int j = 2;
            List<Object[]> list = (List<Object[]>) oeeList;
            Object[] obj1 = list.get(0);
            Object[] obj = list.get(1);//获取所有总合数据
            for (int i = 0; i <= 8; i++) {
                row = sheet.getRow(i + 3);
                Cell cell0 = row.getCell(3);
                if (i == 0) {
                    cell0.setCellValue(obj[1].toString());
                } else {
                    cell0.setCellValue(Integer.parseInt(obj[j].toString()) + Integer.parseInt(obj[j + 9].toString()) + Integer.parseInt(obj[j + 18].toString()) + Integer.parseInt(obj[j + 27].toString()));
                }
                j++;

            }
            for (int i = 0; i <= 35; i++) {
                row = sheet.getRow(i + 12);
                Cell cell0 = row.getCell(3);
                cell0.setCellValue(obj1[i + 2].toString());
            }

            sheet.setForceFormulaRecalculation(true);  //强制执行该sheet中所有公式
            OutputStream os = null;
            fileName = title + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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

    //导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {

        fileName = "现场各课各设备基本资料表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        sheet1.getRow(2).setHeightInPoints(30);
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
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));//合并单头
        Cell cellTitle = row3.createCell(0);
        cellTitle.setCellValue("现场各课各设备基本资料表");
        cellTitle.setCellStyle(style.get("title"));

        //合并单元格
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 8, 11));
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 12, 15));
        for (int i = 0; i <= 7; i++) {
            sheet1.addMergedRegion(new CellRangeAddress(1, 2, i, i));
        }
        //设置宽度
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        int j = 3;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (aInofrmationList == null || aInofrmationList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请知悉");
            return;
        }
        for (Object[] obj : aInofrmationList) {
            row = sheet1.createRow(j);
            for (int i = 0; i <= 15; i++) {
                Cell cell0 = row.createCell(i);
                cell0.setCellStyle(style.get("cell"));
                if (i == 0) {
                    cell0.setCellValue(Integer.parseInt(obj[i].toString()));
                } else if (obj[i] != null) {
                    cell0.setCellValue(obj[i].toString());
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
//获取部门信息并赋值

    public void handleDialogReturnDeptWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Department d = (Department) event.getObject();
            queryDept = d.getDept();
        }
    }
//查询表单需要数据整合

    public void getInformationListForm() {
        this.model.getFilterFields().put("assetItem.category.id =", 3);//只抓机台设备
        this.model.getFilterFields().put("company", userManagedBean.getCompany());
        this.model.getFilterFields().put("qty <>", 0);
        this.model.getSortFields().put("deptname", "ASC");
        this.model.getSortFields().put("formid", "ASC");
        if (this.queryFormId != null && !"".equals(this.queryFormId)) {
            this.model.getFilterFields().put("formid", this.queryFormId);
        } else {
            this.model.getFilterFields().put("formid", userManagedBean.getCurrentCompany().getAssetcode() + "A");
        }
        if (this.queryName != null && !"".equals(this.queryName)) {
            this.model.getFilterFields().put("assetDesc", this.queryName);
        }
        if (this.queryDept != null && !"".equals(this.queryDept)) {
            this.model.getFilterFields().put("deptname", this.queryDept);
        }
        List<AssetCard> eAssetCards = assetCardBean.findByFilters(model.getFilterFields(), model.getSortFields());//获取设备基本资料
        if (eAssetCards == null || eAssetCards.isEmpty()) {
            aInofrmationList.clear();
            return;
        }
        String formid = "";//所有机台编号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!eAssetCards.isEmpty()) {
            for (AssetCard eAssetCard : eAssetCards) {
                formid = formid + "'" + eAssetCard.getFormid() + "',";
            }
        }
        if (!"".equals(formid)) {
            formid = formid.substring(0, formid.length() - 1);
        }

        List<EquipmentAnalyStage> aStageList = equipmentAnalyStageBean.getEquipmentAnalyInformation(formid);//获取对应机台保全阶段现状
        int x = 0;
        List<Object[]> aResultList = equipmentAnalyStageBean.getEquipmentAnalyResultCount(formid);//获取对应机台是否生成过点检单 

        List<Object[]> aStandard = equipmentAnalyStageBean.getEquipmentStandardCount(formid);//获取对应机台是否有点检基准
        aInofrmationList = new ArrayList<>();
        for (AssetCard eAssetCard : eAssetCards) {
            Object[] obj = new Object[17];
            x++;
            obj[0] = x;
            obj[1] = eAssetCard.getFormid();
            obj[2] = eAssetCard.getAssetDesc();
            obj[3] = eAssetCard.getRemark();
            obj[4] = eAssetCard.getAssetSpec();
            obj[5] = eAssetCard.getDeptname();
            if (eAssetCard.getPosition3() != null) {
                obj[6] = eAssetCard.getPosition3().getName();
            }
            for (EquipmentAnalyStage aStage : aStageList) {
                if (eAssetCard.getFormid().equals(aStage.getFormid().getFormid())) {
                    obj[7] = aStage.getEquipmentLevelId().getParamvalue();
                    obj[8] = aStage.getStage();
                    obj[9] = sdf.format(aStage.getPlandate());
                }
            }
            for (Object[] objects : aResultList) {
                if (objects[0].equals(eAssetCard.getFormid())) {
                    if (objects[1].equals("一级")) {
                        obj[10] = "✔";
                    } else {
                        obj[14] = "✔";
                    }
                }
            }
            for (Object[] objects : aStandard) {
                if (objects[0].equals(eAssetCard.getFormid())) {
                    if (objects[1].equals("一级")) {
                        obj[11] = "✔";
                    } else {
                        obj[15] = "✔";
                    }
                }
            }
            aInofrmationList.add(obj);
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
        getInformationListForm();
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

    public List<Object[]> getaInofrmationList() {
        return aInofrmationList;
    }

    public void setaInofrmationList(List<Object[]> aInofrmationList) {
        this.aInofrmationList = aInofrmationList;
    }

    public String getQueryDept() {
        return queryDept;
    }

    public void setQueryDept(String queryDept) {
        this.queryDept = queryDept;
    }

}
