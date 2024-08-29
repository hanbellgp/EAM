package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.AssetCardSpecialBean;
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentRepairModel;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.CompanyBean;
import cn.hanbell.eap.entity.Company;
import com.lightshell.comm.BaseLib;
import java.io.OutputStream;
import java.text.ParseException;
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
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "repairCostSummaryManagedBean")
@SessionScoped
public class RepairCostSummaryManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;

    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    @EJB
    private CompanyBean companyBean;
    private List<EquipmentRepair> equipmentRepairList;
    private List<Company> companyList;
    private String[] company;
       @EJB
    private AssetCardSpecialBean assetCardSpecialBean;
    public RepairCostSummaryManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        model = new EquipmentRepairModel(equipmentRepairBean, userManagedBean,assetCardSpecialBean);
        companyList = companyBean.findBySystemName("EAM");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        String[] str = new String[]{userManagedBean.getCompany()};
        company = str;
        String comSql = " R.company= '" + company[0] + "'";
        queryFormId = null;
        equipmentRepairList = equipmentRepairBean.getRepairCostSummaryList(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), queryFormId, queryName, comSql);
        super.init();
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {

        fileName = "维修费用汇总表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        Row row1;
        Row row2;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 900);
        row1 = sheet1.createRow(1);
        row1.setHeight((short) 800);
        row2 = sheet1.createRow(2);
        row2.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }

        if (equipmentRepairList == null || equipmentRepairList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }

        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("维修费用汇总表");
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        Cell cellTime = row1.createCell(0);
        cellTime.setCellStyle(style.get("date"));
        SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日");
        cellTime.setCellValue(date.format(queryDateBegin) + "-----" + date.format(queryDateEnd));
        int j = 3;

        List<?> itemList = equipmentRepairList;
        List<Object[]> list = (List<Object[]>) itemList;
        for (Object[] eq : list) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            if (eq[0] != null) {
                cell0.setCellValue(eq[0].toString());
            }
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            if (eq[1] != null) {
                cell1.setCellValue(eq[1].toString());
            }
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(eq[2].toString());

            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            if (eq[3] != null) {
                cell3.setCellValue(Double.parseDouble(eq[3].toString()));
            }
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            if (eq[4] != null) {
                cell4.setCellValue(Double.parseDouble(eq[4].toString()));
            }
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            if (eq[5] != null) {
                cell5.setCellValue(Double.parseDouble(eq[5].toString()));
            }
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            if (eq[6] != null) {
                cell6.setCellValue(Double.parseDouble(eq[6].toString()));
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
    private String[] getInventoryTitle() {
        return new String[]{"资产编号", "设备名称", "报修部门", "其他费用(元)", "人工费用(元)", "备件费用(元)", "费用总计(元)"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{20, 15, 20, 10, 10, 10, 10};
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
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String strdate = "";
        String enddate = "";
        String companySql = "";
        if (company.length > 0) {
            for (String sqlCompanyID : company) {
                companySql += "or  R.company= " + " '" + sqlCompanyID + "' ";
            }
            companySql = companySql.substring(2, companySql.length());
        }
        if (queryDateBegin != null) {

            strdate = simpleDateFormat.format(queryDateBegin);
        }
        if (queryDateEnd != null) {
            enddate = simpleDateFormat.format(queryDateEnd);
        }

        equipmentRepairList = equipmentRepairBean.getRepairCostSummaryList(strdate, enddate, queryFormId, queryName, companySql);
    }

    public List<EquipmentRepair> getEquipmentRepairList() {
        return equipmentRepairList;
    }

    public void setEquipmentRepairList(List<EquipmentRepair> equipmentRepairList) {
        this.equipmentRepairList = equipmentRepairList;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public String[] getCompany() {
        return company;
    }

    public void setCompany(String[] company) {
        this.company = company;
    }

}
