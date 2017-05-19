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
@Table(name = "transactiontype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionType.findAll", query = "SELECT t FROM TransactionType t"),
    @NamedQuery(name = "TransactionType.findById", query = "SELECT t FROM TransactionType t WHERE t.id = :id"),
    @NamedQuery(name = "TransactionType.findByTrtype", query = "SELECT t FROM TransactionType t WHERE t.trtype = :trtype"),
    @NamedQuery(name = "TransactionType.findByTrname", query = "SELECT t FROM TransactionType t WHERE t.trname = :trname"),
    @NamedQuery(name = "TransactionType.findByModule", query = "SELECT t FROM TransactionType t WHERE t.module = :module"),
    @NamedQuery(name = "TransactionType.findByIocode", query = "SELECT t FROM TransactionType t WHERE t.iocode = :iocode"),
    @NamedQuery(name = "TransactionType.findByHascost", query = "SELECT t FROM TransactionType t WHERE t.hascost = :hascost"),
    @NamedQuery(name = "TransactionType.findByObjtype", query = "SELECT t FROM TransactionType t WHERE t.objtype = :objtype"),
    @NamedQuery(name = "TransactionType.findByObjselect", query = "SELECT t FROM TransactionType t WHERE t.objselect = :objselect"),
    @NamedQuery(name = "TransactionType.findBySrctype", query = "SELECT t FROM TransactionType t WHERE t.srctype = :srctype"),
    @NamedQuery(name = "TransactionType.findBySrcselect", query = "SELECT t FROM TransactionType t WHERE t.srcselect = :srcselect"),
    @NamedQuery(name = "TransactionType.findByReasontype", query = "SELECT t FROM TransactionType t WHERE t.reasontype = :reasontype"),
    @NamedQuery(name = "TransactionType.findByReasonselect", query = "SELECT t FROM TransactionType t WHERE t.reasonselect = :reasonselect"),
    @NamedQuery(name = "TransactionType.findByStatus", query = "SELECT t FROM TransactionType t WHERE t.status = :status")})
public class TransactionType extends SuperEntity {
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "trtype")
    private String trtype;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "trname")
    private String trname;
    @Size(max = 10)
    @Column(name = "module")
    private String module;
    @Basic(optional = false)
    @NotNull
    @Column(name = "iocode")
    private int iocode;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hascost")
    private boolean hascost;
    @Size(max = 100)
    @Column(name = "objtype")
    private String objtype;
    @Size(max = 45)
    @Column(name = "objselect")
    private String objselect;
    @Size(max = 100)
    @Column(name = "srctype")
    private String srctype;
    @Size(max = 45)
    @Column(name = "srcselect")
    private String srcselect;
    @Size(max = 10)
    @Column(name = "reasontype")
    private String reasontype;
    @Size(max = 45)
    @Column(name = "reasonselect")
    private String reasonselect;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updateindate")
    private boolean updateindate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updateoutdate")
    private boolean updateoutdate;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
    
    public TransactionType() {
    }
    
    public String getTrtype() {
        return trtype;
    }
    
    public void setTrtype(String trtype) {
        this.trtype = trtype;
    }
    
    public String getTrname() {
        return trname;
    }
    
    public void setTrname(String trname) {
        this.trname = trname;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }
    
    public int getIocode() {
        return iocode;
    }
    
    public void setIocode(int iocode) {
        this.iocode = iocode;
    }
    
    public boolean getHascost() {
        return hascost;
    }
    
    public void setHascost(boolean hascost) {
        this.hascost = hascost;
    }
    
    public String getObjtype() {
        return objtype;
    }
    
    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }
    
    public String getObjselect() {
        return objselect;
    }
    
    public void setObjselect(String objselect) {
        this.objselect = objselect;
    }
    
    public String getSrctype() {
        return srctype;
    }
    
    public void setSrctype(String srctype) {
        this.srctype = srctype;
    }
    
    public String getSrcselect() {
        return srcselect;
    }
    
    public void setSrcselect(String srcselect) {
        this.srcselect = srcselect;
    }
    
    public String getReasontype() {
        return reasontype;
    }
    
    public void setReasontype(String reasontype) {
        this.reasontype = reasontype;
    }
    
    public String getReasonselect() {
        return reasonselect;
    }
    
    public void setReasonselect(String reasonselect) {
        this.reasonselect = reasonselect;
    }
    
    public boolean getUpdateindate() {
        return updateindate;
    }
    
    public void setUpdateindate(boolean updateindate) {
        this.updateindate = updateindate;
    }
    
    public boolean getUpdateoutdate() {
        return updateoutdate;
    }
    
    public void setUpdateoutdate(boolean updateoutdate) {
        this.updateoutdate = updateoutdate;
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
        if (!(object instanceof TransactionType)) {
            return false;
        }
        TransactionType other = (TransactionType) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.trtype.equals(other.trtype);
    }
    
    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.TransactionType[ id=" + id + " ]";
    }
    
}
