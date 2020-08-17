/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormEntity;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "equipmentrepair")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentRepair.findAll", query = "SELECT e FROM EquipmentRepair e"),
    @NamedQuery(name = "EquipmentRepair.findById", query = "SELECT e FROM EquipmentRepair e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentRepair.findByCompany", query = "SELECT e FROM EquipmentRepair e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentRepair.findByFormid", query = "SELECT e FROM EquipmentRepair e WHERE e.formid = :formid"),
    @NamedQuery(name = "EquipmentRepair.findByItemno", query = "SELECT e FROM EquipmentRepair e WHERE e.itemno = :itemno"),
    @NamedQuery(name = "EquipmentRepair.findByAssetno", query = "SELECT e FROM EquipmentRepair e WHERE e.assetno = :assetno"),
    @NamedQuery(name = "EquipmentRepair.findByFormdate", query = "SELECT e FROM EquipmentRepair e WHERE e.formdate = :formdate"),
    @NamedQuery(name = "EquipmentRepair.findByRepairuser", query = "SELECT e FROM EquipmentRepair e WHERE e.repairuser = :repairuser"),
    @NamedQuery(name = "EquipmentRepair.findByRepairusername", query = "SELECT e FROM EquipmentRepair e WHERE e.repairusername = :repairusername"),
    @NamedQuery(name = "EquipmentRepair.findByTroublefrom", query = "SELECT e FROM EquipmentRepair e WHERE e.troublefrom = :troublefrom"),
    @NamedQuery(name = "EquipmentRepair.findByServiceuser", query = "SELECT e FROM EquipmentRepair e WHERE e.serviceuser = :serviceuser"),
    @NamedQuery(name = "EquipmentRepair.findByServiceusername", query = "SELECT e FROM EquipmentRepair e WHERE e.serviceusername = :serviceusername"),
    @NamedQuery(name = "EquipmentRepair.findByServicearrivetime", query = "SELECT e FROM EquipmentRepair e WHERE e.servicearrivetime = :servicearrivetime"),
    @NamedQuery(name = "EquipmentRepair.findByRstatus", query = "SELECT e FROM EquipmentRepair e WHERE e.rstatus = :rstatus"),
    @NamedQuery(name = "EquipmentRepair.findByHitchdesc", query = "SELECT e FROM EquipmentRepair e WHERE e.hitchdesc = :hitchdesc"),
    @NamedQuery(name = "EquipmentRepair.findByHitchtype", query = "SELECT e FROM EquipmentRepair e WHERE e.hitchtype = :hitchtype"),
    @NamedQuery(name = "EquipmentRepair.findByRepairmethod", query = "SELECT e FROM EquipmentRepair e WHERE e.repairmethod = :repairmethod"),
    @NamedQuery(name = "EquipmentRepair.findByExcepttime", query = "SELECT e FROM EquipmentRepair e WHERE e.excepttime = :excepttime"),
    @NamedQuery(name = "EquipmentRepair.findByCompletetime", query = "SELECT e FROM EquipmentRepair e WHERE e.completetime = :completetime"),
    @NamedQuery(name = "EquipmentRepair.findByAbrasehitch", query = "SELECT e FROM EquipmentRepair e WHERE e.abrasehitch = :abrasehitch"),
    @NamedQuery(name = "EquipmentRepair.findByHitchsort1", query = "SELECT e FROM EquipmentRepair e WHERE e.hitchsort1 = :hitchsort1"),
    @NamedQuery(name = "EquipmentRepair.findByHitchsort2", query = "SELECT e FROM EquipmentRepair e WHERE e.hitchsort2 = :hitchsort2"),
    @NamedQuery(name = "EquipmentRepair.findByHitchreason", query = "SELECT e FROM EquipmentRepair e WHERE e.hitchreason = :hitchreason"),
    @NamedQuery(name = "EquipmentRepair.findByRepairprocess", query = "SELECT e FROM EquipmentRepair e WHERE e.repairprocess = :repairprocess"),
    @NamedQuery(name = "EquipmentRepair.findByMeasure", query = "SELECT e FROM EquipmentRepair e WHERE e.measure = :measure"),
    @NamedQuery(name = "EquipmentRepair.findByRepaircost", query = "SELECT e FROM EquipmentRepair e WHERE e.repaircost = :repaircost"),
    @NamedQuery(name = "EquipmentRepair.findByRemark", query = "SELECT e FROM EquipmentRepair e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentRepair.findByStatus", query = "SELECT e FROM EquipmentRepair e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentRepair.findByCreator", query = "SELECT e FROM EquipmentRepair e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentRepair.findByCredate", query = "SELECT e FROM EquipmentRepair e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentRepair.findByOptuser", query = "SELECT e FROM EquipmentRepair e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentRepair.findByOptdate", query = "SELECT e FROM EquipmentRepair e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentRepair.findByCfmuser", query = "SELECT e FROM EquipmentRepair e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentRepair.findByCfmdate", query = "SELECT e FROM EquipmentRepair e WHERE e.cfmdate = :cfmdate")})
