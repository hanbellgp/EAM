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
@Table(name = "companygrant")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CompanyGrant.getRowCount", query = "SELECT COUNT(c) FROM CompanyGrant c"),
    @NamedQuery(name = "CompanyGrant.findAll", query = "SELECT c FROM CompanyGrant c"),
    @NamedQuery(name = "CompanyGrant.findById", query = "SELECT c FROM CompanyGrant c WHERE c.id = :id"),
    @NamedQuery(name = "CompanyGrant.findByCompany", query = "SELECT c FROM CompanyGrant c WHERE c.company = :company ORDER BY c.userid"),
    @NamedQuery(name = "CompanyGrant.findByCompanyAndUserid", query = "SELECT c FROM CompanyGrant c WHERE c.company = :company AND c.userid = :userid"),
    @NamedQuery(name = "CompanyGrant.findByUserid", query = "SELECT c FROM CompanyGrant c WHERE c.userid = :userid"),
    @NamedQuery(name = "CompanyGrant.findByStatus", query = "SELECT c FROM CompanyGrant c WHERE c.status = :status")})
public class CompanyGrant extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "userid")
    private String userid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "username")
    private String username;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public CompanyGrant() {
    }

    public CompanyGrant(String company, String userid) {
        this.company = company;
        this.userid = userid;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        if (!(object instanceof CompanyGrant)) {
            return false;
        }
        CompanyGrant other = (CompanyGrant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.CompanyGrant[ id=" + id + " ]";
    }

}
