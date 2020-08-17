/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairFileBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.EquipmentRepairSpareBean;
import cn.hanbell.eam.ejb.EquipmentTroubleBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair2;
import cn.hanbell.eam.entity.EquipmentRepairFile2;
import cn.hanbell.eam.entity.EquipmentRepairHis2;
import cn.hanbell.eam.entity.EquipmentRepairSpare2;
import cn.hanbell.eam.entity.EquipmentSpare2;
import cn.hanbell.eam.entity.EquipmentTrouble2;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.lazy.EquipmentRepairModel;
import cn.hanbell.eam.web.FormMulti3Bean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentAcceptanceManagedBean")
@SessionScoped
public class EquipmentAcceptanceManagedBean extends FormMulti3Bean<EquipmentRepair2, EquipmentRepairFile2, EquipmentRepairSpare2, EquipmentRepairHis2> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairFileBean equipmentRepairFileBean;
    @EJB
    protected EquipmentRepairSpareBean equipmentRepairSpareBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private EquipmentTroubleBean equipmentTroubleBean;
    @EJB
    private SysCodeBean sysCodeBean;
    private String queryEquipmentName;
    private String imageName;
    private String maintenanceSupervisor;
    private String queryServiceuser;
    private String queryDeptname;
    private BigDecimal qty;
    private double maintenanceCosts;
    private List<EquipmentTrouble2> equipmentTroubleList;

    public EquipmentAcceptanceManagedBean() {
        super(EquipmentRepair2.class, EquipmentRepairFile2.class, EquipmentRepairSpare2.class, EquipmentRepairHis2.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        openParams = new HashMap<>();
        superEJB = equipmentRepairBean;
        model = new EquipmentRepairModel(equipmentRepairBean, userManagedBean);
        detailEJB = equipmentRepairFileBean;
        detailEJB2 = equipmentRepairSpareBean;
        detailEJB3 = equipmentRepairHisBean;
        
        queryState = "ALL";
        queryServiceuser = getUserName(userManagedBean.getUserid());
        model.getFilterFields().put("rstatus", queryState);
        model.getFilterFields().put("company", userManagedBean.getCompany());
        model.getFilterFields().put("serviceuser", userManagedBean.getUserid());
        model.getSortFields().put("credate", "DESC");
        super.init();
    }

//保存验收数据
    public void saveAcceptance() {
        currentEntity.setRstatus("40");//更新状态
        createDetail();
        super.update();//To change body of generated methods, choose Tools | Templates.
    }

//选择备件数据处理
    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            EquipmentSpare2 u = (EquipmentSpare2) event.getObject();
            currentDetail2.setUprice(u.getUprice());
            currentDetail2.setSpareno(u.getSpareno());
            currentDetail2.setUserno(currentEntity.getServiceuser());
            currentDetail2.setSparenum(u.getSparenum());
            currentDetail2.setUserdate(getDate());
        }
    }

    @Override
    public void deleteDetail2() {

        super.deleteDetail2(); //To change body of generated methods, choose Tools | Templates.
        getPartsCost();
    }

    //选中备件确认时检查及处理
    @Override
    public void doConfirmDetail2() {
        if (currentDetail2.getSpareno() == null) {
            showErrorMsg("Error", "请选择备件");
            return;
        }
        currentDetail2.setQty(qty.intValue());
        currentDetail2.setPid(currentEntity.getFormid());
        currentDetail2.setCompany(currentEntity.getCompany());
        currentDetail2.setStatus("Y");

        super.doConfirmDetail2();
        getPartsCost();
    }

    //修改维修验收单
    public String editAcceptance(String path) {
        if (currentEntity == null) {
            if (this.currentEntity == null) {
                showErrorMsg("Error", "请选择一条数据！！！");
                return "";
            }
        }
        if (Integer.parseInt(currentEntity.getRstatus()) != 40) {
            showErrorMsg("Error", "请确认选择的单据是否已发起验收单！！！");
            return "";
        }
        addedDetailList.clear();
        //获取联络时间
        currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getCredate(), 0));

        //获取维修时间
        currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
        //获取维修课长
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        equipmentTroubleList = equipmentTroubleBean.findAll();
        getPartsCost();
        return super.edit(path);

    }

    @Override
    public String view(String path) {
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getCredate(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
        }
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
          String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        getPartsCost();
        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }
//获取停机时间

    public void getDowntimes() {
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
    }
//审批前检查数据是否可以审批

    public void approvalCheck(String view) {
        if (Integer.parseInt(currentEntity.getRstatus()) < 40) {
            showErrorMsg("Error", "该条数据未验收，不能审批");
            return;
        }
        if (Integer.parseInt(currentEntity.getRstatus()) > 40) {
            showErrorMsg("Error", "该条数据已审批完成,等待报修人结案");
            return;
        }
        //获取维修课长
        //String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        //String userId = systemUserBean.findByDeptno(deptno).get(0).getUserid();
        //if (!userManagedBean.getUserid().equals(userId)) {
        //  showErrorMsg("Error", "维修课长才能进行审批");
        // return;
        // }
        //获取维修课长
        EquipmentRepairHis2  equipmentrepairhis=new EquipmentRepairHis2();
        currentDetail3=equipmentrepairhis;
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();

        super.openDialog(view); //To change body of generated methods, choose Tools | Templates.
    }

    //审批
    public void approval() {
        if (currentDetail3.getContenct() != null) {
            currentDetail3.setCompany(userManagedBean.getCompany());
            currentDetail3.setStatus("N");
            currentDetail3.setContenct("符合");
            currentDetail3.setPid(currentEntity.getFormid());
        }

        super.update();
        closeDialog();
    }