public class EquipmentRepair extends FormEntity {


    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @JoinColumn(name = "itemno", referencedColumnName = "itemno")
    @ManyToOne
    private AssetCard itemno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "assetno")
    private String assetno;
    @Column(name = "formdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date formdate;
    @Size(max = 45)
    @Column(name = "repairuser")
    private String repairuser;
    @Size(max = 45)
    @Column(name = "repairusername")
    private String repairusername;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "troublefrom")
    private String troublefrom;
    @Size(max = 20)
    @Column(name = "serviceuser")
    private String serviceuser;
    @Size(max = 45)
    @Column(name = "serviceusername")
    private String serviceusername;
    @Column(name = "servicearrivetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date servicearrivetime;
    @Size(max = 2)
    @Column(name = "rstatus")
    private String rstatus;
    @Size(max = 2000)
    @Column(name = "hitchdesc")
    private String hitchdesc;
    @Size(max = 20)
    @Column(name = "hitchtype")
    private String hitchtype;
    @Size(max = 200)
    @Column(name = "repairmethod")
    private String repairmethod;
    @Column(name = "excepttime")
    private Integer excepttime;
    @Column(name = "completetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completetime;
    @Size(max = 20)
    @Column(name = "abrasehitch")
    private String abrasehitch;
    @Size(max = 20)
    @Column(name = "hitchsort1")
    private String hitchsort1;
    @Size(max = 20)
    @Column(name = "hitchsort2")
    private String hitchsort2;
    @Size(max = 200)
    @Column(name = "hitchreason")
    private String hitchreason;
    @Size(max = 200)
    @Column(name = "repairprocess")
    private String repairprocess;
    @Size(max = 2000)
    @Column(name = "measure")
    private String measure;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "repaircost")
    private BigDecimal repaircost;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

   @Transient
    private String maintenanceTime;
    @Transient
    private String contactTime;
    @Transient
    private String downtime;
    public EquipmentRepair() {
    }

    public EquipmentRepair(Integer id) {
        this.id = id;
    }

    public EquipmentRepair(Integer id, String company, String formid, String assetno, String troublefrom, String status) {
        this.id = id;
        this.company = company;
        this.formid = formid;
        this.assetno = assetno;
        this.troublefrom = troublefrom;
        this.status = status;
    }



    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }



    public AssetCard getItemno() {
        return itemno;
    }

    public void setItemno(AssetCard itemno) {
        this.itemno = itemno;
    }





    public String getAssetno() {
        return assetno;
    }

    public void setAssetno(String assetno) {
        this.assetno = assetno;
    }



    public String getRepairuser() {
        return repairuser;
    }

    public void setRepairuser(String repairuser) {
        this.repairuser = repairuser;
    }

    public String getRepairusername() {
        return repairusername;
    }

    public void setRepairusername(String repairusername) {
        this.repairusername = repairusername;
    }

    public String getTroublefrom() {
        return troublefrom;
    }

    public void setTroublefrom(String troublefrom) {
        this.troublefrom = troublefrom;
    }

    public String getServiceuser() {
        return serviceuser;
    }

    public void setServiceuser(String serviceuser) {
        this.serviceuser = serviceuser;
    }

    public String getServiceusername() {
        return serviceusername;
    }

    public void setServiceusername(String serviceusername) {
        this.serviceusername = serviceusername;
    }

    public Date getServicearrivetime() {
        return servicearrivetime;
    }

    public void setServicearrivetime(Date servicearrivetime) {
        this.servicearrivetime = servicearrivetime;
    }

    public String getRstatus() {
        return rstatus;
    }

    public void setRstatus(String rstatus) {
        this.rstatus = rstatus;
    }

    public String getHitchdesc() {
        return hitchdesc;
    }

    public void setHitchdesc(String hitchdesc) {
        this.hitchdesc = hitchdesc;
    }

    public String getHitchtype() {
        return hitchtype;
    }

    public void setHitchtype(String hitchtype) {
        this.hitchtype = hitchtype;
    }

    public String getRepairmethod() {
        return repairmethod;
    }

    public void setRepairmethod(String repairmethod) {
        this.repairmethod = repairmethod;
    }

    public Integer getExcepttime() {
        return excepttime;
    }

    public void setExcepttime(Integer excepttime) {
        this.excepttime = excepttime;
    }

    public Date getCompletetime() {
        return completetime;
    }

    public void setCompletetime(Date completetime) {
        this.completetime = completetime;
    }

    public String getAbrasehitch() {
        return abrasehitch;
    }

    public void setAbrasehitch(String abrasehitch) {
        this.abrasehitch = abrasehitch;
    }

    public String getHitchsort1() {
        return hitchsort1;
    }

    public void setHitchsort1(String hitchsort1) {
        this.hitchsort1 = hitchsort1;
    }

    public String getHitchsort2() {
        return hitchsort2;
    }

    public void setHitchsort2(String hitchsort2) {
        this.hitchsort2 = hitchsort2;
    }

    public String getHitchreason() {
        return hitchreason;
    }

    public void setHitchreason(String hitchreason) {
        this.hitchreason = hitchreason;
    }

    public String getRepairprocess() {
        return repairprocess;
    }

    public void setRepairprocess(String repairprocess) {
        this.repairprocess = repairprocess;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public BigDecimal getRepaircost() {
        return repaircost;
    }

    public void setRepaircost(BigDecimal repaircost) {
        this.repaircost = repaircost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }



    public String getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceTime(String maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    public String getContactTime() {
        return contactTime;
    }

    public void setContactTime(String contactTime) {
        this.contactTime = contactTime;
    }

    public String getDowntime() {
        return downtime;
    }

    public void setDowntime(String downtime) {
        this.downtime = downtime;
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
        if (!(object instanceof EquipmentRepair)) {
            return false;
        }
        EquipmentRepair other = (EquipmentRepair) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentRepair[ id=" + id + " ]";
    }

}
