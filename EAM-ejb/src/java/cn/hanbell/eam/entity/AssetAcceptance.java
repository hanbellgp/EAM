/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormEntity;
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
@Table(name = "assetacceptance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetAcceptance.findAll", query = "SELECT a FROM AssetAcceptance a"),
    @NamedQuery(name = "AssetAcceptance.findById", query = "SELECT a FROM AssetAcceptance a WHERE a.id = :id"),
    @NamedQuery(name = "AssetAcceptance.findByFormid", query = "SELECT a FROM AssetAcceptance a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetAcceptance.findByFormdate", query = "SELECT a FROM AssetAcceptance a WHERE a.formdate = :formdate"),
    @NamedQuery(name = "AssetAcceptance.findByVendorno", query = "SELECT a FROM AssetAcceptance a WHERE a.vendorno = :vendorno"),
    @NamedQuery(name = "AssetAcceptance.findByWarehouseno", query = "SELECT a FROM AssetAcceptance a WHERE a.warehouseno = :warehouseno"),
    @NamedQuery(name = "AssetAcceptance.findByStatus", query = "SELECT a FROM AssetAcceptance a WHERE a.status = :status")})
public class AssetAcceptance extends FormEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 20)
    @Column(name = "vendorno")
    private String vendorno;
    @Size(max = 20)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;

    @Size(max = 20)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 100)
    @Column(name = "remark")
    private String remark;

    public AssetAcceptance() {
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    public String getVendorno() {
        return vendorno;
    }

    public void setVendorno(String vendorno) {
        this.vendorno = vendorno;
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
        if (!(object instanceof AssetAcceptance)) {
            return false;
        }
        AssetAcceptance other = (AssetAcceptance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetAcceptance[ id=" + id + " ]";
    }

    /**
     * @return the deptno
     */
    public String getDeptno() {
        return deptno;
    }

    /**
     * @param deptno the deptno to set
     */
    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    /**
     * @return the deptname
     */
    public String getDeptname() {
        return deptname;
    }

    /**
     * @param deptname the deptname to set
     */
    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

}
