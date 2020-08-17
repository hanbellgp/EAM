/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
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
@Table(name = "equipmenttrouble")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentTrouble.findAll", query = "SELECT e FROM EquipmentTrouble e"),
    @NamedQuery(name = "EquipmentTrouble.findById", query = "SELECT e FROM EquipmentTrouble e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentTrouble.findByTroubleid", query = "SELECT e FROM EquipmentTrouble e WHERE e.troubleid = :troubleid"),
    @NamedQuery(name = "EquipmentTrouble.findByTroublename", query = "SELECT e FROM EquipmentTrouble e WHERE e.troublename = :troublename"),
    @NamedQuery(name = "EquipmentTrouble.findByPid", query = "SELECT e FROM EquipmentTrouble e WHERE e.pid = :pid"),
    @NamedQuery(name = "EquipmentTrouble.findByRemark", query = "SELECT e FROM EquipmentTrouble e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentTrouble.findByStatus", query = "SELECT e FROM EquipmentTrouble e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentTrouble.findByCreator", query = "SELECT e FROM EquipmentTrouble e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentTrouble.findByCredate", query = "SELECT e FROM EquipmentTrouble e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentTrouble.findByOptuser", query = "SELECT e FROM EquipmentTrouble e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentTrouble.findByOptdate", query = "SELECT e FROM EquipmentTrouble e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentTrouble.findByCfmuser", query = "SELECT e FROM EquipmentTrouble e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentTrouble.findByCfmdate", query = "SELECT e FROM EquipmentTrouble e WHERE e.cfmdate = :cfmdate")})
public class EquipmentTrouble extends SuperEntity{


    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "troubleid")
    private String troubleid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "troublename")
    private String troublename;
    @Size(max = 2)
    @Column(name = "pid")
    private String pid;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;



    public EquipmentTrouble() {
    }

    public EquipmentTrouble(Integer id) {
        this.id = id;
    }

    public EquipmentTrouble(Integer id, String troubleid, String troublename, String status) {
        this.id = id;
        this.troubleid = troubleid;
        this.troublename = troublename;
        this.status = status;
    }

   

    public String getTroubleid() {
        return troubleid;
    }

    public void setTroubleid(String troubleid) {
        this.troubleid = troubleid;
    }

    public String getTroublename() {
        return troublename;
    }

    public void setTroublename(String troublename) {
        this.troublename = troublename;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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
        if (!(object instanceof EquipmentTrouble)) {
            return false;
        }
        EquipmentTrouble other = (EquipmentTrouble) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Equipmenttrouble[ id=" + id + " ]";
    }
    
}
