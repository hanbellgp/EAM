/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * @author Administrator
 */
@Entity
@Table(name = "assetparameterdta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetParameterDta.findAll", query = "SELECT a FROM AssetParameterDta a"),
    @NamedQuery(name = "AssetParameterDta.findById", query = "SELECT a FROM AssetParameterDta a WHERE a.id = :id"),
    @NamedQuery(name = "AssetParameterDta.findByCompany", query = "SELECT a FROM AssetParameterDta a WHERE a.company = :company"),
    @NamedQuery(name = "AssetParameterDta.findByPid", query = "SELECT a FROM AssetParameterDta a WHERE a.pid = :pid"),
    @NamedQuery(name = "AssetParameterDta.findBySeq", query = "SELECT a FROM AssetParameterDta a WHERE a.seq = :seq"),
    @NamedQuery(name = "AssetParameterDta.findByParamname", query = "SELECT a FROM AssetParameterDta a WHERE a.paramname = :paramname"),
    @NamedQuery(name = "AssetParameterDta.findByParamvalue", query = "SELECT a FROM AssetParameterDta a WHERE a.paramvalue = :paramvalue"),
    @NamedQuery(name = "AssetParameterDta.findByParamunit", query = "SELECT a FROM AssetParameterDta a WHERE a.paramunit = :paramunit"),
    @NamedQuery(name = "AssetParameterDta.findByRemark", query = "SELECT a FROM AssetParameterDta a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetParameterDta.findByStatus", query = "SELECT a FROM AssetParameterDta a WHERE a.status = :status"),
    @NamedQuery(name = "AssetParameterDta.findByCreator", query = "SELECT a FROM AssetParameterDta a WHERE a.creator = :creator"),
    @NamedQuery(name = "AssetParameterDta.findByCredate", query = "SELECT a FROM AssetParameterDta a WHERE a.credate = :credate"),
    @NamedQuery(name = "AssetParameterDta.findByOptuser", query = "SELECT a FROM AssetParameterDta a WHERE a.optuser = :optuser"),
    @NamedQuery(name = "AssetParameterDta.findByOptdate", query = "SELECT a FROM AssetParameterDta a WHERE a.optdate = :optdate"),
    @NamedQuery(name = "AssetParameterDta.findByCfmuser", query = "SELECT a FROM AssetParameterDta a WHERE a.cfmuser = :cfmuser"),
    @NamedQuery(name = "AssetParameterDta.findByPId", query = "SELECT e FROM AssetParameterDta e WHERE e.pid = :pid ORDER BY e.seq"),
    @NamedQuery(name = "AssetParameterDta.findByCfmdate", query = "SELECT a FROM AssetParameterDta a WHERE a.cfmdate = :cfmdate")})
public class AssetParameterDta extends FormDetailEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    
    @Size(max = 60)
    @Column(name = "paramname")
    private String paramname;
    @Size(max = 40)
    @Column(name = "paramvalue")
    private String paramvalue;
    @Size(max = 20)
    @Column(name = "paramunit")
    private String paramunit;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "status")
    private String status;
    @Size(max = 20)
    @Column(name = "creator")
    private String creator;
    @Column(name = "credate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date credate;
    @Size(max = 20)
    @Column(name = "optuser")
    private String optuser;
    @Column(name = "optdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date optdate;
    @Size(max = 20)
    @Column(name = "cfmuser")
    private String cfmuser;
    @Column(name = "cfmdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cfmdate;

    public AssetParameterDta() {
    }

    public AssetParameterDta(Integer id) {
        this.id = id;
    }

    public AssetParameterDta(Integer id, String company, String pid, String status) {
        this.id = id;
        this.company = company;
        this.pid = pid;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getParamname() {
        return paramname;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }

    public String getParamvalue() {
        return paramvalue;
    }

    public void setParamvalue(String paramvalue) {
        this.paramvalue = paramvalue;
    }

    public String getParamunit() {
        return paramunit;
    }

    public void setParamunit(String paramunit) {
        this.paramunit = paramunit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCredate() {
        return credate;
    }

    public void setCredate(Date credate) {
        this.credate = credate;
    }

    public String getOptuser() {
        return optuser;
    }

    public void setOptuser(String optuser) {
        this.optuser = optuser;
    }

    public Date getOptdate() {
        return optdate;
    }

    public void setOptdate(Date optdate) {
        this.optdate = optdate;
    }

    public String getCfmuser() {
        return cfmuser;
    }

    public void setCfmuser(String cfmuser) {
        this.cfmuser = cfmuser;
    }

    public Date getCfmdate() {
        return cfmdate;
    }

    public void setCfmdate(Date cfmdate) {
        this.cfmdate = cfmdate;
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
        if (!(object instanceof AssetParameterDta)) {
            return false;
        }
        AssetParameterDta other = (AssetParameterDta) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetParameterDta[ id=" + id + " ]";
    }

}
