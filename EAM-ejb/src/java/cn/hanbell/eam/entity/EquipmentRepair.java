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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @NamedQuery(name = "EquipmentRepair.findBySparecost", query = "SELECT e FROM EquipmentRepair e WHERE e.sparecost = :sparecost"),
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

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 45)
    @Column(name = "itemno")
    private String itemno;
    @Size(max = 20)
    @Column(name = "repairdeptno")
    private String repairdeptno;
    @Size(max = 45)
    @Column(name = "repairdeptname")
    private String repairdeptname;
    @Size(max = 20)
    @Column(name = "repairuser")
    private String repairuser;
    @Size(max = 45)
    @Column(name = "repairusername")
    private String repairusername;
    @Size(max = 20)
    @Column(name = "hitchurgency")
    private String hitchurgency;
    @Size(max = 45)
    @Column(name = "troublefrom")
    private String troublefrom;
    @Size(max = 2)
    @Column(name = "repairmethodtype")
    private String repairmethodtype;
    @Size(max = 20)
    @Column(name = "serviceuser")
    private String serviceuser;
    @Size(max = 20)
    @Column(name = "serviceusername")
    private String serviceusername;
    @Size(max = 200)
    @Column(name = "hitchdesc")
    private String hitchdesc;
    @Size(max = 20)
    @Column(name = "hitchtype")
    private String hitchtype;
    @Size(max = 200)
    @Column(name = "hitchalarm")
    private String hitchalarm;
    @Size(max = 200)
    @Column(name = "repairmethod")
    private String repairmethod;
    @Size(max = 2)
    @Column(name = "rstatus")
    private String rstatus;
    @Column(name = "serviceassisttime")
    private Integer serviceassisttime;
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
    @Size(max = 20)
    @Column(name = "hitchdutydeptno")
    private String hitchdutydeptno;
    @Size(max = 45)
    @Column(name = "hitchdutydeptname")
    private String hitchdutydeptname;
    @Size(max = 20)
    @Column(name = "hitchdutyuser")
    private String hitchdutyuser;
    @Size(max = 45)
    @Column(name = "hitchdutyusername")
    private String hitchdutyusername;
    @Size(max = 200)
    @Column(name = "repairprocess")
    private String repairprocess;
    @Size(max = 200)
    @Column(name = "measure")
    private String measure;
    @Size(max = 200)
    @Column(name = "hmeasure")
    private String hmeasure;
    @Size(max = 10)
    @Column(name = "laborcost")
    private String laborcost;
    @Column(name = "laborcosts")
    private BigDecimal laborcosts;
    @Size(max = 2)
    @Column(name = "repairarchive")
    private String repairarchive;
    @Size(max = 2)
    @Column(name = "isdup")
    private String isdup;
    @Size(max = 2)
    @Column(name = "isneedspare")
    private String isneedspare;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
    @JoinColumn(name = "assetno", referencedColumnName = "formid")
    @ManyToOne(optional = true)
    private AssetCard assetno;
    @Size(max = 20)
    @Column(name = "repairarea")
    private String repairarea;
    @Column(name = "hitchtime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hitchtime;
    @Column(name = "stopworktime")
    private Integer stopworktime;
    @Column(name = "servicearrivetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date servicearrivetime;
    @Column(name = "excepttime")
    private Integer excepttime;
    @Column(name = "completetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completetime;
    @Column(name = "autotransfertime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date autotransfertime;
    @Column(name = "repairtransfertime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date repairtransfertime;
    @Column(name = "downinitiatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date downinitiatetime;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sparecost")
    private BigDecimal sparecost;
    @Column(name = "repaircost")
    private BigDecimal repaircost;
    @Transient
    private String maintenanceTime;
    @Transient
    private String contactTime;
    @Transient
    private String downtime;

    public EquipmentRepair(Integer id) {
        this.id = id;
    }

    public EquipmentRepair(Integer id, String company, String formid, AssetCard assetno, String troublefrom, String status) {
        this.id = id;
        this.company = company;
        this.formid = formid;
        this.assetno = assetno;
        this.troublefrom = troublefrom;
        this.status = status;
    }

    public EquipmentRepair() {
    }

    public AssetCard getAssetno() {
        return assetno;
    }

    public void setAssetno(AssetCard assetno) {
        this.assetno = assetno;
    }

    public Date getServicearrivetime() {
        return servicearrivetime;
    }

    public void setServicearrivetime(Date servicearrivetime) {
        this.servicearrivetime = servicearrivetime;
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

    public BigDecimal getSparecost() {
        return sparecost;
    }

    public void setSparecost(BigDecimal sparecost) {
        this.sparecost = sparecost;
    }

    public BigDecimal getRepaircost() {
        return repaircost;
    }

    public void setRepaircost(BigDecimal repaircost) {
        this.repaircost = repaircost;
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

    public Date getHitchtime() {
        return hitchtime;
    }

    public void setHitchtime(Date hitchtime) {
        this.hitchtime = hitchtime;
    }

    public Integer getStopworktime() {
        return stopworktime;
    }

    public void setStopworktime(Integer stopworktime) {
        this.stopworktime = stopworktime;
    }

    public Integer getServiceassisttime() {
        return serviceassisttime;
    }

    public void setServiceassisttime(Integer serviceassisttime) {
        this.serviceassisttime = serviceassisttime;
    }

    public BigDecimal getLaborcosts() {
        return laborcosts;
    }

    public void setLaborcosts(BigDecimal laborcosts) {
        this.laborcosts = laborcosts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public String getRepairdeptno() {
        return repairdeptno;
    }

    public void setRepairdeptno(String repairdeptno) {
        this.repairdeptno = repairdeptno;
    }

    public String getRepairdeptname() {
        return repairdeptname;
    }

    public void setRepairdeptname(String repairdeptname) {
        this.repairdeptname = repairdeptname;
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

    public String getHitchurgency() {
        return hitchurgency;
    }

    public void setHitchurgency(String hitchurgency) {
        this.hitchurgency = hitchurgency;
    }

    public String getTroublefrom() {
        return troublefrom;
    }

    public void setTroublefrom(String troublefrom) {
        this.troublefrom = troublefrom;
    }

    public String getRepairmethodtype() {
        return repairmethodtype;
    }

    public void setRepairmethodtype(String repairmethodtype) {
        this.repairmethodtype = repairmethodtype;
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

    public String getHitchdesc() {
        return hitchdesc;
    }

    public void setHitchdesc(String hitchdesc) {
        this.hitchdesc = hitchdesc;
    }

    public String getHitchtype() {
        return hitchtype;
    }

    public Date getAutotransfertime() {
        return autotransfertime;
    }

    public void setAutotransfertime(Date autotransfertime) {
        this.autotransfertime = autotransfertime;
    }

    public Date getRepairtransfertime() {
        return repairtransfertime;
    }

    public void setRepairtransfertime(Date repairtransfertime) {
        this.repairtransfertime = repairtransfertime;
    }

    public Date getDowninitiatetime() {
        return downinitiatetime;
    }

    public void setDowninitiatetime(Date downinitiatetime) {
        this.downinitiatetime = downinitiatetime;
    }

    public void setHitchtype(String hitchtype) {
        this.hitchtype = hitchtype;
    }

    public String getHitchalarm() {
        return hitchalarm;
    }

    public void setHitchalarm(String hitchalarm) {
        this.hitchalarm = hitchalarm;
    }

    public String getRepairmethod() {
        return repairmethod;
    }

    public void setRepairmethod(String repairmethod) {
        this.repairmethod = repairmethod;
    }

    public String getRstatus() {
        return rstatus;
    }

    public void setRstatus(String rstatus) {
        this.rstatus = rstatus;
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

    public String getHitchdutydeptno() {
        return hitchdutydeptno;
    }

    public void setHitchdutydeptno(String hitchdutydeptno) {
        this.hitchdutydeptno = hitchdutydeptno;
    }

    public String getHitchdutydeptname() {
        return hitchdutydeptname;
    }

    public void setHitchdutydeptname(String hitchdutydeptname) {
        this.hitchdutydeptname = hitchdutydeptname;
    }

    public String getHitchdutyuser() {
        return hitchdutyuser;
    }

    public void setHitchdutyuser(String hitchdutyuser) {
        this.hitchdutyuser = hitchdutyuser;
    }

    public String getHitchdutyusername() {
        return hitchdutyusername;
    }

    public void setHitchdutyusername(String hitchdutyusername) {
        this.hitchdutyusername = hitchdutyusername;
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

    public String getHmeasure() {
        return hmeasure;
    }

    public void setHmeasure(String hmeasure) {
        this.hmeasure = hmeasure;
    }

    public String getLaborcost() {
        return laborcost;
    }

    public void setLaborcost(String laborcost) {
        this.laborcost = laborcost;
    }

    public String getRepairarchive() {
        return repairarchive;
    }

    public void setRepairarchive(String repairarchive) {
        this.repairarchive = repairarchive;
    }

    public String getIsdup() {
        return isdup;
    }

    public void setIsdup(String isdup) {
        this.isdup = isdup;
    }

    public String getIsneedspare() {
        return isneedspare;
    }

    public void setIsneedspare(String isneedspare) {
        this.isneedspare = isneedspare;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRepairarea() {
        return repairarea;
    }

    public void setRepairarea(String repairarea) {
        this.repairarea = repairarea;
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
