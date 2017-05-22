/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @NamedQuery(name = "AssetCategory.findByPid", query = "SELECT a FROM AssetCategory a WHERE a.pid = :pid"),
    @NamedQuery(name = "AssetCategory.findByDeptno", query = "SELECT a FROM AssetCategory a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetCategory.findByDeptname", query = "SELECT a FROM AssetCategory a WHERE a.deptname = :deptname"),
    @NamedQuery(name = "AssetCategory.findByUserno", query = "SELECT a FROM AssetCategory a WHERE a.userno = :userno"),
    @NamedQuery(name = "AssetCategory.findByUsername", query = "SELECT a FROM AssetCategory a WHERE a.username = :username"),
    @NamedQuery(name = "AssetCategory.findByWarehouseno", query = "SELECT a FROM AssetCategory a WHERE a.warehouseno = :warehouseno"),
    @NamedQuery(name = "AssetCategory.findByWarehouseno2", query = "SELECT a FROM AssetCategory a WHERE a.warehouseno2 = :warehouseno2"),
    @NamedQuery(name = "AssetCategory.findByNoauto", query = "SELECT a FROM AssetCategory a WHERE a.noauto = :noauto"),
    @NamedQuery(name = "AssetCategory.findByNochange", query = "SELECT a FROM AssetCategory a WHERE a.nochange = :nochange"),
    @NamedQuery(name = "AssetCategory.findByNolead", query = "SELECT a FROM AssetCategory a WHERE a.nolead = :nolead"),
    @NamedQuery(name = "AssetCategory.findByNoformat", query = "SELECT a FROM AssetCategory a WHERE a.noformat = :noformat"),
    @NamedQuery(name = "AssetCategory.findByNoseqlen", query = "SELECT a FROM AssetCategory a WHERE a.noseqlen = :noseqlen"),
    @NamedQuery(name = "AssetCategory.findByRemark", query = "SELECT a FROM AssetCategory a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetCategory.findByStatus", query = "SELECT a FROM AssetCategory a WHERE a.status = :status"),
    @NamedQuery(name = "AssetCategory.findByCreator", query = "SELECT a FROM AssetCategory a WHERE a.creator = :creator"),
    @NamedQuery(name = "AssetCategory.findByCredate", query = "SELECT a FROM AssetCategory a WHERE a.credate = :credate"),
    @NamedQuery(name = "AssetCategory.findByOptuser", query = "SELECT a FROM AssetCategory a WHERE a.optuser = :optuser"),
    @NamedQuery(name = "AssetCategory.findByOptdate", query = "SELECT a FROM AssetCategory a WHERE a.optdate = :optdate"),
    @NamedQuery(name = "AssetCategory.findByCfmuser", query = "SELECT a FROM AssetCategory a WHERE a.cfmuser = :cfmuser"),
    @NamedQuery(name = "AssetCategory.findByCfmdate", query = "SELECT a FROM AssetCategory a WHERE a.cfmdate = :cfmdate")})
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
    @Column(name = "pid")
    private Integer pid;
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
    @Size(max = 20)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 20)
    @Column(name = "warehouseno2")
    private String warehouseno2;
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

    public AssetCategory(Integer id) {
        this.id = id;
    }

    public AssetCategory(Integer id, String category, String name, boolean noauto, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.noauto = noauto;
        this.status = status;
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

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
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

    public String getWarehouseno() {
        return warehouseno;
    }

    public void setWarehouseno(String warehouseno) {
        this.warehouseno = warehouseno;
    }

    public String getWarehouseno2() {
        return warehouseno2;
    }

    public void setWarehouseno2(String warehouseno2) {
        this.warehouseno2 = warehouseno2;
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
