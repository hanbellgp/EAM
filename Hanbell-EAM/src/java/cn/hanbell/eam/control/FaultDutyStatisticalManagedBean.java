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
import cn.hanbell.eap.ejb.CompanyBean;
import cn.hanbell.eap.entity.Company;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.InputStream;
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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "faultDutyStatisticalManagedBean")
@SessionScoped
public class FaultDutyStatisticalManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHelpers> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;
    @EJB
    private CompanyBean companyBean;
    private List<EquipmentRepair> equipmentRepairsList;
    private PieChartModel pieModel;
    private List<Company> companyList;
    private String[] company;

    public FaultDutyStatisticalManagedBean() {
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        String[] str = new String[]{userManagedBean.getCompany()};
        company = str;
        String comSql = " R.company= '" + company[0] + "'";
        queryFormId = null;
        queryName = null;
        equipmentRepairsList = equipmentRepairBean.getFaultDutyStatisticalList(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), queryFormId, queryName, comSql);
        createPieModel();

    }

//导出界面的EXCEL数据处理
    @Override
    public void print() {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//            测试路径
//            int index = finalFilePath.indexOf("dist/gfdeploy");
//            //正式路径  D:\Java\glassfish5.0.1\glassfish\domains\domain1\applications\EAM\Hanbell-EAM_war\rpt
//                      //D:\Java\glassfish5.0.1\glassfish\domains\domain1\applications\KPI\Hanbell-KPI_war\rpt
//D:\Java\glassfish5.0.1\glassfish\domains\domain1\applications\EAM\Hanbell-EAM_war\WEB-INF\classes\cn\hanbell\eam\control
///D:/C2079/EAM/dist/gfdeploy/EAM/Hanbell-EAM_war/WEB-INF/classes/cn/hanbell/eam/control/RepairManHourSummaryManagedBean.class
//D:/C2079/EAM/Hanbell-EAM/web/rpt/维修工时汇总表模板.xls
            int index = finalFilePath.indexOf("WEB-INF");
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + "rpt/故障责任统计表模板.xls");
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            if (equipmentRepairsList == null || equipmentRepairsList.isEmpty()) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return;
            }
            List<?> itemList = equipmentRepairsList;
            int j = 1;
            List<Object[]> list = (List<Object[]>) itemList;

            int[] sumList = new int[5];
            for (Object[] eq : list) {
                row = sheet.createRow(j);
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

                cell3.setCellValue(Integer.parseInt(eq[3].toString()));
                Cell cell4 = row.createCell(4);
                cell4.setCellStyle(style.get("cell"));
                cell4.setCellValue(Integer.parseInt(eq[4].toString()));
                Cell cell5 = row.createCell(5);
                cell5.setCellStyle(style.get("cell"));
                cell5.setCellValue(Integer.parseInt(eq[5].toString()));
                Cell cell6 = row.createCell(6);
                cell6.setCellStyle(style.get("cell"));
                cell6.setCellValue(Integer.parseInt(eq[6].toString()));
                Cell cell7 = row.createCell(7);
                cell7.setCellStyle(style.get("cell"));
                cell7.setCellValue(Integer.parseInt(eq[7].toString()));
                sumList[0] += Integer.parseInt(eq[3].toString());
                sumList[1] += Integer.parseInt(eq[4].toString());
                sumList[2] += Integer.parseInt(eq[5].toString());
                sumList[3] += Integer.parseInt(eq[6].toString());
                sumList[4] += Integer.parseInt(eq[7].toString());
            }
            row = sheet.getRow(6);
            for (int i = 0; i < 5; i++) {
                Cell cell1 = row.createCell(9 + i);
                cell1.setCellStyle(style.get("cell"));
                cell1.setCellValue(sumList[i]);
            }
            OutputStream os = null;
            fileName = "故障责任统计表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        styles.put("left", leftStyle);

        CellStyle rightStyle = wb.createCellStyle();
        rightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        rightStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        rightStyle.setBorderRight(CellStyle.BORDER_THIN);
        rightStyle.setBorderBottom(CellStyle.BORDER_THIN);
        styles.put("right", rightStyle);
        
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setWrapText(true);//设置自动换行
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());//单元格背景颜色
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setBorderRight(CellStyle.BORDER_THIN);
        titleStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        titleStyle.setBorderLeft(CellStyle.BORDER_THIN);
        titleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        titleStyle.setBorderTop(CellStyle.BORDER_THIN);
        titleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        titleStyle.setBorderBottom(CellStyle.BORDER_THIN);
        titleStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font headFont2 = wb.createFont();
        headFont2.setFontHeightInPoints((short) 20);
        headFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(headFont2);
        styles.put("title", titleStyle);
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

        equipmentRepairsList = equipmentRepairBean.getFaultDutyStatisticalList(strdate, enddate, queryFormId, queryName, companySql);
        createPieModel();
    }

    private void createPieModel() {
        pieModel = new PieChartModel();
        List<?> itemList = equipmentRepairsList;
        List<Object[]> list = (List<Object[]>) itemList;
        int A = 0;
        int B = 0;
        int C = 0;
        int D = 0;
        int E = 0;
        if (equipmentRepairsList != null) {
            for (Object[] objects : list) {
                A += Integer.parseInt(objects[3].toString());
                B += Integer.parseInt(objects[4].toString());
                C += Integer.parseInt(objects[5].toString());
                D += Integer.parseInt(objects[6].toString());
                E += Integer.parseInt(objects[7].toString());
            }
        }

        pieModel.set("使用不当", A);
        pieModel.set("保养不当", B);
        pieModel.set("维修不当", C);
        pieModel.set("劣质配件", D);
        pieModel.set("正常使用寿命", E);
        pieModel.setLegendPosition("e");
        pieModel.setFill(true);
        pieModel.setShowDataLabels(true);
        pieModel.setDiameter(250);
        pieModel.setShadow(false);
    }

    public List<EquipmentRepair> getEquipmentRepairsList() {
        return equipmentRepairsList;
    }

    public void setEquipmentRepairsList(List<EquipmentRepair> equipmentRepairsList) {
        this.equipmentRepairsList = equipmentRepairsList;
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
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
