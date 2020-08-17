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
 * @author Administrator
 */
@Entity
@Table(name = "syscode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SysCode.findAll", query = "SELECT s FROM SysCode s")
    , @NamedQuery(name = "SysCode.findById", query = "SELECT s FROM SysCode s WHERE s.id = :id")
    , @NamedQuery(name = "SysCode.findBySyskind", query = "SELECT s FROM SysCode s WHERE s.syskind = :syskind")
    , @NamedQuery(name = "SysCode.findByCode", query = "SELECT s FROM SysCode s WHERE s.code = :code")
    , @NamedQuery(name = "SysCode.findByCvalue", query = "SELECT s FROM SysCode s WHERE s.cvalue = :cvalue")
    , @NamedQuery(name = "SysCode.findByCdesc", query = "SELECT s FROM SysCode s WHERE s.cdesc = :cdesc")
    , @NamedQuery(name = "SysCode.findByStatus", query = "SELECT s FROM SysCode s WHERE s.status = :status")
    , @NamedQuery(name = "SysCode.findByCreator", query = "SELECT s FROM SysCode s WHERE s.creator = :creator")
    , @NamedQuery(name = "SysCode.findByCredate", query = "SELECT s FROM SysCode s WHERE s.credate = :credate")
    , @NamedQuery(name = "SysCode.findByOptuser", query = "SELECT s FROM SysCode s WHERE s.optuser = :optuser")
    , @NamedQuery(name = "SysCode.findByOptdate", query = "SELECT s FROM SysCode s WHERE s.optdate = :optdate")
    , @NamedQuery(name = "SysCode.findByCfmuser", query = "SELECT s FROM SysCode s WHERE s.cfmuser = :cfmuser")
    , @NamedQuery(name = "SysCode.findBySyskindAndCode", query = "SELECT s FROM SysCode s WHERE s.syskind = :syskind And s.code = :code")
    , @NamedQuery(name = "SysCode.findByCfmdate", query = "SELECT s FROM SysCode s WHERE s.cfmdate = :cfmdate")})
public class SysCode extends SuperEntity {


    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "syskind")
    private String syskind;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "code")
    private String code;
    @Size(max = 100)
    @Column(name = "cvalue")
    private String cvalue;
    @Size(max = 200)
    @Column(name = "cdesc")
    private String cdesc;
 
   

    public SysCode() {
    }

    public SysCode(Integer id) {
        this.id = id;
    }

    public SysCode(Integer id, String syskind, String code, String status) {
        this.id = id;
        this.syskind = syskind;
        this.code = code;
        this.status = status;
    }

   

    public String getSyskind() {
        return syskind;
    }

    public void setSyskind(String syskind) {
        this.syskind = syskind;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCvalue() {
        return cvalue;
    }

    public void setCvalue(String cvalue) {
        this.cvalue = cvalue;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
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
        if (!(object instanceof SysCode)) {
            return false;
        }
        SysCode other = (SysCode) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Syscode[ id=" + id + " ]";
    }
    
}
