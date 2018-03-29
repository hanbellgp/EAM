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
@Table(name = "assettransfer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetTransfer.findAll", query = "SELECT a FROM AssetTransfer a"),
    @NamedQuery(name = "AssetTransfer.findById", query = "SELECT a FROM AssetTransfer a WHERE a.id = :id"),
    @NamedQuery(name = "AssetTransfer.findByCompany", query = "SELECT a FROM AssetTransfer a WHERE a.company = :company"),
    @NamedQuery(name = "AssetTransfer.findByFormid", query = "SELECT a FROM AssetTransfer a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetTransfer.findByFormdate", query = "SELECT a FROM AssetTransfer a WHERE a.formdate = :formdate"),
    @NamedQuery(name = "AssetTransfer.findByDeptno", query = "SELECT a FROM AssetTransfer a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetTransfer.findByDeptname", query = "SELECT a FROM AssetTransfer a WHERE a.deptname = :deptname"),
    @NamedQuery(name = "AssetTransfer.findByStatus", query = "SELECT a FROM AssetTransfer a WHERE a.status = :status")})
public class AssetTransfer extends FormEntity {

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
    @Size(max = 100)
    @Column(name = "remark")
    private String remark;
    @Size(max = 20)
    @Column(name = "srcformid")
    private String srcformid;
    @Size(max = 20)
    @Column(name = "relformid")
    private String relformid;

    public AssetTransfer() {
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSrcformid() {
        return srcformid;
    }

    public void setSrcformid(String srcformid) {
        this.srcformid = srcformid;
    }

    public String getRelformid() {
        return relformid;
    }

    public void setRelformid(String relformid) {
        this.relformid = relformid;
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
        if (!(object instanceof AssetTransfer)) {
            return false;
        }
        AssetTransfer other = (AssetTransfer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetTransfer[ id=" + id + " ]";
    }

}
