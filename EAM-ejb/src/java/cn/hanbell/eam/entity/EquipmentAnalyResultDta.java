/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentanalyresultdta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentAnalyResultDta.findAll", query = "SELECT e FROM EquipmentAnalyResultDta e"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findById", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCompany", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByPid", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.pid = :pid"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findBySeq", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.seq = :seq"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByStandardtype", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.standardtype = :standardtype"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByRespondept", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.respondept = :respondept"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCheckarea", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.checkarea = :checkarea"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCheckcontent", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.checkcontent = :checkcontent"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByJudgestandard", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.judgestandard = :judgestandard"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByMethod", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.method = :method"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByMethodname", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.methodname = :methodname"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByDowntime", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.downtime = :downtime"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByManpower", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.manpower = :manpower"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByManhour", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.manhour = :manhour"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByAreaimage", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.areaimage = :areaimage"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByResulttype", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.resulttype = :resulttype"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByAnalysisresult", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.analysisresult = :analysisresult"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByLastanalysisuser", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.lastanalysisuser = :lastanalysisuser"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByAnalysisuser", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.analysisuser = :analysisuser"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByPlandateo", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.plandateo = :plandateo"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByPlandate", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.plandate = :plandate"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findBySdate", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.sdate = :sdate"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByEdate", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.edate = :edate"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByException", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.exception = :exception"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByProblemsolve", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.problemsolve = :problemsolve"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByAdjustreason", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.adjustreason = :adjustreason"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByRemark", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByStatus", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCreator", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCredate", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByOptuser", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByOptdate", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCfmuser", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByPId", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.pid = :pid ORDER BY e.seq"),
    @NamedQuery(name = "EquipmentAnalyResultDta.findByCfmdate", query = "SELECT e FROM EquipmentAnalyResultDta e WHERE e.cfmdate = :cfmdate")})
