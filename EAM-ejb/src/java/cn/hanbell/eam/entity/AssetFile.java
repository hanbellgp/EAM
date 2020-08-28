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
@Table(name = "assetfile")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetFile.findAll", query = "SELECT a FROM AssetFile a"),
    @NamedQuery(name = "AssetFile.findById", query = "SELECT a FROM AssetFile a WHERE a.id = :id"),
    @NamedQuery(name = "AssetFile.findByCompany", query = "SELECT a FROM AssetFile a WHERE a.company = :company"),
    @NamedQuery(name = "AssetFile.findByPid", query = "SELECT a FROM AssetFile a WHERE a.pid = :pid"),
    @NamedQuery(name = "AssetFile.findBySeq", query = "SELECT a FROM AssetFile a WHERE a.seq = :seq"),
    @NamedQuery(name = "AssetFile.findByFileno", query = "SELECT a FROM AssetFile a WHERE a.fileno = :fileno"),
    @NamedQuery(name = "AssetFile.findByFilename", query = "SELECT a FROM AssetFile a WHERE a.filename = :filename"),
    @NamedQuery(name = "AssetFile.findByFilepath", query = "SELECT a FROM AssetFile a WHERE a.filepath = :filepath"),
    @NamedQuery(name = "AssetFile.findByFilemark", query = "SELECT a FROM AssetFile a WHERE a.filemark = :filemark"),
    @NamedQuery(name = "AssetFile.findByRemark", query = "SELECT a FROM AssetFile a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetFile.findByStatus", query = "SELECT a FROM AssetFile a WHERE a.status = :status"),
    @NamedQuery(name = "AssetFile.findByCreator", query = "SELECT a FROM AssetFile a WHERE a.creator = :creator"),
    @NamedQuery(name = "AssetFile.findByCredate", query = "SELECT a FROM AssetFile a WHERE a.credate = :credate"),
    @NamedQuery(name = "AssetFile.findByOptuser", query = "SELECT a FROM AssetFile a WHERE a.optuser = :optuser"),
    @NamedQuery(name = "AssetFile.findByOptdate", query = "SELECT a FROM AssetFile a WHERE a.optdate = :optdate"),
    @NamedQuery(name = "AssetFile.findByCfmuser", query = "SELECT a FROM AssetFile a WHERE a.cfmuser = :cfmuser"),
    @NamedQuery(name = "AssetFile.findByPId", query = "SELECT e FROM AssetFile e WHERE e.pid = :pid ORDER BY e.seq"),
    @NamedQuery(name = "AssetFile.findByCfmdate", query = "SELECT a FROM AssetFile a WHERE a.cfmdate = :cfmdate")})
public class AssetFile extends FormDetailEntity {


    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
  
    @Size(max = 45)
    @Column(name = "fileno")
    private String fileno;
    @Size(max = 45)
    @Column(name = "filename")
    private String filename;
    @Size(max = 100)
    @Column(name = "filepath")
    private String filepath;
    @Size(max = 45)
    @Column(name = "filemark")
    private String filemark;
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

    public AssetFile() {
    }

    public AssetFile(Integer id) {
        this.id = id;
    }

    public AssetFile(Integer id, String company, String pid, String status) {
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



    public String getFileno() {
        return fileno;
    }

    public void setFileno(String fileno) {
        this.fileno = fileno;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilemark() {
        return filemark;
    }

    public void setFilemark(String filemark) {
        this.filemark = filemark;
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
        if (!(object instanceof AssetFile)) {
            return false;
        }
        AssetFile other = (AssetFile) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetFile[ id=" + id + " ]";
    }
    
}
