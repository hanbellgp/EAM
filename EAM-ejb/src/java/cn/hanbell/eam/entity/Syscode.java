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
    @NamedQuery(name = "Syscode.findAll", query = "SELECT s FROM Syscode s")
    , @NamedQuery(name = "Syscode.findById", query = "SELECT s FROM Syscode s WHERE s.id = :id")
    , @NamedQuery(name = "Syscode.findBySyskind", query = "SELECT s FROM Syscode s WHERE s.syskind = :syskind")
    , @NamedQuery(name = "Syscode.findByCode", query = "SELECT s FROM Syscode s WHERE s.code = :code")
    , @NamedQuery(name = "Syscode.findByCvalue", query = "SELECT s FROM Syscode s WHERE s.cvalue = :cvalue")
    , @NamedQuery(name = "Syscode.findByCdesc", query = "SELECT s FROM Syscode s WHERE s.cdesc = :cdesc")
    , @NamedQuery(name = "Syscode.findByStatus", query = "SELECT s FROM Syscode s WHERE s.status = :status")
    , @NamedQuery(name = "Syscode.findByCreator", query = "SELECT s FROM Syscode s WHERE s.creator = :creator")
    , @NamedQuery(name = "Syscode.findByCredate", query = "SELECT s FROM Syscode s WHERE s.credate = :credate")
    , @NamedQuery(name = "Syscode.findByOptuser", query = "SELECT s FROM Syscode s WHERE s.optuser = :optuser")
    , @NamedQuery(name = "Syscode.findByOptdate", query = "SELECT s FROM Syscode s WHERE s.optdate = :optdate")
    , @NamedQuery(name = "Syscode.findByCfmuser", query = "SELECT s FROM Syscode s WHERE s.cfmuser = :cfmuser")
    , @NamedQuery(name = "Syscode.findBySyskindAndCode", query = "SELECT s FROM Syscode s WHERE s.syskind = :syskind And s.code = :code")
    , @NamedQuery(name = "Syscode.findByCfmdate", query = "SELECT s FROM Syscode s WHERE s.cfmdate = :cfmdate")})
public class Syscode extends SuperEntity {


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
 
   

    public Syscode() {
    }

    public Syscode(Integer id) {
        this.id = id;
    }

    public Syscode(Integer id, String syskind, String code, String status) {
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
        if (!(object instanceof Syscode)) {
            return false;
        }
        Syscode other = (Syscode) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.Syscode[ id=" + id + " ]";
    }
    
}
