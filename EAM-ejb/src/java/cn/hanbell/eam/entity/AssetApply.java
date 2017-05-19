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
@Table(name = "assetapply")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetApply.findAll", query = "SELECT a FROM AssetApply a"),
    @NamedQuery(name = "AssetApply.findById", query = "SELECT a FROM AssetApply a WHERE a.id = :id"),
    @NamedQuery(name = "AssetApply.findByFormid", query = "SELECT a FROM AssetApply a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetApply.findByFormdate", query = "SELECT a FROM AssetApply a WHERE a.formdate = :formdate"),
    @NamedQuery(name = "AssetApply.findByStatus", query = "SELECT a FROM AssetApply a WHERE a.status = :status")})
public class AssetApply extends FormEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 20)
    @Column(name = "applyDeptno")
    private String applyDeptno;
    @Size(max = 45)
    @Column(name = "applyDeptname")
    private String applyDeptname;
    @Size(max = 20)
    @Column(name = "applyUserno")
    private String applyUserno;
    @Size(max = 45)
    @Column(name = "applyUsername")
    private String applyUsername;
    @Size(max = 20)
    @Column(name = "requireDeptno")
    private String requireDeptno;
    @Size(max = 45)
    @Column(name = "requireDeptname")
    private String requireDeptname;
    @Size(max = 20)
    @Column(name = "requireUserno")
    private String requireUserno;
    @Size(max = 45)
    @Column(name = "requireUsername")
    private String requireUsername;
    @Size(max = 100)
    @Column(name = "remark")
    private String remark;

    public AssetApply() {
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
        if (!(object instanceof AssetApply)) {
            return false;
        }
        AssetApply other = (AssetApply) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetApply[ id=" + id + " ]";
    }

    /**
     * @return the applyDeptno
     */
    public String getApplyDeptno() {
        return applyDeptno;
    }

    /**
     * @param applyDeptno the applyDeptno to set
     */
    public void setApplyDeptno(String applyDeptno) {
        this.applyDeptno = applyDeptno;
    }

    /**
     * @return the applyDeptname
     */
    public String getApplyDeptname() {
        return applyDeptname;
    }

    /**
     * @param applyDeptname the applyDeptname to set
     */
    public void setApplyDeptname(String applyDeptname) {
        this.applyDeptname = applyDeptname;
    }

    /**
     * @return the applyUserno
     */
    public String getApplyUserno() {
        return applyUserno;
    }

    /**
     * @param applyUserno the applyUserno to set
     */
    public void setApplyUserno(String applyUserno) {
        this.applyUserno = applyUserno;
    }

    /**
     * @return the applyUsername
     */
    public String getApplyUsername() {
        return applyUsername;
    }

    /**
     * @param applyUsername the applyUsername to set
     */
    public void setApplyUsername(String applyUsername) {
        this.applyUsername = applyUsername;
    }

    /**
     * @return the requireDeptno
     */
    public String getRequireDeptno() {
        return requireDeptno;
    }

    /**
     * @param requireDeptno the requireDeptno to set
     */
    public void setRequireDeptno(String requireDeptno) {
        this.requireDeptno = requireDeptno;
    }

    /**
     * @return the requireDeptname
     */
    public String getRequireDeptname() {
        return requireDeptname;
    }

    /**
     * @param requireDeptname the requireDeptname to set
     */
    public void setRequireDeptname(String requireDeptname) {
        this.requireDeptname = requireDeptname;
    }

    /**
     * @return the requireUserno
     */
    public String getRequireUserno() {
        return requireUserno;
    }

    /**
     * @param requireUserno the requireUserno to set
     */
    public void setRequireUserno(String requireUserno) {
        this.requireUserno = requireUserno;
    }

    /**
     * @return the requireUsername
     */
    public String getRequireUsername() {
        return requireUsername;
    }

    /**
     * @param requireUsername the requireUsername to set
     */
    public void setRequireUsername(String requireUsername) {
        this.requireUsername = requireUsername;
    }

}
