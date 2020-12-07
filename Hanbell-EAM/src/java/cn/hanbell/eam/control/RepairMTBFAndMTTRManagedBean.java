package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import cn.hanbell.eam.entity.SysCode;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "repairMTBFAndMTTRManagedBean")
@SessionScoped
public class RepairMTBFAndMTTRManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHelpers> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private CompanyBean companyBean;
    private List<Object[]> equipmentRepairsList;
    private List<Company> companyList;
    private String[] company;

    public RepairMTBFAndMTTRManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHelpers.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        detailEJB = equipmentRepairHelpersBean;
        if (equipmentRepairsList != null) {
            equipmentRepairsList.clear();
        }
        companyList = companyBean.findBySystemName("EAM");
        company = null;
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {

        fileName = "设备MTBF和MTTR表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        if (equipmentRepairsList == null || equipmentRepairsList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }

        int j = 1;
        List<?> itemList = equipmentRepairsList;

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
            cell1.setCellValue(eq[1].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(eq[2].toString());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(Double.parseDouble(eq[3].toString()));
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(Double.parseDouble(eq[4].toString()));
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(Double.parseDouble(eq[5].toString()));
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(Double.parseDouble(eq[6].toString()));
            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            cell7.setCellValue(Double.parseDouble(eq[7].toString()));
            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            cell8.setCellValue(Double.parseDouble(eq[8].toString()));
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
        return new String[]{"资产编号", "设备名称", "报修部门", "计划工作时间", "故障停机时间", "维修次数", "维修时间", "MTBF(小时/件)", "MTTR(小时/件)"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{20, 20, 20, 15, 15, 15, 15, 10, 10};
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

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        if (queryDateBegin == null) {
            showErrorMsg("Error", "请输入开始时间");
            return;
        }
        if (queryDateEnd == null) {
            showErrorMsg("Error", "请输入结束时间");
            return;
        }
        String pattern = "yyyy/MM/dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String strdate = "";
        String enddate = "";
        List<SysCode> codeList = sysCodeBean.getTroubleNameList("RD", "itemno");
        String sql = "";
        String companySql = "";
        if (company.length > 0) {
            for (String sqlCompanyID : company) {
                companySql += "or  R.company= " + " '" + sqlCompanyID + "' ";
            }
            companySql = companySql.substring(2, companySql.length());
        }
        if (codeList.size() > 0) {
            sql = codeList.stream().map(sqlCode -> "'" + sqlCode.getCvalue() + "',").reduce(sql, String::concat);
        }
        if (sql != null && !"".equals(sql)) {
            sql = sql.substring(0, sql.length() - 1);
        }

        strdate = simpleDateFormat.format(queryDateBegin);

        enddate = simpleDateFormat.format(queryDateEnd);

        equipmentRepairsList = equipmentRepairBean.getMTBFAndMTTR(strdate, enddate, queryFormId, queryName, sql,companySql);
    }

    public List<Object[]> getEquipmentRepairsList() {
        return equipmentRepairsList;
    }

    public void setEquipmentRepairsList(List<Object[]> equipmentRepairsList) {
        this.equipmentRepairsList = equipmentRepairsList;
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
