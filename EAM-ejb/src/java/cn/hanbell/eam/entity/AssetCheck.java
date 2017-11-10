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
@Table(name = "assetcheck")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetCheck.getRowCount", query = "SELECT COUNT(a) FROM AssetCheck a"),
    @NamedQuery(name = "AssetCheck.findAll", query = "SELECT a FROM AssetCheck a"),
    @NamedQuery(name = "AssetCheck.findById", query = "SELECT a FROM AssetCheck a WHERE a.id = :id"),
    @NamedQuery(name = "AssetCheck.findByCompany", query = "SELECT a FROM AssetCheck a WHERE a.company = :company"),
    @NamedQuery(name = "AssetCheck.findByFormid", query = "SELECT a FROM AssetCheck a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetCheck.findByFormdate", query = "SELECT a FROM AssetCheck a WHERE a.formdate = :formdate"),
    @NamedQuery(name = "AssetCheck.findByStatus", query = "SELECT a FROM AssetCheck a WHERE a.status = :status")})
public class AssetCheck extends FormEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;

    @Size(max = 20)
    @Column(name = "formtype")
    private String formtype;
    @Size(max = 20)
    @Column(name = "formkind")
    private String formkind;
    @JoinColumn(name = "categoryid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AssetCategory category;
    @Size(max = 200)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 20)
    @Column(name = "userno")
    private String userno;
    @Size(max = 200)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public AssetCheck() {
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

    public String getFormtype() {
        return formtype;
    }

    public void setFormtype(String formtype) {
        this.formtype = formtype;
    }

    /**
     * @return the formkind
     */
    public String getFormkind() {
        return formkind;
    }

    /**
     * @param formkind the formkind to set
     */
    public void setFormkind(String formkind) {
        this.formkind = formkind;
    }

    /**
     * @return the category
     */
    public AssetCategory getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
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
        if (!(object instanceof AssetCheck)) {
            return false;
        }
        AssetCheck other = (AssetCheck) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetCheck[ id=" + id + " ]";
    }

}
