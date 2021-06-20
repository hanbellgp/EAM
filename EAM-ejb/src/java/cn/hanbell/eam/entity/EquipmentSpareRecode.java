/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormEntity;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "equipmentsparerecode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentSpareRecode.findAll", query = "SELECT e FROM EquipmentSpareRecode e"),
    @NamedQuery(name = "EquipmentSpareRecode.findById", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentSpareRecode.findByCompany", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentSpareRecode.findByFormid", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.formid = :formid"),
    @NamedQuery(name = "EquipmentSpareRecode.findByFormdate", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.formdate = :formdate"),
    @NamedQuery(name = "EquipmentSpareRecode.findByWarehouseno", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.warehouseno = :warehouseno"),
    @NamedQuery(name = "EquipmentSpareRecode.findBySarea", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.sarea = :sarea"),
    @NamedQuery(name = "EquipmentSpareRecode.findBySlocation", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.slocation = :slocation"),
    @NamedQuery(name = "EquipmentSpareRecode.findByAccepttype", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.accepttype = :accepttype"),
    @NamedQuery(name = "EquipmentSpareRecode.findByRelano", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.relano = :relano AND e.status = :status"),
    @NamedQuery(name = "EquipmentSpareRecode.findByRemark", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentSpareRecode.findByStatus", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentSpareRecode.findByCreator", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentSpareRecode.findByCredate", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentSpareRecode.findByOptuser", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentSpareRecode.findByOptdate", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentSpareRecode.findByCfmuser", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentSpareRecode.findByCfmdate", query = "SELECT e FROM EquipmentSpareRecode e WHERE e.cfmdate = :cfmdate")})
public class EquipmentSpareRecode extends FormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 45)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 45)
    @Column(name = "sarea")
    private String sarea;
    @Size(max = 45)
    @Column(name = "slocation")
    private String slocation;
    @Size(max = 2)
    @Column(name = "accepttype")
    private String accepttype;
    @Size(max = 45)
    @Column(name = "relano")
    private String relano;
    @Size(max = 20)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public EquipmentSpareRecode() {
    }

    public EquipmentSpareRecode(Integer id) {
        this.id = id;
    }

    public EquipmentSpareRecode(Integer id, String company, String formid, String status) {
        this.id = id;
        this.company = company;
        this.formid = formid;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWarehouseno() {
        return warehouseno;
    }

    public void setWarehouseno(String warehouseno) {
        this.warehouseno = warehouseno;
    }

    public String getSarea() {
        return sarea;
    }

    public void setSarea(String sarea) {
        this.sarea = sarea;
    }

    public String getSlocation() {
        return slocation;
    }

    public void setSlocation(String slocation) {
        this.slocation = slocation;
    }

    public String getAccepttype() {
        return accepttype;
    }

    public void setAccepttype(String accepttype) {
        this.accepttype = accepttype;
    }

    public String getRelano() {
        return relano;
    }

    public void setRelano(String relano) {
        this.relano = relano;
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
        if (!(object instanceof EquipmentSpareRecode)) {
            return false;
        }
        EquipmentSpareRecode other = (EquipmentSpareRecode) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentSparereCode[ id=" + id + " ]";
    }

}
