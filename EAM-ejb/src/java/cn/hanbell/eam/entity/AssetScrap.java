/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormEntity;
import java.math.BigDecimal;
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
@Table(name = "assetscrap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetScrap.getRowCount", query = "SELECT COUNT(a) FROM AssetScrap a"),
    @NamedQuery(name = "AssetScrap.findAll", query = "SELECT a FROM AssetScrap a"),
    @NamedQuery(name = "AssetScrap.findById", query = "SELECT a FROM AssetScrap a WHERE a.id = :id"),
    @NamedQuery(name = "AssetScrap.findByCompany", query = "SELECT a FROM AssetScrap a WHERE a.company = :company"),
    @NamedQuery(name = "AssetScrap.findByFormid", query = "SELECT a FROM AssetScrap a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetScrap.findByFormdate", query = "SELECT a FROM AssetScrap a WHERE a.formdate = :formdate"),
    @NamedQuery(name = "AssetScrap.findByDeptno", query = "SELECT a FROM AssetScrap a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetScrap.findByDeptname", query = "SELECT a FROM AssetScrap a WHERE a.deptname = :deptname"),
    @NamedQuery(name = "AssetScrap.findByReason", query = "SELECT a FROM AssetScrap a WHERE a.reason = :reason"),
    @NamedQuery(name = "AssetScrap.findByWarehouseno", query = "SELECT a FROM AssetScrap a WHERE a.warehouseno = :warehouseno"),
    @NamedQuery(name = "AssetScrap.findByRemark", query = "SELECT a FROM AssetScrap a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetScrap.findByStatus", query = "SELECT a FROM AssetScrap a WHERE a.status = :status")})
public class AssetScrap extends FormEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 20)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 200)
    @Column(name = "reason")
    private String reason;
    @Size(max = 20)
    @Column(name = "warehouseno")
    private String warehouseno;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "totalAmts")
    private BigDecimal totalAmts;
    @Basic(optional = false)
    @NotNull
    @Column(name = "surplusValue")
    private BigDecimal surplusValue;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public AssetScrap() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getWarehouseno() {
        return warehouseno;
    }

    public void setWarehouseno(String warehouseno) {
        this.warehouseno = warehouseno;
    }

    public BigDecimal getTotalAmts() {
        return totalAmts;
    }

    public void setTotalAmts(BigDecimal totalAmts) {
        this.totalAmts = totalAmts;
    }

    public BigDecimal getSurplusValue() {
        return surplusValue;
    }

    public void setSurplusValue(BigDecimal surplusValue) {
        this.surplusValue = surplusValue;
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
        if (!(object instanceof AssetScrap)) {
            return false;
        }
        AssetScrap other = (AssetScrap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetScrap[ id=" + id + " ]";
    }

}
