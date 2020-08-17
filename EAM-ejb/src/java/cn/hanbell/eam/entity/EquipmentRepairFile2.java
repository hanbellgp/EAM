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
@Table(name = "equipmentrepairfile")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentRepairFile.findAll", query = "SELECT e FROM EquipmentRepairFile e"),
    @NamedQuery(name = "EquipmentRepairFile.findById", query = "SELECT e FROM EquipmentRepairFile e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentRepairFile.findByCompany", query = "SELECT e FROM EquipmentRepairFile e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentRepairFile.findByPid", query = "SELECT e FROM EquipmentRepairFile e WHERE e.pid = :pid"),
    @NamedQuery(name = "EquipmentRepairFile.findBySeq", query = "SELECT e FROM EquipmentRepairFile e WHERE e.seq = :seq"),
    @NamedQuery(name = "EquipmentRepairFile.findByFilefrom", query = "SELECT e FROM EquipmentRepairFile e WHERE e.filefrom = :filefrom"),
    @NamedQuery(name = "EquipmentRepairFile.findByFileno", query = "SELECT e FROM EquipmentRepairFile e WHERE e.fileno = :fileno"),
    @NamedQuery(name = "EquipmentRepairFile.findByFilename", query = "SELECT e FROM EquipmentRepairFile e WHERE e.filename = :filename"),
    @NamedQuery(name = "EquipmentRepairFile.findByFilepath", query = "SELECT e FROM EquipmentRepairFile e WHERE e.filepath = :filepath"),
    @NamedQuery(name = "EquipmentRepairFile.findByFilemark", query = "SELECT e FROM EquipmentRepairFile e WHERE e.filemark = :filemark"),
    @NamedQuery(name = "EquipmentRepairFile.findByRemark", query = "SELECT e FROM EquipmentRepairFile e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentRepairFile.findByStatus", query = "SELECT e FROM EquipmentRepairFile e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentRepairFile.findByCreator", query = "SELECT e FROM EquipmentRepairFile e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentRepairFile.findByCredate", query = "SELECT e FROM EquipmentRepairFile e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentRepairFile.findByOptuser", query = "SELECT e FROM EquipmentRepairFile e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentRepairFile.findByOptdate", query = "SELECT e FROM EquipmentRepairFile e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentRepairFile.findByCfmuser", query = "SELECT e FROM EquipmentRepairFile e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentRepairFile.findByPId", query = "SELECT e FROM EquipmentRepairFile e WHERE e.pid = :pid ORDER BY e.seq"),
    @NamedQuery(name = "EquipmentRepairFile.findByCfmdate", query = "SELECT e FROM EquipmentRepairFile e WHERE e.cfmdate = :cfmdate")})
public class EquipmentRepairFile2 extends FormDetailEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;

    @Size(max = 10)
    @Column(name = "filefrom")
    private String filefrom;
    @Size(max = 45)
    @Column(name = "fileno")
    private String fileno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "filename")
    private String filename;
    @Size(max = 100)
    @Column(name = "filepath")
    private String filepath;
    @Size(max = 200)
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

    public EquipmentRepairFile2() {
    }

    public EquipmentRepairFile2(Integer id) {
        this.id = id;
    }

    public EquipmentRepairFile2(Integer id, String company, String pid, String filename, String status) {
        this.id = id;
        this.company = company;
        this.pid = pid;
        this.filename = filename;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFilefrom() {
        return filefrom;
    }

    public void setFilefrom(String filefrom) {
        this.filefrom = filefrom;
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
        if (!(object instanceof EquipmentRepairFile2)) {
            return false;
        }
        EquipmentRepairFile2 other = (EquipmentRepairFile2) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentRepairFile[ id=" + id + " ]";
    }

}