//获取零件费用

    public Double getPartsCost() {
        maintenanceCosts = 0;
        detailList2.forEach(equipmentrepair -> {
            BigDecimal price = equipmentrepair.getUprice();
            maintenanceCosts += equipmentrepair.getQty() * price.doubleValue();
        });
        return maintenanceCosts;
    }
//加载文件

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

    //处理上传图片数据
    public void handleFileUploadWhenDetailNew(FileUploadEvent event) throws IOException {
        super.handleFileUploadWhenNew(event);
        if (this.fileName != null) {
            this.createDetail();
            int seq = detailList.size() + 1;
            EquipmentRepairFile2 equipmentrepairfile = new EquipmentRepairFile2();
            equipmentrepairfile.setCompany(userManagedBean.getCompany());
            equipmentrepairfile.setFilepath(this.getAppImgPath().replaceAll("//", "/"));
            equipmentrepairfile.setFilename(imageName);
            equipmentrepairfile.setFilefrom("维修图片");
            equipmentrepairfile.setStatus("Y");
            equipmentrepairfile.setSeq(seq);
            equipmentrepairfile.setPid(currentEntity.getFormid());
            detailList.add(equipmentrepairfile);
            addedDetailList.add(equipmentrepairfile);
            super.doConfirmDetail();
        }
    }

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        if (model != null) {
            this.model.getFilterFields().clear();

            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }

            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("formid", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                model.getFilterFields().put("assetno", queryName);
            }
            if (queryEquipmentName != null && !"".equals(queryEquipmentName)) {
                model.getFilterFields().put("itemno.assetDesc", queryEquipmentName);
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("itemno.deptname", queryDeptname);
            }
            if (queryServiceuser != null && !"".equals(queryServiceuser)) {
                model.getFilterFields().put("serviceusername", queryServiceuser);
            }
            model.getFilterFields().put("company", userManagedBean.getCompany());
            model.getFilterFields().put("rstatus", queryState);
            model.getSortFields().put("credate", "DESC");

        }
    }

    //获取显示的进度
    public String getStateName(String str) {
        String queryStateName = "";
        switch (str) {
            case "10":
                queryStateName = "已报修";
                break;
            case "20":
                queryStateName = "维修到达";
                break;
            case "30":
                queryStateName = "维修完成";
                break;
            case "40":
                queryStateName = "维修验收";
                break;
            case "50":
                queryStateName = "维修审核";
                break;
            case "95":
                queryStateName = "报修结案";
                break;
            case "98":
                queryStateName = "作废";
                break;
        }
        return queryStateName;
    }
//处理俩时间显示的格式

    public String getTimeDifference(Date strDate, Date endDate, int downTimes) {
        long l = strDate.getTime() - endDate.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (downTimes != 0) {
            int days = downTimes / (60 * 24);
            downTimes -= days * 60 * 24;
            int hours = (int) Math.floor(downTimes / 60);
            downTimes -= days * 60;
            int minute = (int) (downTimes % 60);
            day = day - days;

            hour = hour - hours;
            if (hour < 0) {
                day = day - 1;
                hour = hour + 24;
            }
            min = min - minute;
            if (min < 0) {
                hour = hour - 1;
                min = min + 60;
            }
            if (day < 0) {
                hour = 0;
                min = 0;
                day = 0;
                s = 0;
            }
            if (hour < 0) {
                hour = 0;
                min = 0;
                day = 0;
                s = 0;
            }
        }
        return "" + day + "天" + hour + "小时" + min + "分";
    }

    //根据用户ID获取用户姓名
    public String getUserName(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        return s.getUsername();
    }

    public String getQueryEquipmentName() {
        return queryEquipmentName;
    }

    public void setQueryEquipmentName(String queryEquipmentName) {
        this.queryEquipmentName = queryEquipmentName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getMaintenanceSupervisor() {
        return maintenanceSupervisor;
    }

    public void setMaintenanceSupervisor(String maintenanceSupervisor) {
        this.maintenanceSupervisor = maintenanceSupervisor;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public String getQueryServiceuser() {
        return queryServiceuser;
    }

    public void setQueryServiceuser(String queryServiceuser) {
        this.queryServiceuser = queryServiceuser;
    }

    public String getQueryDeptname() {
        return queryDeptname;
    }

    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

    public List<EquipmentTrouble2> getEquipmentTroubleList() {
        return equipmentTroubleList;
    }

    public void setEquipmentTroubleList(List<EquipmentTrouble2> equipmentTroubleList) {
        this.equipmentTroubleList = equipmentTroubleList;
    }

    public double getMaintenanceCosts() {
        return maintenanceCosts;
    }

    public void setMaintenanceCosts(double maintenanceCosts) {
        this.maintenanceCosts = maintenanceCosts;
    }

}
