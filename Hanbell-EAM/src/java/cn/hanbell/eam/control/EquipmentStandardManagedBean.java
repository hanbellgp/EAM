/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentStandardBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentStandard;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentStandardModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentStandardManagedBean")
@SessionScoped
public class EquipmentStandardManagedBean extends SuperSingleBean<EquipmentStandard> {

    @EJB
    private EquipmentStandardBean equipmentStandardBean;
    @EJB
    private SysCodeBean sysCodeBean;
    private String queryEquipmentName;
    private String imageName;
    private String queryDept;
    private String queryStandardType;
    private String queryStandardLevel;
    private List<String> paramPosition = null;
    private List<SysCode> standardtypeList;
    private List<SysCode> standardlevelList;
    private List<SysCode> respondeptList;
    private List<SysCode> frequencyunitList;
    private List<SysCode> manhourunitList;

    public EquipmentStandardManagedBean() {
        super(EquipmentStandard.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setCredate(getDate());
        newEntity.setStatus("V");
        newEntity.setCreator(userManagedBean.getUserid());
    }

    @Override
    public void init() {
        superEJB = equipmentStandardBean;
        model = new EquipmentStandardModel(equipmentStandardBean, userManagedBean);
        model.getFilterFields().put("status", "V");//只查询未作废的基准
        standardtypeList = sysCodeBean.getTroubleNameList("RD", "standardtype");
        standardlevelList = sysCodeBean.getTroubleNameList("RD", "standardlevel");
        respondeptList = sysCodeBean.getTroubleNameList("RD", "respondept");
        frequencyunitList = sysCodeBean.getTroubleNameList("RD", "frequencyunit");
        manhourunitList = sysCodeBean.getTroubleNameList("RD", "manhourunit");
        super.init();
        openParams = new HashMap<>();
    }

    //复制基准
    public String copy(String path) {
        if (currentEntity == null) {
            showErrorMsg("Error", "请先选择需要复制的基准！！！");
            return "";
        }
        newEntity = currentEntity;
        return super.create(path);
    }

    //作废更改单价转态为W
    public void invalid() {
        currentEntity.setStatus("W");
        super.update();
    }

    //获取报修人对应部门的设备
    public void screeningOpenDialog(String view) {
        if (paramPosition == null) {
            paramPosition = new ArrayList<>();
        } else {
            paramPosition.clear();

        }
        super.openDialog(view);
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            AssetCard e = (AssetCard) event.getObject();
            if (e.getFormid() != null) {
                newEntity.setAssetno(e.getFormid());
                newEntity.setAssetdesc(e.getAssetDesc());
                newEntity.setItemno(e.getAssetItem().getItemno());
                newEntity.setDeptno(e.getDeptno());
                newEntity.setDeptname(e.getDeptname());
            }
        }
    }

    @Override
    public void update() {
        currentEntity.setOptdate(getDate());
        currentEntity.setOptuser(userManagedBean.getUserid());
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity.getAssetno() == null) {
            showErrorMsg("Error", "请先选择设备编号！！！");
            return false;
        }
        if (newEntity.getCheckarea() == null) {
            showErrorMsg("Error", "请先选择输入检验区域！！！");
            return false;
        }
        if (newEntity.getCheckcontent() == null) {
            showErrorMsg("Error", "请先选择输入检验内容！！！");
            return false;
        }
        if (newEntity.getFrequency() == null) {
            showErrorMsg("Error", "请先选择输入检验周期(M)！！！");
            return false;
        }
        if (newEntity.getLasttime() == null) {
            showErrorMsg("Error", "请先选择输入固定保全时间！！！");
            return false;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd EE hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(newEntity.getLasttime());//设置起时间
        if (newEntity.getFrequencyunit().equals("天")) {
            cal.add(Calendar.DATE, newEntity.getFrequency());
            newEntity.setNexttime(cal.getTime());
        } else if (newEntity.getFrequencyunit().equals("周")) {
            cal.add(Calendar.DATE, newEntity.getFrequency() * 7);
            newEntity.setNexttime(cal.getTime());
        } else if (newEntity.getFrequencyunit().equals("月")) {
            cal.add(Calendar.MONTH, newEntity.getFrequency());
            newEntity.setNexttime(cal.getTime());
        } else if (newEntity.getFrequencyunit().equals("季")) {
            cal.add(Calendar.MONTH, newEntity.getFrequency() * 3);
            newEntity.setNexttime(cal.getTime());
        } else if (newEntity.getFrequencyunit().equals("年")) {
            cal.add(Calendar.YEAR, 1);
            newEntity.setNexttime(cal.getTime());
        }

        return super.doBeforePersist(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getFrequencyunitName(String frequencyunit, int frequency) {
        return frequency + frequencyunit;
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryStandardType != null && !"".equals(queryStandardType)) {
                this.model.getFilterFields().put("standardtype", queryStandardType);
            }
            if (queryStandardLevel != null && !"".equals(this.queryStandardLevel)) {
                this.model.getFilterFields().put("standardlevel", queryStandardLevel);
            }
            if (queryDept != null && !"".equals(queryDept)) {
                this.model.getFilterFields().put("deptname", queryDept);
            }
            if (queryEquipmentName != null && "".equals(queryEquipmentName)) {
                this.model.getFilterFields().put("assetdesc", queryEquipmentName);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("assetno", queryFormId);
            }
            model.getFilterFields().put("status", "V");//只查询未作废的基准
        }
    }

    //处理上传图片数据
    public void handleFileUploadWhenDetailNew(FileUploadEvent event) throws IOException {
        super.handleFileUploadWhenNew(event);
        if (this.fileName != null) {
            newEntity.setAreaimage("../../resources/app/res/" + imageName);
        }
    }

    //处理上传图片数据
    public void handleFileUploadWhenDetailEdit(FileUploadEvent event) throws IOException {
        super.handleFileUploadWhenNew(event);
        if (this.fileName != null) {
            currentEntity.setAreaimage("../../resources/app/res/" + imageName);
        }
    }

    @Override
    protected void upload() throws IOException {
        try {
            final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            request.setCharacterEncoding("UTF-8");
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

    public List<String> getParamPosition() {
        return paramPosition;
    }

    public void setParamPosition(List<String> paramPosition) {
        this.paramPosition = paramPosition;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

}
