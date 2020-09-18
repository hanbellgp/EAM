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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentrepairhelpers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentRepairHelpers.findAll", query = "SELECT e FROM EquipmentRepairHelpers e"),
    @NamedQuery(name = "EquipmentRepairHelpers.findById", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCompany", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByPid", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.pid = :pid"),
    @NamedQuery(name = "EquipmentRepairHelpers.findBySeq", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.seq = :seq"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByRtype", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.rtype = :rtype"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCurnode", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.curnode = :curnode"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCurnode2", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.curnode2 = :curnode2"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByUserno", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.userno = :userno"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByRemark", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByStatus", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCreator", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCredate", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByOptuser", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByOptdate", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCfmuser", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.cfmuser = :cfmuser"),
     @NamedQuery(name = "EquipmentRepairHelpers.findByPId", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.pid = :pid ORDER BY e.seq"),
    @NamedQuery(name = "EquipmentRepairHelpers.findByCfmdate", query = "SELECT e FROM EquipmentRepairHelpers e WHERE e.cfmdate = :cfmdate")})
public class EquipmentRepairHelpers extends FormDetailEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
 
    @Size(max = 2)
    @Column(name = "rtype")
    private String rtype;
    @Size(max = 30)
    @Column(name = "curnode")
    private String curnode;
    @Size(max = 30)
    @Column(name = "curnode2")
    private String curnode2;
    @Size(max = 45)
    @Column(name = "userno")
    private String userno;
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

    public EquipmentRepairHelpers() {
    }

    public EquipmentRepairHelpers(Integer id) {
        this.id = id;
    }

    public EquipmentRepairHelpers(Integer id, String company, String pid, String status) {
        this.id = id;
        this.company = company;
        this.pid = pid;
        this.status = status;
    }

  

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }



    public String getRtype() {
        return rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public String getCurnode() {
        return curnode;
    }

    public void setCurnode(String curnode) {
        this.curnode = curnode;
    }

    public String getCurnode2() {
        return curnode2;
    }

    public void setCurnode2(String curnode2) {
        this.curnode2 = curnode2;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
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
        if (!(object instanceof EquipmentRepairHelpers)) {
            return false;
        }
        EquipmentRepairHelpers other = (EquipmentRepairHelpers) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentRepairHelpers[ id=" + id + " ]";
    }
    
}
