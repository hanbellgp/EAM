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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "assetposition")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetPosition.getRowCount", query = "SELECT COUNT(a) FROM AssetPosition a"),
    @NamedQuery(name = "AssetPosition.findAll", query = "SELECT a FROM AssetPosition a"),
    @NamedQuery(name = "AssetPosition.findById", query = "SELECT a FROM AssetPosition a WHERE a.id = :id"),
    @NamedQuery(name = "AssetPosition.findByCompany", query = "SELECT a FROM AssetPosition a WHERE a.company = :company"),
    @NamedQuery(name = "AssetPosition.findByPosition", query = "SELECT a FROM AssetPosition a WHERE a.position = :position"),
    @NamedQuery(name = "AssetPosition.findByName", query = "SELECT a FROM AssetPosition a WHERE a.name = :name"),
    @NamedQuery(name = "AssetPosition.findByPId", query = "SELECT a FROM AssetPosition a WHERE a.parentPosition.id = :pid"),
    @NamedQuery(name = "AssetPosition.findByRemark", query = "SELECT a FROM AssetPosition a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetPosition.findByStatus", query = "SELECT a FROM AssetPosition a WHERE a.status = :status"),
    @NamedQuery(name = "AssetPosition.findRootByCompany", query = "SELECT a FROM AssetPosition a WHERE a.company = :company AND a.parentPosition is NULL")})
public class AssetPosition extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "position")
    private String position;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;

    @JoinColumn(name = "pid", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private AssetPosition parentPosition;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public AssetPosition() {
    }

    public AssetPosition(String company, String position) {
        this.company = company;
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetPosition getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(AssetPosition parentPosition) {
        this.parentPosition = parentPosition;
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
        if (!(object instanceof AssetPosition)) {
            return false;
        }
        AssetPosition other = (AssetPosition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetPosition[ id=" + id + " ]";
    }

}
