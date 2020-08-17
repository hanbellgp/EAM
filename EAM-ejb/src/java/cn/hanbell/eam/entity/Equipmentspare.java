/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentspare")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equipmentspare.findAll", query = "SELECT e FROM Equipmentspare e")
    , @NamedQuery(name = "Equipmentspare.findById", query = "SELECT e FROM Equipmentspare e WHERE e.id = :id")
    , @NamedQuery(name = "Equipmentspare.findByCompany", query = "SELECT e FROM Equipmentspare e WHERE e.company = :company")
    , @NamedQuery(name = "Equipmentspare.findBySpareno", query = "SELECT e FROM Equipmentspare e WHERE e.spareno = :spareno")
    , @NamedQuery(name = "Equipmentspare.findBySparenum", query = "SELECT e FROM Equipmentspare e WHERE e.sparenum = :sparenum")
    , @NamedQuery(name = "Equipmentspare.findBySparedesc", query = "SELECT e FROM Equipmentspare e WHERE e.sparedesc = :sparedesc")
    , @NamedQuery(name = "Equipmentspare.findBySparemodel", query = "SELECT e FROM Equipmentspare e WHERE e.sparemodel = :sparemodel")
    , @NamedQuery(name = "Equipmentspare.findByLeadtime", query = "SELECT e FROM Equipmentspare e WHERE e.leadtime = :leadtime")
    , @NamedQuery(name = "Equipmentspare.findByUprice", query = "SELECT e FROM Equipmentspare e WHERE e.uprice = :uprice")
    , @NamedQuery(name = "Equipmentspare.findByUserno", query = "SELECT e FROM Equipmentspare e WHERE e.userno = :userno")
    , @NamedQuery(name = "Equipmentspare.findByUserdate", query = "SELECT e FROM Equipmentspare e WHERE e.userdate = :userdate")
    , @NamedQuery(name = "Equipmentspare.findByMinstock", query = "SELECT e FROM Equipmentspare e WHERE e.minstock = :minstock")
    , @NamedQuery(name = "Equipmentspare.findByMaxstock", query = "SELECT e FROM Equipmentspare e WHERE e.maxstock = :maxstock")
    , @NamedQuery(name = "Equipmentspare.findByRemark", query = "SELECT e FROM Equipmentspare e WHERE e.remark = :remark")
    , @NamedQuery(name = "Equipmentspare.findByStatus", query = "SELECT e FROM Equipmentspare e WHERE e.status = :status")
    , @NamedQuery(name = "Equipmentspare.findByCreator", query = "SELECT e FROM Equipmentspare e WHERE e.creator = :creator")
    , @NamedQuery(name = "Equipmentspare.findByCredate", query = "SELECT e FROM Equipmentspare e WHERE e.credate = :credate")
    , @NamedQuery(name = "Equipmentspare.findByOptuser", query = "SELECT e FROM Equipmentspare e WHERE e.optuser = :optuser")
    , @NamedQuery(name = "Equipmentspare.findByOptdate", query = "SELECT e FROM Equipmentspare e WHERE e.optdate = :optdate")
    , @NamedQuery(name = "Equipmentspare.findByCfmuser", query = "SELECT e FROM Equipmentspare e WHERE e.cfmuser = :cfmuser")
    , @NamedQuery(name = "Equipmentspare.findByCfmdate", query = "SELECT e FROM Equipmentspare e WHERE e.cfmdate = :cfmdate")})
public class Equipmentspare extends SuperEntity {


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
    @Column(name = "sparedesc")
    private String sparedesc;
    @Size(max = 45)
    @Column(name = "sparemodel")
    private String sparemodel;
    @Column(name = "leadtime")
    private Integer leadtime;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "uprice")
    private BigDecimal uprice;
    @Size(max = 20)
    @Column(name = "userno")
    private String userno;
    @Size(max = 50)
    @Column(name = "userdate")
    private String userdate;
    @Column(name = "minstock")
    private Integer minstock;
    @Column(name = "maxstock")
    private Integer maxstock;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
  

    public Equipmentspare() {
    }

    public Equipmentspare(Integer id) {
        this.id = id;
    }

    public Equipmentspare(Integer id, String company, String sparenum, String status) {
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

    public BigDecimal getUprice() {
        return uprice;
    }

    public void setUprice(BigDecimal uprice) {
        this.uprice = uprice;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getUserdate() {
        return userdate;
    }

    public void setUserdate(String userdate) {
        this.userdate = userdate;
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
        if (!(object instanceof Equipmentspare)) {
            return false;
        }
        Equipmentspare other = (Equipmentspare) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Equipmentspare[ id=" + id + " ]";
    }
    
}
