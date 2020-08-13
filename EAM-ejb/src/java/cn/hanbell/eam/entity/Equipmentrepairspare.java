/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
import com.lightshell.comm.SuperDetailEntity;
import java.math.BigDecimal;
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
@Table(name = "equipmentrepairspare")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equipmentrepairspare.findAll", query = "SELECT e FROM Equipmentrepairspare e")
    , @NamedQuery(name = "Equipmentrepairspare.findById", query = "SELECT e FROM Equipmentrepairspare e WHERE e.id = :id")
    , @NamedQuery(name = "Equipmentrepairspare.findByCompany", query = "SELECT e FROM Equipmentrepairspare e WHERE e.company = :company")
    , @NamedQuery(name = "Equipmentrepairspare.findByPid", query = "SELECT e FROM Equipmentrepairspare e WHERE e.pid = :pid")
    , @NamedQuery(name = "Equipmentrepairspare.findBySeq", query = "SELECT e FROM Equipmentrepairspare e WHERE e.seq = :seq")
    , @NamedQuery(name = "Equipmentrepairspare.findBySpareno", query = "SELECT e FROM Equipmentrepairspare e WHERE e.spareno = :spareno")
    , @NamedQuery(name = "Equipmentrepairspare.findBySparenum", query = "SELECT e FROM Equipmentrepairspare e WHERE e.sparenum = :sparenum")
    , @NamedQuery(name = "Equipmentrepairspare.findByQty", query = "SELECT e FROM Equipmentrepairspare e WHERE e.qty = :qty")
    , @NamedQuery(name = "Equipmentrepairspare.findByUprice", query = "SELECT e FROM Equipmentrepairspare e WHERE e.uprice = :uprice")
    , @NamedQuery(name = "Equipmentrepairspare.findByUserno", query = "SELECT e FROM Equipmentrepairspare e WHERE e.userno = :userno")
    , @NamedQuery(name = "Equipmentrepairspare.findByUserdate", query = "SELECT e FROM Equipmentrepairspare e WHERE e.userdate = :userdate")
    , @NamedQuery(name = "Equipmentrepairspare.findByRemark", query = "SELECT e FROM Equipmentrepairspare e WHERE e.remark = :remark")
    , @NamedQuery(name = "Equipmentrepairspare.findByStatus", query = "SELECT e FROM Equipmentrepairspare e WHERE e.status = :status")
    , @NamedQuery(name = "Equipmentrepairspare.findByCreator", query = "SELECT e FROM Equipmentrepairspare e WHERE e.creator = :creator")
    , @NamedQuery(name = "Equipmentrepairspare.findByCredate", query = "SELECT e FROM Equipmentrepairspare e WHERE e.credate = :credate")
    , @NamedQuery(name = "Equipmentrepairspare.findByOptuser", query = "SELECT e FROM Equipmentrepairspare e WHERE e.optuser = :optuser")
    , @NamedQuery(name = "Equipmentrepairspare.findByOptdate", query = "SELECT e FROM Equipmentrepairspare e WHERE e.optdate = :optdate")
    , @NamedQuery(name = "Equipmentrepairspare.findByCfmuser", query = "SELECT e FROM Equipmentrepairspare e WHERE e.cfmuser = :cfmuser")
    , @NamedQuery(name = "Equipmentrepairspare.findByCfmdate", query = "SELECT e FROM Equipmentrepairspare e WHERE e.cfmdate = :cfmdate")
    ,@NamedQuery(name = "Equipmentrepairspare.findByPId", query = "SELECT e FROM Equipmentrepairspare e WHERE e.pid = :pid ORDER BY e.seq")})
public class Equipmentrepairspare extends FormDetailEntity {

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
    @Column(name = "qty")
    private Integer qty;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "uprice")
    private BigDecimal uprice;
    @Size(max = 20)
    @Column(name = "userno")
    private String userno;
    @Column(name = "userdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date userdate;
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

    public Equipmentrepairspare() {
    }

    public Equipmentrepairspare(Integer id) {
        this.id = id;
    }

    public Equipmentrepairspare(Integer id, String company,  String sparenum, String status) {
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

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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

    public Date getUserdate() {
        return userdate;
    }

    public void setUserdate(Date userdate) {
        this.userdate = userdate;
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
        if (!(object instanceof Equipmentrepairspare)) {
            return false;
        }
        Equipmentrepairspare other = (Equipmentrepairspare) object;
          if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Equipmentrepairspare[ id=" + id + " ]";
    }

}
