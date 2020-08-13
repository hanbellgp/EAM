/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
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
@Table(name = "equipmentrepairhis")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equipmentrepairhis.findAll", query = "SELECT e FROM Equipmentrepairhis e")
    , @NamedQuery(name = "Equipmentrepairhis.findById", query = "SELECT e FROM Equipmentrepairhis e WHERE e.id = :id")
    , @NamedQuery(name = "Equipmentrepairhis.findByCompany", query = "SELECT e FROM Equipmentrepairhis e WHERE e.company = :company")
    , @NamedQuery(name = "Equipmentrepairhis.findByPid", query = "SELECT e FROM Equipmentrepairhis e WHERE e.pid = :pid")
    , @NamedQuery(name = "Equipmentrepairhis.findBySeq", query = "SELECT e FROM Equipmentrepairhis e WHERE e.seq = :seq")
    , @NamedQuery(name = "Equipmentrepairhis.findByUserno", query = "SELECT e FROM Equipmentrepairhis e WHERE e.userno = :userno")
    , @NamedQuery(name = "Equipmentrepairhis.findByContenct", query = "SELECT e FROM Equipmentrepairhis e WHERE e.contenct = :contenct")
    , @NamedQuery(name = "Equipmentrepairhis.findByNote", query = "SELECT e FROM Equipmentrepairhis e WHERE e.note = :note")
    , @NamedQuery(name = "Equipmentrepairhis.findByRemark", query = "SELECT e FROM Equipmentrepairhis e WHERE e.remark = :remark")
    , @NamedQuery(name = "Equipmentrepairhis.findByStatus", query = "SELECT e FROM Equipmentrepairhis e WHERE e.status = :status")
    , @NamedQuery(name = "Equipmentrepairhis.findByPId", query = "SELECT e FROM Equipmentrepairhis e WHERE e.pid = :pid")})

public class Equipmentrepairhis extends FormDetailEntity {

    @Basic(optional = false)
    @NotNull
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

    public Equipmentrepairhis() {
    }

    public Equipmentrepairhis(Integer id) {
        this.id = id;
    }

    public Equipmentrepairhis(Integer id, String company, String pid, String contenct, String status) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipmentrepairhis)) {
            return false;
        }
        Equipmentrepairhis other = (Equipmentrepairhis) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Equipmentrepairhis[ id=" + id + " ]";
    }

}
