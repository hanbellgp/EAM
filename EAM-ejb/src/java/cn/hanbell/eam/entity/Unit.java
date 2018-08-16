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
 * @author C0160
 */
@Entity
@Table(name = "unit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Unit.findAll", query = "SELECT u FROM Unit u")
    , @NamedQuery(name = "Unit.findById", query = "SELECT u FROM Unit u WHERE u.id = :id")
    , @NamedQuery(name = "Unit.findByUnit", query = "SELECT u FROM Unit u WHERE u.unit = :unit")
    , @NamedQuery(name = "Unit.findByEnunit", query = "SELECT u FROM Unit u WHERE u.enunit = :enunit")
    , @NamedQuery(name = "Unit.findByStatus", query = "SELECT u FROM Unit u WHERE u.status = :status")})
public class Unit extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "unit")
    private String unit;
    @Size(max = 20)
    @Column(name = "enunit")
    private String enunit;

    public Unit() {
    }

    public Unit(Integer id) {
        this.id = id;
    }

    public Unit(Integer id, String unit, String status) {
        this.id = id;
        this.unit = unit;
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEnunit() {
        return enunit;
    }

    public void setEnunit(String enunit) {
        this.enunit = enunit;
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
        if (!(object instanceof Unit)) {
            return false;
        }
        Unit other = (Unit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Unit[ id=" + id + " ]";
    }

}
