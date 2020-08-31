/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetFileBean;
import cn.hanbell.eam.ejb.AssetManufacturerBean;
import cn.hanbell.eam.ejb.AssetParameterDtaBean;

import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetFile;
import cn.hanbell.eam.entity.AssetManufacturer;
import cn.hanbell.eam.entity.AssetParameterDta;
import cn.hanbell.eam.lazy.AssetCardModel;
import cn.hanbell.eam.web.FormMulti3Bean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetParametersManagedBean")
@SessionScoped
public class AssetParametersManagedBean extends FormMulti3Bean<AssetCard, AssetManufacturer, AssetParameterDta, AssetFile> {

    @EJB
    private AssetCardBean assetCardBean;
    @EJB
    private AssetManufacturerBean assetManufacturerBean;
    @EJB
    private AssetParameterDtaBean assetParameterDtaBean;
    @EJB
    private AssetFileBean assetFileBean;
    private String queryItemno;
    private String queryItemdesc;
    private String queryDeptno;
    private String queryDeptname;
    private String queryUserno;
    private String queryUsername;
    private String queryWarehouseno;
    private String level;
    private String voltage;
    private String power;
private String imageName;
    public AssetParametersManagedBean() {
        super(AssetCard.class, AssetManufacturer.class, AssetParameterDta.class, AssetFile.class);
    }

    @Override
    public void init() {
        superEJB = assetCardBean;
        detailEJB = assetManufacturerBean;
        detailEJB2 = assetParameterDtaBean;
        detailEJB3=assetFileBean;
        model = new AssetCardModel(assetCardBean, userManagedBean);
        this.model.getFilterFields().put("assetItem.category.id =", 3);
        model.getSortFields().put("assetItem.itemno", "ASC");
        model.getSortFields().put("formid", "ASC");
        super.init();
    }

    @Override
    public String edit(String path) {
        if (detailList.isEmpty()) {
            createDetail();
        } else {
            currentDetail = detailList.get(0);
        }
        return super.edit(path); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        currentDetail.setPid(currentEntity.getFormid());
        currentDetail.setCompany(currentEntity.getCompany());
        currentDetail.setSeq(1);
        currentDetail.setStatus("V");
        doConfirmDetail();
        if (!"".equals(level) && checkLevel()) {
            createDetail2();
            currentDetail2.setParamname("设备等级");
            currentDetail2.setParamvalue(level);
            currentDetail2.setCompany(currentEntity.getCompany());
            currentDetail2.setStatus("V");
            currentDetail2.setCredate(getDate());
            doConfirmDetail2();
        }
        if (!"".equals(voltage) && checkVoltage()) {
            createDetail2();
            currentDetail2.setParamname("额定电压");
            currentDetail2.setParamvalue(voltage);
            currentDetail2.setCompany(currentEntity.getCompany());
            currentDetail2.setStatus("V");
            currentDetail2.setCredate(getDate());
            currentDetail2.setParamunit("V");
            doConfirmDetail2();
        }
        if (!"".equals(power) && checkPower()) {
            createDetail2();
            currentDetail2.setParamname("额定功率");
            currentDetail2.setParamvalue(power);
            currentDetail2.setCompany(currentEntity.getCompany());
            currentDetail2.setStatus("V");
            currentDetail2.setCredate(getDate());
            currentDetail2.setParamunit("Kw");
            doConfirmDetail2();
        }
        super.update(); //To change body of generated methods, choose Tools | Templates.
        level = "";
        voltage = "";
        power = "";
        if (!detailList.isEmpty()) {
            currentDetail = detailList.get(0);
        } else {
            getDetail();
        }
    }

