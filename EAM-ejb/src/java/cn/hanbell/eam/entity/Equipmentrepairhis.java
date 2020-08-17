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
@Table(name = "equipmentrepairhis")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentRepairHis.findAll", query = "SELECT e FROM EquipmentRepairHis e")
    , @NamedQuery(name = "EquipmentRepairHis.findById", query = "SELECT e FROM EquipmentRepairHis e WHERE e.id = :id")
    , @NamedQuery(name = "EquipmentRepairHis.findByCompany", query = "SELECT e FROM EquipmentRepairHis e WHERE e.company = :company")
    , @NamedQuery(name = "EquipmentRepairHis.findByPid", query = "SELECT e FROM EquipmentRepairHis e WHERE e.pid = :pid")
    , @NamedQuery(name = "EquipmentRepairHis.findBySeq", query = "SELECT e FROM EquipmentRepairHis e WHERE e.seq = :seq")
    , @NamedQuery(name = "EquipmentRepairHis.findByUserno", query = "SELECT e FROM EquipmentRepairHis e WHERE e.userno = :userno")
    , @NamedQuery(name = "EquipmentRepairHis.findByContenct", query = "SELECT e FROM EquipmentRepairHis e WHERE e.contenct = :contenct")
    , @NamedQuery(name = "EquipmentRepairHis.findByNote", query = "SELECT e FROM EquipmentRepairHis e WHERE e.note = :note")
    , @NamedQuery(name = "EquipmentRepairHis.findByRemark", query = "SELECT e FROM EquipmentRepairHis e WHERE e.remark = :remark")
    , @NamedQuery(name = "EquipmentRepairHis.findByStatus", query = "SELECT e FROM EquipmentRepairHis e WHERE e.status = :status")
    , @NamedQuery(name = "EquipmentRepairHis.findByPId", query = "SELECT e FROM EquipmentRepairHis e WHERE e.pid = :pid")})

public class EquipmentRepairHis extends FormDetailEntity {



    @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;

    @Size(max = 45)
    @Column(name = "userno")
    private String userno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "contenct")
    private String contenct;
    @Size(max = 200)
    @Column(name = "note")
    private String note;
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

    public EquipmentRepairHis() {
    }

    public EquipmentRepairHis(Integer id) {
        this.id = id;
    }

    public EquipmentRepairHis(Integer id, String company, String pid, String contenct, String status) {
        this.id = id;
        this.company = company;
        this.pid = pid;
        this.contenct = contenct;
        this.status = status;
    }



    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }



    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getContenct() {
        return contenct;
    }

    public void setContenct(String contenct) {
        this.contenct = contenct;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        if (!(object instanceof EquipmentRepairHis)) {
            return false;
        }
        EquipmentRepairHis other = (EquipmentRepairHis) object;
      if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentRepairHis[ id=" + id + " ]";
    }

}
