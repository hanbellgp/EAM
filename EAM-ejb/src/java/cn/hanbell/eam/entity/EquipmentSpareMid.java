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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentsparemid")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentSpareMid.findAll", query = "SELECT e FROM EquipmentSpareMid e"),
    @NamedQuery(name = "EquipmentSpareMid.findById", query = "SELECT e FROM EquipmentSpareMid e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentSpareMid.findByScategory", query = "SELECT e FROM EquipmentSpareMid e WHERE e.scategory.scategory = :scategory"),
    @NamedQuery(name = "EquipmentSpareMid.findByMcategory", query = "SELECT e FROM EquipmentSpareMid e WHERE e.mcategory = :mcategory"),
    @NamedQuery(name = "EquipmentSpareMid.findByMname", query = "SELECT e FROM EquipmentSpareMid e WHERE e.mname = :mname"),
    @NamedQuery(name = "EquipmentSpareMid.findByRemark", query = "SELECT e FROM EquipmentSpareMid e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentSpareMid.findByStatus", query = "SELECT e FROM EquipmentSpareMid e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentSpareMid.findByCreator", query = "SELECT e FROM EquipmentSpareMid e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentSpareMid.findByCredate", query = "SELECT e FROM EquipmentSpareMid e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentSpareMid.findByOptuser", query = "SELECT e FROM EquipmentSpareMid e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentSpareMid.findByOptdate", query = "SELECT e FROM EquipmentSpareMid e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentSpareMid.findByCfmuser", query = "SELECT e FROM EquipmentSpareMid e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentSpareMid.findByCfmdate", query = "SELECT e FROM EquipmentSpareMid e WHERE e.cfmdate = :cfmdate")})
public class EquipmentSpareMid extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "scategory", referencedColumnName = "scategory")
    @ManyToOne
    private EquipmentSpareClass scategory;
    @Size(max = 4)
    @Column(name = "mcategory")
    private String mcategory;
    @Size(max = 45)
    @Column(name = "mname")
    private String mname;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public EquipmentSpareMid() {
    }

    public EquipmentSpareMid(Integer id) {
        this.id = id;
    }

    public EquipmentSpareMid(Integer id, EquipmentSpareClass scategory, String status) {
        this.id = id;
        this.scategory = scategory;
        this.status = status;
    }

    public EquipmentSpareClass getScategory() {
        return scategory;
    }

    public void setScategory(EquipmentSpareClass scategory) {
        this.scategory = scategory;
    }

    public String getMcategory() {
        return mcategory;
    }

    public void setMcategory(String mcategory) {
        this.mcategory = mcategory;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
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
        if (!(object instanceof EquipmentSpareMid)) {
            return false;
        }
        EquipmentSpareMid other = (EquipmentSpareMid) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentSpareMid[ id=" + id + " ]";
    }

}