public class EquipmentAnalyResultDta extends FormDetailEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 10)
    @Column(name = "standardtype")
    private String standardtype;
    @Size(max = 10)
    @Column(name = "respondept")
    private String respondept;
    @Size(max = 100)
    @Column(name = "checkarea")
    private String checkarea;
    @Size(max = 100)
    @Column(name = "checkcontent")
    private String checkcontent;
    @Size(max = 100)
    @Column(name = "judgestandard")
    private String judgestandard;
    @Size(max = 100)
    @Column(name = "method")
    private String method;
    @Size(max = 100)
    @Column(name = "methodname")
    private String methodname;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "downtime")
    private Float downtime;
    @Size(max = 2)
    @Column(name = "downunit")
    private String downunit;
    @Column(name = "manpower")
    private Float manpower;
    @Column(name = "manhour")
    private Float manhour;
    @Size(max = 100)
    @Column(name = "areaimage")
    private String areaimage;
    @Size(max = 10)
    @Column(name = "resulttype")
    private String resulttype;
    @Size(max = 100)
    @Column(name = "analysisresult")
    private String analysisresult;
    @Size(max = 20)
    @Column(name = "lastanalysisuser")
    private String lastanalysisuser;
    @Size(max = 20)
    @Column(name = "analysisuser")
    private String analysisuser;
    @Column(name = "plandateo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date plandateo;
    @Column(name = "plandate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date plandate;
    @Column(name = "sdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sdate;
    @Column(name = "edate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date edate;
    @Size(max = 100)
    @Column(name = "exception")
    private String exception;
    @Size(max = 100)
    @Column(name = "problemsolve")
    private String problemsolve;
    @Size(max = 100)
    @Column(name = "adjustreason")
    private String adjustreason;
  @Size(max = 45)
    @Column(name = "filename")
    private String filename;
    @Size(max = 100)
    @Column(name = "filepath")
    private String filepath;
 
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "status")
    private String status;
    @Size(max = 20)
    @Column(name = "creator")
    private String creator;
    @Column(name = "credate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date credate;
    @Size(max = 20)
    @Column(name = "optuser")
    private String optuser;
    @Column(name = "optdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date optdate;
    @Size(max = 20)
    @Column(name = "cfmuser")
    private String cfmuser;
    @Column(name = "cfmdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cfmdate;

     @Transient
    private String imageBase;
    public EquipmentAnalyResultDta() {
    }

    public EquipmentAnalyResultDta(Integer id) {
        this.id = id;
    }

    public EquipmentAnalyResultDta(Integer id, String company, String pid, int seq, String status) {
        this.id = id;
        this.company = company;
        this.pid = pid;
        this.seq = seq;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStandardtype() {
        return standardtype;
    }

    public void setStandardtype(String standardtype) {
        this.standardtype = standardtype;
    }

    public String getRespondept() {
        return respondept;
    }

    public void setRespondept(String respondept) {
        this.respondept = respondept;
    }

    public String getCheckarea() {
        return checkarea;
    }

    public void setCheckarea(String checkarea) {
        this.checkarea = checkarea;
    }

    public String getCheckcontent() {
        return checkcontent;
    }

    public void setCheckcontent(String checkcontent) {
        this.checkcontent = checkcontent;
    }

    public String getJudgestandard() {
        return judgestandard;
    }

    public void setJudgestandard(String judgestandard) {
        this.judgestandard = judgestandard;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public String getImageBase() {
        return imageBase;
    }

    public void setImageBase(String imageBase) {
        this.imageBase = imageBase;
    }

    public Float getDowntime() {
        return downtime;
    }

    public void setDowntime(Float downtime) {
        this.downtime = downtime;
    }

    public String getDownunit() {
        return downunit;
    }

    public void setDownunit(String downunit) {
        this.downunit = downunit;
    }

    public Float getManpower() {
        return manpower;
    }

    public void setManpower(Float manpower) {
        this.manpower = manpower;
    }

    public Float getManhour() {
        return manhour;
    }

    public void setManhour(Float manhour) {
        this.manhour = manhour;
    }

    public String getAreaimage() {
        return areaimage;
    }

    public void setAreaimage(String areaimage) {
        this.areaimage = areaimage;
    }

    public String getResulttype() {
        return resulttype;
    }

    public void setResulttype(String resulttype) {
        this.resulttype = resulttype;
    }

    public String getAnalysisresult() {
        return analysisresult;
    }

    public void setAnalysisresult(String analysisresult) {
        this.analysisresult = analysisresult;
    }

    public String getLastanalysisuser() {
        return lastanalysisuser;
    }

    public void setLastanalysisuser(String lastanalysisuser) {
        this.lastanalysisuser = lastanalysisuser;
    }

    public String getAnalysisuser() {
        return analysisuser;
    }

    public void setAnalysisuser(String analysisuser) {
        this.analysisuser = analysisuser;
    }

    public Date getPlandateo() {
        return plandateo;
    }

    public void setPlandateo(Date plandateo) {
        this.plandateo = plandateo;
    }

    public Date getPlandate() {
        return plandate;
    }

    public void setPlandate(Date plandate) {
        this.plandate = plandate;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }

    public Date getEdate() {
        return edate;
    }

    public void setEdate(Date edate) {
        this.edate = edate;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getProblemsolve() {
        return problemsolve;
    }

    public void setProblemsolve(String problemsolve) {
        this.problemsolve = problemsolve;
    }

    public String getAdjustreason() {
        return adjustreason;
    }

    public void setAdjustreason(String adjustreason) {
        this.adjustreason = adjustreason;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCredate() {
        return credate;
    }

    public void setCredate(Date credate) {
        this.credate = credate;
    }

    public String getOptuser() {
        return optuser;
    }

    public void setOptuser(String optuser) {
        this.optuser = optuser;
    }

    public Date getOptdate() {
        return optdate;
    }

    public void setOptdate(Date optdate) {
        this.optdate = optdate;
    }

    public String getCfmuser() {
        return cfmuser;
    }

    public void setCfmuser(String cfmuser) {
        this.cfmuser = cfmuser;
    }

    public Date getCfmdate() {
        return cfmdate;
    }

    public void setCfmdate(Date cfmdate) {
        this.cfmdate = cfmdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentAnalyResultDta)) {
            return false;
        }
        EquipmentAnalyResultDta other = (EquipmentAnalyResultDta) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentAnalyResultDta[ id=" + id + " ]";
    }

}
