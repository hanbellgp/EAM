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
 * @author C1368
 */
@Entity
@Table(name = "assetcategory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetCategory.findAll", query = "SELECT a FROM AssetCategory a"),
    @NamedQuery(name = "AssetCategory.findById", query = "SELECT a FROM AssetCategory a WHERE a.id = :id"),
    @NamedQuery(name = "AssetCategory.findByCategory", query = "SELECT a FROM AssetCategory a WHERE a.category = :category"),
    @NamedQuery(name = "AssetCategory.findByName", query = "SELECT a FROM AssetCategory a WHERE a.name = :name"),
    @NamedQuery(name = "AssetCategory.findByPId", query = "SELECT a FROM AssetCategory a WHERE a.parentCategory.id = :pid  ORDER BY a.category"),
    @NamedQuery(name = "AssetCategory.findRoot", query = "SELECT a FROM AssetCategory a WHERE a.parentCategory IS NULL ORDER BY a.category"),
    @NamedQuery(name = "AssetCategory.findByStatus", query = "SELECT a FROM AssetCategory a WHERE a.status = :status")})
public class AssetCategory extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "category")
    private String category;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;

    @JoinColumn(name = "pid", referencedColumnName = "id")
    @ManyToOne
    private AssetCategory parentCategory;

    @Size(max = 20)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 20)
    @Column(name = "userno")
    private String userno;
    @Size(max = 45)
    @Column(name = "username")
    private String username;

    @JoinColumn(name = "warehouseno", referencedColumnName = "warehouseno")
    @ManyToOne
    private Warehouse warehouse;

    @JoinColumn(name = "warehouseno2", referencedColumnName = "warehouseno")
    @ManyToOne
    private Warehouse warehouse2;

    @Basic(optional = false)
    @NotNull
    @Column(name = "noauto")
    private boolean noauto;
    @Column(name = "nochange")
    private Boolean nochange;
    @Size(max = 20)
    @Column(name = "nolead")
    private String nolead;
    @Size(max = 8)
    @Column(name = "noformat")
    private String noformat;
    @Column(name = "noseqlen")
    private Integer noseqlen;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public AssetCategory() {
    }

    public AssetCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetCategory getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(AssetCategory parentCategory) {
        this.parentCategory = parentCategory;
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

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Warehouse getWarehouse2() {
        return warehouse2;
    }

    public void setWarehouse2(Warehouse warehouse2) {
        this.warehouse2 = warehouse2;
    }

    public boolean getNoauto() {
        return noauto;
    }

    public void setNoauto(boolean noauto) {
        this.noauto = noauto;
    }

    public Boolean getNochange() {
        return nochange;
    }

    public void setNochange(Boolean nochange) {
        this.nochange = nochange;
    }

    public String getNolead() {
        return nolead;
    }

    public void setNolead(String nolead) {
        this.nolead = nolead;
    }

    public String getNoformat() {
        return noformat;
    }

    public void setNoformat(String noformat) {
        this.noformat = noformat;
    }

    public Integer getNoseqlen() {
        return noseqlen;
    }

    public void setNoseqlen(Integer noseqlen) {
        this.noseqlen = noseqlen;
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
        if (!(object instanceof AssetCategory)) {
            return false;
        }
        AssetCategory other = (AssetCategory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetCategory[ id=" + id + " ]";
    }

}
