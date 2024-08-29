/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.EquipmentAnalyResultBean;
import cn.hanbell.eam.ejb.EquipmentAnalyResultDtaBean;
import cn.hanbell.eam.ejb.EquipmentStandardBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentAnalyResult;
import cn.hanbell.eam.entity.EquipmentAnalyResultDta;
import cn.hanbell.eam.entity.EquipmentRepairFile;
import cn.hanbell.eam.entity.EquipmentStandard;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentAnalyResultModel;
import cn.hanbell.eam.web.FormMultiBean;
import com.lightshell.comm.BaseLib;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentAnalyResultManagedBean")
@SessionScoped
public class EquipmentAnalyResultManagedBean extends FormMultiBean<EquipmentAnalyResult, EquipmentAnalyResultDta> {

    @EJB
    private EquipmentAnalyResultBean equipmentAnalyResultBean;
    @EJB
    private EquipmentAnalyResultDtaBean equipmentAnalyResultDtaBean;
    @EJB
    private EquipmentStandardBean equipmentStandardBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private AssetCardBean assetCardBean;
    private String queryEquipmentName;
    private String queryDept;
    private String queryStandardType;
    private String queryStandardLevel;
    private String queryUserno;
    private String imageName;
    private List<SysCode> standardtypeList;
    private List<SysCode> standardlevelList;
    private List<SysCode> respondeptList;
    private List<SysCode> frequencyunitList;
    private List<SysCode> manhourunitList;

    public EquipmentAnalyResultManagedBean() {
        super(EquipmentAnalyResult.class, EquipmentAnalyResultDta.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCredate(getDate());
        newEntity.setFormdate(getDate());
        newEntity.setStatus("N");
        newEntity.setStandardlevel("一级");//自主保全只能生成一级的保全单
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCreator(userManagedBean.getUserid());

    }

    //处理上传图片数据
    public void handleFileUploadWhenDetailNew(FileUploadEvent event) throws IOException {
        super.handleFileUploadWhenNew(event);
        if (this.fileName != null) {
            currentDetail.setFilepath("../../resources/app/res/" + imageName);
            currentDetail.setFilename(fileName);
        }
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getAssetno()==null||"".equals(newEntity.getAssetno())) {
             showErrorMsg("Error", "请先选择设备编号");
            return false;
        }
        
        return super.doBeforePersist(); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
    @Override
    protected void upload() throws IOException {
        try {
            final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//            request.setCharacterEncoding("UTF-8");
            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
            imageName = String.valueOf(date.getTime());
            final InputStream in = this.file.getInputstream();
            final File dir = new File(this.getAppResPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            imageName = imageName + this.getFileName();
            final OutputStream out = new FileOutputStream(new File(dir.getAbsolutePath() + "//" + imageName));
            int read = 0;
            final byte[] bytes = new byte[1024];
            while (true) {
                read = in.read(bytes);
                if (read < 0) {
                    break;
                }
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", e.toString());
            FacesContext.getCurrentInstance().addMessage((String) null, msg);
        }
    }

    public void persistCJ() {
        newEntity.setIsspotcheck("Y");
        super.persist(); //To change body of generated methods, choose Tools | Templates.
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {

        fileName = "自主保全记录" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        List<EquipmentAnalyResult> eResult = equipmentAnalyResultBean.findByFilters(model.getFilterFields(), model.getSortFields());
        if (eResult == null || eResult.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("自主保全记录");
        int j = 2;
        for (EquipmentAnalyResult eq : eResult) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(date1.format(eq.getFormdate()));
            cell0 = row.createCell(1);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getAssetno());
            cell0 = row.createCell(2);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getMesid());
            cell0 = row.createCell(3);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getAssetdesc());
            cell0 = row.createCell(4);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getDeptname());
            cell0 = row.createCell(5);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getStandardlevel());
            cell0 = row.createCell(6);
            cell0.setCellStyle(style.get("cell"));
            if (eq.getStartdate() != null) {
                cell0.setCellValue(date.format(eq.getStartdate()));
            }
            cell0 = row.createCell(7);
            cell0.setCellStyle(style.get("cell"));
            if (eq.getEnddate() != null) {
                cell0.setCellValue(date.format(eq.getEnddate()));
            }
            cell0 = row.createCell(8);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq.getDowntime());

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
        return new String[]{"保养计划日期", "资产编号", "MES编号", "设备名称", "所属部门", "基准等级", "开始时间", "结束时间", "是否停机"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 20, 20, 10, 20, 20, 10, 10};
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
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        int seq = 0;//获取已完成的不是异常的数量
        int abnormal = 0;//异常的数量
        for (EquipmentAnalyResultDta eDta : detailList) {
            if (eDta.getAnalysisresult() != null && eDta.getAnalysisresult().equals("异常")) {
                currentEntity.setAnalysisresult("异常");
                abnormal++;
            } else if (eDta.getAnalysisresult() != null) {
                seq++;
            }
        }
        if (seq + abnormal == detailList.size()) {//判断是否该张单子已实施完毕
            if (currentEntity.getEnddate() == null) {//判断保养结束日期是否已经赋值，没有则赋值
                currentEntity.setEnddate(getDate());
            }
            currentEntity.setStatus("V");
        } else if (seq != 0 || abnormal != 0) {//判断是否正在实施
            currentEntity.setStatus("S");
        }
        if (seq == detailList.size()) {
            currentEntity.setAnalysisresult("正常");
        }
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    //作废更改单价转态为N
    public void invalid() {
        if (currentEntity == null) {
            showErrorMsg("Error", "请选择要作废的保全记录");
            return;
        }
        currentEntity.setStatus("Z");
        super.update();
    }

