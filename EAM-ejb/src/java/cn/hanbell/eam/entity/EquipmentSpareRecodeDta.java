/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentsparerecodedta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentSpareRecodeDta.findAll", query = "SELECT e FROM EquipmentSpareRecodeDta e"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findById", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByPid", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.pid = :pid"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findBySeq", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.seq = :seq"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findBySparenum", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.sparenum = :sparenum"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByCqty", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.cqty = :cqty"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByUprice", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.uprice = :uprice"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByRemark", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByStatus", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByCreator", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByCredate", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByOptuser", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByOptdate", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByCfmuser", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByCfmdate", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.cfmdate = :cfmdate"),
    @NamedQuery(name = "EquipmentSpareRecodeDta.findByPId", query = "SELECT e FROM EquipmentSpareRecodeDta e WHERE e.pid = :pid")})
public class EquipmentSpareRecodeDta extends FormDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "sparenum", referencedColumnName = "sparenum")
    @ManyToOne
    private EquipmentSpare sparenum;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cqty")
    private BigDecimal cqty;
    @Column(name = "uprice")
    private BigDecimal uprice;
    @Size(max = 45)
    @Column(name = "slocation")
    private String slocation;
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

    public EquipmentSpareRecodeDta() {
    }

    public EquipmentSpareRecodeDta(Integer id) {
        this.id = id;
    }

    public EquipmentSpareRecodeDta(Integer id, String pid, int seq, String status) {
        this.id = id;
        this.pid = pid;
        this.seq = seq;
        this.status = status;
    }

    public EquipmentSpare getSparenum() {
        return sparenum;
    }

    public void setSparenum(EquipmentSpare sparenum) {
        this.sparenum = sparenum;
    }

    public BigDecimal getCqty() {
        return cqty;
    }

    public void setCqty(BigDecimal cqty) {
        this.cqty = cqty;
    }

    public BigDecimal getUprice() {
        return uprice;
    }

    public void setUprice(BigDecimal uprice) {
        this.uprice = uprice;
    }

    public String getSlocation() {
        return slocation;
    }

    public void setSlocation(String slocation) {
        this.slocation = slocation;
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
        if (!(object instanceof EquipmentSpareRecodeDta)) {
            return false;
        }
        EquipmentSpareRecodeDta other = (EquipmentSpareRecodeDta) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentSpareRecodeDta[ id=" + id + " ]";
    }

}
