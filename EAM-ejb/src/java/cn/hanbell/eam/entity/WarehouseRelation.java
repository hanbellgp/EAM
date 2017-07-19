/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperDetailEntity;
import java.util.Objects;
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
 * @author C0160
 */
@Entity
@Table(name = "warehouserelation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WarehouseRelation.findAll", query = "SELECT w FROM WarehouseRelation w"),
    @NamedQuery(name = "WarehouseRelation.findById", query = "SELECT w FROM WarehouseRelation w WHERE w.id = :id"),
    @NamedQuery(name = "WarehouseRelation.findByPId", query = "SELECT w FROM WarehouseRelation w WHERE w.pid = :pid ORDER BY w.company"),
    @NamedQuery(name = "WarehouseRelation.findByCompanyAndPId", query = "SELECT w FROM WarehouseRelation w WHERE w.company = :company AND w.pid = :pid")})
public class WarehouseRelation extends SuperDetailEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public WarehouseRelation() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWarehouseno() {
        return warehouseno;
    }

    public void setWarehouseno(String warehouseno) {
        this.warehouseno = warehouseno;
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
        if (!(object instanceof WarehouseRelation)) {
            return false;
        }
        WarehouseRelation other = (WarehouseRelation) object;
        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.WarehouseRelation[ id=" + id + " ]";
    }

}
