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
@Table(name = "assetadjust")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetAdjust.getRowCount", query = "SELECT COUNT(a) FROM AssetAdjust a"),
    @NamedQuery(name = "AssetAdjust.findAll", query = "SELECT a FROM AssetAdjust a"),
    @NamedQuery(name = "AssetAdjust.findById", query = "SELECT a FROM AssetAdjust a WHERE a.id = :id"),
    @NamedQuery(name = "AssetAdjust.findByCompany", query = "SELECT a FROM AssetAdjust a WHERE a.company = :company"),
    @NamedQuery(name = "AssetAdjust.findByFormid", query = "SELECT a FROM AssetAdjust a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetAdjust.findByFormdate", query = "SELECT a FROM AssetAdjust a WHERE a.formdate = :formdate"),
    @NamedQuery(name = "AssetAdjust.findByDeptno", query = "SELECT a FROM AssetAdjust a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetAdjust.findByDeptname", query = "SELECT a FROM AssetAdjust a WHERE a.deptname = :deptname")})
public class AssetAdjust extends FormEntity {

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
    @Size(max = 45)
    @Column(name = "objtype")
    private String objtype;
    @Size(max = 20)
    @Column(name = "objno")
    private String objno;
    @Size(max = 45)
    @Column(name = "reason")
    private String reason;
    @Size(max = 20)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public AssetAdjust() {
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

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }

    public String getObjno() {
        return objno;
    }

    public void setObjno(String objno) {
        this.objno = objno;
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
        if (!(object instanceof AssetAdjust)) {
            return false;
        }
        AssetAdjust other = (AssetAdjust) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetAdjust[ id=" + id + " ]";
    }

}
