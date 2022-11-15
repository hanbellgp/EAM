/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "equipmentanalystage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentAnalyStage.findAll", query = "SELECT e FROM EquipmentAnalyStage e"),
    @NamedQuery(name = "EquipmentAnalyStage.findById", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentAnalyStage.findByCompany", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentAnalyStage.findByFormid", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.formid = :formid"),
    @NamedQuery(name = "EquipmentAnalyStage.findByFormdate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.formdate = :formdate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByStage", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.stage = :stage"),
    @NamedQuery(name = "EquipmentAnalyStage.findByPlandate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.plandate = :plandate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByActdate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.actdate = :actdate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByBackreason", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.backreason = :backreason"),
    @NamedQuery(name = "EquipmentAnalyStage.findByImprove", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.improve = :improve"),
    @NamedQuery(name = "EquipmentAnalyStage.findByAssume", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.assume = :assume"),
    @NamedQuery(name = "EquipmentAnalyStage.findByImproveplandate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.improveplandate = :improveplandate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByImproveactdate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.improveactdate = :improveactdate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByRemark", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentAnalyStage.findByStatus", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentAnalyStage.findByCreator", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentAnalyStage.findByCredate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByOptuser", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentAnalyStage.findByOptdate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentAnalyStage.findByCfmuser", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentAnalyStage.findByCfmdate", query = "SELECT e FROM EquipmentAnalyStage e WHERE e.cfmdate = :cfmdate")})
public class EquipmentAnalyStage extends SuperEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @JoinColumn(name = "formid", referencedColumnName = "formid")
    @ManyToOne(optional = true)
    private AssetCard formid;
    @JoinColumn(name = "equipmentLevelId", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private AssetParameterDta equipmentLevelId;
    @Column(name = "formdate")
    @Temporal(TemporalType.DATE)
    private Date formdate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "stage")
    private String stage;
    @Column(name = "plandate")
    @Temporal(TemporalType.DATE)
    private Date plandate;
    @Column(name = "actdate")
    @Temporal(TemporalType.DATE)
    private Date actdate;
    @Size(max = 200)
    @Column(name = "backreason")
    private String backreason;
    @Size(max = 200)
    @Column(name = "improve")
    private String improve;
    @Size(max = 200)
    @Column(name = "assume")
    private String assume;
    @Column(name = "improveplandate")
    @Temporal(TemporalType.DATE)
    private Date improveplandate;
    @Column(name = "improveactdate")
    @Temporal(TemporalType.DATE)
    private Date improveactdate;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;


    public EquipmentAnalyStage() {
    }

    public EquipmentAnalyStage(Integer id) {
        this.id = id;
    }

    public EquipmentAnalyStage(Integer id, String company, String stage, String status) {
        this.id = id;
        this.company = company;
        this.stage = stage;
        this.status = status;
    }

    

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
 public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public AssetCard getFormid() {
        return formid;
    }

   

    public void setFormid(AssetCard formid) {
        this.formid = formid;
    }

    public Date getFormdate() {
        return formdate;
    }

    public void setFormdate(Date formdate) {
        this.formdate = formdate;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Date getPlandate() {
        return plandate;
    }

    public void setPlandate(Date plandate) {
        this.plandate = plandate;
    }

    public Date getActdate() {
        return actdate;
    }

    public void setActdate(Date actdate) {
        this.actdate = actdate;
    }

    public String getBackreason() {
        return backreason;
    }

    public void setBackreason(String backreason) {
        this.backreason = backreason;
    }

    public String getImprove() {
        return improve;
    }

    public void setImprove(String improve) {
        this.improve = improve;
    }

    public String getAssume() {
        return assume;
    }

    public void setAssume(String assume) {
        this.assume = assume;
    }

    public Date getImproveplandate() {
        return improveplandate;
    }

    public void setImproveplandate(Date improveplandate) {
        this.improveplandate = improveplandate;
    }

    public Date getImproveactdate() {
        return improveactdate;
    }

    public void setImproveactdate(Date improveactdate) {
        this.improveactdate = improveactdate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public AssetParameterDta getEquipmentLevelId() {
        return equipmentLevelId;
    }

    public void setEquipmentLevelId(AssetParameterDta equipmentLevelId) {
        this.equipmentLevelId = equipmentLevelId;
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
        if (!(object instanceof EquipmentAnalyStage)) {
            return false;
        }
        EquipmentAnalyStage other = (EquipmentAnalyStage) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentAnalyStage[ id=" + id + " ]";
    }

}
