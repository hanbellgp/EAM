/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormEntity;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentanalyresult")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentAnalyResult.findAll", query = "SELECT e FROM EquipmentAnalyResult e"),
    @NamedQuery(name = "EquipmentAnalyResult.findById", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentAnalyResult.findByFormid", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.formid = :formid"),
    @NamedQuery(name = "EquipmentAnalyResult.findByFormdate", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.formdate = :formdate"),
    @NamedQuery(name = "EquipmentAnalyResult.findByCompany", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentAnalyResult.findByAssetno", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.assetno = :assetno"),
    @NamedQuery(name = "EquipmentAnalyResult.findByAssetdesc", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.assetdesc = :assetdesc"),
    @NamedQuery(name = "EquipmentAnalyResult.findBySpareno", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.spareno = :spareno"),
    @NamedQuery(name = "EquipmentAnalyResult.findByDeptno", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.deptno = :deptno"),
    @NamedQuery(name = "EquipmentAnalyResult.findByDeptname", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.deptname = :deptname"),
    @NamedQuery(name = "EquipmentAnalyResult.findByStandardlevel", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.standardlevel = :standardlevel"),
    @NamedQuery(name = "EquipmentAnalyResult.findByStartdate", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.startdate = :startdate"),
    @NamedQuery(name = "EquipmentAnalyResult.findByEnddate", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.enddate = :enddate"),
    @NamedQuery(name = "EquipmentAnalyResult.findByAnalysisresult", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.analysisresult = :analysisresult"),
    @NamedQuery(name = "EquipmentAnalyResult.findByRemark", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentAnalyResult.findByStatus", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentAnalyResult.findByCreator", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentAnalyResult.findByCredate", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentAnalyResult.findByOptuser", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentAnalyResult.findByOptdate", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentAnalyResult.findByCfmuser", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentAnalyResult.findByCfmdate", query = "SELECT e FROM EquipmentAnalyResult e WHERE e.cfmdate = :cfmdate")})
public class EquipmentAnalyResult extends FormEntity {

   
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "formid")
    private String formid;
    @Column(name = "formdate")
    @Temporal(TemporalType.DATE)
    private Date formdate;
    @Size(max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 45)
    @Column(name = "assetno")
    private String assetno;
    @Size(max = 45)
    @Column(name = "assetdesc")
    private String assetdesc;
    @Size(max = 45)
    @Column(name = "spareno")
    private String spareno;
    @Size(max = 45)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 10)
    @Column(name = "standardlevel")
    private String standardlevel;
    @Column(name = "startdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startdate;
    @Column(name = "enddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enddate;
    @Size(max = 10)
    @Column(name = "analysisresult")
    private String analysisresult;
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

    public EquipmentAnalyResult() {
    }

    public EquipmentAnalyResult(Integer id) {
        this.id = id;
    }

    public EquipmentAnalyResult(Integer id, String formid, String status) {
        this.id = id;
        this.formid = formid;
        this.status = status;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAssetno() {
        return assetno;
    }

    public void setAssetno(String assetno) {
        this.assetno = assetno;
    }

    public String getAssetdesc() {
        return assetdesc;
    }

    public void setAssetdesc(String assetdesc) {
        this.assetdesc = assetdesc;
    }

    public String getSpareno() {
        return spareno;
    }

    public void setSpareno(String spareno) {
        this.spareno = spareno;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getStandardlevel() {
        return standardlevel;
    }

    public void setStandardlevel(String standardlevel) {
        this.standardlevel = standardlevel;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getAnalysisresult() {
        return analysisresult;
    }

    public void setAnalysisresult(String analysisresult) {
        this.analysisresult = analysisresult;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        if (!(object instanceof EquipmentAnalyResult)) {
            return false;
        }
        EquipmentAnalyResult other = (EquipmentAnalyResult) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentAnalyResult[ id=" + id + " ]";
    }
    
}
