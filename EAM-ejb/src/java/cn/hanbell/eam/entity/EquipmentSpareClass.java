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
@Table(name = "equipmentspareclass")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentSpareClass.findAll", query = "SELECT e FROM EquipmentSpareClass e"),
    @NamedQuery(name = "EquipmentSpareClass.findById", query = "SELECT e FROM EquipmentSpareClass e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentSpareClass.findByScategory", query = "SELECT e FROM EquipmentSpareClass e WHERE e.scategory = :scategory"),
    @NamedQuery(name = "EquipmentSpareClass.findBySname", query = "SELECT e FROM EquipmentSpareClass e WHERE e.sname = :sname"),
    @NamedQuery(name = "EquipmentSpareClass.findByRemark", query = "SELECT e FROM EquipmentSpareClass e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentSpareClass.findByStatus", query = "SELECT e FROM EquipmentSpareClass e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentSpareClass.findByCreator", query = "SELECT e FROM EquipmentSpareClass e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentSpareClass.findByCredate", query = "SELECT e FROM EquipmentSpareClass e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentSpareClass.findByOptuser", query = "SELECT e FROM EquipmentSpareClass e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentSpareClass.findByOptdate", query = "SELECT e FROM EquipmentSpareClass e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentSpareClass.findByCfmuser", query = "SELECT e FROM EquipmentSpareClass e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentSpareClass.findByCfmdate", query = "SELECT e FROM EquipmentSpareClass e WHERE e.cfmdate = :cfmdate")})
public class EquipmentSpareClass extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "scategory")
    private String scategory;
    @Size(max = 45)
    @Column(name = "sname")
    private String sname;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
    public EquipmentSpareClass() {
    }

    public EquipmentSpareClass(Integer id) {
        this.id = id;
    }

    public EquipmentSpareClass(Integer id, String scategory, String status) {
        this.id = id;
        this.scategory = scategory;
        this.status = status;
    }

  

    public String getScategory() {
        return scategory;
    }

    public void setScategory(String scategory) {
        this.scategory = scategory;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
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
        if (!(object instanceof EquipmentSpareClass)) {
            return false;
        }
        EquipmentSpareClass other = (EquipmentSpareClass) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentSpareClass[ id=" + id + " ]";
    }
    
}