    @Override
    public void init() {
        superEJB = equipmentAnalyResultBean;
        model = new EquipmentAnalyResultModel(equipmentAnalyResultBean, userManagedBean);
        detailEJB = equipmentAnalyResultDtaBean;
        standardtypeList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "standardtype");
        standardlevelList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "standardlevel");
        respondeptList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "respondept");
        frequencyunitList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "frequencyunit");
        manhourunitList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "manhourunit");
        queryState = "N";//初始查询待实施的数据
        queryStandardLevel = "一级";//初始查询等级一级的数据
        queryDateBegin = getDate();
        queryDateEnd = getDate();
        if (queryDateBegin != null) {
            model.getFilterFields().put("formdateBegin", queryDateBegin);
        }
        if (queryDateEnd != null) {
            model.getFilterFields().put("formdateEnd", queryDateEnd);
        }
        this.model.getFilterFields().put("deptno", userManagedBean.getCurrentUser().getDeptno().substring(0, 3));
        this.model.getFilterFields().put("status", queryState);
        this.model.getFilterFields().put("standardlevel", queryStandardLevel);
        this.model.getSortFields().put("formid", "ASC");
        super.init();
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        detailList.clear();//清除前面选择的设备基准
        if (event.getObject() != null && newEntity != null) {
            AssetCard e = (AssetCard) event.getObject();
            openOptions = new HashMap<>();
            if (queryDept != null && !"".equals(queryDept)) {
                openOptions.put("respondept", queryDept);
            }
            if (newEntity.getStandardlevel() != null && !"".equals(newEntity.getStandardlevel())) {
                openOptions.put("standardlevel", newEntity.getStandardlevel());
            }
            if (queryStandardType != null && !"".equals(queryStandardType)) {
                openOptions.put("standardtype", queryStandardType);
            }
            if (e.getFormid() != null && !"".equals(e.getFormid())) {
                openOptions.put("assetno", e.getFormid());
            }
            // List<EquipmentStandard> eStandard = equipmentStandardBean.getEquipmentStandardList(queryDept, newEntity.getStandardlevel(), queryStandardType, e.getFormid());//获取设备下对应的基准
            List<EquipmentStandard> eStandard = equipmentStandardBean.findByFilters(openOptions); //List<EquipmentStandard> eStandard = equipmentStandardBean.findByAssetno(e.getFormid(),newEntity.getStandardlevel());//获取设备下对应的基准
            newEntity.setAssetno(e.getFormid());
            newEntity.setSpareno(e.getAssetItem().getItemno());
            newEntity.setDeptname(e.getDeptname());
            newEntity.setDeptno(e.getDeptno());
            newEntity.setAssetdesc(e.getAssetDesc());
            int seq = 1;
            for (EquipmentStandard eS : eStandard) {
                EquipmentAnalyResultDta eArDta = new EquipmentAnalyResultDta();//将查出的基准一一筛入保全子类
                eArDta.setStandardtype(eS.getStandardtype());
                eArDta.setCompany(newEntity.getCompany());
                eArDta.setRespondept(eS.getRespondept());
                eArDta.setSeq(seq);
                eArDta.setCheckarea(eS.getCheckarea());
                eArDta.setCheckcontent(eS.getCheckcontent());
                eArDta.setJudgestandard(eS.getJudgestandard());
                eArDta.setMethod(eS.getMethod());
                eArDta.setMethodname(eS.getMethodname());
                eArDta.setDowntime(eS.getDowntime());
                eArDta.setDownunit(eS.getDownunit());
                eArDta.setManhour(eS.getManhour());
                eArDta.setManpower(eS.getManpower());
                eArDta.setAreaimage(eS.getAreaimage());
                eArDta.setPlandate(eS.getNexttime());
                eArDta.setCreator(userManagedBean.getUserid());
                eArDta.setCredate(getDate());

                eArDta.setStatus("N");
                seq++;
                detailList.add(eArDta);
                addedDetailList.add(eArDta);
            }
        }
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail.getCheckcontent().equals("输入当前区域条码")) {
            if (!currentDetail.getException().equals(currentDetail.getJudgestandard())) {//输入的区域条码不匹配则不管前端选择的异常，直接赋值异常
                currentDetail.setAnalysisresult("异常");
            }
        }
        currentDetail.setSdate(currentEntity.getCfmdate());
        currentDetail.setEdate(getDate());
        currentEntity.setCfmdate(getDate());
        super.doConfirmDetail();//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String edit(String path) {
        if (currentEntity.getIsspotcheck()!=null && !currentEntity.getCreator().equals(userManagedBean.getUserid())) {
            showErrorMsg("Error", "抽检单只有创建人才能点检！！！");
            return "";
        }

        if (currentEntity.getStartdate() == null) {
            currentEntity.setStartdate(getDate());
        }
        return super.edit(path); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createDetail() {
        super.createDetail(); //To change body of generated methods, choose Tools | Templates.
        currentDetail.setStatus("N");
        currentDetail.setCredate(getDate());
        currentDetail.setCreator(userManagedBean.getUserid());
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            this.model.getSortFields().clear();
            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("assetno", queryFormId);
            }
            if (queryStandardLevel != null && !"".equals(this.queryStandardLevel)) {
                this.model.getFilterFields().put("standardlevel", queryStandardLevel);
            }
            if (queryState != null && !"".equals(this.queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
            if (queryEquipmentName != null && !"".equals(this.queryEquipmentName)) {
                this.model.getFilterFields().put("assetdesc", queryEquipmentName);
            }
            if (queryDept != null && !"".equals(this.queryDept)) {
                this.model.getFilterFields().put("deptname", queryDept);
            }

        }
    }

    public String getStutusName(String status) {
        if (status.equals("N")) {
            return "待实施";
        } else if (status.equals("S")) {
            return "实施中";
        } else if (status.equals("V")) {
            return "已完成";
        } else {
            return "已作废";
        }

    }

    public String getQueryUserno() {
        return queryUserno;
    }

    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

    public String getQueryEquipmentName() {
        return queryEquipmentName;
    }

    public void setQueryEquipmentName(String queryEquipmentName) {
        this.queryEquipmentName = queryEquipmentName;
    }

    public String getQueryDept() {
        return queryDept;
    }

    public void setQueryDept(String queryDept) {
        this.queryDept = queryDept;
    }

    public String getQueryStandardType() {
        return queryStandardType;
    }

    public void setQueryStandardType(String queryStandardType) {
        this.queryStandardType = queryStandardType;
    }

    public String getQueryStandardLevel() {
        return queryStandardLevel;
    }

    public void setQueryStandardLevel(String queryStandardLevel) {
        this.queryStandardLevel = queryStandardLevel;
    }

    public List<SysCode> getStandardtypeList() {
        return standardtypeList;
    }

    public void setStandardtypeList(List<SysCode> standardtypeList) {
        this.standardtypeList = standardtypeList;
    }

    public List<SysCode> getStandardlevelList() {
        return standardlevelList;
    }

    public void setStandardlevelList(List<SysCode> standardlevelList) {
        this.standardlevelList = standardlevelList;
    }

    public List<SysCode> getRespondeptList() {
        return respondeptList;
    }

    public void setRespondeptList(List<SysCode> respondeptList) {
        this.respondeptList = respondeptList;
    }

    public List<SysCode> getFrequencyunitList() {
        return frequencyunitList;
    }

    public void setFrequencyunitList(List<SysCode> frequencyunitList) {
        this.frequencyunitList = frequencyunitList;
    }

    public List<SysCode> getManhourunitList() {
        return manhourunitList;
    }

    public void setManhourunitList(List<SysCode> manhourunitList) {
        this.manhourunitList = manhourunitList;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
