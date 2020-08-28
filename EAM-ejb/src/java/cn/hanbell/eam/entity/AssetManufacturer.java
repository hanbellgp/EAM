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
@Table(name = "assetmanufacturer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetManufacturer.findAll", query = "SELECT a FROM AssetManufacturer a"),
    @NamedQuery(name = "AssetManufacturer.findById", query = "SELECT a FROM AssetManufacturer a WHERE a.id = :id"),
    @NamedQuery(name = "AssetManufacturer.findByCompany", query = "SELECT a FROM AssetManufacturer a WHERE a.company = :company"),
    @NamedQuery(name = "AssetManufacturer.findByPid", query = "SELECT a FROM AssetManufacturer a WHERE a.pid = :pid"),
    @NamedQuery(name = "AssetManufacturer.findBySeq", query = "SELECT a FROM AssetManufacturer a WHERE a.seq = :seq"),
    @NamedQuery(name = "AssetManufacturer.findByManufacturer", query = "SELECT a FROM AssetManufacturer a WHERE a.manufacturer = :manufacturer"),
    @NamedQuery(name = "AssetManufacturer.findByManudate", query = "SELECT a FROM AssetManufacturer a WHERE a.manudate = :manudate"),
    @NamedQuery(name = "AssetManufacturer.findByManuno", query = "SELECT a FROM AssetManufacturer a WHERE a.manuno = :manuno"),
    @NamedQuery(name = "AssetManufacturer.findByManucountry", query = "SELECT a FROM AssetManufacturer a WHERE a.manucountry = :manucountry"),
    @NamedQuery(name = "AssetManufacturer.findBySupplier", query = "SELECT a FROM AssetManufacturer a WHERE a.supplier = :supplier"),
    @NamedQuery(name = "AssetManufacturer.findByManutype", query = "SELECT a FROM AssetManufacturer a WHERE a.manutype = :manutype"),
    @NamedQuery(name = "AssetManufacturer.findByContacter1", query = "SELECT a FROM AssetManufacturer a WHERE a.contacter1 = :contacter1"),
    @NamedQuery(name = "AssetManufacturer.findByContactertel1", query = "SELECT a FROM AssetManufacturer a WHERE a.contactertel1 = :contactertel1"),
    @NamedQuery(name = "AssetManufacturer.findByContacteradd1", query = "SELECT a FROM AssetManufacturer a WHERE a.contacteradd1 = :contacteradd1"),
    @NamedQuery(name = "AssetManufacturer.findByContacter2", query = "SELECT a FROM AssetManufacturer a WHERE a.contacter2 = :contacter2"),
    @NamedQuery(name = "AssetManufacturer.findByContactertel2", query = "SELECT a FROM AssetManufacturer a WHERE a.contactertel2 = :contactertel2"),
    @NamedQuery(name = "AssetManufacturer.findByContacteradd2", query = "SELECT a FROM AssetManufacturer a WHERE a.contacteradd2 = :contacteradd2"),
    @NamedQuery(name = "AssetManufacturer.findByMmark", query = "SELECT a FROM AssetManufacturer a WHERE a.mmark = :mmark"),
    @NamedQuery(name = "AssetManufacturer.findByRemark", query = "SELECT a FROM AssetManufacturer a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetManufacturer.findByStatus", query = "SELECT a FROM AssetManufacturer a WHERE a.status = :status"),
    @NamedQuery(name = "AssetManufacturer.findByCreator", query = "SELECT a FROM AssetManufacturer a WHERE a.creator = :creator"),
    @NamedQuery(name = "AssetManufacturer.findByCredate", query = "SELECT a FROM AssetManufacturer a WHERE a.credate = :credate"),
    @NamedQuery(name = "AssetManufacturer.findByOptuser", query = "SELECT a FROM AssetManufacturer a WHERE a.optuser = :optuser"),
    @NamedQuery(name = "AssetManufacturer.findByOptdate", query = "SELECT a FROM AssetManufacturer a WHERE a.optdate = :optdate"),
    @NamedQuery(name = "AssetManufacturer.findByCfmuser", query = "SELECT a FROM AssetManufacturer a WHERE a.cfmuser = :cfmuser"),
     @NamedQuery(name = "AssetManufacturer.findByPId", query = "SELECT e FROM AssetManufacturer e WHERE e.pid = :pid ORDER BY e.seq"),
    @NamedQuery(name = "AssetManufacturer.findByCfmdate", query = "SELECT a FROM AssetManufacturer a WHERE a.cfmdate = :cfmdate")})
public class AssetManufacturer extends FormDetailEntity {

   
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 45)
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "manudate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manudate;
    @Size(max = 45)
    @Column(name = "manuno")
    private String manuno;
    @Size(max = 45)
    @Column(name = "manucountry")
    private String manucountry;
    @Size(max = 45)
    @Column(name = "supplier")
    private String supplier;
    @Size(max = 20)
    @Column(name = "manutype")
    private String manutype;
    @Size(max = 45)
    @Column(name = "contacter1")
    private String contacter1;
    @Size(max = 30)
    @Column(name = "contactertel1")
    private String contactertel1;
    @Size(max = 100)
    @Column(name = "contacteradd1")
    private String contacteradd1;
    @Size(max = 45)
    @Column(name = "contacter2")
    private String contacter2;
    @Size(max = 30)
    @Column(name = "contactertel2")
    private String contactertel2;
    @Size(max = 100)
    @Column(name = "contacteradd2")
    private String contacteradd2;
    @Size(max = 100)
    @Column(name = "mmark")
    private String mmark;
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

    public AssetManufacturer() {
    }

    public AssetManufacturer(Integer id) {
        this.id = id;
    }

    public AssetManufacturer(Integer id, String company, String pid, String status) {
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

 


  

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Date getManudate() {
        return manudate;
    }

    public void setManudate(Date manudate) {
        this.manudate = manudate;
    }

    public String getManuno() {
        return manuno;
    }

    public void setManuno(String manuno) {
        this.manuno = manuno;
    }

    public String getManucountry() {
        return manucountry;
    }

    public void setManucountry(String manucountry) {
        this.manucountry = manucountry;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getManutype() {
        return manutype;
    }

    public void setManutype(String manutype) {
        this.manutype = manutype;
    }

    public String getContacter1() {
        return contacter1;
    }

    public void setContacter1(String contacter1) {
        this.contacter1 = contacter1;
    }

    public String getContactertel1() {
        return contactertel1;
    }

    public void setContactertel1(String contactertel1) {
        this.contactertel1 = contactertel1;
    }

    public String getContacteradd1() {
        return contacteradd1;
    }

    public void setContacteradd1(String contacteradd1) {
        this.contacteradd1 = contacteradd1;
    }

    public String getContacter2() {
        return contacter2;
    }

    public void setContacter2(String contacter2) {
        this.contacter2 = contacter2;
    }

    public String getContactertel2() {
        return contactertel2;
    }

    public void setContactertel2(String contactertel2) {
        this.contactertel2 = contactertel2;
    }

    public String getContacteradd2() {
        return contacteradd2;
    }

    public void setContacteradd2(String contacteradd2) {
        this.contacteradd2 = contacteradd2;
    }

    public String getMmark() {
        return mmark;
    }

    public void setMmark(String mmark) {
        this.mmark = mmark;
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
        if (!(object instanceof AssetManufacturer)) {
            return false;
        }
        AssetManufacturer other = (AssetManufacturer) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetManufacturer[ id=" + id + " ]";
    }
    
}
