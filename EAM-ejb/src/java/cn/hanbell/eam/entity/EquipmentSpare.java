/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author C2079
 */
@Entity
@Table(name = "equipmentspare")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentSpare.findAll", query = "SELECT e FROM EquipmentSpare e"),
    @NamedQuery(name = "EquipmentSpare.findById", query = "SELECT e FROM EquipmentSpare e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentSpare.findByCompany", query = "SELECT e FROM EquipmentSpare e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentSpare.findBySpareno", query = "SELECT e FROM EquipmentSpare e WHERE e.spareno = :spareno"),
    @NamedQuery(name = "EquipmentSpare.findBySparenum", query = "SELECT e FROM EquipmentSpare e WHERE e.sparenum LIKE :sparenum and e.company = :company"),
    @NamedQuery(name = "EquipmentSpare.findBySparedesc", query = "SELECT e FROM EquipmentSpare e WHERE e.sparedesc = :sparedesc"),
    @NamedQuery(name = "EquipmentSpare.findBySparemodel", query = "SELECT e FROM EquipmentSpare e WHERE e.sparemodel = :sparemodel"),
    @NamedQuery(name = "EquipmentSpare.findByLeadtime", query = "SELECT e FROM EquipmentSpare e WHERE e.leadtime = :leadtime"),
    @NamedQuery(name = "EquipmentSpare.findByUnit", query = "SELECT e FROM EquipmentSpare e WHERE e.unit = :unit"),
    @NamedQuery(name = "EquipmentSpare.findByBrand", query = "SELECT e FROM EquipmentSpare e WHERE e.brand = :brand"),
    @NamedQuery(name = "EquipmentSpare.findBySavemethod", query = "SELECT e FROM EquipmentSpare e WHERE e.savemethod = :savemethod"),
    @NamedQuery(name = "EquipmentSpare.findByQualitycycle", query = "SELECT e FROM EquipmentSpare e WHERE e.qualitycycle = :qualitycycle"),
    @NamedQuery(name = "EquipmentSpare.findByServiceusername", query = "SELECT e FROM EquipmentSpare e WHERE e.serviceusername = :serviceusername"),
    @NamedQuery(name = "EquipmentSpare.findByMinstock", query = "SELECT e FROM EquipmentSpare e WHERE e.minstock = :minstock"),
    @NamedQuery(name = "EquipmentSpare.findByMaxstock", query = "SELECT e FROM EquipmentSpare e WHERE e.maxstock = :maxstock"),
    @NamedQuery(name = "EquipmentSpare.findByRemark", query = "SELECT e FROM EquipmentSpare e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentSpare.findByStatus", query = "SELECT e FROM EquipmentSpare e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentSpare.findByCreator", query = "SELECT e FROM EquipmentSpare e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentSpare.findByCredate", query = "SELECT e FROM EquipmentSpare e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentSpare.findByOptuser", query = "SELECT e FROM EquipmentSpare e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentSpare.findByOptdate", query = "SELECT e FROM EquipmentSpare e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentSpare.findByCfmuser", query = "SELECT e FROM EquipmentSpare e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentSpare.findByAllBasicInfo", query = "SELECT e FROM EquipmentSpare e WHERE e.company = :company AND e.spareno LIKE :spareno OR e.sparenum LIKE :sparenum OR e.sparedesc LIKE :sparedesc "),
    @NamedQuery(name = "EquipmentSpare.findByCfmdate", query = "SELECT e FROM EquipmentSpare e WHERE e.cfmdate = :cfmdate")})
public class EquipmentSpare extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 45)
    @Column(name = "spareno")
    private String spareno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "sparenum")
    private String sparenum;
    @Size(max = 45)
    @Column(name = "sname")
    private String sname;
      @Size(max = 4)
    @Column(name = "scategory")
    private String scategory;
        @Size(max = 45)
    @Column(name = "mname")
    private String mname;
    @Size(max = 4)
    @Column(name = "mcategory")
    private String mcategory;
    @Size(max = 50)
    @Column(name = "brand")
    private String brand;

    @Size(max = 30)
    @Column(name = "suse")
    private String suse;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
    @Size(max = 45)
    @Column(name = "sparedesc")
    private String sparedesc;
    @Size(max = 45)
    @Column(name = "sparemodel")
    private String sparemodel;
   @Size(max = 50)
    @Column(name = "savemethod")
    private String savemethod;
    @Column(name = "qualitycycle")
    private Integer qualitycycle;
    @Size(max = 50)
    @Column(name = "serviceusername")
    private String serviceusername;
    @Column(name = "leadtime")
    private Integer leadtime;
    @JoinColumn(name = "unit", referencedColumnName = "id")
    @ManyToOne
    private Unit unit;
    @Column(name = "minstock")
    private Integer minstock;
    @Column(name = "maxstock")
    private Integer maxstock;
     @Column(name = "servicelife")
    @Temporal(TemporalType.TIMESTAMP)
    private Date servicelife;
    public EquipmentSpare() {
    }

    public EquipmentSpare(Integer id) {
        this.id = id;
    }

    public EquipmentSpare(Integer id, String company, String sparenum, String status) {
        this.id = id;
        this.company = company;
        this.sparenum = sparenum;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSpareno() {
        return spareno;
    }

    public void setSpareno(String spareno) {
        this.spareno = spareno;
    }

    public String getSparenum() {
        return sparenum;
    }

    public void setSparenum(String sparenum) {
        this.sparenum = sparenum;
    }

    public String getSparedesc() {
        return sparedesc;
    }

    public void setSparedesc(String sparedesc) {
        this.sparedesc = sparedesc;
    }

    public String getSparemodel() {
        return sparemodel;
    }

    public void setSparemodel(String sparemodel) {
        this.sparemodel = sparemodel;
    }

    public Integer getLeadtime() {
        return leadtime;
    }

    public void setLeadtime(Integer leadtime) {
        this.leadtime = leadtime;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSuse() {
        return suse;
    }

    public void setSuse(String suse) {
        this.suse = suse;
    }

    public Integer getMinstock() {
        return minstock;
    }

    public void setMinstock(Integer minstock) {
        this.minstock = minstock;
    }

    public Integer getMaxstock() {
        return maxstock;
    }

    public void setMaxstock(Integer maxstock) {
        this.maxstock = maxstock;
    }

    public String getSavemethod() {
        return savemethod;
    }

    public void setSavemethod(String savemethod) {
        this.savemethod = savemethod;
    }

    public Integer getQualitycycle() {
        return qualitycycle;
    }

    public void setQualitycycle(Integer qualitycycle) {
        this.qualitycycle = qualitycycle;
    }

    public String getServiceusername() {
        return serviceusername;
    }

    public void setServiceusername(String serviceusername) {
        this.serviceusername = serviceusername;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getScategory() {
        return scategory;
    }

    public void setScategory(String scategory) {
        this.scategory = scategory;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMcategory() {
        return mcategory;
    }

    public void setMcategory(String mcategory) {
        this.mcategory = mcategory;
    }

    public Date getServicelife() {
        return servicelife;
    }

    public void setServicelife(Date servicelife) {
        this.servicelife = servicelife;
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
        if (!(object instanceof EquipmentSpare)) {
            return false;
        }
        EquipmentSpare other = (EquipmentSpare) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentSpare[ id=" + id + " ]";
    }

}