    public void doConfirmDetailCheck() {
        if ("额定电压".equals(currentDetail2.getParamname())) {
            showErrorMsg("Error", "不能修改额定电压的名称");
            return;
        }
        if ("额定电流".equals(currentDetail2.getParamname())) {
            showErrorMsg("Error", "不能修改额定电流的名称");
            return;
        }
        if ("设备等级".equals(currentDetail2.getParamname())) {
            showErrorMsg("Error", "不能修改设备等级的名称");
            return;
        }
        currentDetail2.setCompany(currentEntity.getCompany());
        currentDetail2.setStatus("V");
        currentDetail2.setCredate(getDate());
        super.doConfirmDetail2(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteDetail2() {
        if ("设备等级".equals(currentDetail2.getParamname())) {
            showErrorMsg("Error", "不能删除该条数据");
            return;
        }
        if ("额定功率".equals(currentDetail2.getParamname())) {
            showErrorMsg("Error", "不能删除该条数据");
            return;
        }
        if ("额定电压".equals(currentDetail2.getParamname())) {
            showErrorMsg("Error", "不能删除该条数据");
            return;
        }
        super.deleteDetail2(); //To change body of generated methods, choose Tools | Templates.
    }

    //检查设备等级是否已经存在
    public boolean checkLevel() {
        return detailList2.stream().noneMatch(list -> ("设备等级".equals(list.getParamname())));
    }
    //检查额定电压是否已经存在

    public boolean checkVoltage() {
        return detailList2.stream().noneMatch(list -> ("额定电压".equals(list.getParamname())));
    }

    //检查额定功率是否已经存在
    public boolean checkPower() {
        return detailList2.stream().noneMatch(list -> ("额定功率".equals(list.getParamname())));
    }

    //检查更新或添加到项的名字是否是以下三个
    public boolean checkParamname() {
        if (currentDetail2 == null) {
            return false;
        }
        return "设备等级".equals(currentDetail2.getParamname()) || "额定电压".equals(currentDetail2.getParamname()) || "额定功率".equals(currentDetail2.getParamname());
    }

    //获取对应的厂商List
    public void getDetail() {
        detailList = assetManufacturerBean.findByPId(currentEntity.getFormid());
        currentDetail = detailList.get(0);

    }
  //处理上传图片数据
    public void handleFileUploadWhenDetailNew(FileUploadEvent event) {
        super.handleFileUploadWhenNew(event);
        if (this.fileName != null) {
            this.createDetail();
            int seq = detailList3.size() + 1;
            AssetFile assetFile = new AssetFile();
            assetFile.setCompany(userManagedBean.getCompany());
            assetFile.setFilepath("../../resources/app/res/"+imageName);
            assetFile.setStatus("Y");
            assetFile.setSeq(seq);
            assetFile.setPid(currentEntity.getFormid());
            assetFile.setFilename(fileName);
            detailList3.add(assetFile);
            addedDetailList3.add(assetFile);
        }
    }
    //加载文件

    @Override
    protected void upload() throws IOException {
        try {
            final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            request.setCharacterEncoding("UTF-8");
            
            SimpleDateFormat f=new SimpleDateFormat("yyyyMMddHHmmss");
            imageName = f.format(getDate());
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
    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (this.queryFormId != null && !"".equals(this.queryFormId)) {
                this.model.getFilterFields().put("formid", this.queryFormId);
            }
            if (this.queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("assetDesc", this.queryName);
            }
            if (this.queryItemno != null && !"".equals(this.queryItemno)) {
                this.model.getFilterFields().put("assetItem.itemno", this.queryItemno);
            }
            if (this.queryItemdesc != null && !"".equals(this.queryItemdesc)) {
                this.model.getFilterFields().put("assetItem.itemdesc", this.queryItemdesc);
            }
            if (this.queryDeptno != null && !"".equals(this.queryDeptno)) {
                this.model.getFilterFields().put("deptno", this.queryDeptno);
            }
            if (this.queryDeptname != null && !"".equals(this.queryDeptname)) {
                this.model.getFilterFields().put("deptname", this.queryDeptname);
            }
            if (this.queryUserno != null && !"".equals(this.queryUserno)) {
                this.model.getFilterFields().put("userno", this.queryUserno);
            }
            if (this.queryUsername != null && !"".equals(this.queryUsername)) {
                this.model.getFilterFields().put("username", this.queryUsername);
            }
            if (this.queryWarehouseno != null && !"".equals(this.queryWarehouseno)) {
                this.model.getFilterFields().put("warehouse.warehouseno", this.queryWarehouseno);
            }
            this.model.getFilterFields().put("assetItem.category.id =", 3);
            model.getSortFields().put("assetItem.itemno", "ASC");
            model.getSortFields().put("formid", "ASC");
        }
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
     * @return the queryDeptname
     */
    public String getQueryDeptname() {
        return queryDeptname;
    }

    /**
     * @param queryDeptname the queryDeptname to set
     */
    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
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
     * @return the queryUsername
     */
    public String getQueryUsername() {
        return queryUsername;
    }

    /**
     * @param queryUsername the queryUsername to set
     */
    public void setQueryUsername(String queryUsername) {
        this.queryUsername = queryUsername;
    }

    /**
     * @return the queryWarehouseno
     */
    public String getQueryWarehouseno() {
        return queryWarehouseno;
    }

    /**
     * @param queryWarehouseno the queryWarehouseno to set
     */
    public void setQueryWarehouseno(String queryWarehouseno) {
        this.queryWarehouseno = queryWarehouseno;
    }

    /**
     * @return the queryItemno
     */
    public String getQueryItemno() {
        return queryItemno;
    }

    /**
     * @param queryItemno the queryItemno to set
     */
    public void setQueryItemno(String queryItemno) {
        this.queryItemno = queryItemno;
    }

    /**
     * @return the queryItemdesc
     */
    public String getQueryItemdesc() {
        return queryItemdesc;
    }

    /**
     * @param queryItemdesc the queryItemdesc to set
     */
    public void setQueryItemdesc(String queryItemdesc) {
        this.queryItemdesc = queryItemdesc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
